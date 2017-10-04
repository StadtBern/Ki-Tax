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

package ch.dvbern.ebegu.dto.dataexport.v1;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.types.DateRange;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter to change to create the ExportDTO of a given Verfuegung
 */
public class ExportConverter {


	public VerfuegungenExportDTO createVerfuegungenExportDTO(List<Verfuegung> verfuegungenToConvert) {
		List<VerfuegungExportDTO> verfuegungExportDTOS = verfuegungenToConvert
			.stream()
			.map(this::createVerfuegungExportDTOFromVerfuegung)
			.collect(Collectors.toList());
		VerfuegungenExportDTO exportDTO = new VerfuegungenExportDTO();
		exportDTO.setVerfuegungen(verfuegungExportDTOS);
		return exportDTO;

	}

	private VerfuegungExportDTO createVerfuegungExportDTOFromVerfuegung(@Nonnull Verfuegung verfuegung) {
		Validate.notNull(verfuegung, "verfuegung must be set");

		VerfuegungExportDTO verfuegungDTO = new VerfuegungExportDTO();
		verfuegungDTO.setRefnr(verfuegung.getBetreuung().getBGNummer());
		DateRange periode = verfuegung.getBetreuung().extractGesuchsperiode().getGueltigkeit();
		verfuegungDTO.setVon(periode.getGueltigAb());
		verfuegungDTO.setBis(periode.getGueltigBis());
		verfuegungDTO.setVersion(verfuegung.getBetreuung().extractGesuch().getLaufnummer());
		verfuegungDTO.setVerfuegtAm(verfuegung.getTimestampErstellt());
		verfuegungDTO.setKind(createKindExportDTOFromKind(verfuegung.getBetreuung().getKind()));
		GesuchstellerContainer gs1 = verfuegung.getBetreuung().extractGesuch().getGesuchsteller1();
		verfuegungDTO.setGesuchsteller(createGesuchstellerExportDTOFromGesuchsteller(gs1));
		verfuegungDTO.setBetreuung(createBetreuungExportDTOFromBetreuung(verfuegung.getBetreuung()));
		// Verrechnete Zeitabschnitte
		List<ZeitabschnittExportDTO> zeitabschnitte = verfuegung.getZeitabschnitte().stream()
			.filter(abschnitt -> !abschnitt.getZahlungsstatus().isIgnoriert())
			.map(this::createZeitabschnittExportDTOFromZeitabschnitt)
			.collect(Collectors.toList());
		verfuegungDTO.setZeitabschnitte(zeitabschnitte);
		// Ignorierte Zeitabschnitte
		List<ZeitabschnittExportDTO> zeitabschnitteIgnoriert = verfuegung.getZeitabschnitte().stream()
			.filter(abschnitt -> abschnitt.getZahlungsstatus().isIgnoriert())
			.map(this::createZeitabschnittExportDTOFromZeitabschnitt)
			.collect(Collectors.toList());
		verfuegungDTO.setIgnorierteZeitabschnitte(zeitabschnitteIgnoriert);
		return verfuegungDTO;
	}


	private KindExportDTO createKindExportDTOFromKind(KindContainer kindCont) {
		Kind kindJA = kindCont.getKindJA();
		return new KindExportDTO(kindJA.getVorname(), kindJA.getNachname(), kindJA.getGeburtsdatum());


	}

	private GesuchstellerExportDTO createGesuchstellerExportDTOFromGesuchsteller(GesuchstellerContainer gesuchstellerContainer) {
		Gesuchsteller gesuchstellerJA = gesuchstellerContainer.getGesuchstellerJA();
		return new GesuchstellerExportDTO(gesuchstellerJA.getVorname(), gesuchstellerJA.getNachname(), gesuchstellerJA.getMail());
	}

	private BetreuungExportDTO createBetreuungExportDTOFromBetreuung(Betreuung betreuung) {
		BetreuungExportDTO betreuungExportDto = new BetreuungExportDTO();
		betreuungExportDto.setBetreuungsArt(betreuung.getBetreuungsangebotTyp());
		betreuungExportDto.setInstitution(createInstitutionExportDTOFromInstStammdaten(betreuung.getInstitutionStammdaten()));
		return betreuungExportDto;
	}

	private InstitutionExportDTO createInstitutionExportDTOFromInstStammdaten(InstitutionStammdaten institutionStammdaten) {
		Institution institution = institutionStammdaten.getInstitution();
		String instID = institution.getId();
		String name = institution.getName();
		String traegerschaft = institution.getTraegerschaft() != null ? institution.getTraegerschaft().getName() : null;
		AdresseExportDTO adresse = createAdresseExportDTOFromAdresse(institutionStammdaten.getAdresse());
		return new InstitutionExportDTO(instID, name, traegerschaft, adresse);


	}

	private AdresseExportDTO createAdresseExportDTOFromAdresse(Adresse adresse) {
		return new AdresseExportDTO(adresse.getStrasse(), adresse.getHausnummer(), adresse.getZusatzzeile(), adresse.getOrt(), adresse.getPlz(), adresse.getLand());
	}

	private ZeitabschnittExportDTO createZeitabschnittExportDTOFromZeitabschnitt(VerfuegungZeitabschnitt zeitabschnitt) {
		LocalDate von = zeitabschnitt.getGueltigkeit().getGueltigAb();
		LocalDate bis = zeitabschnitt.getGueltigkeit().getGueltigBis();
		int effektiveBetr = zeitabschnitt.getBetreuungspensum();
		int anspruchPct = zeitabschnitt.getAnspruchberechtigtesPensum();
		int vergPct = zeitabschnitt.getBgPensum();
		BigDecimal vollkosten = zeitabschnitt.getVollkosten();
		BigDecimal verguenstigung = zeitabschnitt.getVerguenstigung();
		return new ZeitabschnittExportDTO(von, bis, effektiveBetr, anspruchPct, vergPct, vollkosten, verguenstigung);

	}

}
