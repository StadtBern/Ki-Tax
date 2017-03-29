package ch.dvbern.ebegu.ewk;

import ch.bern.e_gov.cra.Adresse;
import ch.bern.e_gov.cra.Beziehung;
import ch.bern.e_gov.e_begu.egov_002.PersonenInformationen;
import ch.bern.e_gov.e_begu.egov_002.PersonenSucheResp;
import ch.dvbern.ebegu.dto.personensuche.*;
import ch.dvbern.ebegu.enums.Geschlecht;

import javax.annotation.Nonnull;

/**
 * Konverter zwischen EWK-Objekten und unseren DTOs
 */
public class EWKConverter {

	public static final String MANN = "M";
	public static final String FRAU = "W";
	public static final String ADRESSTYP_WOHNADRESSE = "1";
	public static final String ADRESSTYP_WEGZUG = "2"; //TODO (hefr) Wegzugsadresse Beispiel????
	public static final String ADRESSTYP_ZUZUG = "3";

	public static EWKResultat convertFromEWK(@Nonnull PersonenSucheResp response) {
		EWKResultat resultat = new EWKResultat();
		resultat.setAnzahlResultate(response.getAnzahlTreffer().intValue());
		for (PersonenInformationen personenInformationen : response.getPerson()) {
			EWKPerson ewkPerson = convertFromEWK(personenInformationen);

			for (Adresse adresse : personenInformationen.getAdresse()) {
				EWKAdresse ewkAdresse = convertFromEWK(adresse);
				if (ADRESSTYP_WOHNADRESSE.equals(ewkAdresse.getAdresstyp())) {
					ewkPerson.setWohnadresse(ewkAdresse);
				} else if (ADRESSTYP_WEGZUG.equals(ewkAdresse.getAdresstyp())) {
					ewkPerson.setWegzugsadresse(ewkAdresse);
				} else if (ADRESSTYP_ZUZUG.equals(ewkAdresse.getAdresstyp())) {
					ewkPerson.setZuzugsadresse(ewkAdresse);
				}
			}

			for (Beziehung beziehung : personenInformationen.getBeziehung()) {
				EWKBeziehung ewkBeziehung = convertFromEWK(beziehung);
				ewkPerson.getBeziehungen().add(ewkBeziehung);
			}
			resultat.getPersonen().add(ewkPerson);
		}
		return resultat;
	}

	public static EWKPerson convertFromEWK(@Nonnull PersonenInformationen personenInformationen) {
		EWKPerson ewkPerson = new EWKPerson();
		ewkPerson.setPersonID(personenInformationen.getPersonID());
		ewkPerson.setEinwohnercode(EWKEinwohnercode.getEWKEinwohnercode(personenInformationen.getEinwohnercode().get(0).getCode()));
		ewkPerson.setNachname(personenInformationen.getNachname());
		ewkPerson.setLedigname(personenInformationen.getLedigname());
		ewkPerson.setVorname(personenInformationen.getVorname());
		ewkPerson.setRufname(personenInformationen.getRufname());
		ewkPerson.setGeburtsdatum(personenInformationen.getGeburtsdatum());
		ewkPerson.setZuzugsdatum(personenInformationen.getZuzugsdatum());
		ewkPerson.setNationalitaet(personenInformationen.getNationalitaet());
		ewkPerson.setZivilstand(personenInformationen.getZivilstand());
		ewkPerson.setZivilstandTxt(personenInformationen.getZivilstandTxt());
		ewkPerson.setZivilstandsdatum(personenInformationen.getZivilstandsdatum());
		ewkPerson.setGeschlecht(MANN.equals(personenInformationen.getGeschlecht()) ? Geschlecht.MAENNLICH : Geschlecht.WEIBLICH);
		ewkPerson.setBewilligungsart(personenInformationen.getBewilligungsart());
		ewkPerson.setBewilligungsartTxt(personenInformationen.getBewilligungsartTxt());
		ewkPerson.setBewilligungBis(personenInformationen.getBewilligungBis());
		return ewkPerson;
	}

	public static EWKAdresse convertFromEWK(@Nonnull Adresse adresse) {
		EWKAdresse ewkAdresse = new EWKAdresse();
		ewkAdresse.setAdresstyp(adresse.getAdresstyp());
		ewkAdresse.setAdresstypTxt(adresse.getAdresstypTxt());
		ewkAdresse.setGueltigVon(adresse.getGueltigVon());
		ewkAdresse.setGueltigBis(adresse.getGueltigBis());
		ewkAdresse.setCoName(adresse.getCOName());
		ewkAdresse.setPostfach(adresse.getPostfach());
		ewkAdresse.setBfSGemeinde(adresse.getBfSGemeinde());
		ewkAdresse.setStrasse(adresse.getStrasse());
		ewkAdresse.setHausnummer(adresse.getHausnummer());
		ewkAdresse.setPostleitzahl(adresse.getPostleitzahl());
		ewkAdresse.setOrt(adresse.getOrt());
		ewkAdresse.setKanton(adresse.getKanton());
		ewkAdresse.setLand(adresse.getLand());
		return ewkAdresse;
	}

	public static EWKBeziehung convertFromEWK(@Nonnull Beziehung beziehung) {
		EWKBeziehung ewkBeziehung = new EWKBeziehung();
		ewkBeziehung.setPersonID(beziehung.getPersonID());
		ewkBeziehung.setBeziehungstyp(beziehung.getBeziehungstyp());
		ewkBeziehung.setBeziehungstypTxt(beziehung.getBeziehungstypTxt());
		ewkBeziehung.setNachname(beziehung.getNachname());
		ewkBeziehung.setLedigname(beziehung.getLedigname());
		ewkBeziehung.setVorname(beziehung.getVorname());
		ewkBeziehung.setRufname(beziehung.getRufname());
		ewkBeziehung.setGeburtsdatum(beziehung.getGeburtsdatum());
		ewkBeziehung.setAdresse(convertFromEWK(beziehung.getAdresse()));
		return ewkBeziehung;
	}
}
