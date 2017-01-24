package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.enums.UserRoleName;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service fuer erstellen und mutieren von Schulungsdaten
 */
@SuppressWarnings(value = {"DLS_DEAD_LOCAL_STORE", "DM_CONVERT_CASE", "EI_EXPOSE_REP"})
@Stateless
@Local(SchulungService.class)
@RolesAllowed(value = {UserRoleName.ADMIN, UserRoleName.SUPER_ADMIN})
public class SchulungServiceBean extends AbstractBaseService implements SchulungService {

	private static final String TRAEGERSCHAFT_FISCH_ID = "11111111-1111-1111-1111-111111111111";

	private static final String INSTITUTION_FORELLE_ID = "22222222-1111-1111-1111-111111111111";
	private static final String INSTITUTION_HECHT_ID = "22222222-1111-1111-1111-222222222222";

	private static final String KITA_FORELLE_ID = "33333333-1111-1111-1111-111111111111";
	private static final String TAGESELTERN_FORELLE_ID = "33333333-1111-1111-2222-111111111111";
	private static final String KITA_HECHT_ID = "33333333-1111-1111-1111-222222222222";

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
	private FallService fallService;

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

	private static final Logger LOG = LoggerFactory.getLogger(SchulungServiceBean.class);

	//TODO (hefr) Die Liste der BENUTZER zurückgeben
	//TODO (hefr) 20-30 weitere Gesuche erstellen

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

		for (String s : GESUCHSTELLER_LIST) {
			removeGesucheFallAndBenutzer(s);
		}

		benutzerService.removeBenutzer(getUsername(BENUTZER_FISCH_NAME, BENUTZER_FISCH_VORNAME));
		benutzerService.removeBenutzer(getUsername(BENUTZER_FORELLE_NAME, BENUTZER_FORELLE_VORNAME));

		institutionStammdatenService.removeInstitutionStammdaten(KITA_FORELLE_ID);
		institutionStammdatenService.removeInstitutionStammdaten(TAGESELTERN_FORELLE_ID);
		institutionStammdatenService.removeInstitutionStammdaten(KITA_HECHT_ID);

		institutionService.deleteInstitution(INSTITUTION_FORELLE_ID);
		institutionService.deleteInstitution(INSTITUTION_HECHT_ID);

		traegerschaftService.removeTraegerschaft(TRAEGERSCHAFT_FISCH_ID);
	}

	@SuppressWarnings("unused")
	@Override
	public void createSchulungsdaten() {
		Traegerschaft traegerschaftFisch = createTraegerschaft(TRAEGERSCHAFT_FISCH_ID, "Fisch");
		Institution institutionForelle = createtInstitution(INSTITUTION_FORELLE_ID, "Forelle", traegerschaftFisch);
		Institution institutionHecht = createtInstitution(INSTITUTION_HECHT_ID, "Hecht", traegerschaftFisch);

//		InstitutionStammdaten kitaForelle = createInstitutionStammdaten(KITA_FORELLE_ID, institutionForelle, BetreuungsangebotTyp.KITA);
//		InstitutionStammdaten tageselternForelle = createInstitutionStammdaten(TAGESELTERN_FORELLE_ID, institutionForelle, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
//		InstitutionStammdaten kitaHecht = createInstitutionStammdaten(KITA_HECHT_ID, institutionHecht, BetreuungsangebotTyp.KITA);
		createInstitutionStammdaten(KITA_FORELLE_ID, institutionForelle, BetreuungsangebotTyp.KITA);
		createInstitutionStammdaten(TAGESELTERN_FORELLE_ID, institutionForelle, BetreuungsangebotTyp.TAGESELTERN_KLEINKIND);
		createInstitutionStammdaten(KITA_HECHT_ID, institutionHecht, BetreuungsangebotTyp.KITA);

		createBenutzer(BENUTZER_FISCH_NAME, BENUTZER_FISCH_VORNAME, traegerschaftFisch, null);
		createBenutzer(BENUTZER_FORELLE_NAME, BENUTZER_FORELLE_VORNAME, null, institutionForelle);

		for (String s : GESUCHSTELLER_LIST) {
			createGesuchsteller(s);
		}
	}

	@Override
	@PermitAll
	public String[] getSchulungBenutzer() {
		return (String[]) ArrayUtils.clone(GESUCHSTELLER_LIST);
	}

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
		Optional<Benutzer> benutzerOptional = benutzerService.findBenutzer(getUsername(nachname));
		if (benutzerOptional.isPresent()) {
			Benutzer benutzer = benutzerOptional.get();
			Optional<Fall> fallOptional = fallService.findFallByBesitzer(benutzer);
			if (fallOptional.isPresent()) {
				Fall fall = fallOptional.get();
				List<JaxAntragDTO> allAntragDTOForFall = gesuchService.getAllAntragDTOForFall(fall.getId());
				for (JaxAntragDTO jaxAntragDTO : allAntragDTOForFall) {
					gesuchService.removeGesuch(jaxAntragDTO.getAntragId());
				}
				fallService.removeFall(fall);
			}
			benutzerService.removeBenutzer(getUsername(nachname));
		}
	}
}
