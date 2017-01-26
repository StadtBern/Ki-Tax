package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.Mitteilung_;
import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.*;


/**
 * Service fuer Mitteilungen
 */
@Stateless
@Local(MitteilungService.class)
@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.GESUCHSTELLER, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
@SuppressWarnings(value = {"PMD.AvoidDuplicateLiterals"})
public class MitteilungServiceBean extends AbstractBaseService implements MitteilungService {


	@Inject
	private Persistence<Mitteilung> persistence;

	@Inject
	private BenutzerService benutzerService;


	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Mitteilung saveMitteilung(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);
		return persistence.merge(mitteilung);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Mitteilung setMitteilungGelesen(@Nonnull String mitteilungsId) {
		return setMitteilungsStatusIfBerechtigt(mitteilungsId, MitteilungStatus.GELESEN);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Mitteilung setMitteilungErledigt(@Nonnull String mitteilungsId) {
		return setMitteilungsStatusIfBerechtigt(mitteilungsId, MitteilungStatus.ERLEDIGT);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Optional<Mitteilung> findMitteilung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Mitteilung mitteilung = persistence.find(Mitteilung.class, key);
		return Optional.ofNullable(mitteilung);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

		Predicate predicateFall = cb.equal(root.get(Mitteilung_.fall), fall);
		predicates.add(predicateFall);

		Predicate predicateEntwurf = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateSender = cb.equal(root.get(Mitteilung_.senderTyp), mitteilungTeilnehmerTyp);
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		Predicate predicateSenderOrEmpfaenger = cb.or(predicateSender, predicateEmpfaenger);
		predicates.add(predicateSenderOrEmpfaenger);

		query.orderBy(cb.desc(root.get(Mitteilung_.sentDatum)));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	@SuppressWarnings("LocalVariableNamingConvention")
	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Mitteilung> getMitteilungenForPosteingang() {
		// Bedingungen: JA-Rolle, Ich bin Empfänger, oder Empfäger ist unbekannt aber Empfänger-Typ ist Jugendamt
		// Bedingungen: Ich bin (persönlicher) Empfänger, oder die Nachricht hat keinen persönlichen Empfänger, entspricht aber meinem MitteilungTeilnehmerTyp
		// Damit ist es später erweiterbar für andere Rollen, nicht nur Jugendamt

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Persönlicher Empfänger
		Benutzer loggedInBenutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("getMitteilungenForCurrentRolle", "No User is logged in"));
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaenger), loggedInBenutzer);

		Predicate predicateEntwurf = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		// Kein Persönlicher Empfänger, aber richtiger Empfangs-Typ
		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateEmpfaengerNull = cb.isNull(root.get(Mitteilung_.empfaenger));
		Predicate predicateEmpfaengerTyp = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		Predicate predicateEmpfaengerNullAberRichtigerTyp = cb.and(predicateEmpfaengerNull, predicateEmpfaengerTyp);

		// Entweder das eine oder das andere...
		predicates.add(cb.or(predicateEmpfaenger, predicateEmpfaengerNullAberRichtigerTyp));

		// Aber auf jeden Fall unerledigt
		Predicate predicateNichtErledigt = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ERLEDIGT);
		predicates.add(predicateNichtErledigt);

		query.orderBy(cb.desc(root.get(Mitteilung_.sentDatum)));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nullable
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Mitteilung getEntwurfForCurrentRolle(Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

		Predicate predicateFall = cb.equal(root.get(Mitteilung_.fall), fall);
		predicates.add(predicateFall);

		Predicate predicateEntwurf = cb.equal(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateSender = cb.equal(root.get(Mitteilung_.senderTyp), mitteilungTeilnehmerTyp);
		predicates.add(predicateSender);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaSingleResult(query);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public void removeMitteilung(Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);
		persistence.remove(mitteilung);
	}

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
			case SACHBEARBEITER_JA: {
				return MitteilungTeilnehmerTyp.JUGENDAMT;
			}
			default:
				return null;
		}
	}

	private Mitteilung setMitteilungsStatusIfBerechtigt(@Nonnull String mitteilungsId, MitteilungStatus mitteilungStatus) {
		Optional<Mitteilung> mitteilungOptional = findMitteilung(mitteilungsId);
		Mitteilung mitteilung = mitteilungOptional.orElseThrow(() -> new EbeguRuntimeException("setMitteilungsStatusIfBerechtigt", "Mitteilung not found"));
		if (mitteilung.getEmpfaengerTyp().equals(getMitteilungTeilnehmerTypForCurrentUser())) {
			mitteilung.setMitteilungStatus(mitteilungStatus);
		}
		return mitteilung;
	}
}


