package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer familiensituation
 */
@Stateless
@Local(FamiliensituationService.class)
public class FamiliensituationServiceBean extends AbstractBaseService implements FamiliensituationService {

	@Inject
	private Persistence<Familiensituation> persistence;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;
	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;


	@Nonnull
	@Override
	public Familiensituation saveFamiliensituation(Gesuch gesuch, Familiensituation oldFamiliensituation, @Nonnull Familiensituation newFamiliensituation) {
		Objects.requireNonNull(newFamiliensituation);
		Objects.requireNonNull(gesuch);

		// Falls noch nicht vorhanden, werden die GemeinsameSteuererklaerung fuer FS und EV auf false gesetzt
		if (gesuch.isMutation() && EbeguUtil.fromOneGSToTwoGS(oldFamiliensituation, newFamiliensituation)) {

			if (newFamiliensituation.getGemeinsameSteuererklaerung() == null) {
				newFamiliensituation.setGemeinsameSteuererklaerung(false);
			}
			if (gesuch.getEinkommensverschlechterungInfo() != null) { //eigentlich darf es bei einer Mutation nie null sein. Trotzdem zur Sicherheit...
				if (gesuch.getEinkommensverschlechterungInfo().getGemeinsameSteuererklaerung_BjP1() == null) {
					gesuch.getEinkommensverschlechterungInfo().setGemeinsameSteuererklaerung_BjP1(false);
				}
				if (gesuch.getEinkommensverschlechterungInfo().getGemeinsameSteuererklaerung_BjP2() == null) {
					gesuch.getEinkommensverschlechterungInfo().setGemeinsameSteuererklaerung_BjP2(false);
				}
				einkommensverschlechterungInfoService.updateEinkommensverschlechterungInfo(gesuch.getEinkommensverschlechterungInfo());
			}
		}

		final Familiensituation mergedFamiliensituation = persistence.merge(newFamiliensituation);

		gesuch.setFamiliensituation(mergedFamiliensituation);

		//Alle Daten des GS2 loeschen wenn man von 2GS auf 1GS wechselt und GS2 bereits erstellt wurde
		if (gesuch.getGesuchsteller2() != null && isNeededToRemoveGesuchsteller2(gesuch, mergedFamiliensituation, oldFamiliensituation)) {
			gesuchstellerService.removeGesuchsteller(gesuch.getGesuchsteller2());
			gesuch.setGesuchsteller2(null);
		}

		wizardStepService.updateSteps(gesuch.getId(), oldFamiliensituation,
			mergedFamiliensituation, WizardStepName.FAMILIENSITUATION);

		return mergedFamiliensituation;
	}

	@Nonnull
	@Override
	public Optional<Familiensituation> findFamiliensituation(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Familiensituation a =  persistence.find(Familiensituation.class, key);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<Familiensituation> getAllFamiliensituatione() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Familiensituation.class));
	}

	@Override
	public void removeFamiliensituation(@Nonnull Familiensituation familiensituation) {
		Validate.notNull(familiensituation);
		Optional<Familiensituation> familiensituationToRemove = findFamiliensituation(familiensituation.getId());
		familiensituationToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeFall", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, familiensituation));
		persistence.remove(familiensituationToRemove.get());
	}

	/**
	 * Wenn die neue Familiensituation nur 1GS hat und der zweite GS schon existiert, wird dieser
	 * und seine Daten endgueltig geloescht. Dies gilt aber nur fuer ERSTGESUCH. Bei Mutationen wird
	 * der 2GS nie geloescht
	 * @return
	 */
	private boolean isNeededToRemoveGesuchsteller2(Gesuch gesuch, Familiensituation newFamiliensituation, Familiensituation oldFamiliensituation) {
		return (!gesuch.isMutation() && gesuch.getGesuchsteller2() != null && !newFamiliensituation.hasSecondGesuchsteller())
			|| (gesuch.isMutation() && gesuch.getGesuchsteller2() != null && !oldFamiliensituation.hasSecondGesuchsteller());
	}

}
