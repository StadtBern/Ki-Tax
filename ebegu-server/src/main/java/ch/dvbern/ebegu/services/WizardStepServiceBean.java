package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Gesuch
 */
@Stateless
@Local(WizardStepService.class)
public class WizardStepServiceBean extends AbstractBaseService implements WizardStepService {

	@Inject
	private Persistence<WizardStep> persistence;


	@Override
	@Nonnull
	public WizardStep saveWizardStep(@Nonnull WizardStep kind) {
		Objects.requireNonNull(kind);
		return persistence.merge(kind);
	}

	@Override
	@Nonnull
	public Optional<WizardStep> findWizardStep(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		WizardStep a =  persistence.find(WizardStep.class, key);
		return Optional.ofNullable(a);
	}

	@Override
	public List<WizardStep> findWizardStepsFromGesuch(String gesuchId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<WizardStep> query = cb.createQuery(WizardStep.class);
		Root<WizardStep> root = query.from(WizardStep.class);
		Predicate predWizardStepFromGesuch = cb.equal(root.get(WizardStep_.gesuch).get(Gesuch_.id), gesuchId);

		query.where(predWizardStepFromGesuch);
		return persistence.getCriteriaResults(query);
	}

	@Override
	public List<WizardStep> updateSteps(String gesuchId, AbstractEntity oldEntity, AbstractEntity newEntity, WizardStepName stepName) {
		final List<WizardStep> wizardSteps = findWizardStepsFromGesuch(gesuchId);
		updateAllStatus(wizardSteps, oldEntity, newEntity, stepName);
		wizardSteps.stream().forEach(wizardStep -> saveWizardStep(wizardStep));
		return wizardSteps;
	}

	/**
	 * Hier wird es geschaut, was fuer ein Objekttyp aktualisiert wurde. Dann wird die entsprechende Logik durchgefuehrt, um zu wissen welche anderen
	 * Steps von diesen Aenderungen beeinflusst wurden. Mit dieser Information werden alle betroffenen Status dementsprechend geaendert.
	 * @param wizardSteps
	 * @param oldEntity
	 * @param newEntity
	 * @param stepName
	 */
	private void updateAllStatus(List<WizardStep> wizardSteps, AbstractEntity oldEntity, AbstractEntity newEntity, WizardStepName stepName) {
		if (WizardStepName.FAMILIENSITUATION.equals(stepName) && oldEntity instanceof Familiensituation && newEntity instanceof Familiensituation) {
			updateAllStatusForFamiliensituation(wizardSteps, (Familiensituation) oldEntity, (Familiensituation) newEntity);
		}
//		else if (WizardStepName.BETREUUNG.equals(stepName) && oldEntity instanceof Betreuung && newEntity instanceof Betreuung) {
//			updateAllStatusForBetreuung(wizardSteps, (Betreuung) oldEntity, (Betreuung) newEntity);
//		}
		else {
			updateStatusSingleStep(wizardSteps, stepName);
		}
	}

	private void updateAllStatusForFamiliensituation(List<WizardStep> wizardSteps, Familiensituation oldEntity, Familiensituation newEntity) {
		for (WizardStep wizardStep: wizardSteps) {
			if (WizardStepName.FAMILIENSITUATION.equals(wizardStep.getWizardStepName())) {
				wizardStep.setWizardStepStatus(WizardStepStatus.OK);
			}
			else if ((WizardStepName.GESUCHSTELLER.equals(wizardStep.getWizardStepName())
				|| WizardStepName.FINANZIELLE_SITUATION.equals(wizardStep.getWizardStepName()))
				&& fromOneGSToTwoGS(oldEntity, newEntity)) {
				wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
			}
		}
	}

	/**
	 * Berechnet ob die Daten bei der Familiensituation von einem GS auf 2 GS geaendert wurde.
	 * @param oldFamiliensituation
	 * @param newFamiliensituation
	 * @return
	 */
	private boolean fromOneGSToTwoGS(Familiensituation oldFamiliensituation, Familiensituation newFamiliensituation) {
		Validate.notNull(oldFamiliensituation);
		Validate.notNull(newFamiliensituation);
		return !oldFamiliensituation.hasSecondGesuchsteller() && newFamiliensituation.hasSecondGesuchsteller();
	}

	/**
	 * Der Step mit dem uebergebenen StepName bekommt den Status OK. Diese Methode wird immer aufgerufen, um den Status vom aktualisierten
	 * Objekt auf OK zu setzen
	 * @param wizardSteps
	 * @param stepName
	 */
	private void updateStatusSingleStep(List<WizardStep> wizardSteps, WizardStepName stepName) {
		for (WizardStep wizardStep: wizardSteps) {
			if (wizardStep.getWizardStepName().equals(stepName)) {
				wizardStep.setWizardStepStatus(WizardStepStatus.OK);
			}
		}
	}

}
