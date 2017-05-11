package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.testfaelle.*;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.FreigabeCopyUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service fuer erstellen und mutieren von Testfällen
 */
@Stateless
@Local(TestfaelleService.class)
@RolesAllowed(value = {UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN})
public class TestfaelleServiceBean extends AbstractBaseService implements TestfaelleService {


	@Inject
	private GesuchsperiodeService gesuchsperiodeService;
	@Inject
	private InstitutionStammdatenService institutionStammdatenService;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private FallService fallService;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private FamiliensituationService familiensituationService;
	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private KindService kindService;
	@Inject
	private BetreuungService betreuungService;
	@Inject
	private ErwerbspensumService erwerbspensumService;
	@Inject
	private FinanzielleSituationService finanzielleSituationService;
	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;
	@Inject
	private EinkommensverschlechterungService einkommensverschlechterungService;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private VerfuegungService verfuegungService;

	@Override
	@Nonnull
	public StringBuilder createAndSaveTestfaelle(String fallid,
												 boolean betreuungenBestaetigt,
												 boolean verfuegen, @Nullable String gesuchsPeriodeId) {
		return this.createAndSaveTestfaelle(fallid, 1, betreuungenBestaetigt, verfuegen, null, gesuchsPeriodeId);
	}

	@Nonnull
	@SuppressWarnings(value = {"PMD.NcssMethodCount", "PMD.AvoidDuplicateLiterals"})
	public StringBuilder createAndSaveTestfaelle(String fallid,
												 Integer iterationCount,
												 boolean betreuungenBestaetigt,
												 boolean verfuegen, Benutzer besitzer, @Nullable String gesuchsPeriodeId) {

		iterationCount = (iterationCount == null || iterationCount == 0) ? 1 : iterationCount;

		Gesuchsperiode gesuchsperiode;
		if (StringUtils.isNotEmpty(gesuchsPeriodeId)) {
			gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(gesuchsPeriodeId).orElseThrow(() -> new EbeguEntityNotFoundException("createAndSaveTestfaelle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchsPeriodeId));
		} else {
			gesuchsperiode = getNeuesteGesuchsperiode();
		}
		List<InstitutionStammdaten> institutionStammdatenList = getInstitutionsstammdatenForTestfaelle();

		StringBuilder responseString = new StringBuilder("");
		for (int i = 0; i < iterationCount; i++) {

			if (WaeltiDagmar.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Dagmar Waelti erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (FeutzIvonne.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Yvonne Feutz erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (PerreiraMarcia.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Marcia Perreira erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (WaltherLaura.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Laura Walther erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (LuethiMeret.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Meret Luethi erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (BeckerNora.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Nora Becker erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (MeierMeret.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall07_MeierMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Meier Meret erstellt, Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (UmzugAusInAusBern.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall08_UmzugAusInAusBern(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Umzug Aus-In-Aus Bern Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (UmzugVorGesuchsperiode.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall10_UmzugVorGesuchsperiode(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Umzug Vor Gesuchsperiode Fallnummer: '").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (Abwesenheit.equals(fallid)) {
				final Gesuch gesuch = createAndSaveGesuch(new Testfall09_Abwesenheit(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				responseString.append("Fall Abwesenheit Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV1.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_01(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 1 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV2.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_02(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 2 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV3.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_03(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 3 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV4.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_04(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 4 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV5.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_05(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 5 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV6.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_06(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 6 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV7.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_07(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 7 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV8.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_08(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 8 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV9.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_09(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 9 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if (ASIV10.equals(fallid)) {
				final Gesuch gesuch = createAndSaveAsivGesuch(new Testfall_ASIV_10(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Fall ASIV 10 Fallnummer: ").append(gesuch.getFall().getFallNummer()).append("', AntragID: ").append(gesuch.getId());
			} else if ("all".equals(fallid)) {
				createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall07_MeierMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall08_UmzugAusInAusBern(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveGesuch(new Testfall09_Abwesenheit(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_01(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_02(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_03(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_04(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_05(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_06(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_07(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_08(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_09(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				createAndSaveAsivGesuch(new Testfall_ASIV_10(gesuchsperiode, institutionStammdatenList, true), verfuegen, besitzer);
				responseString.append("Testfaelle 1-9 und ASIV-Testfaelle 1-8 erstellt");
			} else {
				responseString.append("Usage: /Nummer des Testfalls an die URL anhaengen. Bisher umgesetzt: 1-9. '/all' erstellt alle Testfaelle");
			}

		}
		return responseString;
	}


	public StringBuilder createAndSaveAsOnlineGesuch(@Nonnull String fallid,
													 boolean betreuungenBestaetigt,
													 boolean verfuegen, @Nonnull String username, @Nullable String gesuchsPeriodeId) {

		removeGesucheOfGS(username);
		Benutzer benutzer = benutzerService.findBenutzer(username).orElse(benutzerService.getCurrentBenutzer().orElse(null));
		return this.createAndSaveTestfaelle(fallid, 1, betreuungenBestaetigt, verfuegen, benutzer, gesuchsPeriodeId);

	}


	@Override
	@Nullable
	public Gesuch createAndSaveTestfaelle(String fallid,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen) {

		Gesuchsperiode gesuchsperiode = getNeuesteGesuchsperiode();
		List<InstitutionStammdaten> institutionStammdatenList = getInstitutionsstammdatenForTestfaelle();

		if (WaeltiDagmar.equals(fallid)) {
			return createAndSaveGesuch(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (FeutzIvonne.equals(fallid)) {
			return createAndSaveGesuch(new Testfall02_FeutzYvonne(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (PerreiraMarcia.equals(fallid)) {
			return createAndSaveGesuch(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (WaltherLaura.equals(fallid)) {
			return createAndSaveGesuch(new Testfall04_WaltherLaura(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (LuethiMeret.equals(fallid)) {
			return createAndSaveGesuch(new Testfall05_LuethiMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (BeckerNora.equals(fallid)) {
			return createAndSaveGesuch(new Testfall06_BeckerNora(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (MeierMeret.equals(fallid)) {
			return createAndSaveGesuch(new Testfall07_MeierMeret(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (UmzugAusInAusBern.equals(fallid)) {
			return createAndSaveGesuch(new Testfall08_UmzugAusInAusBern(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (Abwesenheit.equals(fallid)) {
			return createAndSaveGesuch(new Testfall09_Abwesenheit(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (UmzugVorGesuchsperiode.equals(fallid)) {
			return createAndSaveGesuch(new Testfall10_UmzugVorGesuchsperiode(gesuchsperiode, institutionStammdatenList, betreuungenBestaetigt), verfuegen, null);
		}
		if (ASIV1.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_01(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV2.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_02(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV3.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_03(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV4.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_04(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV5.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_05(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV6.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_06(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV7.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_07(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV8.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_08(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV9.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_09(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		if (ASIV10.equals(fallid)) {
			return createAndSaveAsivGesuch(new Testfall_ASIV_10(gesuchsperiode, institutionStammdatenList, true), verfuegen, null);
		}
		return null;
	}

	@Override
	public void removeGesucheOfGS(String username) {
		Benutzer benutzer = benutzerService.findBenutzer(username).orElse(null);
		Optional<Fall> existingFall = fallService.findFallByBesitzer(benutzer);
		if (existingFall.isPresent()) {
			fallService.removeFall(existingFall.get());
		}
	}

	@Override
	public Gesuch mutierenHeirat(@Nonnull Long fallNummer, @Nonnull String gesuchsperiodeId, @Nonnull LocalDate eingangsdatum, LocalDate aenderungPer, boolean verfuegen) {
		Validate.notNull(eingangsdatum);
		Validate.notNull(gesuchsperiodeId);
		Validate.notNull(fallNummer);
		Validate.notNull(aenderungPer);

		Familiensituation newFamsit = new Familiensituation();
		newFamsit.setFamilienstatus(EnumFamilienstatus.VERHEIRATET);
		newFamsit.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ZU_ZWEIT);
		newFamsit.setGemeinsameSteuererklaerung(true);
		newFamsit.setAenderungPer(aenderungPer);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(fallNummer, gesuchsperiodeId, eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			final FamiliensituationContainer familiensituationContainer = mutation.getFamiliensituationContainer();
			familiensituationContainer.setFamiliensituationErstgesuch(familiensituationContainer.getFamiliensituationJA());
			familiensituationContainer.setFamiliensituationJA(newFamsit);

			familiensituationService.saveFamiliensituation(mutation, familiensituationContainer, null);
			final GesuchstellerContainer gesuchsteller2 = gesuchstellerService
				.saveGesuchsteller(createGesuchstellerHeirat(mutation.getGesuchsteller1()), mutation, 2, false);

			mutation.setGesuchsteller2(gesuchsteller2);
			gesuchService.createGesuch(mutation);
			gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			return mutation;
		}

		return gesuchOptional.orElse(null);
	}

	@Override
	public Gesuch mutierenScheidung(@Nonnull Long fallNummer, @Nonnull String gesuchsperiodeId, @Nonnull LocalDate eingangsdatum, LocalDate aenderungPer, boolean verfuegen) {

		Validate.notNull(eingangsdatum);
		Validate.notNull(gesuchsperiodeId);
		Validate.notNull(fallNummer);
		Validate.notNull(aenderungPer);

		Familiensituation newFamsit = new Familiensituation();
		newFamsit.setFamilienstatus(EnumFamilienstatus.ALLEINERZIEHEND);
		newFamsit.setGesuchstellerKardinalitaet(EnumGesuchstellerKardinalitaet.ALLEINE);
		newFamsit.setAenderungPer(aenderungPer);

		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(fallNummer, gesuchsperiodeId, eingangsdatum);
		if (gesuchOptional.isPresent()) {
			final Gesuch mutation = gesuchOptional.get();
			final FamiliensituationContainer familiensituationContainer = mutation.getFamiliensituationContainer();
			familiensituationContainer.setFamiliensituationErstgesuch(familiensituationContainer.getFamiliensituationJA());
			familiensituationContainer.setFamiliensituationJA(newFamsit);
			familiensituationService.saveFamiliensituation(mutation, familiensituationContainer, null);
			gesuchService.createGesuch(mutation);
			gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			return mutation;
		}

		return gesuchOptional.orElse(null);
	}

	private Gesuchsperiode getNeuesteGesuchsperiode() {
		Collection<Gesuchsperiode> allActiveGesuchsperioden = gesuchsperiodeService.getAllActiveGesuchsperioden();
		return allActiveGesuchsperioden.iterator().next();
	}

	@Override
	public List<InstitutionStammdaten> getInstitutionsstammdatenForTestfaelle() {
		List<InstitutionStammdaten> institutionStammdatenList = new ArrayList<>();
		Optional<InstitutionStammdaten> optionalAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_WEISSENSTEIN_KITA);
		Optional<InstitutionStammdaten> optionalBruennen = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_BRUENNEN_KITA);
		Optional<InstitutionStammdaten> optionalTagiAaregg = institutionStammdatenService.findInstitutionStammdaten(AbstractTestfall.ID_INSTITUTION_STAMMDATEN_WEISSENSTEIN_TAGI);

		optionalAaregg.ifPresent(institutionStammdatenList::add);
		optionalBruennen.ifPresent(institutionStammdatenList::add);
		optionalTagiAaregg.ifPresent(institutionStammdatenList::add);
		return institutionStammdatenList;
	}

	/**
	 * Diese Methode ist etwas lang und haesslich aber das ist weil wir versuchen, den ganzen Prozess zu simulieren. D.h. wir speichern
	 * alle Objekte hintereinander, um die entsprechenden Services auszufuehren, damit die interne Logik auch durchgefuehrt wird.
	 * Nachteil ist, dass man vor allem die WizardSteps vorbereiten muss, damit der Prozess so laeuft wie auf dem web browser.
	 * <p>
	 * Am Ende der Methode und zur Sicherheit, updaten wir das Gesuch ein letztes Mal, um uns zu vergewissern, dass alle Daten gespeichert wurden.
	 * <p>
	 * Die Methode geht davon aus, dass die Daten nur eingetragen wurden und noch keine Betreuung bzw. Verfuegung bearbeitet ist.
	 * Aus diesem Grund, bleibt das Gesuch mit Status IN_BEARBEITUNG_JA
	 *
	 * @param fromTestfall testfall
	 * @param besitzer     wenn der besitzer gesetzt ist wird der fall diesem besitzer zugeordnet
	 */
	@Override
	public Gesuch createAndSaveGesuch(AbstractTestfall fromTestfall, boolean verfuegen, @Nullable Benutzer besitzer) {
		final List<Gesuch> gesuche = gesuchService.findGesuchByGSName(fromTestfall.getNachname(), fromTestfall.getVorname());
		if (!gesuche.isEmpty()) {
			fromTestfall.setFall(gesuche.iterator().next().getFall());
		}

		final Optional<Benutzer> currentBenutzer = benutzerService.getCurrentBenutzer();
		Optional<Fall> fallByBesitzer = fallService.findFallByBesitzer(besitzer); //fall kann schon existieren
		Fall fall;
		if (!fallByBesitzer.isPresent()) {
			if (currentBenutzer.isPresent()) {
				fall = fromTestfall.createFall(currentBenutzer.get());
			} else {
				fall = fromTestfall.createFall();
			}
		} else {
			fall = fallByBesitzer.get();
			fall.setNextNumberKind(1); //reset
		}
		if (besitzer != null) {
			fall.setBesitzer(besitzer);
		}
		final Fall persistedFall = fallService.saveFall(fall);
		fromTestfall.setFall(persistedFall); // dies wird gebraucht, weil fallService.saveFall ein merge macht.

		fromTestfall.createGesuch(LocalDate.of(2016, Month.FEBRUARY, 15));
		gesuchService.createGesuch(fromTestfall.getGesuch());
		Gesuch gesuch = fromTestfall.fillInGesuch();

		if (besitzer != null) {
			gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_GS);
			gesuch.setEingangsart(Eingangsart.ONLINE);
		} else {
			gesuch.setEingangsart(Eingangsart.PAPIER);
		}

		gesuchVerfuegenUndSpeichern(verfuegen, gesuch, false);

		return gesuch;

	}

	public Gesuch createAndSaveAsivGesuch(AbstractASIVTestfall fromTestfall, boolean verfuegen, Benutzer besitzer) {
		final Gesuch erstgesuch = createAndSaveGesuch(fromTestfall, true, besitzer);
		// Mutation
		Optional<Gesuch> gesuchOptional = gesuchService.antragMutieren(erstgesuch.getId(), LocalDate.of(2016, Month.MARCH, 1));
		if (gesuchOptional.isPresent()) {
			Gesuch mutation = fromTestfall.createMutation(gesuchOptional.get());
			gesuchService.createGesuch(mutation);
			familiensituationService.saveFamiliensituation(mutation, mutation.getFamiliensituationContainer(), null);
			gesuchVerfuegenUndSpeichern(verfuegen, mutation, true);
			setWizardStepOkayAndVerfuegbar(wizardStepService.findWizardStepFromGesuch(mutation.getId(), WizardStepName.GESUCHSTELLER).getId());
			setWizardStepOkayAndVerfuegbar(wizardStepService.findWizardStepFromGesuch(mutation.getId(), WizardStepName.FINANZIELLE_SITUATION).getId());
			setWizardStepOkayAndVerfuegbar(wizardStepService.findWizardStepFromGesuch(mutation.getId(), WizardStepName.EINKOMMENSVERSCHLECHTERUNG).getId());
			return mutation;
		}
		return null;
	}

	private void setWizardStepOkayAndVerfuegbar(String wizardStepId) {
		Optional<WizardStep> wizardStep = wizardStepService.findWizardStep(wizardStepId);
		if (wizardStep.isPresent()) {
			wizardStep.get().setVerfuegbar(true);
			wizardStep.get().setWizardStepStatus(WizardStepStatus.OK);
		}
	}

	@Override
	public void gesuchVerfuegenUndSpeichern(boolean verfuegen, Gesuch gesuch, boolean mutation) {
		final List<WizardStep> wizardStepsFromGesuch = wizardStepService.findWizardStepsFromGesuch(gesuch.getId());

		if (!mutation) {
			saveFamiliensituation(gesuch, wizardStepsFromGesuch);
			saveGesuchsteller(gesuch, wizardStepsFromGesuch);
			saveKinder(gesuch, wizardStepsFromGesuch);
			saveBetreuungen(gesuch, wizardStepsFromGesuch);
			saveErwerbspensen(gesuch, wizardStepsFromGesuch);
			saveFinanzielleSituation(gesuch, wizardStepsFromGesuch);
			saveEinkommensverschlechterung(gesuch, wizardStepsFromGesuch);

			gesuchService.updateGesuch(gesuch, false); // just save all other objects before updating dokumente and verfuegungen
			saveDokumente(wizardStepsFromGesuch);
			saveVerfuegungen(gesuch, wizardStepsFromGesuch);
		}

		if (verfuegen) {
			FreigabeCopyUtil.copyForFreigabe(gesuch);
			verfuegungService.calculateVerfuegung(gesuch);
			gesuch.getKindContainers().stream().forEach(kindContainer -> {
				kindContainer.getBetreuungen().stream().forEach(betreuung -> {
					verfuegungService.persistVerfuegung(betreuung.getVerfuegung(), betreuung.getId(), Betreuungsstatus.VERFUEGT);
				});
			});
		}
		wizardStepService.updateSteps(gesuch.getId(), null, null, WizardStepName.VERFUEGEN);
	}

	private void saveVerfuegungen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (!gesuch.getStatus().isAnyStatusOfVerfuegt()) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.VERFUEGEN, WizardStepStatus.WARTEN);
		} else {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.VERFUEGEN, WizardStepStatus.OK);
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.VERFUEGEN);
	}

	private void saveDokumente(List<WizardStep> wizardStepsFromGesuch) {
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.DOKUMENTE, WizardStepStatus.IN_BEARBEITUNG);
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.DOKUMENTE);
	}

	private void saveEinkommensverschlechterung(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getEinkommensverschlechterungInfoContainer() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.IN_BEARBEITUNG);
			einkommensverschlechterungInfoService.createEinkommensverschlechterungInfo(gesuch.getEinkommensverschlechterungInfoContainer());
		}
		if (gesuch.getGesuchsteller1() != null && gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer() != null) {
			einkommensverschlechterungService.saveEinkommensverschlechterungContainer(gesuch.getGesuchsteller1().getEinkommensverschlechterungContainer(), gesuch.getId());
		}
		if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer() != null) {
			einkommensverschlechterungService.saveEinkommensverschlechterungContainer(gesuch.getGesuchsteller2().getEinkommensverschlechterungContainer(), gesuch.getId());
		}
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG, WizardStepStatus.OK);
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.EINKOMMENSVERSCHLECHTERUNG);
	}

	private void saveFinanzielleSituation(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getGesuchsteller1() != null && gesuch.getGesuchsteller1().getFinanzielleSituationContainer() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.IN_BEARBEITUNG);
			finanzielleSituationService.saveFinanzielleSituation(gesuch.getGesuchsteller1().getFinanzielleSituationContainer(), gesuch.getId());
		}
		if (gesuch.getGesuchsteller2() != null && gesuch.getGesuchsteller2().getFinanzielleSituationContainer() != null) {
			finanzielleSituationService.saveFinanzielleSituation(gesuch.getGesuchsteller2().getFinanzielleSituationContainer(), gesuch.getId());
		}
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FINANZIELLE_SITUATION, WizardStepStatus.OK);
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.FINANZIELLE_SITUATION);
	}

	private void saveErwerbspensen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.ERWERBSPENSUM, WizardStepStatus.IN_BEARBEITUNG);
			gesuch.getGesuchsteller1().getErwerbspensenContainers()
				.forEach(erwerbspensumContainer -> erwerbspensumService.saveErwerbspensum(erwerbspensumContainer, gesuch));
		}
		if (gesuch.getGesuchsteller2() != null) {
			gesuch.getGesuchsteller2().getErwerbspensenContainers()
				.forEach(erwerbspensumContainer -> erwerbspensumService.saveErwerbspensum(erwerbspensumContainer, gesuch));
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.ERWERBSPENSUM);
	}

	private void saveBetreuungen(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.BETREUUNG, WizardStepStatus.IN_BEARBEITUNG);
		gesuch.getKindContainers().forEach(kindContainer
			-> kindContainer.getBetreuungen().forEach(betreuung -> betreuungService.saveBetreuung(betreuung, false)));
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.BETREUUNG);
	}

	private void saveKinder(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.KINDER, WizardStepStatus.IN_BEARBEITUNG);
		gesuch.getKindContainers().forEach(kindContainer -> kindService.saveKind(kindContainer));
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.KINDER);
	}

	private void saveGesuchsteller(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.GESUCHSTELLER, WizardStepStatus.IN_BEARBEITUNG);
			gesuchstellerService.saveGesuchsteller(gesuch.getGesuchsteller1(), gesuch, 1, false);
		}
		if (gesuch.getGesuchsteller2() != null) {
			gesuchstellerService.saveGesuchsteller(gesuch.getGesuchsteller2(), gesuch, 2, false);
		}
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.GESUCHSTELLER);
		// Umzug wird by default OK und verfuegbar, da es nicht notwendig ist, einen Umzug einzutragen
		setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.UMZUG);
		setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.UMZUG, WizardStepStatus.OK);
	}

	private void saveFamiliensituation(Gesuch gesuch, List<WizardStep> wizardStepsFromGesuch) {
		if (gesuch.extractFamiliensituation() != null) {
			setWizardStepInStatus(wizardStepsFromGesuch, WizardStepName.FAMILIENSITUATION, WizardStepStatus.IN_BEARBEITUNG);
			familiensituationService.saveFamiliensituation(gesuch, gesuch.getFamiliensituationContainer(), null);
			setWizardStepVerfuegbar(wizardStepsFromGesuch, WizardStepName.FAMILIENSITUATION);
		}
	}

	private void setWizardStepInStatus(List<WizardStep> wizardSteps, WizardStepName stepName, WizardStepStatus status) {
		final WizardStep wizardStep = getWizardStepByName(wizardSteps, stepName);
		if (wizardStep != null) {
			wizardStep.setWizardStepStatus(status);
			wizardStepService.saveWizardStep(wizardStep);
		}
	}

	private void setWizardStepVerfuegbar(List<WizardStep> wizardSteps, WizardStepName stepName) {
		final WizardStep wizardStep = getWizardStepByName(wizardSteps, stepName);
		if (wizardStep != null) {
			wizardStep.setVerfuegbar(true);
			wizardStepService.saveWizardStep(wizardStep);
		}
	}

	private WizardStep getWizardStepByName(List<WizardStep> wizardSteps, WizardStepName stepName) {
		for (WizardStep wizardStep : wizardSteps) {
			if (stepName.equals(wizardStep.getWizardStepName())) {
				return wizardStep;
			}
		}
		return null;
	}

	private GesuchstellerContainer createGesuchstellerHeirat(GesuchstellerContainer gesuchsteller1) {
		GesuchstellerContainer gesuchsteller2 = new GesuchstellerContainer();
		Gesuchsteller gesuchsteller = new Gesuchsteller();
		gesuchsteller.setGeburtsdatum(LocalDate.of(1984, 12, 12));
		gesuchsteller.setVorname("Tim");
		gesuchsteller.setNachname(gesuchsteller1.extractNachname());
		gesuchsteller.setGeschlecht(Geschlecht.MAENNLICH);
		gesuchsteller.setMail("tim.tester@example.com");
		gesuchsteller.setMobile("076 309 30 58");
		gesuchsteller.setTelefon("031 378 24 24");
		gesuchsteller.setEwkPersonId("0761234567897");

		gesuchsteller2.setGesuchstellerJA(gesuchsteller);
		gesuchsteller2.addAdresse(createGesuchstellerAdresseHeirat(gesuchsteller2));

		final ErwerbspensumContainer erwerbspensumContainer = createErwerbspensumContainer();
		erwerbspensumContainer.setGesuchsteller(gesuchsteller2);
		gesuchsteller2.getErwerbspensenContainers().add(erwerbspensumContainer);

		return gesuchsteller2;
	}

	private GesuchstellerAdresseContainer createGesuchstellerAdresseHeirat(GesuchstellerContainer gsCont) {
		GesuchstellerAdresseContainer gsAdresseContainer = new GesuchstellerAdresseContainer();

		GesuchstellerAdresse gesuchstellerAdresse = new GesuchstellerAdresse();
		gesuchstellerAdresse.setStrasse("Nussbaumstrasse");
		gesuchstellerAdresse.setHausnummer("21");
		gesuchstellerAdresse.setZusatzzeile("c/o Uwe Untermieter");
		gesuchstellerAdresse.setPlz("3014");
		gesuchstellerAdresse.setOrt("Bern");
		gesuchstellerAdresse.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		gesuchstellerAdresse.setAdresseTyp(AdresseTyp.WOHNADRESSE);

		gsAdresseContainer.setGesuchstellerContainer(gsCont);
		gsAdresseContainer.setGesuchstellerAdresseJA(gesuchstellerAdresse);

		return gsAdresseContainer;
	}

	private ErwerbspensumContainer createErwerbspensumContainer() {
		ErwerbspensumContainer epCont = new ErwerbspensumContainer();
		epCont.setErwerbspensumGS(createErwerbspensumData());
		Erwerbspensum epKorrigiertJA = createErwerbspensumData();
		epKorrigiertJA.setTaetigkeit(Taetigkeit.ANGESTELLT);
		epCont.setErwerbspensumJA(epKorrigiertJA);
		return epCont;
	}

	private Erwerbspensum createErwerbspensumData() {
		Erwerbspensum ep = new Erwerbspensum();
		ep.setTaetigkeit(Taetigkeit.ANGESTELLT);
		ep.setPensum(80);
		ep.setZuschlagZuErwerbspensum(true);
		ep.setZuschlagsgrund(Zuschlagsgrund.LANGER_ARBWEITSWEG);
		ep.setZuschlagsprozent(10);
		ep.setGueltigkeit(new DateRange(Constants.START_OF_TIME, Constants.END_OF_TIME));
		return ep;
	}
}
