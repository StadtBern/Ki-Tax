package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.testfaelle.*;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.FreigabeCopyUtil;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer erstellen und mutieren von Schulungsdaten
 */
@SuppressWarnings(value = {"DLS_DEAD_LOCAL_STORE", "DM_CONVERT_CASE", "EI_EXPOSE_REP", "ConstantNamingConvention", "NonBooleanMethodNameMayNotStartWithQuestion", "SpringAutowiredFieldsWarningInspection"})
@Stateless
@Local(SchulungService.class)
@RolesAllowed({SUPER_ADMIN, ADMIN})
public class SchulungServiceBean extends AbstractBaseService implements SchulungService {

	private static final Random RANDOM = new Random();

	private static final String TRAEGERSCHAFT_FISCH_ID = "11111111-1111-1111-1111-111111111111";

	private static final String INSTITUTION_FORELLE_ID = "22222222-1111-1111-1111-111111111111";
	private static final String INSTITUTION_HECHT_ID = "22222222-1111-1111-1111-222222222222";

	private static final String KITA_FORELLE_ID = "33333333-1111-1111-1111-111111111111";
	private static final String TAGESELTERN_FORELLE_ID = "33333333-1111-1111-2222-111111111111";
	private static final String KITA_HECHT_ID = "33333333-1111-1111-1111-222222222222";

	private static final String GESUCH_ID = "44444444-1111-1111-1111-1111111111XX";

	private static final String BENUTZER_FISCH_NAME = "Fisch";
	private static final String BENUTZER_FISCH_VORNAME = "Fritz";
	private static final String BENUTZER_FORELLE_NAME = "Forelle";
	private static final String BENUTZER_FORELLE_VORNAME = "Franz";

	private static final String GESUCHSTELLER_VORNAME = "Sandra";
	private static final String[] GESUCHSTELLER_LIST = new String[]{"Huber",
	                                                         "Müller",
	                                                         "Gerber",
	                                                         "Antonelli",
	                                                         "Schüpbach",
	                                                         "Kovac",
	                                                         "Ackermann",
	                                                         "Keller",
	                                                         "Wyttenbach",
	                                                         "Rindlisbacher",
	                                                         "Dubois",
	                                                         "Menet",
	                                                         "Burri",
	                                                         "Schmid",
	                                                         "Rodriguez"};


	@Inject
	private GesuchService gesuchService;

	@Inject
	private MandantService mandantService;

	@Inject
	private TraegerschaftService traegerschaftService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private InstitutionStammdatenService institutionStammdatenService;

	@Inject
	private BenutzerService benutzerService;

	@Inject
	private TestfaelleService testfaelleService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private Persistence<AuthorisierterBenutzer> persistence;

	private static final Logger LOG = LoggerFactory.getLogger(SchulungServiceBean.class);

	@Override
	public void resetSchulungsdaten() {
		LOG.info("Lösche Schulungsdaten... ");
		deleteSchulungsdaten();
		LOG.info("Erstelle Schulungsdaten...");
		createSchulungsdaten();
		LOG.info("... beendet");
	}

	@Override
	public void deleteSchulungsdaten() {

		removeFaelleForSuche();

		for (String s : GESUCHSTELLER_LIST) {
			removeGesucheFallAndBenutzer(s);
		}

		removeBenutzer(getUsername(BENUTZER_FISCH_NAME, BENUTZER_FISCH_VORNAME));
		removeBenutzer(getUsername(BENUTZER_FORELLE_NAME, BENUTZER_FORELLE_VORNAME));

		if (institutionStammdatenService.findInstitutionStammdaten(KITA_FORELLE_ID).isPresent()) {
			institutionStammdatenService.removeInstitutionStammdaten(KITA_FORELLE_ID);
		}
		if (institutionStammdatenService.findInstitutionStammdaten(TAGESELTERN_FORELLE_ID).isPresent()) {
			institutionStammdatenService.removeInstitutionStammdaten(TAGESELTERN_FORELLE_ID);
		}
		if (institutionStammdatenService.findInstitutionStammdaten(KITA_HECHT_ID).isPresent()) {
			institutionStammdatenService.removeInstitutionStammdaten(KITA_HECHT_ID);
		}

		if (institutionService.findInstitution(INSTITUTION_FORELLE_ID).isPresent()) {
			institutionService.deleteInstitution(INSTITUTION_FORELLE_ID);
		}
		if (institutionService.findInstitution(INSTITUTION_HECHT_ID).isPresent()) {
			institutionService.deleteInstitution(INSTITUTION_HECHT_ID);
		}

		if (traegerschaftService.findTraegerschaft(TRAEGERSCHAFT_FISCH_ID).isPresent()) {
			traegerschaftService.removeTraegerschaft(TRAEGERSCHAFT_FISCH_ID);
		}
	}

