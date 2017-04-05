package ch.dvbern.ebegu.ws.ewk;

import ch.bern.e_gov.cra.Adresse;
import ch.bern.e_gov.cra.Beziehung;
import ch.bern.e_gov.cra.Einwohnercode;
import ch.bern.e_gov.e_begu.egov_002.PersonenInformationen;
import ch.bern.e_gov.e_begu.egov_002.PersonenSucheResp;
import ch.dvbern.ebegu.dto.personensuche.*;
import ch.dvbern.ebegu.enums.Geschlecht;

import javax.annotation.Nonnull;
import java.math.BigInteger;

/**
 * Konverter zwischen EWK-Objekten und unseren DTOs
 */
public class EWKConverter {

	public static final String MANN = "M";

	public static EWKResultat convertFromEWK(@Nonnull PersonenSucheResp response, BigInteger maxResults) {
		EWKResultat resultat = new EWKResultat();
		resultat.setAnzahlResultate(response.getAnzahlTreffer().intValue());
		resultat.setMaxResultate(maxResults.intValue());
		for (PersonenInformationen personenInformationen : response.getPerson()) {
			EWKPerson ewkPerson = convertFromEWK(personenInformationen);
			for (Adresse adresse : personenInformationen.getAdresse()) {
				EWKAdresse ewkAdresse = convertFromEWK(adresse);
				ewkPerson.getAdressen().add(ewkAdresse);
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
		for (Einwohnercode einwohnercode : personenInformationen.getEinwohnercode()) {
			EWKEinwohnercode ewkEinwohnercode = convertFromEWK(einwohnercode);
			ewkPerson.getEinwohnercodes().add(ewkEinwohnercode);
		}
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

	public static EWKEinwohnercode convertFromEWK(@Nonnull Einwohnercode einwohnercode) {
		EWKEinwohnercode ewkEinwohnercode = new EWKEinwohnercode();
		ewkEinwohnercode.setCode(einwohnercode.getCode());
		ewkEinwohnercode.setCodeTxt(einwohnercode.getCodeTxt());
		ewkEinwohnercode.setGueltigVon(einwohnercode.getGueltigVon());
		ewkEinwohnercode.setGueltigBis(einwohnercode.getGueltigBis());
		return ewkEinwohnercode;
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
