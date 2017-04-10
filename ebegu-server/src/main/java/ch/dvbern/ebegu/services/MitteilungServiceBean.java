package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private MailService mailService;

	@Inject
	private ApplicationPropertyService applicationPropertyService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	private final Logger LOG = LoggerFactory.getLogger(MitteilungServiceBean.class.getSimpleName());


	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Mitteilung sendMitteilung(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);
		if (!MitteilungStatus.ENTWURF.equals(mitteilung.getMitteilungStatus())) {
			throw new IllegalArgumentException("Mitteilung ist nicht im Status ENTWURF und kann nicht gesendet werden");
		}
		mitteilung.setMitteilungStatus(MitteilungStatus.NEU);
		mitteilung.setSentDatum(LocalDateTime.now());
		ensureEmpfaengerIsSet(mitteilung);

		// Falls die Mitteilung an einen Gesuchsteller geht, muss dieser benachrichtigt werden. Es muss zuerst geprueft werden, dass
		// die Mitteilung valid ist, dafuer brauchen wir den Validator

		try {
			Validator validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
			final Set<ConstraintViolation<Mitteilung>> validationErrors = validator.validate(mitteilung);
			if (!validationErrors.isEmpty()) {
				throw new ConstraintViolationException(validationErrors);
			}

			if (MitteilungTeilnehmerTyp.GESUCHSTELLER.equals(mitteilung.getEmpfaengerTyp()) && mitteilung.getEmpfaenger() != null) {
				mailService.sendInfoMitteilungErhalten(mitteilung);
			}

		} catch (MailException e) {
			LOG.error("Mail InfoMitteilungErhalten konnte nicht verschickt werden fuer Mitteilung " + mitteilung.getId(), e);
			throw new EbeguRuntimeException("sendMitteilung", ErrorCodeEnum.ERROR_MAIL, e);
		}

		return persistence.merge(mitteilung);
	}

	/**
	 * Falls der dazugehörige Fall noch keinen Verantwortlichen hat, so soll die Mitteilung beim vom Admin
	 * definierten Default-Verantwortlichen angezeigt werden
	 *
	 * @param mitteilung
	 */
	private void ensureEmpfaengerIsSet(@Nonnull Mitteilung mitteilung) {

		if (MitteilungTeilnehmerTyp.JUGENDAMT.equals(mitteilung.getEmpfaengerTyp())) {
			mitteilung.setEmpfaenger(mitteilung.getFall().getVerantwortlicher());
			if (mitteilung.getEmpfaenger() == null) {
				String propertyDefaultVerantwortlicher = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEFAULT_VERANTWORTLICHER);
				if (StringUtils.isNotEmpty(propertyDefaultVerantwortlicher)) {
					Optional<Benutzer> benutzer = benutzerService.findBenutzer(propertyDefaultVerantwortlicher);
					if (benutzer.isPresent()) {
						mitteilung.setEmpfaenger(benutzer.get());
					} else {
						LOG.warn("Es ist kein gueltiger DEFAULT Verantwortlicher fuer Mitteilungen gesetzt. Bitte Propertys pruefen");
					}
				}
			}
		}
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Mitteilung saveEntwurf(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);
		if (!MitteilungStatus.ENTWURF.equals(mitteilung.getMitteilungStatus())) {
			throw new IllegalArgumentException("Mitteilung ist nicht im Status ENTWURF und kann nicht als Entwurf gespeichert werden");
		}
		return persistence.merge(mitteilung);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Mitteilung setMitteilungGelesen(@Nonnull String mitteilungsId) {
		return setMitteilungsStatusIfBerechtigt(mitteilungsId, MitteilungStatus.GELESEN, MitteilungStatus.NEU, MitteilungStatus.ERLEDIGT);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Mitteilung setMitteilungErledigt(@Nonnull String mitteilungsId) {
		return setMitteilungsStatusIfBerechtigt(mitteilungsId, MitteilungStatus.ERLEDIGT, MitteilungStatus.GELESEN);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Optional<Mitteilung> findMitteilung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Mitteilung mitteilung = persistence.find(Mitteilung.class, key);
		return Optional.ofNullable(mitteilung);
	}

	@Override
	@Nonnull
	public Optional<Betreuungsmitteilung> findBetreuungsmitteilung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Betreuungsmitteilung mitteilung = persistence.find(Betreuungsmitteilung.class, key);
		return Optional.ofNullable(mitteilung);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");
		return getMitteilungenForCurrentRolle(Mitteilung_.fall, fall);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Mitteilung> getMitteilungenForCurrentRolle(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung muss gesetzt sein");
		return getMitteilungenForCurrentRolle(Mitteilung_.betreuung, betreuung);
	}

	private <T> Collection<Mitteilung> getMitteilungenForCurrentRolle(SingularAttribute<Mitteilung, T> attribute, @Nonnull T linkedEntity) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

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
		return persistence.getCriteriaResults(query);
	}

	@SuppressWarnings("LocalVariableNamingConvention")
	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Mitteilung> getMitteilungenForPosteingang() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();

		CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);

		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Keine Entwuerfe fuer Posteingang
		Predicate predicateEntwurf = cb.notEqual(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.ENTWURF);
		predicates.add(predicateEntwurf);

		// Richtiger Empfangs-Typ. Persoenlicher Empfaenger wird nicht beachtet sondern auf Client mit Filter geloest
		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateEmpfaengerTyp = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		predicates.add(predicateEmpfaengerTyp);

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
	public Mitteilung getEntwurfForCurrentRolle(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");
		return getEntwurfForCurrentRolle(Mitteilung_.fall, fall);
	}

	@Override
	@Nullable
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Mitteilung getEntwurfForCurrentRolle(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, "betreuung muss gesetzt sein");
		return getEntwurfForCurrentRolle(Mitteilung_.betreuung, betreuung);
	}

	private <T> Mitteilung getEntwurfForCurrentRolle(SingularAttribute<Mitteilung, T> attribute, @Nonnull T linkedEntity) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

		Predicate predicateLinkedObject = cb.equal(root.get(attribute), linkedEntity);
		predicates.add(predicateLinkedObject);

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
	public void removeMitteilung(@Nonnull Mitteilung mitteilung) {
		Objects.requireNonNull(mitteilung);
		persistence.remove(mitteilung);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public void removeAllMitteilungenForFall(@Nonnull Fall fall) {
		Collection<Mitteilung> mitteilungen = criteriaQueryHelper.getEntitiesByAttribute(Mitteilung.class, fall, Mitteilung_.fall);
		for (Mitteilung poscht : mitteilungen) {
			persistence.remove(Mitteilung.class, poscht.getId());
		}
	}

	@Override
	public Collection<Mitteilung> setAllNewMitteilungenOfFallGelesen(Fall fall) {
		Collection<Mitteilung> mitteilungen = getMitteilungenForCurrentRolle(fall);
		for (Mitteilung mitteilung : mitteilungen) {
			if (MitteilungStatus.NEU.equals(mitteilung.getMitteilungStatus())) {
				setMitteilungsStatusIfBerechtigt(mitteilung, MitteilungStatus.GELESEN, MitteilungStatus.NEU);
			}
		}
		return mitteilungen;
	}

	@Override
	public Collection<Mitteilung> getNewMitteilungenForCurrentRolleAndFall(Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mitteilung> query = cb.createQuery(Mitteilung.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

		Predicate predicateFall = cb.equal(root.get(Mitteilung_.fall), fall);
		predicates.add(predicateFall);

		Predicate predicateNew = cb.equal(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.NEU);
		predicates.add(predicateNew);

		MitteilungTeilnehmerTyp mitteilungTeilnehmerTyp = getMitteilungTeilnehmerTypForCurrentUser();
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaengerTyp), mitteilungTeilnehmerTyp);
		predicates.add(predicateEmpfaenger);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nonnull
	public Long getAmountNewMitteilungenForCurrentBenutzer() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Mitteilung> root = query.from(Mitteilung.class);
		List<Expression<Boolean>> predicates = new ArrayList<>();

		Predicate predicateNew = cb.equal(root.get(Mitteilung_.mitteilungStatus), MitteilungStatus.NEU);
		predicates.add(predicateNew);

		Benutzer loggedInBenutzer = benutzerService.getCurrentBenutzer().orElseThrow(() -> new EbeguRuntimeException("getAmountNewMitteilungenForCurrentBenutzer", "No User is logged in"));
		Predicate predicateEmpfaenger = cb.equal(root.get(Mitteilung_.empfaenger), loggedInBenutzer);
		predicates.add(predicateEmpfaenger);

		query.select(cb.countDistinct(root));
		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaSingleResult(query);
	}

	@Override
	@RolesAllowed({SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Betreuungsmitteilung sendBetreuungsmitteilung(Betreuungsmitteilung betreuungsmitteilung) {
		Objects.requireNonNull(betreuungsmitteilung);
		if (!MitteilungTeilnehmerTyp.INSTITUTION.equals(betreuungsmitteilung.getSenderTyp())) {
			throw new IllegalArgumentException("Eine Betreuungsmitteilung darf nur bei einer Institution geschickt werden");
		}
		if (!MitteilungTeilnehmerTyp.JUGENDAMT.equals(betreuungsmitteilung.getEmpfaengerTyp())) {
			throw new IllegalArgumentException("Eine Betreuungsmitteilung darf nur an das Jugendamt geschickt werden");
		}
		betreuungsmitteilung.setMitteilungStatus(MitteilungStatus.NEU); // vorsichtshalber
		betreuungsmitteilung.setSentDatum(LocalDateTime.now());
		ensureEmpfaengerIsSet(betreuungsmitteilung);

		return persistence.persist(betreuungsmitteilung); // A Betreuungsmitteilung is created and sent, therefore persist and not merge
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Gesuch applyBetreuungsmitteilung(@NotNull Betreuungsmitteilung mitteilung) {
		final Gesuch gesuch = mitteilung.getBetreuung().extractGesuch();
		// neustes Gesuch lesen
		final Optional<Gesuch> neustesGesuchOpt;
		try {
			neustesGesuchOpt = gesuchService.getNeustesGesuchFuerGesuch(gesuch);
		} catch (EJBTransactionRolledbackException exception) {
			//Wenn der Sachbearbeiter den neusten Antrag nicht lesen darf ist es ein noch nicht freigegebener ONLINE Antrag
			if(exception.getCause().getClass().equals(EJBAccessException.class)) {
				throw new EbeguRuntimeException("applyBetreuungsmitteilung", ErrorCodeEnum.ERROR_EXISTING_ONLINE_MUTATION, exception);
			}
			throw exception;
		}

		if (neustesGesuchOpt.isPresent()) {
			final Gesuch neustesGesuch = neustesGesuchOpt.get();
			if (!AntragStatus.VERFUEGT.equals(neustesGesuch.getStatus()) && neustesGesuch.isMutation()) {
				//betreuungsaenderungen der bestehenden, offenen Mutation hinzufuegen (wenn wir hier sind muss es sich um ein PAPIER) Antrag handeln
				applyBetreuungsmitteilungToMutation(neustesGesuch, mitteilung);
				return neustesGesuch;
			}
			else if (AntragStatus.VERFUEGT.equals(neustesGesuch.getStatus())) {
				// create Mutation if there is currently no Mutation
				final Optional<Gesuch> mutationOpt = this.gesuchService.antragMutieren(gesuch.getId(), LocalDate.now());
				if (mutationOpt.isPresent()) {
					Gesuch persistedMutation = gesuchService.createGesuch(mutationOpt.get());
					applyBetreuungsmitteilungToMutation(persistedMutation, mitteilung);
					return persistedMutation;
				}
			}
			else {
				throw new EbeguRuntimeException("applyBetreuungsmitteilung", "Fehler beim Erstellen einer Mutation", "Das Erstgesuch ist noch nicht Freigegeben");
			}
		}
		return gesuch;
	}

	@Override
	public Optional<Betreuungsmitteilung> findNewestBetreuungsmitteilung(String betreuungId) {
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
		return Optional.of(result.get(0));
	}

	private Betreuungsmitteilung applyBetreuungsmitteilungToMutation(Gesuch gesuch, Betreuungsmitteilung mitteilung) {
		final Optional<Betreuung> betreuungToChangeOpt = gesuch.extractBetreuungsFromBetreuungNummer(mitteilung.getBetreuung().getBetreuungNummer());
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
				betreuungService.saveBetreuung(existingBetreuung, false);
			}
			mitteilung.setApplied(true);
			mitteilung.setMitteilungStatus(MitteilungStatus.ERLEDIGT);
			return persistence.merge(mitteilung);
		}
		return mitteilung;
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

	private Mitteilung setMitteilungsStatusIfBerechtigt(@Nonnull String mitteilungsId, MitteilungStatus statusRequested, @Nonnull MitteilungStatus... statusRequired) {
		Optional<Mitteilung> mitteilungOptional = findMitteilung(mitteilungsId);
		Mitteilung mitteilung = mitteilungOptional.orElseThrow(() -> new EbeguRuntimeException("setMitteilungsStatusIfBerechtigt", "Mitteilung not found"));
		return setMitteilungsStatusIfBerechtigt(mitteilung, statusRequested, statusRequired);
	}

	private Mitteilung setMitteilungsStatusIfBerechtigt(@Nonnull Mitteilung mitteilung, MitteilungStatus statusRequested, @Nonnull MitteilungStatus... statusRequired) {
		if (!Arrays.asList(statusRequired).contains(mitteilung.getMitteilungStatus())) {
			throw new IllegalStateException("Mitteilung " + mitteilung.getId() + " ist im falschen Status: " + mitteilung.getMitteilungStatus() + " anstatt " + Arrays.toString(statusRequired));
		}
		if (mitteilung.getEmpfaengerTyp().equals(getMitteilungTeilnehmerTypForCurrentUser())) {
			mitteilung.setMitteilungStatus(statusRequested);
		}
		return persistence.merge(mitteilung);
	}
}


