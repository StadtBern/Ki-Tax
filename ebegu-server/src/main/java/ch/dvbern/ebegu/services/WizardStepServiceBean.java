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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.entities.WizardStep_;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.enums.UserRole.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRole.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRole.SCHULAMT;

/**
 * Service fuer Gesuch
 */
@Stateless
@Local(WizardStepService.class)
@PermitAll
public class WizardStepServiceBean extends AbstractBaseService implements WizardStepService {

	private static final Logger LOG = LoggerFactory.getLogger(WizardStepServiceBean.class);

	@Inject
	private Persistence persistence;
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
	@Inject
	private AntragStatusHistoryService antragStatusHistoryService;
	@Inject
	private Authorizer authorizer;
	@Inject
	private PrincipalBean principalBean;
	@Inject
	private GeneratedDokumentService generatedDokumentService;
	@Inject
	private MailService mailService;
	@Inject
	private GesuchService gesuchService;

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
		WizardStep a = persistence.find(WizardStep.class, key);
		authorizer.checkReadAuthorization(a);
		return Optional.ofNullable(a);
	}

	@Override
	public List<WizardStep> findWizardStepsFromGesuch(String gesuchId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<WizardStep> query = cb.createQuery(WizardStep.class);
		Root<WizardStep> root = query.from(WizardStep.class);
		Predicate predWizardStepFromGesuch = cb.equal(root.get(WizardStep_.gesuch).get(Gesuch_.id), gesuchId);

		query.where(predWizardStepFromGesuch);
		final List<WizardStep> criteriaResults = persistence.getCriteriaResults(query);
		criteriaResults.forEach(result -> authorizer.checkReadAuthorization(result));
		return criteriaResults;
	}

	@Override
	public WizardStep findWizardStepFromGesuch(String gesuchId, WizardStepName stepName) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<WizardStep> query = cb.createQuery(WizardStep.class);
		Root<WizardStep> root = query.from(WizardStep.class);
		Predicate predWizardStepFromGesuch = cb.equal(root.get(WizardStep_.gesuch).get(Gesuch_.id), gesuchId);
		Predicate predWizardStepName = cb.equal(root.get(WizardStep_.wizardStepName), stepName);

		query.where(predWizardStepFromGesuch, predWizardStepName);
		final WizardStep result = persistence.getCriteriaSingleResult(query);
		authorizer.checkReadAuthorization(result);
		return result;
	}

	@Override
	public List<WizardStep> updateSteps(String gesuchId, @Nullable AbstractEntity oldEntity, @Nullable AbstractEntity newEntity,
		WizardStepName stepName) {
		final List<WizardStep> wizardSteps = findWizardStepsFromGesuch(gesuchId);
		updateAllStatus(wizardSteps, oldEntity, newEntity, stepName);
		wizardSteps.forEach(this::saveWizardStep);
		return wizardSteps;
	}

	@Nonnull
	@Override
	public List<WizardStep> createWizardStepList(Gesuch gesuch) {
		List<WizardStep> wizardStepList = new ArrayList<>();
		if (AntragTyp.MUTATION == gesuch.getTyp()) {
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.GESUCH_ERSTELLEN, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.GESUCHSTELLER, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.UMZUG, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.KINDER, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.BETREUUNG, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.ABWESENHEIT, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.ERWERBSPENSUM, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.DOKUMENTE, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.FREIGABE, WizardStepStatus.OK, true)));
			// Verfuegen muss WARTEN sein, da die Betreuungen nochmal verfuegt werden muessen
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.VERFUEGEN, WizardStepStatus.WARTEN, true)));
		} else { // GESUCH
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.GESUCH_ERSTELLEN, WizardStepStatus.OK, true)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.GESUCHSTELLER, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.UMZUG, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.KINDER, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.BETREUUNG, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.ABWESENHEIT, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.ERWERBSPENSUM, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.DOKUMENTE, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.FREIGABE, WizardStepStatus.UNBESUCHT, false)));
			wizardStepList.add(saveWizardStep(createWizardStepObject(gesuch, WizardStepName.VERFUEGEN, WizardStepStatus.UNBESUCHT, false)));
		}
		return wizardStepList;
	}

	/**
	 * Hier wird es geschaut, was fuer ein Objekttyp aktualisiert wurde. Dann wird die entsprechende Logik durchgefuehrt, um zu wissen welche anderen
	 * Steps von diesen Aenderungen beeinflusst wurden. Mit dieser Information werden alle betroffenen Status dementsprechend geaendert.
	 * Dazu werden die Angaben in oldEntity mit denen in newEntity verglichen und dann wird entsprechend reagiert
	 */
	private void updateAllStatus(List<WizardStep> wizardSteps, @Nullable AbstractEntity oldEntity, @Nullable AbstractEntity newEntity, WizardStepName stepName) {
		if (WizardStepName.FAMILIENSITUATION == stepName && oldEntity instanceof Familiensituation && newEntity instanceof Familiensituation) {
			updateAllStatusForFamiliensituation(wizardSteps, (Familiensituation) oldEntity, (Familiensituation) newEntity);
		} else if (WizardStepName.GESUCHSTELLER == stepName) {
			updateAllStatusForGesuchsteller(wizardSteps);
		} else if (WizardStepName.UMZUG == stepName) {
			updateAllStatusForUmzug(wizardSteps);
		} else if (WizardStepName.BETREUUNG == stepName) {
			updateAllStatusForBetreuung(wizardSteps);
		} else if (WizardStepName.ABWESENHEIT == stepName) {
			updateAllStatusForAbwesenheit(wizardSteps);
		} else if (WizardStepName.KINDER == stepName) {
			updateAllStatusForKinder(wizardSteps);
		} else if (WizardStepName.ERWERBSPENSUM == stepName) {
			updateAllStatusForErwerbspensum(wizardSteps);
		} else if (WizardStepName.EINKOMMENSVERSCHLECHTERUNG == stepName && newEntity instanceof EinkommensverschlechterungInfoContainer) {
			updateAllStatusForEinkommensverschlechterungInfo(wizardSteps, (EinkommensverschlechterungInfoContainer) oldEntity, (EinkommensverschlechterungInfoContainer) newEntity);
		} else if (WizardStepName.EINKOMMENSVERSCHLECHTERUNG == stepName && newEntity instanceof EinkommensverschlechterungContainer) {
			updateAllStatusForEinkommensverschlechterung(wizardSteps);
		} else if (WizardStepName.DOKUMENTE == stepName) {
			updateAllStatusForDokumente(wizardSteps);
		} else if (WizardStepName.VERFUEGEN == stepName) {
			updateAllStatusForVerfuegen(wizardSteps);
		} else if (WizardStepName.FINANZIELLE_SITUATION == stepName) {
			updateAllStatusForFinSit(wizardSteps);
		} else {
			updateStatusSingleStep(wizardSteps, stepName);
		}
	}

	/**
	 * Wenn die Seite schon besucht ist dann soll der Status auf ok/mutiert oder notOK (bei wechsel ekv von nein auf ja) gesetzt werden
	 */
	private void updateAllStatusForEinkommensverschlechterungInfo(List<WizardStep> wizardSteps, EinkommensverschlechterungInfoContainer oldEntity,
		EinkommensverschlechterungInfoContainer newEntity) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()
				&& WizardStepName.EINKOMMENSVERSCHLECHTERUNG == wizardStep.getWizardStepName()) {
				if (!newEntity.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung()) {
					setWizardStepOkOrMutiert(wizardStep);
				} else if (oldEntity == null || !oldEntity.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung()
					|| (!oldEntity.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus1() && newEntity.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus1())
					|| (!oldEntity.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2() && newEntity.getEinkommensverschlechterungInfoJA().getEkvFuerBasisJahrPlus2())) {
					// beim Wechseln von KEIN_EV auf EV oder von KEIN_EV_FUER_BASISJAHR2 auf EV_FUER_BASISJAHR2
					wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
				} else if (wizardStep.getGesuch().isMutation() && WizardStepStatus.NOK != wizardStep.getWizardStepStatus()) {
					setWizardStepOkOrMutiert(wizardStep);
				}
			}
		}
	}

	private void updateAllStatusForEinkommensverschlechterung(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()
				&& WizardStepStatus.NOK != wizardStep.getWizardStepStatus()
				&& WizardStepName.EINKOMMENSVERSCHLECHTERUNG == wizardStep.getWizardStepName()
				&& wizardStep.getGesuch().isMutation()) {

				setWizardStepOkOrMutiert(wizardStep);
			}
		}
	}

	private void updateAllStatusForDokumente(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()
				&& WizardStepName.DOKUMENTE == wizardStep.getWizardStepName()) {

				final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil
					.mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(wizardStep.getGesuch()),
						dokumentGrundService.findAllDokumentGrundByGesuch(wizardStep.getGesuch()), wizardStep.getGesuch());

				boolean allNeededDokumenteUploaded = true;
				for (DokumentGrund dokumentGrund : dokumentGrundsMerged) {
					//der DokumenntGrundTyp SONSTIGE_NACHWEISE gehoert nie zu den needed dokumenten
					if (!dokumentGrund.getDokumentGrundTyp().equals(DokumentGrundTyp.SONSTIGE_NACHWEISE) && dokumentGrund.isNeeded() && dokumentGrund.isEmpty()) {
						allNeededDokumenteUploaded = false;
						break;
					}
				}

				// TODO reviewer // wenn ersichtlich sein soll dass die sonstigen Dokumente angepasst wurden, muss das hier angepasst werden
				// TODO reviewer // dies ist aber warscheinlich nicht nötig... da es nicht relevante dokumente sind
				if (allNeededDokumenteUploaded) {
					setWizardStepOkOrMutiert(wizardStep);
				} else {
					if (wizardStep.getGesuch().isMutation()) {
						//TODO reviewer // der wizardstepstatus (bei Dokumente) einer Mutation kann in den Zustand "mutiert" wechseln, obwohl nichts geändert wurde
						//TODO reviewer // wenn nicht alle Dokumente vorhanden waren aber dieses update trotzdem aufgerufen wird (upload bei sonstige und dann wieder loeschen)
						wizardStep.setWizardStepStatus(WizardStepStatus.MUTIERT);
					} else {
						wizardStep.setWizardStepStatus(WizardStepStatus.IN_BEARBEITUNG);
					}
				}
			}
		}
	}

	/**
	 * Holt alle Erwerbspensen und Betreuungen von der Datenbank. Nur die Betreuungen vom Typ anders als TAGESSCHULE und TAGESELTERN_SCHULKIND werden beruecksichtigt
	 * Wenn die Anzahl solcher Betreuungen grosser als 0 ist, dann wird es geprueft, ob es Erwerbspensen gibt, wenn nicht der Status aendert auf NOK.
	 * In allen anderen Faellen wird der Status auf OK gesetzt
	 */
	private void updateAllStatusForErwerbspensum(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName()) {
				checkStepStatusForErwerbspensum(wizardStep, false);
			}
		}
	}

	/**
	 * Wenn der Status aller Betreuungen des Gesuchs VERFUEGT ist, dann wechseln wir den Staus von VERFUEGEN auf OK.
	 * Der Status des Gesuchs wechselt auch dann auf VERFUEGT, da alle Angebote sind verfuegt
	 */
	private void updateAllStatusForVerfuegen(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepName.VERFUEGEN == wizardStep.getWizardStepName()
				&& WizardStepStatus.OK != wizardStep.getWizardStepStatus()) {
				final List<Betreuung> betreuungenFromGesuch = betreuungService.findAllBetreuungenFromGesuch(wizardStep.getGesuch().getId());
				if (betreuungenFromGesuch.stream().allMatch(betreuung ->
					Betreuungsstatus.VERFUEGT == betreuung.getBetreuungsstatus() ||
						Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG == betreuung.getBetreuungsstatus() ||
						Betreuungsstatus.NICHT_EINGETRETEN == betreuung.getBetreuungsstatus() ||
						Betreuungsstatus.SCHULAMT == betreuung.getBetreuungsstatus())) {

					wizardStep.setWizardStepStatus(WizardStepStatus.OK);
					wizardStep.getGesuch().setStatus(AntragStatus.VERFUEGT);
					gesuchService.postGesuchVerfuegen(wizardStep.getGesuch());

					// Hier wird das Gesuch oder die Mutation effektiv verfügt. Daher müssen hier noch andere Services gerufen werden!
					try {
						generatedDokumentService.getBegleitschreibenDokument(wizardStep.getGesuch());
					} catch (MimeTypeParseException | MergeDocException e) {
						LOG.error("Error updating Deckblatt Dokument", e);
					}

					try {
						if (!wizardStep.getGesuch().isMutation()) {
							// Erstgesuch
							mailService.sendInfoVerfuegtGesuch(wizardStep.getGesuch());
						} else {
							// Mutation
							mailService.sendInfoVerfuegtMutation(wizardStep.getGesuch());
						}
					} catch (MailException e) {
						LOG.error("Error sending Mail zu gesuchsteller", e);
					}

					antragStatusHistoryService.saveStatusChange(wizardStep.getGesuch(), null);
				}
			}
		}
	}

	/**
	 * Wenn der Status von Gesuchsteller auf OK gesetzt wird, koennen wir davon ausgehen, dass die benoetigten GS
	 * eingetragen wurden. Deswegen kann man die steps FINANZIELLE_SITUATION und EINKOMMENSVERSCHLECHTERUNG aktivieren
	 */
	private void updateAllStatusForGesuchsteller(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepName.GESUCHSTELLER == wizardStep.getWizardStepName()) {
				setWizardStepOkOrMutiert(wizardStep);
			} else if ((WizardStepName.FINANZIELLE_SITUATION == wizardStep.getWizardStepName()
				|| WizardStepName.EINKOMMENSVERSCHLECHTERUNG == wizardStep.getWizardStepName()
				|| WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName())
				&& !wizardStep.getVerfuegbar()
				&& WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()) {
				wizardStep.setVerfuegbar(true);
			}
		}
	}

	private void updateAllStatusForUmzug(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepName.UMZUG == wizardStep.getWizardStepName()) {
				setWizardStepOkOrMutiert(wizardStep);
			}
		}
	}

	private void updateAllStatusForAbwesenheit(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepName.ABWESENHEIT == wizardStep.getWizardStepName()) {
				setWizardStepOkOrMutiert(wizardStep);
			}
		}
	}

	private void updateAllStatusForFinSit(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepName.FINANZIELLE_SITUATION == wizardStep.getWizardStepName() && wizardStep.getGesuch().isMutation()) {
				setWizardStepOkOrMutiert(wizardStep);
			}
		}
	}

	@Override
	public void setWizardStepOkOrMutiert(@NotNull WizardStep wizardStep) {
		wizardStep.setWizardStepStatus(getWizardStepStatusOkOrMutiert(wizardStep));
	}

	private WizardStepStatus getWizardStepStatusOkOrMutiert(WizardStep wizardStep) {
		if (AntragTyp.MUTATION != wizardStep.getGesuch().getTyp()) {
			// just to avoid doing the calculation for Mutation if it is not needed
			return WizardStepStatus.OK;
		}

		final List<AbstractEntity> newObjects = getStepRelatedObjects(wizardStep.getWizardStepName(), wizardStep.getGesuch());
		Optional<Gesuch> vorgaengerGesuch = this.gesuchService.findGesuch(wizardStep.getGesuch().getVorgaengerId(), false);
		if (!vorgaengerGesuch.isPresent()) {
			throw new EbeguEntityNotFoundException("getWizardStepStatusOkOrMutiert", ErrorCodeEnum
				.ERROR_VORGAENGER_MISSING, "Vorgaenger Gesuch fuer Mutation nicht gefunden");
		}
		final List<AbstractEntity> vorgaengerObjects = getStepRelatedObjects(wizardStep.getWizardStepName(), vorgaengerGesuch.get());
		boolean isMutiert = isObjectMutiert(newObjects, vorgaengerObjects);
		if (AntragTyp.MUTATION == wizardStep.getGesuch().getTyp() && isMutiert) {
			return WizardStepStatus.MUTIERT;
		}
		return WizardStepStatus.OK;
	}

	/**
	 * Returns all Objects that are related to the given Step. For instance for the Step GESUCHSTELLER it returns
	 * the object Gesuchsteller1 and Gesuchsteller2. These objects can then be used to check for changes.
	 */
	@SuppressWarnings("OverlyComplexMethod")
	private List<AbstractEntity> getStepRelatedObjects(@NotNull WizardStepName wizardStepName, @NotNull Gesuch gesuch) {
		List<AbstractEntity> relatedObjects = new ArrayList<>();
		if (WizardStepName.FAMILIENSITUATION == wizardStepName
			&& gesuch.getFamiliensituationContainer() != null) {
			relatedObjects.add(gesuch.getFamiliensituationContainer().getFamiliensituationJA());
		} else if (WizardStepName.GESUCHSTELLER == wizardStepName) {
			addRelatedObjectsForGesuchsteller(relatedObjects, gesuch.getGesuchsteller1());
			addRelatedObjectsForGesuchsteller(relatedObjects, gesuch.getGesuchsteller2());
		} else if (WizardStepName.UMZUG == wizardStepName) {
			addRelatedObjectsForUmzug(gesuch.getGesuchsteller1(), relatedObjects);
			addRelatedObjectsForUmzug(gesuch.getGesuchsteller2(), relatedObjects);
		} else if (WizardStepName.KINDER == wizardStepName) {
			relatedObjects.addAll(gesuch.getKindContainers());
		} else if (WizardStepName.BETREUUNG == wizardStepName) {
			relatedObjects.addAll(gesuch.extractAllBetreuungen());
		} else if (WizardStepName.ABWESENHEIT == wizardStepName) {
			relatedObjects.addAll(gesuch.extractAllAbwesenheiten());
		} else if (WizardStepName.ERWERBSPENSUM == wizardStepName) {
			if (gesuch.getGesuchsteller1() != null) {
				relatedObjects.addAll(gesuch.getGesuchsteller1().getErwerbspensenContainers());
			}
			if (gesuch.getGesuchsteller2() != null) {
				relatedObjects.addAll(gesuch.getGesuchsteller2().getErwerbspensenContainers());
			}
		} else if (WizardStepName.FINANZIELLE_SITUATION == wizardStepName) {
			if (gesuch.getGesuchsteller1() != null) {
				relatedObjects.add(gesuch.getGesuchsteller1().getFinanzielleSituationContainer());
			}
			if (gesuch.getGesuchsteller2() != null) {
				relatedObjects.add(gesuch.getGesuchsteller2().getFinanzielleSituationContainer());
			}
		} else if (WizardStepName.EINKOMMENSVERSCHLECHTERUNG == wizardStepName) {
			if (gesuch != null) {
				final EinkommensverschlechterungInfoContainer ekvInfo = gesuch.getEinkommensverschlechterungInfoContainer();
				if (ekvInfo != null) {
					relatedObjects.add(ekvInfo);
					if (ekvInfo.getEinkommensverschlechterungInfoJA().getEinkommensverschlechterung()) {
						if (gesuch.getGesuchsteller1() != null && gesuch.getGesuchsteller1()
							.getEinkommensverschlechterungContainer() != null) {
							relatedObjects.add(gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer());
						}
						if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2()
							.getEinkommensverschlechterungContainer() != null) {
							relatedObjects.add(gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer());
						}
					}
				}
			}
		} else if (WizardStepName.DOKUMENTE == wizardStepName) {
			relatedObjects.addAll(dokumentGrundService.findAllDokumentGrundByGesuch(gesuch));
		}
		return relatedObjects;
	}

	/**
	 * Adds all Adressen of the given Gesuchsteller that are set as umzug
	 */
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	private void addRelatedObjectsForUmzug(@Nullable GesuchstellerContainer gesuchsteller, List<AbstractEntity> relatedObjects) {
		if (gesuchsteller != null) {
			for (GesuchstellerAdresseContainer adresse : gesuchsteller.getAdressen()) {
				if (!adresse.extractIsKorrespondenzAdresse() && !adresse.getGesuchstellerAdresseJA().getGueltigkeit()
					.getGueltigAb().isEqual(Constants.START_OF_TIME)) { // only the first Adresse starts at START_OF_TIME
					relatedObjects.add(adresse);
				}
			}
		}
	}

	/**
	 * Adds the Gesuchsteller itself and her korrespondeyAdresse.
	 */
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	private void addRelatedObjectsForGesuchsteller(List<AbstractEntity> relatedObjects, @Nullable GesuchstellerContainer gesuchsteller) {
		if (gesuchsteller != null) {
			relatedObjects.add(gesuchsteller.getGesuchstellerJA());
			for (GesuchstellerAdresseContainer adresse : gesuchsteller.getAdressen()) {
				// add Korrespondezadresse and first Wohnadresse
				if (adresse.extractIsKorrespondenzAdresse() || adresse.getGesuchstellerAdresseJA().getGueltigkeit()
					.getGueltigAb().isEqual(Constants.START_OF_TIME)) { // only the first Wohnadresse starts at START_OF_TIME
					relatedObjects.add(adresse);
				}
			}
		}
	}

	/**
	 * Returns true when given list have different sizes. If not, it checks whether the content of each object
	 * of the list newEntities is the same as it was in the list oldEntities. Any change will make the method return
	 * true
	 */
	private boolean isObjectMutiert(@NotNull List<AbstractEntity> newEntities, @NotNull List<AbstractEntity> oldEntities) {
		if (oldEntities.size() != newEntities.size()) {
			return true;
		}
		for (AbstractEntity newEntity : newEntities) {
			if (newEntity != null && newEntity.getVorgaengerId() == null) {
				return true; // if there is no vorgaenger it must have changed
			}
			if (newEntity != null && newEntity.getVorgaengerId() != null) {
				final AbstractEntity vorgaengerEntity = persistence.find(newEntity.getClass(), newEntity.getVorgaengerId());
				if (vorgaengerEntity == null || !newEntity.isSame(vorgaengerEntity)) {
					return true;
				}
			}
		}
		return false;
	}

	private void updateAllStatusForBetreuung(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()) {
				if (WizardStepName.BETREUUNG == wizardStep.getWizardStepName()) {
					checkStepStatusForBetreuung(wizardStep, false);
				} else if (!principalBean.isCallerInAnyOfRole(SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT)
					&& WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName()) {
					// SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION und SCHULAMT duerfen beim Aendern einer Betreuung
					// den Status von ERWERBPENSUM nicht aendern
					checkStepStatusForErwerbspensum(wizardStep, true);
				}
			}
		}
	}

	private void updateAllStatusForKinder(List<WizardStep> wizardSteps) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()) {
				if (WizardStepName.BETREUUNG == wizardStep.getWizardStepName()) {
					checkStepStatusForBetreuung(wizardStep, true);
				} else if (WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName()) {
					checkStepStatusForErwerbspensum(wizardStep, true);
				} else if (WizardStepName.KINDER == wizardStep.getWizardStepName()) {
					final List<KindContainer> kinderFromGesuch = kindService.findAllKinderFromGesuch(wizardStep.getGesuch().getId())
						.stream().filter(kindContainer -> kindContainer.getKindJA().getFamilienErgaenzendeBetreuung())
						.collect(Collectors.toList());
					WizardStepStatus status;
					if (kinderFromGesuch.isEmpty()) {
						status = WizardStepStatus.NOK;
					} else {
						status = getWizardStepStatusOkOrMutiert(wizardStep);
					}
					wizardStep.setWizardStepStatus(status);
				}
			}
		}
	}

	private void updateAllStatusForFamiliensituation(List<WizardStep> wizardSteps, Familiensituation oldEntity, Familiensituation newEntity) {
		for (WizardStep wizardStep : wizardSteps) {
			if (WizardStepStatus.UNBESUCHT != wizardStep.getWizardStepStatus()) { // vermeide, dass der Status eines unbesuchten Steps geaendert wird
				if (WizardStepName.FAMILIENSITUATION == wizardStep.getWizardStepName()) {
					setWizardStepOkOrMutiert(wizardStep);
				} else if (EbeguUtil.fromOneGSToTwoGS(oldEntity, newEntity) && wizardStep.getGesuch().getGesuchsteller2() == null) {

					if (WizardStepName.GESUCHSTELLER == wizardStep.getWizardStepName()) {
						wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
						wizardStep.setVerfuegbar(true);

					} else if (WizardStepName.FINANZIELLE_SITUATION == wizardStep.getWizardStepName()
						|| WizardStepName.EINKOMMENSVERSCHLECHTERUNG == wizardStep.getWizardStepName()
						|| (WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName()
						&& erwerbspensumService.isErwerbspensumRequired(wizardStep.getGesuch()))) {
						wizardStep.setVerfuegbar(false);
						wizardStep.setWizardStepStatus(WizardStepStatus.NOK);

					}
					//kann man effektiv sagen dass bei nur einem GS niemals Rote Schritte FinanzielleSituation und EVK gibt
				} else if (!newEntity.hasSecondGesuchsteller() && wizardStep.getGesuch().getGesuchsteller1() != null) { // nur 1 GS
					if (WizardStepName.GESUCHSTELLER == wizardStep.getWizardStepName()) {
						if (wizardStep.getGesuch().isMutation()) {
							setWizardStepOkOrMutiert(wizardStep);
						} else if (wizardStep.getWizardStepStatus() == WizardStepStatus.NOK) {
							wizardStep.setWizardStepStatus(WizardStepStatus.OK);
						}

					} else if (WizardStepName.FINANZIELLE_SITUATION == wizardStep.getWizardStepName()
						|| WizardStepName.EINKOMMENSVERSCHLECHTERUNG == wizardStep.getWizardStepName()
						|| (WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName()
						&& !erwerbspensumService.isErwerbspensumRequired(wizardStep.getGesuch()))) {

						setVerfuegbarAndOK(wizardStep);

					} else if (WizardStepName.ERWERBSPENSUM == wizardStep.getWizardStepName()
						&& erwerbspensumService.isErwerbspensumRequired(wizardStep.getGesuch())) {

						if (wizardStep.getGesuch().getGesuchsteller1().getErwerbspensenContainers().isEmpty()) {
							if (wizardStep.getWizardStepStatus() != WizardStepStatus.NOK) {
								wizardStep.setVerfuegbar(true);
								wizardStep.setWizardStepStatus(WizardStepStatus.NOK);
							}
						} else {
							setVerfuegbarAndOK(wizardStep);
						}
					}
				}
			}
		}
	}

	private void setVerfuegbarAndOK(WizardStep wizardStep) {
		if (wizardStep.getGesuch().isMutation()) {
			wizardStep.setVerfuegbar(true);
			setWizardStepOkOrMutiert(wizardStep);
		} else if (wizardStep.getWizardStepStatus() == WizardStepStatus.NOK) {
			wizardStep.setVerfuegbar(true);
			wizardStep.setWizardStepStatus(WizardStepStatus.OK);
		}
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	private void checkStepStatusForBetreuung(WizardStep wizardStep, boolean changesBecauseOtherStates) {
		final List<Betreuung> betreuungenFromGesuch = betreuungService.findAllBetreuungenFromGesuch(wizardStep.getGesuch().getId());
		WizardStepStatus status;
		if (changesBecauseOtherStates && wizardStep.getWizardStepStatus() != WizardStepStatus.MUTIERT) {
			status = WizardStepStatus.OK;
		} else {
			status = getWizardStepStatusOkOrMutiert(wizardStep);
		}

		if (betreuungenFromGesuch.size() <= 0) {
			status = WizardStepStatus.NOK;
		} else {
			for (Betreuung betreuung : betreuungenFromGesuch) {
				if (Betreuungsstatus.ABGEWIESEN == betreuung.getBetreuungsstatus()) {
					status = WizardStepStatus.NOK;
					break;
				}
				if (Betreuungsstatus.WARTEN == betreuung.getBetreuungsstatus()) {
					status = WizardStepStatus.PLATZBESTAETIGUNG;
				}
			}
		}
		wizardStep.setWizardStepStatus(status);
	}

	/**
	 * Erwerbspensum muss nur erfasst werden, falls mind. 1 Kita oder 1 Tageseltern Kleinkind Angebot erfasst wurde
	 * und mind. eines dieser Kinder keine Fachstelle involviert hat
	 */
	@SuppressWarnings({ "LocalVariableNamingConvention", "NonBooleanMethodNameMayNotStartWithQuestion" })
	private void checkStepStatusForErwerbspensum(WizardStep wizardStep, boolean changesBecauseOtherStates) {
		Gesuch gesuch = wizardStep.getGesuch();
		boolean erwerbspensumRequired = erwerbspensumService.isErwerbspensumRequired(wizardStep.getGesuch());

		WizardStepStatus status = null;
		if (erwerbspensumRequired) {
			if (gesuch.getGesuchsteller1() != null && erwerbspensumService.findErwerbspensenForGesuchsteller(gesuch.getGesuchsteller1()).isEmpty()) {
				status = WizardStepStatus.NOK;
			}
			if (status != WizardStepStatus.NOK && gesuch.getGesuchsteller2() != null && erwerbspensumService.findErwerbspensenForGesuchsteller(gesuch.getGesuchsteller2()).isEmpty()) {
				status = WizardStepStatus.NOK;
			}
		} else if (changesBecauseOtherStates && wizardStep.getWizardStepStatus() != WizardStepStatus.MUTIERT) {
			status = WizardStepStatus.OK;
		}
		// Ansonsten OK bzw. MUTIERT
		if (status == null) {
			status = getWizardStepStatusOkOrMutiert(wizardStep);
		}
		wizardStep.setWizardStepStatus(status);
	}

	/**
	 * Der Step mit dem uebergebenen StepName bekommt den Status OK. Diese Methode wird immer aufgerufen, um den Status vom aktualisierten
	 * Objekt auf OK zu setzen
	 */
	private void updateStatusSingleStep(List<WizardStep> wizardSteps, WizardStepName stepName) {
		for (WizardStep wizardStep : wizardSteps) {
			if (wizardStep.getWizardStepName() == stepName) {
				wizardStep.setWizardStepStatus(WizardStepStatus.OK);
			}
		}
	}

	private WizardStep createWizardStepObject(Gesuch gesuch, WizardStepName wizardStepName, WizardStepStatus stepStatus,
		Boolean verfuegbar) {
		final WizardStep wizardStep = new WizardStep();
		wizardStep.setGesuch(gesuch);
		wizardStep.setVerfuegbar(verfuegbar != null ? verfuegbar : false);
		wizardStep.setWizardStepName(wizardStepName);
		wizardStep.setWizardStepStatus(stepStatus);
		return wizardStep;
	}

	@Override
	public void removeSteps(Gesuch gesToRemove) {
		List<WizardStep> wizardStepsFromGesuch = findWizardStepsFromGesuch(gesToRemove.getId());
		for (WizardStep wizardStep : wizardStepsFromGesuch) {
			persistence.remove(WizardStep.class, wizardStep.getId());
		}
	}

	@Override
	public void setWizardStepOkay(@Nonnull String gesuchId, @Nonnull WizardStepName stepName) {
		final WizardStep freigabeStep = findWizardStepFromGesuch(gesuchId, stepName);
		Objects.requireNonNull(freigabeStep, stepName.name() + " WizardStep fuer gesuch nicht gefunden " + gesuchId);
		if (WizardStepStatus.OK != freigabeStep.getWizardStepStatus()) {
			freigabeStep.setWizardStepStatus(WizardStepStatus.OK);
			saveWizardStep(freigabeStep);
		}
	}
}