	@Override
	public void createSchulungsdaten() {
		Traegerschaft traegerschaftFisch = createTraegerschaft(TRAEGERSCHAFT_FISCH_ID, "Fisch");
		Institution institutionForelle = createtInstitution(INSTITUTION_FORELLE_ID, "Forelle", traegerschaftFisch);
		Institution institutionHecht = createtInstitution(INSTITUTION_HECHT_ID, "Hecht", traegerschaftFisch);

		InstitutionStammdaten kitaForelle = createInstitutionStammdaten(KITA_FORELLE_ID, institutionForelle, BetreuungsangebotTyp.KITA);
		InstitutionStammdaten tageselternForelle = createInstitutionStammdaten(TAGESELTERN_FORELLE_ID, institutionForelle, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
		InstitutionStammdaten kitaHecht = createInstitutionStammdaten(KITA_HECHT_ID, institutionHecht, BetreuungsangebotTyp.KITA);
		List<InstitutionStammdaten> institutionenForSchulung = new ArrayList<>();
		institutionenForSchulung.add(kitaForelle);
		institutionenForSchulung.add(tageselternForelle);
		institutionenForSchulung.add(kitaHecht);

		createBenutzer(BENUTZER_FISCH_NAME, BENUTZER_FISCH_VORNAME, traegerschaftFisch, null);
		createBenutzer(BENUTZER_FORELLE_NAME, BENUTZER_FORELLE_VORNAME, null, institutionForelle);

		for (String s : GESUCHSTELLER_LIST) {
			createGesuchsteller(s);
		}

		createFaelleForSuche(institutionenForSchulung);
	}

	@Override
	@PermitAll
	public String[] getSchulungBenutzer() {
		return (String[]) ArrayUtils.clone(GESUCHSTELLER_LIST);
	}

	@SuppressWarnings("SameParameterValue")
	private Traegerschaft createTraegerschaft(String id, String name) {
		Traegerschaft traegerschaft = new Traegerschaft();
		traegerschaft.setId(id);
		traegerschaft.setName(name);
		traegerschaft.setMail("hallo@dvbern.ch");
		return traegerschaftService.saveTraegerschaft(traegerschaft);
	}

	private Institution createtInstitution(String id, String name, Traegerschaft traegerschaft) {
		Mandant mandant = mandantService.getFirst();
		Institution institution = new Institution();
		institution.setId(id);
		institution.setName(name);
		institution.setMandant(mandant);
		institution.setTraegerschaft(traegerschaft);
		institution.setMail("hallo@dvbern.ch");
		return institutionService.createInstitution(institution);
	}

	@SuppressWarnings("MagicNumber")
	private InstitutionStammdaten createInstitutionStammdaten(String id, Institution institution, BetreuungsangebotTyp betreuungsangebotTyp) {
		InstitutionStammdaten instStammdaten = new InstitutionStammdaten();
		instStammdaten.setId(id);
		instStammdaten.setIban(new IBAN("CH39 0900 0000 3066 3817 2"));
		instStammdaten.setOeffnungsstunden(BigDecimal.valueOf(11.50));
		instStammdaten.setOeffnungstage(BigDecimal.valueOf(240));
		instStammdaten.setGueltigkeit(Constants.DEFAULT_GUELTIGKEIT);
		instStammdaten.setBetreuungsangebotTyp(betreuungsangebotTyp);
		instStammdaten.setAdresse(createAdresse(id));
		instStammdaten.setInstitution(institution);
		return institutionStammdatenService.saveInstitutionStammdaten(instStammdaten);
	}

	private Adresse createAdresse(String id) {
		Adresse adresse = new Adresse();
		adresse.setId(id);
		adresse.setStrasse("Nussbaumstrasse");
		adresse.setHausnummer("21");
		adresse.setPlz("3014");
		adresse.setOrt("Bern");
		adresse.setGueltigkeit(new DateRange(LocalDate.now(), Constants.END_OF_TIME));
		return adresse;
	}

	private Benutzer createGesuchsteller(String name) {
		Mandant mandant = mandantService.getFirst();
		Benutzer benutzer = new Benutzer();
		benutzer.setVorname(GESUCHSTELLER_VORNAME);
		benutzer.setNachname(name);
		benutzer.setRole(UserRole.GESUCHSTELLER);
		benutzer.setEmail(getUsername(name) + "@mailinator.com");
		benutzer.setUsername(getUsername(name));
		benutzer.setMandant(mandant);
		return benutzerService.saveBenutzer(benutzer);
	}

	private Benutzer createBenutzer(String name, String vorname, Traegerschaft traegerschaft, Institution institution) {
		Mandant mandant = mandantService.getFirst();
		Benutzer benutzer = new Benutzer();
		benutzer.setVorname(vorname);
		benutzer.setNachname(name);
		if (traegerschaft != null) {
			benutzer.setRole(UserRole.SACHBEARBEITER_TRAEGERSCHAFT);
			benutzer.setTraegerschaft(traegerschaft);
		}
		if (institution != null) {
			benutzer.setRole(UserRole.SACHBEARBEITER_INSTITUTION);
			benutzer.setInstitution(institution);
		}
		benutzer.setEmail(getUsername(name) + "@mailinator.com");
		benutzer.setUsername(getUsername(name, vorname));
		benutzer.setMandant(mandant);
		return benutzerService.saveBenutzer(benutzer);
	}

	private String getUsername(String name) {
		return getUsername(name, GESUCHSTELLER_VORNAME);
	}

	@SuppressWarnings(value = {"DM_CONVERT_CASE"})
	private String getUsername(String name, String vorname) {
		String username = vorname.toLowerCase(Locale.GERMAN) + "." + name.toLowerCase(Locale.GERMAN);
		username = username.replaceAll("ü", "ue");
		return username;
	}

	private void removeGesucheFallAndBenutzer(String nachname) {
		testfaelleService.removeGesucheOfGS(getUsername(nachname));
		removeBenutzer(getUsername(nachname));
	}

	private void createFaelleForSuche(List<InstitutionStammdaten> institutionenForSchulung) {
		Gesuchsperiode gesuchsperiode = gesuchsperiodeService.getAllActiveGesuchsperioden().iterator().next();
		List<InstitutionStammdaten> institutionenForTestfall = testfaelleService.getInstitutionsstammdatenForTestfaelle();

		createFall(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionenForTestfall), "01", institutionenForSchulung);
		createFall(new Testfall02_FeutzYvonne(gesuchsperiode, institutionenForTestfall), "02", institutionenForSchulung);
		createFall(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionenForTestfall), "03", institutionenForSchulung);
		createFall(new Testfall04_WaltherLaura(gesuchsperiode, institutionenForTestfall), "04", institutionenForSchulung);
		createFall(new Testfall05_LuethiMeret(gesuchsperiode, institutionenForTestfall), "05", institutionenForSchulung);
		createFall(new Testfall06_BeckerNora(gesuchsperiode, institutionenForTestfall), "06", institutionenForSchulung);
		createFall(new Testfall07_MeierMeret(gesuchsperiode, institutionenForTestfall), "07", institutionenForSchulung);

