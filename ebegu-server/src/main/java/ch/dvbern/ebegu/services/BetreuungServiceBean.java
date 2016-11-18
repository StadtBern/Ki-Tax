package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.rules.BetreuungsgutscheinEvaluator;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import javax.validation.Valid;
import java.util.*;

/**
 * Service fuer Betreuung
 */
@Stateless
@Local(BetreuungService.class)
public class BetreuungServiceBean extends AbstractBaseService implements BetreuungService {

	@Inject
	private Persistence<Betreuung> persistence;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private InstitutionService institutionService;

	private final Logger LOG = LoggerFactory.getLogger(BetreuungsgutscheinEvaluator.class.getSimpleName());


	@Override
	@Nonnull
	public Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung, Boolean abwesenheit) {
		Objects.requireNonNull(betreuung);

		final Betreuung mergedBetreuung = persistence.merge(betreuung);

		//jetzt noch wizard step updaten
		if (abwesenheit) {
			wizardStepService.updateSteps(mergedBetreuung.getKind().getGesuch().getId(), null, null, WizardStepName.ABWESENHEIT);
		}
		else {
			wizardStepService.updateSteps(mergedBetreuung.getKind().getGesuch().getId(), null, null, WizardStepName.BETREUUNG);
		}

		return mergedBetreuung;
	}

	@Override
	@Nonnull
	public Optional<Betreuung> findBetreuung(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Betreuung a = persistence.find(Betreuung.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Betreuung findBetreuungWithBetreuungsPensen(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		root.fetch(Betreuung_.betreuungspensumContainers, JoinType.LEFT);
		root.fetch(Betreuung_.abwesenheitContainers, JoinType.LEFT);
		query.select(root);
		Predicate idPred = cb.equal(root.get(Betreuung_.id), key);
		query.where(idPred);
		return persistence.getCriteriaSingleResult(query);
	}

	@Override
	public void removeBetreuung(@Nonnull String betreuungId) {
		Objects.requireNonNull(betreuungId);
		Optional<Betreuung> betreuungToRemove = findBetreuung(betreuungId);
		betreuungToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeBetreuung", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungId));
		final String gesuchId = betreuungToRemove.get().getKind().getGesuch().getId();
		persistence.remove(betreuungToRemove.get());
		wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.BETREUUNG); //auch bei entfernen wizard updaten
	}

	@Override
	public void removeBetreuung(@Nonnull Betreuung betreuung) {
		persistence.remove(betreuung);
	}

	@Override
	@Nonnull
	public Collection<Betreuung> getPendenzenForInstitutionsOrTraegerschaftUser() {
		Collection<Institution> instForCurrBenutzer = institutionService.getInstitutionenForCurrentBenutzer();
		if (!instForCurrBenutzer.isEmpty()) {
			return getPendenzenForInstitution((Institution[]) instForCurrBenutzer.toArray(new Institution[instForCurrBenutzer.size()]));
		}
		return Collections.emptyList();
	}

	@Override
	@Nonnull
	public List<Betreuung> findAllBetreuungenFromGesuch(@Nonnull String gesuchId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		// Betreuung from Gesuch
		Predicate predicateInstitution = root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchId);

		query.where(predicateInstitution);
		return persistence.getCriteriaResults(query);
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

			query.where(predicateStatus, predicateInstitution);
			return persistence.getCriteriaResults(query);
		}
		LOG.warn("Tried to read Pendenzen for institution but no institutionen specified");
		return Collections.emptyList();
	}

	@Override
	@Nonnull
	public Betreuung schliessenOhneVerfuegen(@Nonnull Betreuung betreuung) {
		return closeBetreuung(betreuung, Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG);
	}

	@Override
	@Nonnull
	public Betreuung nichtEintreten(@Nonnull Betreuung betreuung) {
		return closeBetreuung(betreuung, Betreuungsstatus.NICHT_EINGETRETEN);
	}

	@Nonnull
	private Betreuung closeBetreuung(@Nonnull Betreuung betreuung, @Nonnull Betreuungsstatus status) {
		betreuung.setBetreuungsstatus(status);
		final Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		wizardStepService.updateSteps(persistedBetreuung.extractGesuch().getId(), null, null, WizardStepName.VERFUEGEN);
		return persistedBetreuung;
	}
}
