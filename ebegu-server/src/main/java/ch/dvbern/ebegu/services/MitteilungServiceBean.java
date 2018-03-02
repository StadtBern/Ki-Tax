/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.dto.suchfilter.smarttable.MitteilungPredicateObjectDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.MitteilungTableFilterDTO;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuung_;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.BetreuungsmitteilungPensum;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung_;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Fall_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.KindContainer_;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.Mitteilung_;
import ch.dvbern.ebegu.enums.Amt;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.SearchMode;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguExistingAntragException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.services.util.SearchUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.lib.cdipersistence.Persistence;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.JURIST;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Mitteilungen
 */
@Stateless
@Local(MitteilungService.class)
@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION,
	SACHBEARBEITER_TRAEGERSCHAFT, ADMINISTRATOR_SCHULAMT, SCHULAMT })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MitteilungServiceBean extends AbstractBaseService implements MitteilungService {

	private static final Logger LOG = LoggerFactory.getLogger(MitteilungServiceBean.class.getSimpleName());

	@Inject
	private Persistence persistence;

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private MailService mailService;

	@Inject
	private FallService fallService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private ApplicationPropertyService applicationPropertyService;


	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Mitteilung sendMitteilung(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);

		checkMitteilungDataConsistency(mitteilung);

		if (MitteilungStatus.ENTWURF != mitteilung.getMitteilungStatus()) {
			throw new IllegalArgumentException("Mitteilung ist nicht im Status ENTWURF und kann nicht gesendet werden");
		}
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung.setSentDatum(LocalDateTime.now());

		authorizer.checkWriteAuthorizationMitteilung(mitteilung);
		setSenderAndEmpfaenger(mitteilung);

		// Falls die Mitteilung an einen Gesuchsteller geht, muss dieser benachrichtigt werden. Es muss zuerst geprueft werden, dass
		// die Mitteilung valid ist, dafuer brauchen wir den Validator

		try {
			Validator validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
			final Set<ConstraintViolation<Mitteilung>> validationErrors = validator.validate(mitteilung);
			if (!validationErrors.isEmpty()) {
				throw new ConstraintViolationException(validationErrors);
			}

			if (MitteilungTeilnehmerTyp.GESUCHSTELLER == mitteilung.getEmpfaengerTyp() && mitteilung.getEmpfaenger() != null) {
				mailService.sendInfoMitteilungErhalten(mitteilung);
			}

		} catch (MailException e) {
			LOG.error(String.format("Mail InfoMitteilungErhalten konnte nicht verschickt werden fuer Mitteilung %s", mitteilung.getId()), e);
			throw new EbeguRuntimeException("sendMitteilung", ErrorCodeEnum.ERROR_MAIL, e);
		}

		return persistence.merge(mitteilung);
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	private void checkMitteilungDataConsistency(@Nonnull Mitteilung mitteilung) {
		if (!mitteilung.isNew()) {
			Mitteilung persistedMitteilung = findMitteilung(mitteilung.getId()).orElseThrow(() -> new EbeguEntityNotFoundException
				("sendMitteilung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "MitteilungId invalid: " + mitteilung.getId()));

			// Die gespeicherte wie auch die uebergebene Mitteilung muss im Status ENTWURF sein
			if (MitteilungStatus.ENTWURF != persistedMitteilung.getMitteilungStatus()) {
				throw new IllegalArgumentException("Mitteilung aus DB ist nicht im Status ENTWURF und kann nicht gesendet werden");
			}
			if (!persistedMitteilung.getSender().equals(mitteilung.getSender())) {
				throw new IllegalArgumentException("Mitteilung aus DB hat anderen Sender gesetzt");
			}
		}
	}

	private void setSenderAndEmpfaenger(@Nonnull Mitteilung mitteilung) {
		Benutzer benutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new IllegalStateException("Benutzer ist nicht eingeloggt!"));

		Optional<Benutzer> optEmpfaengerAmt = fallService.getHauptOrDefaultVerantwortlicher(mitteilung.getFall());
		final Benutzer empfaengerAmt = optEmpfaengerAmt.orElseThrow(() ->
			new EbeguRuntimeException("setSenderAndEmpfaenger", ErrorCodeEnum.ERROR_VERANTWORTLICHER_NOT_FOUND, mitteilung.getId())
		);

		mitteilung.setSender(benutzer);
		switch (benutzer.getRole()) {
		case GESUCHSTELLER: {
			mitteilung.setEmpfaenger(empfaengerAmt);
			mitteilung.setEmpfaengerTyp(MitteilungTeilnehmerTyp.JUGENDAMT);
			mitteilung.setSenderTyp(MitteilungTeilnehmerTyp.GESUCHSTELLER);
			break;
		}
		case SACHBEARBEITER_INSTITUTION:
		case SACHBEARBEITER_TRAEGERSCHAFT: {
			mitteilung.setEmpfaenger(empfaengerAmt);
			mitteilung.setEmpfaengerTyp(MitteilungTeilnehmerTyp.JUGENDAMT);
			mitteilung.setSenderTyp(MitteilungTeilnehmerTyp.INSTITUTION);
			break;
		}
		case SACHBEARBEITER_JA:
		case ADMIN:
		case SUPER_ADMIN:
		case SCHULAMT:
		case ADMINISTRATOR_SCHULAMT:
			Benutzer besitzer = mitteilung.getFall().getBesitzer();
			mitteilung.setEmpfaenger(besitzer);
			mitteilung.setEmpfaengerTyp(MitteilungTeilnehmerTyp.GESUCHSTELLER);

			mitteilung.setSenderTyp(MitteilungTeilnehmerTyp.JUGENDAMT);
			break;
		}
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Mitteilung saveEntwurf(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);

		checkMitteilungDataConsistency(mitteilung);

		if (MitteilungStatus.ENTWURF != mitteilung.getMitteilungStatus()) {
			throw new IllegalArgumentException("Mitteilung ist nicht im Status ENTWURF und kann nicht als Entwurf gespeichert werden");
		}
		setSenderAndEmpfaenger(mitteilung);
		authorizer.checkWriteAuthorizationMitteilung(mitteilung);
		return persistence.merge(mitteilung);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Mitteilung setMitteilungGelesen(@Nonnull String mitteilungsId) {
		return setMitteilungsStatusIfBerechtigt(mitteilungsId, MitteilungStatus.GELESEN, MitteilungStatus.NEU, MitteilungStatus.ERLEDIGT);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Mitteilung setMitteilungErledigt(@Nonnull String mitteilungsId) {
		return setMitteilungsStatusIfBerechtigt(mitteilungsId, MitteilungStatus.ERLEDIGT, MitteilungStatus.GELESEN);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Optional<Mitteilung> findMitteilung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Mitteilung mitteilung = persistence.find(Mitteilung.class, key);
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		return Optional.ofNullable(mitteilung);
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Optional<Betreuungsmitteilung> findBetreuungsmitteilung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Betreuungsmitteilung mitteilung = persistence.find(Betreuungsmitteilung.class, key);
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		return Optional.ofNullable(mitteilung);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN })
	public Collection<Betreuungsmitteilung> findAllBetreuungsmitteilungenForBetreuung(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung muss gesetzt sein");

		// Diese Methode wird nur beim Loeschen einer Online-Mutation durch den Admin beim Erstellen einer Papier-Mutation verwendet.
		// Wir koennen in diesem Fall die normale AuthCheck verwenden, da niemand vom JA fuer die vorhandene Online-Mutation des GS nach
		// herkoemmlichem Schema berechtigt ist. Wir duerfen hier aber trotzdem loeschen. Methode ist aber nur fuer ADMIN und SUPER_ADMIN verfuegbar.

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuungsmitteilung> query = cb.createQuery(Betreuungsmitteilung.class);
		Root<Betreuungsmitteilung> root = query.from(Betreuungsmitteilung.class);
		List<Predicate> predicates = new ArrayList<>();

		ParameterExpression<Betreuung> betreuungParam = cb.parameter(Betreuung.class, "betreuunParam");
		ParameterExpression<MitteilungStatus> statusParam = cb.parameter(MitteilungStatus.class, "statusParam");

		Predicate predicateLinkedObject = cb.equal(root.get(Betreuungsmitteilung_.betreuung), betreuungParam);
		predicates.add(predicateLinkedObject);

		Predicate predicateEntwurf = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), statusParam);
		predicates.add(predicateEntwurf);

		query.orderBy(cb.desc(root.get(Mitteilung_.sentDatum)));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));

		TypedQuery<Betreuungsmitteilung> tq = persistence.getEntityManager().createQuery(query);

		tq.setParameter("betreuunParam", betreuung);
		tq.setParameter("statusParam", MitteilungStatus.ENTWURF);

		return tq.getResultList();
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Collection<Mitteilung> findAllMitteilungenForBetreuung(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung muss gesetzt sein");

		authorizer.checkReadAuthorization(betreuung);

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Predicate> predicates = new ArrayList<>();

		ParameterExpression<Betreuung> betreuungParam = cb.parameter(Betreuung.class, "betreuunParam");

		Predicate predicateLinkedObject = cb.equal(root.get(Mitteilung_.betreuung), betreuungParam);
		predicates.add(predicateLinkedObject);

		query.orderBy(cb.desc(root.get(Mitteilung_.sentDatum)));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));

		TypedQuery<Mitteilung> tq = persistence.getEntityManager().createQuery(query);

		tq.setParameter("betreuunParam", betreuung);

		return tq.getResultList();
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");
		authorizer.checkReadAuthorizationFall(fall);
		return getMitteilungenForCurrentRolle(Mitteilung_.fall, fall);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT })
	public Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung muss gesetzt sein");
		authorizer.checkReadAuthorization(betreuung);
		return getMitteilungenForCurrentRolle(Mitteilung_.betreuung, betreuung);
	}

	private <T> Collection<Mitteilung> getMitteilungenForCurrentRolle(SingularAttribute<Mitteilung, T> attribute, @Nonnull T linkedEntity) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Predicate> predicates = new ArrayList<>();

		Predicate predicateLinkedObject = cb.equal(root.get(attribute), linkedEntity);
		predicates.add(predicateLinkedObject);

		Predicate predicateEntwurf = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateSender = cb.equal(root.get(Mitteilung_.senderTyp), mitteilungTeilnehmerTyp);
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		Predicate predicateSenderOrEmpfaenger = cb.or(predicateSender, predicateEmpfaenger);
		predicates.add(predicateSenderOrEmpfaenger);

		query.orderBy(cb.desc(root.get(Mitteilung_.sentDatum)));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		List<Mitteilung> mitteilungen = persistence.getCriteriaResults(query);
		authorizer.checkReadAuthorizationMitteilungen(mitteilungen);
		return mitteilungen;
	}

	@Override
	@Nullable
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Mitteilung getEntwurfForCurrentRolle(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");
		return getEntwurfForCurrentRolle(Mitteilung_.fall, fall);
	}

	@Override
	@Nullable
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT })
	public Mitteilung getEntwurfForCurrentRolle(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung muss gesetzt sein");
		return getEntwurfForCurrentRolle(Mitteilung_.betreuung, betreuung);
	}

	private <T> Mitteilung getEntwurfForCurrentRolle(SingularAttribute<Mitteilung, T> attribute, @Nonnull T linkedEntity) {
		Benutzer loggedInBenutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("getEntwurfForCurrentRolle", "No User is logged in"));
		UserRole currentUserRole = loggedInBenutzer.getRole();
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Predicate> predicates = new ArrayList<>();

		Predicate predicateLinkedObject = cb.equal(root.get(attribute), linkedEntity);
		predicates.add(predicateLinkedObject);

		Predicate predicateEntwurf = cb.equal(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateSender = cb.equal(root.get(Mitteilung_.senderTyp), mitteilungTeilnehmerTyp);
		predicates.add(predicateSender);

		if (currentUserRole.isRoleJugendamt()) {
			Predicate predicateSenderGleichesAmt = root.get(Mitteilung_.sender).get(Benutzer_.role).in(UserRole.getJugendamtRoles());
			predicates.add(predicateSenderGleichesAmt);
		} else if (currentUserRole.isRoleSchulamt()) {
			Predicate predicateSenderGleichesAmt = root.get(Mitteilung_.sender).get(Benutzer_.role).in(UserRole.getSchulamtRoles());
			predicates.add(predicateSenderGleichesAmt);
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		Mitteilung entwurf = persistence.getCriteriaSingleResult(query);
		authorizer.checkWriteAuthorizationMitteilung(entwurf);
		return entwurf;
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT })
	public void removeMitteilung(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);
		authorizer.checkWriteAuthorizationMitteilung(mitteilung);
		persistence.remove(mitteilung);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN })
	public void removeAllMitteilungenForFall(@Nonnull Fall fall) {
		Collection<Mitteilung> mitteilungen = criteriaQueryHelper.getEntitiesByAttribute(Mitteilung.class, fall, Mitteilung_.fall);
		for (Mitteilung mitteilung : mitteilungen) {
			authorizer.checkWriteAuthorizationMitteilung(mitteilung);
			persistence.remove(Mitteilung.class, mitteilung.getId());
		}
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN })
	public void removeAllBetreuungMitteilungenForGesuch(@Nonnull Gesuch gesuch) {
		authorizer.checkWriteAuthorization(gesuch);

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		final Join<Betreuung, KindContainer> join = root.join(Mitteilung_.betreuung, JoinType.LEFT)
			.join(Betreuung_.kind, JoinType.LEFT);

		Predicate gesuchPred = cb.equal(join.get(KindContainer_.gesuch), gesuch);
		Predicate withBetreuungPred = cb.isNotNull(root.get(Mitteilung_.betreuung));

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, gesuchPred, withBetreuungPred));
		final List<Mitteilung> mitteilungen = persistence.getCriteriaResults(query);

		for (Mitteilung mitteilung : mitteilungen) {
			persistence.remove(Mitteilung.class, mitteilung.getId());
		}
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Collection<Mitteilung> setAllNewMitteilungenOfFallGelesen(@Nonnull Fall fall) {
		Collection<Mitteilung> mitteilungen = getMitteilungenForCurrentRolle(fall);
		for (Mitteilung mitteilung : mitteilungen) {
			if (MitteilungStatus.NEU == mitteilung.getMitteilungStatus()) {
				setMitteilungsStatusIfBerechtigt(mitteilung, MitteilungStatus.GELESEN, MitteilungStatus.NEU);
			}
		}
		return mitteilungen;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Collection<Mitteilung> getNewMitteilungenForCurrentRolleAndFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");
		authorizer.checkReadAuthorizationFall(fall);

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Predicate> predicates = new ArrayList<>();

		Predicate predicateFall = cb.equal(root.get(Mitteilung_.fall), fall);
		predicates.add(predicateFall);

		Predicate predicateNew = cb.equal(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.NEU);
		predicates.add(predicateNew);

		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		predicates.add(predicateEmpfaenger);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		List<Mitteilung> mitteilungen = persistence.getCriteriaResults(query);
		authorizer.checkReadAuthorizationMitteilungen(mitteilungen);
		return mitteilungen;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Long getAmountNewMitteilungenForCurrentBenutzer() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Predicate> predicates = new ArrayList<>();

		Predicate predicateNew = cb.equal(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.NEU);
		predicates.add(predicateNew);

		Benutzer loggedInBenutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException
			("getAmountNewMitteilungenForCurrentBenutzer", "No User is logged in"));
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaenger), loggedInBenutzer);
		predicates.add(predicateEmpfaenger);

		query.select(cb.countDistinct(root));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaSingleResult(query);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT })
	public Betreuungsmitteilung sendBetreuungsmitteilung(@Nonnull Betreuungsmitteilung betreuungsmitteilung) {
		Objects.requireNonNull(betreuungsmitteilung);
		if (MitteilungTeilnehmerTyp.INSTITUTION != betreuungsmitteilung.getSenderTyp()) {
			throw new IllegalArgumentException("Eine Betreuungsmitteilung darf nur bei einer Institution geschickt werden");
		}
		if (MitteilungTeilnehmerTyp.JUGENDAMT != betreuungsmitteilung.getEmpfaengerTyp()) {
			throw new IllegalArgumentException("Eine Betreuungsmitteilung darf nur an das Jugendamt geschickt werden");
		}
		betreuungsmitteilung.setMitteilungStatus(MitteilungStatus.NEU); // vorsichtshalber
		betreuungsmitteilung.setSentDatum(LocalDateTime.now());
		authorizer.checkWriteAuthorizationMitteilung(betreuungsmitteilung);
		setSenderAndEmpfaenger(betreuungsmitteilung);

		return persistence.persist(betreuungsmitteilung); // A Betreuungsmitteilung is created and sent, therefore persist and not merge
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	public Gesuch applyBetreuungsmitteilung(@Nonnull Betreuungsmitteilung mitteilung) {
		final Gesuch gesuch = mitteilung.getBetreuung().extractGesuch();
		authorizer.checkWriteAuthorization(gesuch);
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		// neustes Gesuch lesen
		final Optional<Gesuch> neustesGesuchOpt;
		try {
			neustesGesuchOpt = gesuchService.getNeustesGesuchFuerGesuch(gesuch);
		} catch (EJBTransactionRolledbackException exception) {
			//Wenn der Sachbearbeiter den neusten Antrag nicht lesen darf ist es ein noch nicht freigegebener ONLINE Antrag
			if (exception.getCause().getClass().equals(EJBAccessException.class)) {
				throw new EbeguExistingAntragException("applyBetreuungsmitteilung", ErrorCodeEnum.ERROR_EXISTING_ONLINE_MUTATION,
					exception, gesuch.getFall().getId(), gesuch.getGesuchsperiode().getId());
			}
			throw exception;
		}
		if (neustesGesuchOpt.isPresent()) {
			final Gesuch neustesGesuch = neustesGesuchOpt.get();
			// Sobald irgendein Antrag dieser Periode geperrt ist, darf keine Mutationsmeldungs-Mutation erstellt werden!
			if (neustesGesuch.isGesperrtWegenBeschwerde()) {
				throw new EbeguRuntimeException("applyBetreuungsmitteilung", ErrorCodeEnum.ERROR_MUTATIONSMELDUNG_FALL_GESPERRT);
			} else if (AntragStatus.VERFUEGEN == neustesGesuch.getStatus()) {
				throw new EbeguRuntimeException("applyBetreuungsmitteilung", ErrorCodeEnum.ERROR_MUTATIONSMELDUNG_STATUS_VERFUEGEN);
			} else if (!AntragStatus.getVerfuegtAndSTVStates().contains(neustesGesuch.getStatus()) && neustesGesuch.isMutation()) {
				//betreuungsaenderungen der bestehenden, offenen Mutation hinzufuegen (wenn wir hier sind muss es sich um ein PAPIER) Antrag handeln
				applyBetreuungsmitteilungToMutation(neustesGesuch, mitteilung);
				return neustesGesuch;
			} else if (AntragStatus.getVerfuegtAndSTVStates().contains(neustesGesuch.getStatus())) {
				// create Mutation if there is currently no Mutation
				final Optional<Gesuch> mutationOpt = this.gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());
				if (mutationOpt.isPresent()) {
					Gesuch persistedMutation = gesuchService.createGesuch(mutationOpt.get());
					applyBetreuungsmitteilungToMutation(persistedMutation, mitteilung);
					return persistedMutation;
				}
			} else {
				throw new EbeguRuntimeException("applyBetreuungsmitteilung", ErrorCodeEnum.ERROR_MUTATIONSMELDUNG_GESUCH_NICHT_FREIGEGEBEN);
			}
		}
		return gesuch;
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Optional<Betreuungsmitteilung> findNewestBetreuungsmitteilung(@Nonnull String betreuungId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuungsmitteilung> query = cb.createQuery(Betreuungsmitteilung.class);
		Root<Betreuungsmitteilung> root = query.from(Betreuungsmitteilung.class);

		Predicate predicateLinkedObject = cb.equal(root.get(Betreuungsmitteilung_.betreuung).get(Betreuung_.id), betreuungId);

		query.orderBy(cb.desc(root.get(Betreuungsmitteilung_.sentDatum)));
		query.where(predicateLinkedObject);

		final List<Betreuungsmitteilung> result = persistence.getEntityManager().createQuery(query).setFirstResult(0).setMaxResults(1).getResultList();
		if (result.isEmpty()) {
			return Optional.empty();
		}
		Betreuungsmitteilung firstResult = result.get(0);
		authorizer.checkReadAuthorizationMitteilung(firstResult);
		return Optional.of(firstResult);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Mitteilung mitteilungUebergebenAnJugendamt(@Nonnull String mitteilungId) {
		Mitteilung mitteilung = findMitteilung(mitteilungId).orElseThrow(() -> new EbeguRuntimeException("mitteilungUebergebenAnJugendamt", "Mitteilung not found"));
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		// Dass der eingeloggte Benutzer Schulamt ist, ist schon durch die Berechtigungen geprueft. Es muss noch sichergestellt werden, dass die Meldung
		// auch tatsaechlich dem Schulamt "gehoert"
		if (mitteilung.getEmpfaengerAmt() == Amt.SCHULAMT) {
			// An wen soll die Meldung delegiert werden?
			Benutzer verantwortlicherJA = mitteilung.getFall().getVerantwortlicher();
			if (verantwortlicherJA == null) {
				// Kein JA-Verantwortlicher definiert. Wir nehmen den Default-Verantwortlichen
				Optional<Benutzer> optVerantwortlicherJA = applicationPropertyService.readDefaultVerantwortlicherFromProperties();
				verantwortlicherJA = optVerantwortlicherJA.orElseThrow(() ->
					new EbeguRuntimeException("mitteilungUebergebenAnJugendamt", ErrorCodeEnum.ERROR_EMPFAENGER_JA_NOT_FOUND, mitteilung.getId())
				);
			}
			// Den VerantwortlichenJA als Empfänger setzen
			mitteilung.setEmpfaenger(verantwortlicherJA);
			mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
			return persistence.merge(mitteilung);

		}
		throw new IllegalArgumentException("Die Mitteilung hat entweder keinen Empfänger oder dieser ist nicht in Rolle Schulamt");
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Mitteilung mitteilungUebergebenAnSchulamt(@Nonnull String mitteilungId) {
		Mitteilung mitteilung = findMitteilung(mitteilungId).orElseThrow(() -> new EbeguRuntimeException("mitteilungUebergebenAnSchulamt", "Mitteilung not found"));
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		// Dass der eingeloggte Benutzer Jugendamt ist, ist schon durch die Berechtigungen geprueft. Es muss noch sichergestellt werden, dass die Meldung
		// auch tatsaechlich dem Jugendamt "gehoert"
		if (mitteilung.getEmpfaengerAmt() == Amt.JUGENDAMT) {
			// An wen soll die Meldung delegiert werden?
			Benutzer verantwortlicherSCH = mitteilung.getFall().getVerantwortlicherSCH();
			if (verantwortlicherSCH == null) {
				// Kein SCH-Verantwortlicher definiert. Wir nehmen den Default-Verantwortlichen
				Optional<Benutzer> optVerantwortlicherSCH = applicationPropertyService.readDefaultVerantwortlicherSCHFromProperties();
				verantwortlicherSCH = optVerantwortlicherSCH.orElseThrow(() ->
					new EbeguRuntimeException("mitteilungUebergebenAnSchulamt", ErrorCodeEnum.ERROR_EMPFAENGER_SCH_NOT_FOUND, mitteilung.getId())
				);
			}
			// Den VerantwortlichenJA als Empfänger setzen
			mitteilung.setEmpfaenger(verantwortlicherSCH);
			mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
			return persistence.merge(mitteilung);

		}
		throw new IllegalArgumentException("Die Mitteilung hat entweder keinen Empfänger oder dieser ist nicht in Rolle Jugendamt");
	}

	@Nonnull
	@Override
	public Pair<Long, List<Mitteilung>> searchMitteilungen(@Nonnull MitteilungTableFilterDTO mitteilungTableFilterDto, @Nonnull Boolean includeClosed) {
		Pair<Long, List<Mitteilung>> result;
		Long countResult = searchMitteilungen(mitteilungTableFilterDto, includeClosed, SearchMode.COUNT).getLeft();
		if (countResult.equals(0L)) {    // no result found
			result = new ImmutablePair<>(0L, Collections.emptyList());
		} else {
			Pair<Long, List<Mitteilung>> searchResult = searchMitteilungen(mitteilungTableFilterDto, includeClosed, SearchMode.SEARCH);
			result = new ImmutablePair<>(countResult, searchResult.getRight());
		}
		return result;
	}

	@SuppressWarnings({"rawtypes", "unchecked", "PMD.NcssMethodCount"}) // Je nach Abfrage ist es String oder Long
	private Pair<Long, List<Mitteilung>> searchMitteilungen(@Nonnull MitteilungTableFilterDTO mitteilungTableFilterDto, @Nonnull Boolean includeClosed, @Nonnull SearchMode mode) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		CriteriaQuery query = SearchUtil.getQueryForSearchMode(cb, mode);

		// Construct from-clause
		Root<Mitteilung> root = query.from(Mitteilung.class);

		// Join all the relevant relations
		Join<Mitteilung, Fall> joinFall = root.join(Mitteilung_.fall, JoinType.INNER);
		Join<Fall, Benutzer> joinBesitzer = joinFall.join(Fall_.besitzer, JoinType.LEFT);
		Join<Mitteilung, Benutzer> joinSender = root.join(Mitteilung_.sender, JoinType.LEFT);
		Join<Mitteilung, Benutzer> joinEmpfaenger = root.join(Mitteilung_.empfaenger, JoinType.LEFT);

		// Predicates derived from PredicateDTO (Filter coming from client)
		MitteilungPredicateObjectDTO predicateObjectDto = mitteilungTableFilterDto.getSearch().getPredicateObject();

		//prepare predicates
		List<Predicate> predicates = new ArrayList<>();

		// Keine Entwuerfe fuer Posteingang
		Predicate predicateEntwurf = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		// Richtiger Empfangs-Typ. Persoenlicher Empfaenger wird nicht beachtet sondern auf Client mit Filter geloest
		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateEmpfaengerTyp = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		predicates.add(predicateEmpfaengerTyp);

		if (predicateObjectDto != null) {

			// sender
			if (predicateObjectDto.getSender() != null) {
				predicates.add(
					cb.or(
						cb.like(joinSender.get(Benutzer_.nachname), SearchUtil.withWildcards(predicateObjectDto.getSender())),
						cb.like(joinSender.get(Benutzer_.vorname), SearchUtil.withWildcards(predicateObjectDto.getSender()))
					));
			}
			// fallNummer
			if (predicateObjectDto.getFallNummer() != null) {
				// Die Fallnummer muss als String mit LIKE verglichen werden: Bei Eingabe von "14" soll der Fall "114" kommen
				Expression<String> fallNummerAsString = joinFall.get(Fall_.fallNummer).as(String.class);
				String fallNummerWithWildcards = SearchUtil.withWildcards(predicateObjectDto.getFallNummer());
				predicates.add(cb.like(fallNummerAsString, fallNummerWithWildcards));
			}
			// familienName
			if (predicateObjectDto.getFamilienName() != null) {
				predicates.add(
					cb.or(
						cb.like(joinBesitzer.get(Benutzer_.nachname), SearchUtil.withWildcards(predicateObjectDto.getFamilienName())),
						cb.like(joinBesitzer.get(Benutzer_.vorname), SearchUtil.withWildcards(predicateObjectDto.getFamilienName()))
					));
			}
			// subject
			if (predicateObjectDto.getSubject() != null) {
				predicates.add(cb.like(root.get(Mitteilung_.subject), SearchUtil.withWildcards(predicateObjectDto.getSubject())));
			}
			// sentDatum
			if (predicateObjectDto.getSentDatum() != null) {
				try {
					LocalDate searchDate = LocalDate.parse(predicateObjectDto.getSentDatum(), Constants.DATE_FORMATTER);
					predicates.add(cb.between(root.get(Mitteilung_.sentDatum), searchDate.atStartOfDay(), searchDate.plusDays(1).atStartOfDay()));
				} catch (DateTimeParseException e) {
					// Kein gueltiges Datum. Es kann kein Mitteilung geben, welches passt. Wir geben leer zurueck
					return new ImmutablePair<>(0L, Collections.emptyList());
				}
			}
			// empfaenger
			if (predicateObjectDto.getEmpfaenger() != null) {
				String[] strings = predicateObjectDto.getEmpfaenger().split(" ");
				predicates.add(
					cb.and(
						cb.equal(joinEmpfaenger.get(Benutzer_.vorname), strings[0]),
						cb.equal(joinEmpfaenger.get(Benutzer_.nachname), strings[1])
					));
			}
			// empfaengerAmt
			if (predicateObjectDto.getEmpfaengerAmt() != null) {
				Amt amt = Amt.valueOf(predicateObjectDto.getEmpfaengerAmt());
				switch (amt) {
				case JUGENDAMT:
					predicates.add(joinEmpfaenger.get(Benutzer_.role).in(UserRole.getJugendamtSuperadminRoles()));
					break;
				case SCHULAMT:
					predicates.add(joinEmpfaenger.get(Benutzer_.role).in(UserRole.getSchulamtRoles()));
					break;
				}
			}
			// mitteilungStatus
			if (predicateObjectDto.getMitteilungStatus() != null) {
				MitteilungStatus mitteilungStatus = MitteilungStatus.valueOf(predicateObjectDto.getMitteilungStatus());
				predicates.add(cb.equal(root.get(Mitteilung_.mitteilungStatus), mitteilungStatus));
			}
			// Inkl. abgeschlossene
			if (!includeClosed) {
				Predicate predicateNichtErledigt = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ERLEDIGT);
				predicates.add(predicateNichtErledigt);
			}
		}

		// Construct the select- and where-clause
		switch (mode) {
		case SEARCH:
			//noinspection unchecked // Je nach Abfrage ist das Query String oder Long
			query.select(root.get(Mitteilung_.id)).where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
			constructOrderByClause(mitteilungTableFilterDto, cb, query, root, joinFall, joinBesitzer, joinSender, joinEmpfaenger);
			break;
		case COUNT:
			//noinspection unchecked // Je nach Abfrage ist das Query String oder Long
			query.select(cb.countDistinct(root.get(Gesuch_.id)))
				.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
			break;
		}

		// Prepare and execute the query and build the result
		Pair<Long, List<Mitteilung>> result = null;
		switch (mode) {
		case SEARCH:
			List<String> gesuchIds = persistence.getCriteriaResults(query); //select all ids in order, may contain duplicates
			List<Mitteilung> pagedResult;
			if (mitteilungTableFilterDto.getPagination() != null) {
				int firstIndex = mitteilungTableFilterDto.getPagination().getStart();
				Integer maxresults = mitteilungTableFilterDto.getPagination().getNumber();
				List<String> orderedIdsToLoad = SearchUtil.determineDistinctGesuchIdsToLoad(gesuchIds, firstIndex, maxresults);
				pagedResult = findMitteilungen(orderedIdsToLoad);
			} else {
				pagedResult = findMitteilungen(gesuchIds);
			}
			result = new ImmutablePair<>(null, pagedResult);
			break;
		case COUNT:
			Long count = (Long) persistence.getCriteriaSingleResult(query);
			result = new ImmutablePair<>(count, null);
			break;
		}
		return result;
	}


	@SuppressWarnings("ReuseOfLocalVariable")
	private void constructOrderByClause(@Nonnull MitteilungTableFilterDTO tableFilterDTO, CriteriaBuilder cb, CriteriaQuery query,
			Root<Mitteilung> root, Join<Mitteilung, Fall> joinFall, Join<Fall, Benutzer> joinBesitzer,
			Join<Mitteilung, Benutzer> joinSender, Join<Mitteilung, Benutzer> joinEmpfaenger) {
		Expression<?> expression = null;
		if (tableFilterDTO.getSort() != null && tableFilterDTO.getSort().getPredicate() != null) {
			switch (tableFilterDTO.getSort().getPredicate()) {
			case "sender":
				expression = joinSender.get(Benutzer_.vorname);
				break;
			case "fallNummer":
				expression = joinFall.get(Fall_.fallNummer);
				break;
			case "familienName":
				expression = joinBesitzer.get(Benutzer_.vorname);
				break;
			case "subject":
				expression = root.get(Mitteilung_.subject);
				break;
			case "sentDatum":
				expression = root.get(Mitteilung_.sentDatum);
				break;
			case "empfaenger":
				expression = joinEmpfaenger.get(Benutzer_.vorname);
				break;
			case "empfaengerAmt":
				String sJugendamt = ServerMessageUtil.getMessage(Amt.class.getSimpleName() + '_' + Amt.JUGENDAMT.name());
				String sSchulamt = ServerMessageUtil.getMessage(Amt.class.getSimpleName() + '_' + Amt.SCHULAMT.name());
				expression = cb.selectCase().when(joinEmpfaenger.get(Benutzer_.role).in(UserRole.getJugendamtRoles()), sJugendamt).otherwise(sSchulamt);
				break;
			case "mitteilungStatus":
				expression = root.get(Mitteilung_.mitteilungStatus);
				break;
			default:
				LOG.warn("Using default sort by SentDatum because there is no specific clause for predicate {}", tableFilterDTO.getSort().getPredicate());
				expression = root.get(Mitteilung_.sentDatum);
				break;
			}
			query.orderBy(tableFilterDTO.getSort().getReverse() ? cb.asc(expression) : cb.desc(expression));
		} else {
			// Default sort when nothing is choosen
			expression = root.get(Mitteilung_.sentDatum);
			query.orderBy(cb.desc(expression));
		}
	}

	private List<Mitteilung> findMitteilungen(@Nonnull List<String> gesuchIds) {
		if (!gesuchIds.isEmpty()) {
			final CriteriaBuilder cb = persistence.getCriteriaBuilder();
			final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
			Root<Mitteilung> root = query.from(Mitteilung.class);
			Predicate predicate = root.get(Mitteilung_.id).in(gesuchIds);
			query.where(predicate);
			//reduce to unique gesuche
			List<Mitteilung> listWithDuplicates = persistence.getCriteriaResults(query);
			LinkedHashSet<Mitteilung> set = new LinkedHashSet<>();
			//richtige reihenfolge beibehalten
			for (String gesuchId : gesuchIds) {
				listWithDuplicates.stream()
					.filter(gesuch -> gesuch.getId().equals(gesuchId))
					.findFirst()
					.ifPresent(set::add);
			}
			return new ArrayList<>(set);
		}
		return Collections.emptyList();
	}

	private void applyBetreuungsmitteilungToMutation(Gesuch gesuch, Betreuungsmitteilung mitteilung) {
		authorizer.checkWriteAuthorization(gesuch);
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		final Optional<Betreuung> betreuungToChangeOpt = gesuch.extractBetreuungsFromBetreuungNummer(mitteilung.getBetreuung().getKind().getKindNummer(),
			mitteilung.getBetreuung().getBetreuungNummer());
		if (betreuungToChangeOpt.isPresent()) {
			Betreuung existingBetreuung = betreuungToChangeOpt.get();
			existingBetreuung.getBetreuungspensumContainers().clear();//delete all current Betreuungspensen before we add the modified list
			for (final BetreuungsmitteilungPensum betPensumMitteilung : mitteilung.getBetreuungspensen()) {
				BetreuungspensumContainer betPenCont = new BetreuungspensumContainer();
				betPenCont.setBetreuung(existingBetreuung);
				Betreuungspensum betPensumJA = new Betreuungspensum(betPensumMitteilung);
				//gs container muss nicht mikopiert werden
				betPenCont.setBetreuungspensumJA(betPensumJA);
				existingBetreuung.getBetreuungspensumContainers().add(betPenCont);
			}
			// when we apply a Betreuungsmitteilung we have to change the status to BESTAETIGT
			existingBetreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
			betreuungService.saveBetreuung(existingBetreuung, false);
			mitteilung.setApplied(true);
			mitteilung.setMitteilungStatus(MitteilungStatus.ERLEDIGT);
			persistence.merge(mitteilung);
		}
	}

	@Nullable
	private MitteilungTeilnehmerTyp getMitteilungTeilnehmerTypForCurrentUser() {
		Benutzer loggedInBenutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("getMitteilungenForCurrentRolle", "No User is logged in"));
		//noinspection EnumSwitchStatementWhichMissesCases
		switch (loggedInBenutzer.getRole()) {
		case GESUCHSTELLER: {
			return MitteilungTeilnehmerTyp.GESUCHSTELLER;
		}
		case SACHBEARBEITER_INSTITUTION:
		case SACHBEARBEITER_TRAEGERSCHAFT: {
			return MitteilungTeilnehmerTyp.INSTITUTION;
		}
		case SUPER_ADMIN:
		case ADMIN:
		case SACHBEARBEITER_JA:
		case JURIST:
		case SCHULAMT:
		case ADMINISTRATOR_SCHULAMT:
		case REVISOR: {
			return MitteilungTeilnehmerTyp.JUGENDAMT;
		}
		default:
			return null;
		}
	}

	@SuppressWarnings("OverloadedVarargsMethod")
	private Mitteilung setMitteilungsStatusIfBerechtigt(@Nonnull String mitteilungsId, @Nonnull MitteilungStatus statusRequested,
		@Nonnull MitteilungStatus... statusRequired) {
		Optional<Mitteilung> mitteilungOptional = findMitteilung(mitteilungsId);
		Mitteilung mitteilung = mitteilungOptional.orElseThrow(() -> new EbeguRuntimeException("setMitteilungsStatusIfBerechtigt", "Mitteilung not found"));
		return setMitteilungsStatusIfBerechtigt(mitteilung, statusRequested, statusRequired);
	}

	@SuppressWarnings("OverloadedVarargsMethod")
	private Mitteilung setMitteilungsStatusIfBerechtigt(@Nonnull Mitteilung mitteilung, @Nonnull MitteilungStatus statusRequested,
		@Nonnull MitteilungStatus... statusRequired) {
		authorizer.checkReadAuthorizationMitteilung(mitteilung);
		if (!Arrays.asList(statusRequired).contains(mitteilung.getMitteilungStatus())) {
			throw new IllegalStateException("Mitteilung " + mitteilung.getId() + " ist im falschen Status: " + mitteilung.getMitteilungStatus() + " anstatt "
				+ Arrays.toString(statusRequired));
		}
		// Es muss sowohl der EmpfaengerTyp (bei Institution und GS) wie auch das Amt (bei JA und SCH) uebereinstimmen
		Benutzer loggedInBenutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("setMitteilungsStatusIfBerechtigt", "No User is logged in"));
		boolean sameEmpfaengerTyp = mitteilung.getEmpfaengerTyp() == getMitteilungTeilnehmerTypForCurrentUser();
		boolean sameAmt = mitteilung.getEmpfaengerAmt() == loggedInBenutzer.getAmt();
		if (sameEmpfaengerTyp && sameAmt) {
			mitteilung.setMitteilungStatus(statusRequested);
		}
		return persistence.merge(mitteilung);
	}
}