		createFall(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionenForTestfall), "08", "Gerber", "Milena", institutionenForSchulung);
		createFall(new Testfall02_FeutzYvonne(gesuchsperiode, institutionenForTestfall), "09", "Bernasconi", "Claudia", institutionenForSchulung);
		createFall(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionenForTestfall), "10", "Odermatt", "Yasmin", institutionenForSchulung);
		createFall(new Testfall04_WaltherLaura(gesuchsperiode, institutionenForTestfall), "11", "Hefti", "Sarah", institutionenForSchulung);
		createFall(new Testfall05_LuethiMeret(gesuchsperiode, institutionenForTestfall), "12", "Schmid", "Natalie", institutionenForSchulung);
		createFall(new Testfall06_BeckerNora(gesuchsperiode, institutionenForTestfall), "13", "Kälin", "Judith", institutionenForSchulung);
		createFall(new Testfall07_MeierMeret(gesuchsperiode, institutionenForTestfall), "14", "Werlen", "Franziska", institutionenForSchulung);

		createFall(new Testfall01_WaeltiDagmar(gesuchsperiode, institutionenForTestfall), "15", "Iten", "Joy", institutionenForSchulung);
		createFall(new Testfall02_FeutzYvonne(gesuchsperiode, institutionenForTestfall), "16", "Keller", "Birgit", institutionenForSchulung);
		createFall(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionenForTestfall), "17", "Hofer", "Melanie", institutionenForSchulung);
		createFall(new Testfall04_WaltherLaura(gesuchsperiode, institutionenForTestfall), "18", "Steiner", "Stefanie", institutionenForSchulung);
		createFall(new Testfall05_LuethiMeret(gesuchsperiode, institutionenForTestfall), "19", "Widmer", "Ursula", institutionenForSchulung);
		createFall(new Testfall06_BeckerNora(gesuchsperiode, institutionenForTestfall), "20", "Graf", "Anna", institutionenForSchulung);
		createFall(new Testfall07_MeierMeret(gesuchsperiode, institutionenForTestfall), "21", "Zimmermann", "Katrin", institutionenForSchulung);

		createFall(new Testfall02_FeutzYvonne(gesuchsperiode, institutionenForTestfall), "22", "Hofstetter", "Anneliese", institutionenForSchulung);
		createFall(new Testfall03_PerreiraMarcia(gesuchsperiode, institutionenForTestfall), "23", "Arnold", "Madeleine", institutionenForSchulung);
		createFall(new Testfall04_WaltherLaura(gesuchsperiode, institutionenForTestfall), "24", "Schneebeli", "Janine", institutionenForSchulung);
		createFall(new Testfall05_LuethiMeret(gesuchsperiode, institutionenForTestfall), "25", "Weber", "Marianne", institutionenForSchulung);

	}

	private void createFall(AbstractTestfall testfall, String id, String nachname, String vorname, List<InstitutionStammdaten> institutionenForSchulung) {
		testfall.setFixId(GESUCH_ID.replaceAll("XX", id));
		Gesuch gesuch = createFallForSuche(testfall, nachname, vorname, institutionenForSchulung);
		FreigabeCopyUtil.copyForFreigabe(gesuch);
		gesuchService.updateGesuch(gesuch, false);
	}

	private void createFall(AbstractTestfall testfall, String id, List<InstitutionStammdaten> institutionenForSchulung) {
		testfall.setFixId(GESUCH_ID.replaceAll("XX", id));
		Gesuch gesuch = createFallForSuche(testfall, institutionenForSchulung);
		FreigabeCopyUtil.copyForFreigabe(gesuch);
		gesuchService.updateGesuch(gesuch, false);
	}

	@SuppressWarnings("ConstantConditions")
	private Gesuch createFallForSuche(AbstractTestfall testfall, String nachname, String vorname, List<InstitutionStammdaten> institutionenForSchulung ) {
		Gesuch gesuch = createFallForSuche(testfall, institutionenForSchulung);
		gesuch.getGesuchsteller1().getGesuchstellerJA().setNachname(nachname);
		gesuch.getGesuchsteller1().getGesuchstellerJA().setVorname(vorname);
		return gesuchService.updateGesuch(gesuch, false);
	}

	private Gesuch createFallForSuche(AbstractTestfall testfall, List<InstitutionStammdaten> institutionenForSchulung ) {
		@SuppressWarnings("DuplicateBooleanBranch")
		boolean verfuegen = RANDOM.nextBoolean() && RANDOM.nextBoolean(); // Damit VERFUEGT nicht zu haeufig...
		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, verfuegen, null);
		gesuch.setEingangsdatum(LocalDate.now());

		// Gesuch entweder online oder papier
		boolean online = RANDOM.nextBoolean();
		Eingangsart eingangsart = online ? Eingangsart.ONLINE : Eingangsart.PAPIER;
		gesuch.setEingangsart(eingangsart);

		// Institutionen anpassen
		List<Betreuung> betreuungList = gesuch.extractAllBetreuungen();
		for (Betreuung betreuung : betreuungList) {
			InstitutionStammdaten institutionStammdaten = institutionenForSchulung.get(RANDOM.nextInt(institutionenForSchulung.size()));
			betreuung.setInstitutionStammdaten(institutionStammdaten);
			if (verfuegen) {
				betreuung.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
			} else {
				// Etwas haeufiger WARTEN als BESTAETIGT/ABGELEHNT
				Betreuungsstatus[] statussis = new Betreuungsstatus[]{Betreuungsstatus.WARTEN, Betreuungsstatus.WARTEN,Betreuungsstatus.WARTEN,Betreuungsstatus.BESTAETIGT, Betreuungsstatus.ABGEWIESEN};
				Betreuungsstatus status = Collections.unmodifiableList(Arrays.asList(statussis)).get(RANDOM.nextInt(statussis.length));
				betreuung.setBetreuungsstatus(status);
				if (Betreuungsstatus.ABGEWIESEN.equals(status)) {
					betreuung.setGrundAblehnung("Abgelehnt");
				}
			}
		}
		return gesuchService.updateGesuch(gesuch, false);
	}

	private void removeBenutzer(String username) {
		Collection<AuthorisierterBenutzer> entitiesByAttribute = criteriaQueryHelper.getEntitiesByAttribute(AuthorisierterBenutzer.class, username, AuthorisierterBenutzer_.username);
		for (AuthorisierterBenutzer authorisierterBenutzer : entitiesByAttribute) {
			persistence.remove(authorisierterBenutzer);
		}
		if (benutzerService.findBenutzer(username).isPresent()) {
			benutzerService.removeBenutzer(username);
		}
	}

	@SuppressWarnings("MagicNumber")
	private void removeFaelleForSuche() {
		int anzahlFaelle = 25;
		for (int i = 1; i <= anzahlFaelle; i++) {
			String id = GESUCH_ID.replaceAll("XX", StringUtils.leftPad("" + i, 2, "0"));
			Optional<Gesuch> gesuchOptional = gesuchService.findGesuch(id);
			gesuchOptional.ifPresent(gesuch -> gesuchService.removeGesuch(id));
			persistence.getEntityManager().flush();
		}
	}
}
