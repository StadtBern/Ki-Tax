package ch.dvbern.ebegu.services.vorlagen;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 12.08.2016
*/

import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementiert den Verfuegungsmuster
 */
public class VerfuegungPrintDTO implements VerfuegungPrint {

	private Betreuung betreuung;

	public VerfuegungPrintDTO(Betreuung betreuung) {

		this.betreuung = betreuung;
	}

	@Override
	public String getGesuchstellerName() {

		return betreuung.extractGesuch().getGesuchsteller1().getFullName();
	}

	@Override
	public String getGesuchstellerStrasse() {

		if (getGesuchstellerAdresse() != null) {
			return getGesuchstellerAdresse().getStrasse();
		}
		return "";
	}

	@Override
	public String getGesuchstellerPLZStadt() {

		if (getGesuchstellerAdresse() != null) {
			return getGesuchstellerAdresse().getPlz() + " " + getGesuchstellerAdresse().getOrt();
		}
		return "";
	}

	@Nullable
	private GesuchstellerAdresse getGesuchstellerAdresse() {

		List<GesuchstellerAdresse> adressen = betreuung.extractGesuch().getGesuchsteller1().getAdressen();
		GesuchstellerAdresse wohnadresse = null;
		for (GesuchstellerAdresse gesuchstellerAdresse : adressen) {
			if (gesuchstellerAdresse.getAdresseTyp().equals(AdresseTyp.KORRESPONDENZADRESSE)) {
				return gesuchstellerAdresse;
			}
			wohnadresse = gesuchstellerAdresse;
		}
		return wohnadresse;
	}

	@Override
	public String getReferenzNummer() {
		return betreuung.getBGNummer();
	}

	@Override
	public String getVerfuegungsdatum() {

		// TODO ZEAB ist das Setzen der Verfuegungsdatum Korrekt
		return Constants.DATE_FORMATTER.format(betreuung.getVerfuegung().getTimestampErstellt());

	}

	@Override
	public String getGesuchsteller1() {

		return betreuung.extractGesuch().getGesuchsteller1().getFullName();
	}

	@Override
	public String getGesuchsteller2() {

		return betreuung.extractGesuch().getGesuchsteller2().getFullName();
	}

	@Override
	public String getKindNameVorname() {

		return betreuung.getKind().getKindJA().getFullName();
	}

	@Override
	public String getKindGeburtsdatum() {

		return Constants.DATE_FORMATTER.format(betreuung.getKind().getKindJA().getGeburtsdatum());
	}

	@Override
	public String getKitaBezeichnung() {

		return betreuung.getInstitutionStammdaten().getInstitution().getName();
	}

	@Override
	public String getAnspruchAb() {
		return Constants.DATE_FORMATTER.format(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigAb());
	}

	@Override
	public String getAnspruchBis() {
		return Constants.DATE_FORMATTER.format(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
	}

	@Override
	public List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt() {

		List<VerfuegungZeitabschnittPrint> result = new ArrayList<>();
		result.addAll(betreuung.getVerfuegung().getZeitabschnitte().stream().map(VerfuegungZeitabschnittPrintDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public String getBemerkung() {

		return betreuung.getVerfuegung().getGeneratedBemerkungen() + " " + betreuung.getVerfuegung().getManuelleBemerkungen();
	}

	@Override
	public boolean existGesuchsteller2() {

		return betreuung.extractGesuch().getGesuchsteller2() != null;
	}

	@Override
	public boolean isPensumGrosser0() {

		List<VerfuegungZeitabschnittPrint> vzList = getVerfuegungZeitabschnitt();
		int value = 0;
		for (VerfuegungZeitabschnittPrint verfuegungZeitabschnitt : vzList) {
			value = value + verfuegungZeitabschnitt.getBGPensum();
			// BG-Pensum
		}
		return value > 0;
	}

	@Override
	public boolean isMutation() {

		// TODO ZEAB ist so Korrekt
		return betreuung.getVerfuegung().getTimestampMutiert() != null;
	}
}
