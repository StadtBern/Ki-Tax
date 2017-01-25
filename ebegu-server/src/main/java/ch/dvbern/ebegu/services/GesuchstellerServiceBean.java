package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
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
 * Service fuer Gesuchsteller
 */
@Stateless
@Local(GesuchstellerService.class)
public class GesuchstellerServiceBean extends AbstractBaseService implements GesuchstellerService {

	@Inject
	private Persistence<GesuchstellerContainer> persistence;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public GesuchstellerContainer saveGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller, final Gesuch gesuch, Integer gsNumber,
										   boolean umzug) {
		Objects.requireNonNull(gesuchsteller);
		Objects.requireNonNull(gesuch);
		Objects.requireNonNull(gsNumber);

		if (gesuch.isMutation() && gsNumber == 2 && gesuchsteller.getFinanzielleSituationContainer() == null) {
			// be Mutationen fuer den GS2 muss eine leere Finanzielle Situation hinzugefuegt werden, wenn sie noch nicht existiert
			final FinanzielleSituationContainer finanzielleSituationContainer = new FinanzielleSituationContainer();
			final FinanzielleSituation finanzielleSituationJA = new FinanzielleSituation();
			finanzielleSituationJA.setSteuerveranlagungErhalten(false); // by default
			finanzielleSituationJA.setSteuererklaerungAusgefuellt(false); // by default
			finanzielleSituationContainer.setFinanzielleSituationJA(finanzielleSituationJA); // alle Werte by default auf null -> nichts eingetragen
			finanzielleSituationContainer.setJahr(gesuch.getGesuchsteller1().getFinanzielleSituationContainer().getJahr()); // copy it from GS1
			finanzielleSituationContainer.setGesuchsteller(gesuchsteller);
			gesuchsteller.setFinanzielleSituationContainer(finanzielleSituationContainer);
		}

		if (gesuch.isMutation() && gesuch.extractEinkommensverschlechterungInfo() == null
			&& gsNumber == 2 && gesuchsteller.getEinkommensverschlechterungContainer() == null) {

			EinkommensverschlechterungContainer evContainer = new EinkommensverschlechterungContainer();
			evContainer.setGesuchsteller(gesuchsteller);
			gesuchsteller.setEinkommensverschlechterungContainer(evContainer);
			if (gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer() != null) {
				if (gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus1() != null) {
					final Einkommensverschlechterung ekvJABasisJahrPlus1 = new Einkommensverschlechterung();
					ekvJABasisJahrPlus1.setSteuerveranlagungErhalten(false); // by default
					ekvJABasisJahrPlus1.setSteuererklaerungAusgefuellt(false); // by default
					gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus1(ekvJABasisJahrPlus1);
				}
				if (gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer().getEkvJABasisJahrPlus2() != null) {
					final Einkommensverschlechterung ekvJABasisJahrPlus2 = new Einkommensverschlechterung();
					ekvJABasisJahrPlus2.setSteuerveranlagungErhalten(false); // by default
					ekvJABasisJahrPlus2.setSteuererklaerungAusgefuellt(false); // by default
					gesuchsteller.getEinkommensverschlechterungContainer().setEkvJABasisJahrPlus2(ekvJABasisJahrPlus2);
				}
			}
		}

		final GesuchstellerContainer mergedGesuchsteller = persistence.merge(gesuchsteller);
		updateWizStepsForGesuchstellerView(gesuch, gsNumber, umzug);
		return mergedGesuchsteller;
	}

	private void updateWizStepsForGesuchstellerView(Gesuch gesuch, Integer gsNumber, boolean umzug) {
		//Wenn beide Gesuchsteller ausgefuellt werden muessen (z.B bei einer Mutation die die Familiensituation aendert
		// (i.e. von 1GS auf 2GS) wollen wir den Benutzer zwingen beide Gesuchsteller Seiten zu besuchen bevor wir auf ok setzten.
		// Ansonsten setzten wir es sofort auf ok
		if (umzug) {
			wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.UMZUG);
		} else {
			WizardStep existingWizStep = wizardStepService.findWizardStepFromGesuch(gesuch.getId(), WizardStepName.GESUCHSTELLER);
			WizardStepStatus gesuchStepStatus = existingWizStep != null ?  existingWizStep.getWizardStepStatus() : null;
			if (WizardStepStatus.NOK.equals(gesuchStepStatus) || WizardStepStatus.IN_BEARBEITUNG.equals(gesuchStepStatus)) {
				if (isSavingLastNecessaryGesuchsteller(gesuch, gsNumber)) {
					wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.GESUCHSTELLER);
				}
			} else {
				wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.GESUCHSTELLER);
			}
		}
	}

	/**
	 * Wenn aufgrund der Familiensituation 2 GS noetig sind kommt hier true zurueck wenn gsNumber = 2 ist. sonst false
	 * Wenn aufgrund der Familiensitation 1 GS noetig ist kommt hier true zurueck wenn gsNumber = 1
	 */
	private boolean isSavingLastNecessaryGesuchsteller(Gesuch gesuch, Integer gsNumber) {
		return (gesuch.extractFamiliensituation().hasSecondGesuchsteller() && gsNumber == 2)
			|| (!gesuch.extractFamiliensituation().hasSecondGesuchsteller() && gsNumber == 1);
	}

	@Nonnull
	@Override
	public Optional<GesuchstellerContainer> findGesuchsteller(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		GesuchstellerContainer a = persistence.find(GesuchstellerContainer.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<GesuchstellerContainer> getAllGesuchsteller() {
		return new ArrayList<>(criteriaQueryHelper.getAll(GesuchstellerContainer.class));
	}

	@Override
	public void removeGesuchsteller(@Nonnull GesuchstellerContainer gesuchsteller) {
		Validate.notNull(gesuchsteller);
		Optional<GesuchstellerContainer> gesuchstellerToRemove = findGesuchsteller(gesuchsteller.getId());
		gesuchstellerToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeGesuchsteller", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsteller));
		persistence.remove(gesuchstellerToRemove.get());
	}
}
