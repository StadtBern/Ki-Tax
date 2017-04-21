package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import javax.validation.Valid;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer Betreuung
 */
@Stateless
@Local(BetreuungService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT})
public class BetreuungServiceBean extends AbstractBaseService implements BetreuungService {

	@Inject
	private Persistence<Betreuung> persistence;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private Authorizer authorizer;
	@Inject
	private MailService mailService;

	private final Logger LOG = LoggerFactory.getLogger(BetreuungServiceBean.class.getSimpleName());


	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung, @Nonnull Boolean isAbwesenheit) {
		Objects.requireNonNull(betreuung);
		final Betreuung mergedBetreuung = persistence.merge(betreuung);

		//jetzt noch wizard step updaten
		if (isAbwesenheit) {
			wizardStepService.updateSteps(mergedBetreuung.getKind().getGesuch().getId(), null, null, WizardStepName.ABWESENHEIT);
		} else {
			wizardStepService.updateSteps(mergedBetreuung.getKind().getGesuch().getId(), null, null, WizardStepName.BETREUUNG);
		}

		return mergedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION})
	public Betreuung betreuungPlatzAbweisen(@Valid @Nonnull Betreuung betreuung) {
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		try {
			// Bei Ablehnung einer Betreuung muss eine E-Mail geschickt werden
			mailService.sendInfoBetreuungAbgelehnt(persistedBetreuung);
		} catch (MailException e) {
			LOG.error("Mail InfoBetreuungAbgelehnt konnte nicht verschickt werden fuer Betreuung " + betreuung.getId(), e);
		}
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION})
	public Betreuung betreuungPlatzBestaetigen(@Valid @Nonnull Betreuung betreuung) {
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		try {
			Gesuch gesuch = betreuung.extractGesuch();
			if (gesuch.areAllBetreuungenBestaetigt()) {
				// Sobald alle Betreuungen bestaetigt sind, eine Mail schreiben
				mailService.sendInfoBetreuungenBestaetigt(gesuch);
			}
		} catch (MailException e) {
			LOG.error("Mail InfoBetreuungenBestaetigt konnte nicht verschickt werden fuer Betreuung " + betreuung.getId(), e);
		}
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Optional<Betreuung> findBetreuung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Betreuung betr = persistence.find(Betreuung.class, key);
		if (betr != null) {
			authorizer.checkReadAuthorization(betr);
		}
		return Optional.ofNullable(betr);
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public Optional<Betreuung> findBetreuungWithBetreuungsPensen(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		root.fetch(Betreuung_.betreuungspensumContainers, JoinType.LEFT);
		root.fetch(Betreuung_.abwesenheitContainers, JoinType.LEFT);
		query.select(root);
		Predicate idPred = cb.equal(root.get(Betreuung_.id), key);
		query.where(idPred);
		Betreuung result = persistence.getCriteriaSingleResult(query);
		if (result != null) {
			authorizer.checkReadAuthorization(result);
		}
		return Optional.ofNullable(result);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public void removeBetreuung(@Nonnull String betreuungId) {
		Objects.requireNonNull(betreuungId);
		Optional<Betreuung> betrToRemoveOpt = findBetreuung(betreuungId);
		Betreuung betreuungToRemove = betrToRemoveOpt.orElseThrow(() -> new EbeguEntityNotFoundException("removeBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungId));
		final String gesuchId = betreuungToRemove.getKind().getGesuch().getId();
		authorizer.checkWriteAuthorization(betreuungToRemove);
		persistence.remove(betreuungToRemove);
		wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.BETREUUNG); //auch bei entfernen wizard updaten
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public void removeBetreuung(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung);
		authorizer.checkWriteAuthorization(betreuung);
		persistence.remove(betreuung);
	}

	@Override
	@Nonnull
	@RolesAllowed(value ={ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Betreuung> getPendenzenForInstitutionsOrTraegerschaftUser() {
		Collection<Institution> instForCurrBenutzer = institutionService.getAllowedInstitutionenForCurrentBenutzer();
		if (!instForCurrBenutzer.isEmpty()) {
			return getPendenzenForInstitution((Institution[]) instForCurrBenutzer.toArray(new Institution[instForCurrBenutzer.size()]));
		}
		return Collections.emptyList();
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public List<Betreuung> findAllBetreuungenFromGesuch(@Nonnull String gesuchId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		// Betreuung from Gesuch
		Predicate predicateInstitution = root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchId);

		query.where(predicateInstitution);
		authorizer.checkReadAuthorizationGesuchId(gesuchId);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER})
	public List<Betreuung> findAllBetreuungenWithVerfuegungFromFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");


		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);


		Root<Betreuung> root = query.from(Betreuung.class);
		List<Expression<Boolean>> predicatesToUse = new ArrayList<>();

		Predicate fallPredicate = cb.equal(root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.fall), fall);
		predicatesToUse.add(fallPredicate);

		Predicate predicateBetreuung = root.get(Betreuung_.betreuungsstatus).in(Betreuungsstatus.hasVerfuegung);
		predicatesToUse.add(predicateBetreuung);

		Predicate verfuegungPredicate = cb.isNotNull(root.get(Betreuung_.verfuegung));
		predicatesToUse.add(verfuegungPredicate);

		Collection<Institution> institutionen = institutionService.getAllowedInstitutionenForCurrentBenutzer();
		Predicate predicateInstitution = root.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(Arrays.asList(institutionen));
		predicatesToUse.add(predicateInstitution);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicatesToUse)).orderBy(cb.desc(root.get(Betreuung_.verfuegung).get(Verfuegung_.timestampErstellt)));

		List<Betreuung> criteriaResults = persistence.getCriteriaResults(query);

		criteriaResults.forEach(betreuung -> authorizer.checkReadAuthorization(betreuung));

		return criteriaResults;
	}


	/**
	 * Liest alle Betreuungen die zu einer der mitgegebenen Institution gehoeren und die im Status WARTEN sind
	 *
	 * @param institutionen
	 * @return
	 */
	@Nonnull
	private Collection<Betreuung> getPendenzenForInstitution(@Nonnull Institution... institutionen) {
		if (institutionen != null) {
			Objects.requireNonNull(institutionen, "institutionen muss gesetzt sein");
			final CriteriaBuilder cb = persistence.getCriteriaBuilder();
			final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
			Root<Betreuung> root = query.from(Betreuung.class);
			// Status muss WARTEN sein
			Predicate predicateStatus = cb.equal(root.get(Betreuung_.betreuungsstatus), Betreuungsstatus.WARTEN);
			// Institution
			Predicate predicateInstitution = root.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(Arrays.asList(institutionen));
			// Gesuchsperiode darf nicht geschlossen sein
			Predicate predicateGesuchsperiode = root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.gesuchsperiode).get(Gesuchsperiode_.status).in(GesuchsperiodeStatus.AKTIV, GesuchsperiodeStatus.INAKTIV);

			query.where(predicateStatus, predicateInstitution, predicateGesuchsperiode);
			List<Betreuung> betreuungen = persistence.getCriteriaResults(query);
			authorizer.checkReadAuthorizationForAllBetreuungen(betreuungen);
			return betreuungen;
		}
		LOG.warn("Tried to read Pendenzen for institution but no institutionen specified");
		return Collections.emptyList();
	}

	@Override
	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Betreuung schliessenOhneVerfuegen(@Nonnull Betreuung betreuung) {
		return closeBetreuung(betreuung, Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG);
	}

	@Nonnull
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	private Betreuung closeBetreuung(@Nonnull Betreuung betreuung, @Nonnull Betreuungsstatus status) {
		betreuung.setBetreuungsstatus(status);
		final Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		authorizer.checkWriteAuthorization(persistedBetreuung);
		wizardStepService.updateSteps(persistedBetreuung.extractGesuch().getId(), null, null, WizardStepName.VERFUEGEN);
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, REVISOR})
	public List<Betreuung> getAllBetreuungenWithMissingStatistics() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);

		Root<Betreuung> root = query.from(Betreuung.class);
		Join<Betreuung, KindContainer> joinKindContainer = root.join(Betreuung_.kind, JoinType.LEFT);
		Join<KindContainer, Gesuch> joinGesuch = joinKindContainer.join(KindContainer_.gesuch, JoinType.LEFT);

		Predicate predicateMutation = cb.equal(joinGesuch.get(Gesuch_.typ), AntragTyp.MUTATION);
		Predicate predicateFlag = cb.isNull(root.get(Betreuung_.betreuungMutiert));
		Predicate predicateStatus = joinGesuch.get(Gesuch_.status).in(AntragStatus.getAllVerfuegtStates());

		query.where(predicateMutation, predicateFlag, predicateStatus);
		query.orderBy(cb.desc(joinGesuch.get(Gesuch_.laufnummer)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, REVISOR})
	public List<Abwesenheit> getAllAbwesenheitenWithMissingStatistics() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Abwesenheit> query = cb.createQuery(Abwesenheit.class);

		Root<Abwesenheit> root = query.from(Abwesenheit.class);
		Join<Abwesenheit, AbwesenheitContainer> joinAbwesenheitContainer = root.join(Abwesenheit_.abwesenheitContainer, JoinType.LEFT);
		Join<AbwesenheitContainer, Betreuung> joinBetreuung = joinAbwesenheitContainer.join(AbwesenheitContainer_.betreuung, JoinType.LEFT);
		Join<Betreuung, KindContainer> joinKindContainer = joinBetreuung.join(Betreuung_.kind, JoinType.LEFT);
		Join<KindContainer, Gesuch> joinGesuch = joinKindContainer.join(KindContainer_.gesuch, JoinType.LEFT);

		Predicate predicateMutation = cb.equal(joinGesuch.get(Gesuch_.typ), AntragTyp.MUTATION);
		Predicate predicateFlag = cb.isNull(joinBetreuung.get(Betreuung_.abwesenheitMutiert));
		Predicate predicateStatus = joinGesuch.get(Gesuch_.status).in(AntragStatus.getAllVerfuegtStates());

		query.where(predicateMutation, predicateFlag, predicateStatus);
		query.orderBy(cb.desc(joinGesuch.get(Gesuch_.laufnummer)));
		return persistence.getCriteriaResults(query);
	}
}
