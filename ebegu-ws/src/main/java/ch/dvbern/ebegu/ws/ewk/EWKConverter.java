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

package ch.dvbern.ebegu.ws.ewk;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import ch.bern.e_gov.cra.Adresse;
import ch.bern.e_gov.cra.Beziehung;
import ch.bern.e_gov.cra.Einwohnercode;
import ch.bern.e_gov.e_begu.egov_002.PersonenInformationen;
import ch.bern.e_gov.e_begu.egov_002.PersonenSucheResp;
import ch.dvbern.ebegu.dto.personensuche.EWKAdresse;
import ch.dvbern.ebegu.dto.personensuche.EWKBeziehung;
import ch.dvbern.ebegu.dto.personensuche.EWKEinwohnercode;
import ch.dvbern.ebegu.dto.personensuche.EWKPerson;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;

/**
 * Konverter zwischen EWK-Objekten und unseren DTOs
 */
public final class EWKConverter {

	public static final String MANN = "M";

	private EWKConverter() {
		// Util-Class, should not be initialized
	}

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
		if (beziehung.getAdresse() != null) {
			ewkBeziehung.setAdresse(convertFromEWK(beziehung.getAdresse()));
		}
		return ewkBeziehung;
	}
}
