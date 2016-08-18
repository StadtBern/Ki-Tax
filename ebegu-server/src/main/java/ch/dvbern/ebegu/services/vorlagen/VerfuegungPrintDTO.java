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

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transferobjekt
 */
public class VerfuegungPrintDTO implements VerfuegungPrint {

	private Betreuung betreuung;

	/**
	 * @param betreuung
	 */
	public VerfuegungPrintDTO(Betreuung betreuung) {

		this.betreuung = betreuung;
	}

	/**
	 * @return GesuchstellerName
	 */
	@Override
	public String getGesuchstellerName() {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1();
		if (gesuchsteller.isPresent()) {
			return gesuchsteller.get().getFullName();
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */
	@Override
	public String getGesuchstellerStrasse() {

		Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse();
		if (gesuchstellerAdresse.isPresent()) {
			return gesuchstellerAdresse.get().getStrasse();
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */
	@Override
	public String getGesuchstellerPLZStadt() {

		Optional<GesuchstellerAdresse> gesuchstellerAdresse = getGesuchstellerAdresse();
		if (gesuchstellerAdresse.isPresent()) {
			return gesuchstellerAdresse.get().getPlz() + " " + gesuchstellerAdresse.get().getOrt();
		}
		return "";
	}

	/**
	 * @return Gesuchsteller-ReferenzNummer
	 */
	@Override
	public String getReferenzNummer() {

		return betreuung.getBGNummer();
	}

	/**
	 * @return Gesuchsteller-Verfuegungsdatum
	 */
	@Override
	public String getVerfuegungsdatum() {

		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent()) {
			Verfuegung verfuegung1 = verfuegung.get();
			if (verfuegung1.getTimestampErstellt() != null) {
				// TODO ZEAB ist das Setzen der Verfuegungsdatum Korrekt
				return Constants.DATE_FORMATTER.format(verfuegung1.getTimestampErstellt());
			}
		}
		return "";
	}

	/**
	 * @return Name des Gesuchsteller1
	 */
	@Override
	public String getGesuchsteller1() {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1();
		if (gesuchsteller.isPresent()) {
			return gesuchsteller.get().getFullName();
		}
		return "";
	}

	/**
	 * @return Name des Gesuchsteller2
	 */
	@Override
	public String getGesuchsteller2() {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller2();
		if (gesuchsteller.isPresent()) {
			return gesuchsteller.get().getFullName();
		}
		return "";
	}

	/**
	 * @return Name Vorname des Kindes
	 */
	@Override
	public String getKindNameVorname() {

		return extractKind().getFullName();
	}

	/**
	 * @return Geburtsdatum des Kindes
	 */
	@Override
	public String getKindGeburtsdatum() {

		return Constants.DATE_FORMATTER.format(betreuung.getKind().getKindJA().getGeburtsdatum());
	}

	/**
	 * @return Kita Name
	 */
	public String getKitaBezeichnung() {

		return betreuung.getInstitutionStammdaten().getInstitution().getName();
	}

	/**
	 * @return AnspruchAb
	 */
	@Override
	public String getAnspruchAb() {

		return Constants.DATE_FORMATTER.format(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigAb());
	}

	/**
	 * @return AnspruchBis
	 */
	@Override
	public String getAnspruchBis() {

		return Constants.DATE_FORMATTER.format(betreuung.extractGesuchsperiode().getGueltigkeit().getGueltigBis());
	}

	/**
	 * @return VerfuegungZeitabschnitten
	 */
	@Override
	public List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt() {

		List<VerfuegungZeitabschnittPrint> result = new ArrayList<>();
		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent()) {
			result.addAll(verfuegung.get().getZeitabschnitte().stream().map(VerfuegungZeitabschnittPrintDTO::new).collect(Collectors.toList()));
		}
		return result;
	}

	/**
	 * @return Bemerkungen
	 */
	@Override
	public String getBemerkungen() {

		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent()) {
			StringBuilder bemerkungen = new StringBuilder();
			if (betreuung.getVerfuegung().getGeneratedBemerkungen() != null) {
				bemerkungen.append(betreuung.getVerfuegung().getGeneratedBemerkungen());
			}
			if (betreuung.getVerfuegung().getManuelleBemerkungen() != null) {
				bemerkungen.append(betreuung.getVerfuegung().getManuelleBemerkungen());
			}
			return bemerkungen.toString();
		}
		return "";
	}

	/**
	 * @return true falls Gesuchsteller 2 existiert
	 */
	@Override
	public boolean existGesuchsteller2() {

		return betreuung.extractGesuch().getGesuchsteller2() != null;
	}

	/**
	 * @return true falls Pensum groesser 0 ist
	 */
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

	/**
	 * @return true falls es sich um eine Mutation handelt
	 */
	public boolean isMutation() {

		// TODO Team: Muss angepasst werden, sobald wir Mutationen unterstuetzen
		return false;
	}

	@Nonnull
	private Optional<Gesuchsteller> extractGesuchsteller1() {

		Gesuchsteller gs1 = betreuung.extractGesuch().getGesuchsteller1();
		if (gs1 != null) {
			return Optional.of(gs1);
		}
		return Optional.empty();
	}

	@Nonnull
	private Optional<Gesuchsteller> extractGesuchsteller2() {

		Gesuchsteller gs2 = betreuung.extractGesuch().getGesuchsteller2();
		if (gs2 != null) {
			return Optional.of(gs2);
		}
		return Optional.empty();
	}

	@Nonnull
	private Kind extractKind() {

		return betreuung.getKind().getKindJA();
	}

	@Nonnull
	private Optional<GesuchstellerAdresse> getGesuchstellerAdresse() {

		Optional<Gesuchsteller> gesuchsteller = extractGesuchsteller1();
		if (gesuchsteller.isPresent()) {
			List<GesuchstellerAdresse> adressen = gesuchsteller.get().getAdressen();
			GesuchstellerAdresse wohnadresse = null;
			for (GesuchstellerAdresse gesuchstellerAdresse : adressen) {
				if (gesuchstellerAdresse.getAdresseTyp().equals(AdresseTyp.KORRESPONDENZADRESSE)) {
					return Optional.of(gesuchstellerAdresse);
				}
				wohnadresse = gesuchstellerAdresse;
			}
			if (wohnadresse != null) {
				return Optional.of(wohnadresse);
			}
		}
		return Optional.empty();
	}

	@Nonnull
	private Optional<Verfuegung> extractVerfuegung() {

		Verfuegung verfuegung = betreuung.getVerfuegung();
		if (verfuegung != null) {
			return Optional.of(verfuegung);
		}
		return Optional.empty();
	}
}
