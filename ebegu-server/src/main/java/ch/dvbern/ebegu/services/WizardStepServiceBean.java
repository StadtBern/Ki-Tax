package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.rules.Anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.util.DokumenteUtil;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service fuer Gesuch
 */
@Stateless
@Local(WizardStepService.class)
public class WizardStepServiceBean extends AbstractBaseService implements WizardStepService {

	@Inject
	private Persistence<WizardStep> persistence;
	@Inject
	private BetreuungService betreuungService;
	@Inject
	private KindService kindService;
	@Inject
	private ErwerbspensumService erwerbspensumService;
	@Inject
	private DokumentGrundService dokumentGrundService;
	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;


	@Override
	@Nonnull
	public WizardStep saveWizardStep(@Nonnull WizardStep wizardStep) {
		Objects.requireNonNull(wizardStep);
		return persistence.merge(wizardStep);
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
	 * Dazu werden die Angaben in oldEntity mit denen in newEntity verglichen und dann wird entsprechend reagiert
	 * @param wizardSteps
	 * @param oldEntity
	 * @param newEntity
	 * @param stepName
	 */
	private void updateAllStatus(List<WizardStep> wizardSteps, AbstractEntity oldEntity, AbstractEntity newEntity, WizardStepName stepName) {
		if (WizardStepName.FAMILIENSITUATION.equals(stepName) && oldEntity instanceof Familiensituation && newEntity instanceof Familiensituation) {
			updateAllStatusForFamiliensituation(wizardSteps, (Familiensituation) oldEntity, (Familiensituation) newEntity);
		}
		else if (WizardStepName.GESUCHSTELLER.equals(stepName)) {
			updateAllStatusForGesuchsteller(wizardSteps);
		}
		else if (WizardStepName.BETREUUNG.equals(stepName)) {
			updateAllStatusForBetreuung(wizardSteps);
		}
		else if (WizardStepName.KINDER.equals(stepName)) {
			updateAllStatusForKinder(wizardSteps);
		}
		else if (WizardStepName.ERWERBSPENSUM.equals(stepName)) {
			updateAllStatusForErwerbspensum(wizardSteps);
		}
		else if (WizardStepName.EINKOMMENSVERSCHLECHTERUNG.equals(stepName) && oldEntity instanceof EinkommensverschlechterungInfo
			&& newEntity instanceof EinkommensverschlechterungInfo) {
			updateAllStatusForEinkommensverschlechterungInfo(wizardSteps, (EinkommensverschlechterungInfo) oldEntity, (EinkommensverschlechterungInfo) newEntity);
		}
		else if (WizardStepName.DOKUMENTE.equals(stepName)) {
			updateAllStatusForDokumente(wizardSteps);
		}
		else {
			updateStatusSingleStep(wizardSteps, stepName);
		}
	}

	private void updateAllStatusForEinkommensverschlechterungInfo(List<WizardStep> wizardSteps, EinkommensverschlechterungInfo oldEntity,
																  EinkommensverschlechterungInfo newEntity) {
		for (WizardStep wizardStep: wizardSteps) {
			if (!WizardStepStatus.UNBESUCHT.equals(wizardStep.getWizardStepStatus())
				&& WizardStepName.EINKOMMENSVERSCHLECHTERUNG.equals(wizardStep.getWizardStepName())) {
				if (oldEntity.getEinkommensverschlechterung() && !newEntity.getEinkommensverschlechterung()) {
					wizardStep.setWizardStepStatus(WizardStepStatus.OK);
				}
				else if (!oldEntity.getEinkommensverschlechterung() && newEntity.getEinkommensverschlechterung()) {
					wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
				}
			}
		}
	}

	private void updateAllStatusForDokumente(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep: wizardSteps) {
			if (!WizardStepStatus.UNBESUCHT.equals(wizardStep.getWizardStepStatus())
				&& WizardStepName.DOKUMENTE.equals(wizardStep.getWizardStepName())) {

				final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil
					.mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(wizardStep.getGesuch()),
						dokumentGrundService.getAllDokumentGrundByGesuch(wizardStep.getGesuch()));

				boolean allNeededDokumenteUploaded = true;
				for (DokumentGrund dokumentGrund : dokumentGrundsMerged) {
					if (dokumentGrund.isNeeded() && dokumentGrund.isEmpty()) {
						allNeededDokumenteUploaded = false;
						break;
					}
				}
				wizardStep.setWizardStepStatus(allNeededDokumenteUploaded ? WizardStepStatus.OK : WizardStepStatus.IN_BEARBEITUNG);
			}
		}
	}

	/**
	 * Holt alle Erwerbspensen und Betreuungen von der Datenbank. Nur die Betreuungen vom Typ anders als TAGESSCHULE und TAGESELTERN_SCHULKIND werden beruecksichtigt
	 * Wenn die Anzahl solcher Betreuungen grosser als 0 ist, dann wird es geprueft, ob es Erwerbspensen gibt, wenn nicht der Status aendert auf NOK.
	 * In allen anderen Faellen wird der Status auf OK gesetzt
	 * @param wizardSteps
	 */
	private void updateAllStatusForErwerbspensum(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep: wizardSteps) {
			if (WizardStepName.ERWERBSPENSUM.equals(wizardStep.getWizardStepName())) {
				checkStepStatusForErwerbspensum(wizardStep);
			}
		}
	}

	/**
	 * Wenn der Status von Gesuchsteller auf OK gesetzt wird, koennen wir davon ausgehen, dass die benoetigten GS
	 * eingetragen wurden. Deswegen kann man die steps FINANZIELLE_SITUATION und EINKOMMENSVERSCHLECHTERUNG aktivieren
	 * @param wizardSteps
	 */
	private void updateAllStatusForGesuchsteller(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep: wizardSteps) {
			if (WizardStepName.GESUCHSTELLER.equals(wizardStep.getWizardStepName())) {
				wizardStep.setWizardStepStatus(WizardStepStatus.OK);
			}
			else if ((WizardStepName.FINANZIELLE_SITUATION.equals(wizardStep.getWizardStepName())
				|| WizardStepName.EINKOMMENSVERSCHLECHTERUNG.equals(wizardStep.getWizardStepName()))
				&& !wizardStep.getVerfuegbar()
				&& !WizardStepStatus.UNBESUCHT.equals(wizardStep.getWizardStepStatus())) {
				wizardStep.setVerfuegbar(true);
			}
		}
	}

	private void updateAllStatusForBetreuung(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep: wizardSteps) {
			if (!WizardStepStatus.UNBESUCHT.equals(wizardStep.getWizardStepStatus())) {
				if (WizardStepName.BETREUUNG.equals(wizardStep.getWizardStepName())) {
					checkStepStatusForBetreuung(wizardStep);
				}
				else if (WizardStepName.ERWERBSPENSUM.equals(wizardStep.getWizardStepName())) {
					checkStepStatusForErwerbspensum(wizardStep);
				}
			}
		}
	}

	private void updateAllStatusForKinder(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep: wizardSteps) {
			if (!WizardStepStatus.UNBESUCHT.equals(wizardStep.getWizardStepStatus())) {
				if (WizardStepName.BETREUUNG.equals(wizardStep.getWizardStepName())) {
					checkStepStatusForBetreuung(wizardStep);
				}
				else if (WizardStepName.ERWERBSPENSUM.equals(wizardStep.getWizardStepName())) {
					checkStepStatusForErwerbspensum(wizardStep);
				}
				else if (WizardStepName.KINDER.equals(wizardStep.getWizardStepName())) {
					final List<KindContainer> kinderFromGesuch = kindService.findAllKinderFromGesuch(wizardStep.getGesuch().getId())
						.stream().filter(kindContainer -> kindContainer.getKindJA().getFamilienErgaenzendeBetreuung())
						.collect(Collectors.toList());
					WizardStepStatus status = (kinderFromGesuch.size() > 0) ? WizardStepStatus.OK : WizardStepStatus.NOK;
					wizardStep.setWizardStepStatus(status);
				}
			}
		}
	}

	private void updateAllStatusForFamiliensituation(List<WizardStep> wizardSteps, Familiensituation oldEntity, Familiensituation newEntity) {
		for (WizardStep wizardStep: wizardSteps) {
			if (!WizardStepStatus.UNBESUCHT.equals(wizardStep.getWizardStepStatus())) { // vermeide, dass der Status eines unbesuchten Steps geaendert wird
				if (WizardStepName.FAMILIENSITUATION.equals(wizardStep.getWizardStepName())) {
					wizardStep.setWizardStepStatus(WizardStepStatus.OK);
				}
				else if (fromOneGSToTwoGS(oldEntity, newEntity)) {
					if (WizardStepName.GESUCHSTELLER.equals(wizardStep.getWizardStepName())) {
						wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
					}
					else if (WizardStepName.FINANZIELLE_SITUATION.equals(wizardStep.getWizardStepName())
					|| WizardStepName.EINKOMMENSVERSCHLECHTERUNG.equals(wizardStep.getWizardStepName())) {
						wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
						wizardStep.setVerfuegbar(false);
					}
				}
			}
		}
	}

	private void checkStepStatusForBetreuung(WizardStep wizardStep) {
		final List<Betreuung> betreuungenFromGesuch = betreuungService.findAllBetreuungenFromGesuch(wizardStep.getGesuch().getId());
		WizardStepStatus status = WizardStepStatus.OK;
		if (betreuungenFromGesuch.size() <= 0) {
			status = WizardStepStatus.NOK;
		}
		else {
			for (Betreuung betreuung : betreuungenFromGesuch) {
				if (Betreuungsstatus.ABGEWIESEN.equals(betreuung.getBetreuungsstatus())) {
					status = WizardStepStatus.NOK;
					break;
				} else if (Betreuungsstatus.WARTEN.equals(betreuung.getBetreuungsstatus())) {
					status = WizardStepStatus.PLATZBESTAETIGUNG;
				}
			}
		}
		wizardStep.setWizardStepStatus(status);
	}

	/**
	 * Erwerbspensum muss nur erfasst werden, falls mind. 1 Kita oder 1 Tageseltern Kleinkind Angebot erfasst wurde
	 * und mind. eines dieser Kinder keine Fachstelle involviert hat
	 * @param wizardStep
	 */
	private void checkStepStatusForErwerbspensum(WizardStep wizardStep) {
		final List<Betreuung> allBetreuungenRequiringErwerbspensum = betreuungService.findAllBetreuungenFromGesuch(wizardStep.getGesuch().getId())
			.stream().filter(betreuung ->
				betreuung.getKind().getKindJA().getPensumFachstelle() == null
				&& (BetreuungsangebotTyp.KITA == betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp()
					|| BetreuungsangebotTyp.TAGESELTERN_KLEINKIND == betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp()))
			.collect(Collectors.toList());

		final Collection<ErwerbspensumContainer> erwerbspensenForGesuch = erwerbspensumService.findErwerbspensenFromGesuch(wizardStep.getGesuch().getId());
		WizardStepStatus status = (allBetreuungenRequiringErwerbspensum.size() > 0 && erwerbspensenForGesuch.size() <= 0)
			? WizardStepStatus.NOK: WizardStepStatus.OK ;
		wizardStep.setWizardStepStatus(status);
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
