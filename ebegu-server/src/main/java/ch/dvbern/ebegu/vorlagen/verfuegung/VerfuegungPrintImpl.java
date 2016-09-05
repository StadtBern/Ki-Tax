package ch.dvbern.ebegu.vorlagen.verfuegung;
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.util.Constants;

/**
 * Transferobjekt
 */
public class VerfuegungPrintImpl implements VerfuegungPrint {

	private Betreuung betreuung;

	/**
	 * @param betreuung
	 */
	public VerfuegungPrintImpl(Betreuung betreuung) {

		this.betreuung = betreuung;
	}

	@Override
	public String getTitel() {

		// TODO ZEAB Implementieren
		return "Verfügung / Bestätigung";
	}

	@Override
	public String getAngebot() {

		// TODO ZEAB Implementieren
		return "Kita";
	}

	@Override
	public String getInstitution() {

		return betreuung.getInstitutionStammdaten().getInstitution().getName();
	}

	/**
	 * @return Gesuchsteller-ReferenzNummer
	 */
	@Override
	public String getReferenznummer() {

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
		// TODO ZEAB was muss hier passieren?? Leer ausdrucken??
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
			result.addAll(verfuegung.get().getZeitabschnitte().stream().map(VerfuegungZeitabschnittPrintImpl::new).collect(Collectors.toList()));
		}
		return result;
	}

	/**
	 * @return Bemerkungen
	 */
	@Override
	public String getManuelleBemerkungen() {

		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent()) {
			if (betreuung.getVerfuegung().getGeneratedBemerkungen() != null) {
				return betreuung.getVerfuegung().getGeneratedBemerkungen();
			}
		}
		return "";
	}

	public String getGeneratedBemerkungen() {

		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent()) {

			if (betreuung.getVerfuegung().getManuelleBemerkungen() != null) {
				return betreuung.getVerfuegung().getManuelleBemerkungen();
			}

		}

		return "";

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

	@Override
	public boolean isPensumIst0() {

		return !isPensumGrosser0();
	}

	/**
	 * @return true falls es sich um eine Mutation handelt
	 */
	public boolean isMutation() {

		// TODO Team: Muss angepasst werden, sobald wir Mutationen unterstuetzen
		return true;
	}

	@Override
	public boolean isPrintbemerkung() {

		return isPrintManuellebemerkung() || isPrintGeneratedBemerkung();
	}

	@Override
	public boolean isPrintManuellebemerkung() {

		return !"".equalsIgnoreCase(getManuelleBemerkungen());
	}

	@Override
	public boolean isPrintGeneratedBemerkung() {

		return !"".equalsIgnoreCase(getGeneratedBemerkungen());
	}

	@Nonnull
	private Kind extractKind() {

		return betreuung.getKind().getKindJA();
	}

	@Nonnull
	private Optional<Verfuegung> extractVerfuegung() {

		Verfuegung verfuegung = betreuung.getVerfuegung();
		if (verfuegung != null) {
			return Optional.of(verfuegung);
		}
		return Optional.empty();
	}

	@Override
	public boolean isPrintSeitenumbruch() {

		// Fall Zeilen anzahl grosser als 8 muss 'Rechtsmittelbelehrung' auf eine neue Seite gedruckt werden
		return ermittleAnzahlZeilen() > 8;
	}

	private int ermittleAnzahlZeilen() {

		int rows = getVerfuegungZeitabschnitt().size();
		StringBuilder builder = new StringBuilder();
		builder.append(getManuelleBemerkungen());
		builder.append("\n");
		builder.append(getGeneratedBemerkungen());
		builder.append("\n");

		try {
			InputStream ins = new ByteArrayInputStream(builder.toString().getBytes());
			Reader r = new InputStreamReader(ins, "UTF-8");
			BufferedReader br = new BufferedReader(r);
			String line;

			while ((line = br.readLine()) != null) {
				rows++;
			}
		} catch (IOException e) {
			// TUE NIX
		}

		return rows;
	}
}
