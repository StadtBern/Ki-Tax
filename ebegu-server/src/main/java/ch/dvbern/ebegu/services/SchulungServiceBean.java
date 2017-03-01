package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
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
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
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
	private static final String KITA_BRUENNEN_STAMMDATEN_ID = "9a0eb656-b6b7-4613-8f55-4e0e4720455e";

	private static final String GESUCH_ID = "44444444-1111-1111-1111-1111111111XX";

	private static final String BENUTZER_FISCH_USERNAME = "sch20";
	private static final String BENUTZER_FORELLE_USERNAME = "sch21";
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
	                                                         "Rodriguez",
															 "Nussbaum"};


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
	private FallService fallService;

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

		for (int i = 0; i < GESUCHSTELLER_LIST.length; i++) {
			removeGesucheFallAndBenutzer(i + 1);
		}

		// Bevor die Testinstitutionen geloescht werden, muss sichergestellt sein, dass diese von keinen "normalen"
		// Testfaellen verwendet werden -> auf Kita Brünnen umbiegen
		Optional<InstitutionStammdaten> institutionStammdatenOptional = institutionStammdatenService.findInstitutionStammdaten(KITA_BRUENNEN_STAMMDATEN_ID);
		if (institutionStammdatenOptional.isPresent()) {
			InstitutionStammdaten institutionStammdaten = institutionStammdatenOptional.get();
			assertInstitutionNotUsedInNormalenGesuchen(KITA_FORELLE_ID, institutionStammdaten);
			assertInstitutionNotUsedInNormalenGesuchen(TAGESELTERN_FORELLE_ID, institutionStammdaten);
			assertInstitutionNotUsedInNormalenGesuchen(KITA_HECHT_ID, institutionStammdaten);
		}

		removeBenutzer(BENUTZER_FISCH_USERNAME);
		removeBenutzer(BENUTZER_FORELLE_USERNAME);

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
		List<InstitutionStammdaten> institutionenForSchulung = new LinkedList<>();
		institutionenForSchulung.add(kitaForelle);
		institutionenForSchulung.add(tageselternForelle);
		institutionenForSchulung.add(kitaHecht);

		createBenutzer(BENUTZER_FISCH_NAME, BENUTZER_FISCH_VORNAME, traegerschaftFisch, null, BENUTZER_FISCH_USERNAME);
		createBenutzer(BENUTZER_FORELLE_NAME, BENUTZER_FORELLE_VORNAME, null, institutionForelle, BENUTZER_FORELLE_USERNAME);

		for (int i = 0; i < GESUCHSTELLER_LIST.length; i++) {
			createGesuchsteller(GESUCHSTELLER_LIST[i], getUsername(i + 1));
		}

		try {
			createFaelleForSuche(institutionenForSchulung);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
			throw new EbeguRuntimeException("createSchulungsdaten", "Could not create Schulungsdaten", e);
		}
	}

	@Override
	@PermitAll
	public String[] getSchulungBenutzer() {
		String[] clone = (String[]) ArrayUtils.clone(GESUCHSTELLER_LIST);
		List<String> list = Arrays.asList(clone);
		Collections.sort(list);
		return list.toArray(new String[list.size()]);
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

	private Benutzer createGesuchsteller(String name, String username) {
		Mandant mandant = mandantService.getFirst();
		Benutzer benutzer = new Benutzer();
		benutzer.setVorname(GESUCHSTELLER_VORNAME);
		benutzer.setNachname(name);
		benutzer.setRole(UserRole.GESUCHSTELLER);
		benutzer.setEmail(GESUCHSTELLER_VORNAME.toLowerCase(Locale.GERMAN) + "." + name.toLowerCase(Locale.GERMAN) + "@mailinator.com");
		benutzer.setUsername(username);
		benutzer.setMandant(mandant);
		return benutzerService.saveBenutzer(benutzer);
	}

	private Benutzer createBenutzer(String name, String vorname, Traegerschaft traegerschaft, Institution institution, String username) {
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
		benutzer.setEmail(vorname.toLowerCase(Locale.GERMAN) + "." + name.toLowerCase(Locale.GERMAN) + "@mailinator.com");
		benutzer.setUsername(username);
		benutzer.setMandant(mandant);
		return benutzerService.saveBenutzer(benutzer);
	}

	@SuppressWarnings(value = {"DM_CONVERT_CASE"})
	private String getUsername(int position) {
		return "sch" + String.format("%02d", position);
	}

	private void removeGesucheFallAndBenutzer(int position) {
		testfaelleService.removeGesucheOfGS(getUsername(position));
		removeBenutzer(getUsername(position));
	}

	private void createFaelleForSuche(List<InstitutionStammdaten> institutionenForSchulung) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Gesuchsperiode gesuchsperiode = gesuchsperiodeService.getAllActiveGesuchsperioden().iterator().next();
		List<InstitutionStammdaten> institutionenForTestfall = testfaelleService.getInstitutionsstammdatenForTestfaelle();

		createFall(Testfall01_WaeltiDagmar.class, gesuchsperiode, institutionenForTestfall, "01", null, null, institutionenForSchulung, true);
		createFall(Testfall02_FeutzYvonne.class, gesuchsperiode, institutionenForTestfall, "02",null, null, institutionenForSchulung);
		createFall(Testfall03_PerreiraMarcia.class, gesuchsperiode, institutionenForTestfall, "03",null, null, institutionenForSchulung);
		createFall(Testfall04_WaltherLaura.class, gesuchsperiode, institutionenForTestfall, "04", null, null, institutionenForSchulung);
		createFall(Testfall05_LuethiMeret.class, gesuchsperiode, institutionenForTestfall, "05", null, null, institutionenForSchulung);
		createFall(Testfall06_BeckerNora.class, gesuchsperiode, institutionenForTestfall, "06", null, null, institutionenForSchulung);
		createFall(Testfall07_MeierMeret.class, gesuchsperiode, institutionenForTestfall, "07", null, null, institutionenForSchulung);

		createFall(Testfall01_WaeltiDagmar.class, gesuchsperiode, institutionenForTestfall, "08", "Gerber", "Milena", institutionenForSchulung);
		createFall(Testfall02_FeutzYvonne.class, gesuchsperiode, institutionenForTestfall, "09", "Bernasconi", "Claudia", institutionenForSchulung);
		createFall(Testfall03_PerreiraMarcia.class, gesuchsperiode, institutionenForTestfall, "10", "Odermatt", "Yasmin", institutionenForSchulung);
		createFall(Testfall04_WaltherLaura.class, gesuchsperiode, institutionenForTestfall, "11", "Hefti", "Sarah", institutionenForSchulung);
		createFall(Testfall05_LuethiMeret.class, gesuchsperiode, institutionenForTestfall, "12", "Schmid", "Natalie", institutionenForSchulung);
		createFall(Testfall06_BeckerNora.class, gesuchsperiode, institutionenForTestfall, "13", "Kälin", "Judith", institutionenForSchulung);
		createFall(Testfall07_MeierMeret.class, gesuchsperiode, institutionenForTestfall, "14", "Werlen", "Franziska", institutionenForSchulung);

		createFall(Testfall01_WaeltiDagmar.class, gesuchsperiode, institutionenForTestfall, "15", "Iten", "Joy", institutionenForSchulung);
		createFall(Testfall02_FeutzYvonne.class, gesuchsperiode, institutionenForTestfall, "16", "Keller", "Birgit", institutionenForSchulung);
		createFall(Testfall03_PerreiraMarcia.class, gesuchsperiode, institutionenForTestfall, "17", "Hofer", "Melanie", institutionenForSchulung);
		createFall(Testfall04_WaltherLaura.class, gesuchsperiode, institutionenForTestfall, "18", "Steiner", "Stefanie", institutionenForSchulung);
		createFall(Testfall05_LuethiMeret.class, gesuchsperiode, institutionenForTestfall, "19", "Widmer", "Ursula", institutionenForSchulung);
		createFall(Testfall06_BeckerNora.class, gesuchsperiode, institutionenForTestfall, "20", "Graf", "Anna", institutionenForSchulung);
		createFall(Testfall07_MeierMeret.class, gesuchsperiode, institutionenForTestfall, "21", "Zimmermann", "Katrin", institutionenForSchulung);

		createFall(Testfall02_FeutzYvonne.class, gesuchsperiode, institutionenForTestfall, "22", "Hofstetter", "Anneliese", institutionenForSchulung);
		createFall(Testfall03_PerreiraMarcia.class, gesuchsperiode, institutionenForTestfall, "23", "Arnold", "Madeleine", institutionenForSchulung);
		createFall(Testfall04_WaltherLaura.class, gesuchsperiode, institutionenForTestfall, "24", "Schneebeli", "Janine", institutionenForSchulung);
		createFall(Testfall05_LuethiMeret.class, gesuchsperiode, institutionenForTestfall, "25", "Weber", "Marianne", institutionenForSchulung);

	}

	private void createFall(Class<? extends AbstractTestfall> classTestfall, Gesuchsperiode gesuchsperiode, List<InstitutionStammdaten> institutionenForTestfall, String id, String nachname, String vorname, List<InstitutionStammdaten> institutionenForSchulung, boolean noRandom) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		boolean verfuegen = RANDOM.nextBoolean() && RANDOM.nextBoolean(); // Damit VERFUEGT nicht zu haeufig...
		if (noRandom) {
			verfuegen = true;
		}
		AbstractTestfall testfall = classTestfall.getConstructor(Gesuchsperiode.class, Collection.class, Boolean.TYPE).newInstance(gesuchsperiode, institutionenForTestfall, verfuegen);
		testfall.setFixId(GESUCH_ID.replaceAll("XX", id));
		Gesuch gesuch = createFallForSuche(testfall, nachname, vorname, institutionenForSchulung, verfuegen, noRandom);
		FreigabeCopyUtil.copyForFreigabe(gesuch);
		gesuchService.updateGesuch(gesuch, false);
	}

	private void createFall(Class<? extends AbstractTestfall> classTestfall, Gesuchsperiode gesuchsperiode, List<InstitutionStammdaten> institutionenForTestfall, String id, String nachname, String vorname, List<InstitutionStammdaten> institutionenForSchulung) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		createFall(classTestfall, gesuchsperiode, institutionenForTestfall, id, nachname, vorname, institutionenForSchulung, false);
	}

	@SuppressWarnings("ConstantConditions")
	private Gesuch createFallForSuche(AbstractTestfall testfall, String nachname, String vorname, List<InstitutionStammdaten> institutionenForSchulung, boolean verfuegen, boolean noRandom) {
		Gesuch gesuch = createFallForSuche(testfall, institutionenForSchulung, verfuegen, noRandom);
		if (StringUtils.isNotEmpty(nachname)) {
			gesuch.getGesuchsteller1().getGesuchstellerJA().setNachname(nachname);
		}
		if (StringUtils.isNotEmpty(vorname)) {
			gesuch.getGesuchsteller1().getGesuchstellerJA().setVorname(vorname);
		}
		return gesuchService.updateGesuch(gesuch, false);
	}

	private Gesuch createFallForSuche(AbstractTestfall testfall, String nachname, String vorname, List<InstitutionStammdaten> institutionenForSchulung, boolean verfuegen) {
		return createFallForSuche(testfall, nachname, vorname, institutionenForSchulung, verfuegen, false);
	}

	@Inject
	private WizardStepService wizardStepService;

	private Gesuch createFallForSuche(AbstractTestfall testfall, List<InstitutionStammdaten> institutionenForSchulung, boolean verfuegen, boolean noRandom) {
		@SuppressWarnings("DuplicateBooleanBranch")

		Gesuch gesuch = testfaelleService.createAndSaveGesuch(testfall, verfuegen, null);
		gesuch.setEingangsdatum(LocalDate.now());

		// Gesuch entweder online oder papier
		boolean online = RANDOM.nextBoolean();
		Eingangsart eingangsart = online ? Eingangsart.ONLINE : Eingangsart.PAPIER;
		gesuch.setEingangsart(eingangsart);

		// Institutionen anpassen
		List<Betreuung> betreuungList = gesuch.extractAllBetreuungen();
		for (Betreuung betreuung : betreuungList) {
			if (noRandom) {
				if (betreuung.getBetreuungNummer() == 1) {
					InstitutionStammdaten institutionStammdaten = institutionenForSchulung.get(2);
					betreuung.setInstitutionStammdaten(institutionStammdaten);
				} else {
					InstitutionStammdaten institutionStammdaten = institutionenForSchulung.get(0);
					betreuung.setInstitutionStammdaten(institutionStammdaten);
				}
				betreuung.setBetreuungsstatus(Betreuungsstatus.VERFUEGT);
			} else {
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
		}
		Gesuch savedGesuch = gesuchService.updateGesuch(gesuch, false);
		if (verfuegen) {
			wizardStepService.updateSteps(savedGesuch.getId(), null, null, WizardStepName.VERFUEGEN);
		}
		return savedGesuch;
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
			if (gesuchOptional.isPresent()) {
				final Optional<Fall> fall = fallService.findFall(gesuchOptional.get().getFall().getId());
				if (fall.isPresent()) {
					// Fall und seine abhaengigen Gesuche loeschen
					fallService.removeFall(fall.get());
				}
			}
		}
	}

	private void assertInstitutionNotUsedInNormalenGesuchen(String institutionId, InstitutionStammdaten toReplace) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		Join<Betreuung, InstitutionStammdaten> join = root.join(Betreuung_.institutionStammdaten, JoinType.LEFT);

		query.select(root);
		Predicate idPred = cb.equal(join.get(InstitutionStammdaten_.id), institutionId);
		query.where(idPred);
		List<Betreuung> criteriaResults = persistence.getCriteriaResults(query);
		for (Betreuung betreuung : criteriaResults) {
			betreuung.setInstitutionStammdaten(toReplace);
			persistence.merge(betreuung);
		}
	}
}
