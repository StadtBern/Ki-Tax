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

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.Gueltigkeit;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Transferobjekt
 */
public class VerfuegungPrintImpl implements VerfuegungPrint {

	private Betreuung betreuung;

	//formatiert
	private String letzteVerfuegungDatum;

	/**
	 * @param betreuung
	 */
	public VerfuegungPrintImpl(Betreuung betreuung, @Nullable LocalDate letzteVerfuegungDatum) {
		this.letzteVerfuegungDatum = letzteVerfuegungDatum != null ? Constants.DATE_FORMATTER.format(letzteVerfuegungDatum) : null;
		this.betreuung = betreuung;
	}

	@Override
	public String getTitel() {

		// TODO ZEAB Implementieren
		return "Verfügung / Bestätigung";
	}

	@Override
	public String getAngebot() {

		return ServerMessageUtil.translateEnumValue(betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp());
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
		return letzteVerfuegungDatum;
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
	@Override
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

			result.addAll(verfuegung.get().getZeitabschnitte().stream()
				.sorted(Gueltigkeit.GUELTIG_AB_COMPARATOR.reversed())
				.map(VerfuegungZeitabschnittPrintImpl::new)
				.collect(Collectors.toList()));
			ListIterator<VerfuegungZeitabschnittPrint> listIterator = result.listIterator();
			while (listIterator.hasNext()){
				VerfuegungZeitabschnittPrint zeitabschnitt = listIterator.next();
				if (zeitabschnitt.getBetreuung() <= 0) {
					listIterator.remove();
				} else {
					break;
				}
			}

			Collections.reverse(result);
			listIterator = result.listIterator();
			while (listIterator.hasNext()){
				VerfuegungZeitabschnittPrint zeitabschnitt = listIterator.next();
				if (zeitabschnitt.getBetreuung() <= 0) {
					listIterator.remove();
				} else {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Wenn die Betreuung VERFUEGT ist -> manuelle Bemerkungen Wenn die Betreuung noch nicht VERFUEGT ist -> generated
	 * Bemerkungen
	 *
	 * @return
	 */
	@Override
	public List<AufzaehlungPrint> getManuelleBemerkungen() {

		List<AufzaehlungPrint> bemerkungen = new ArrayList<>();
		Optional<Verfuegung> verfuegung = extractVerfuegung();
		if (verfuegung.isPresent() && StringUtils.isNotEmpty(verfuegung.get().getManuelleBemerkungen())) {
			bemerkungen.addAll(splitBemerkungen(verfuegung.get().getManuelleBemerkungen()));
		}
		return bemerkungen;
	}

	/**
	 * Zerlegt die Bemerkungen (Delimiter \n) und bereitet die in einer Liste.
	 *
	 * @param bemerkungen
	 * @return List mit Bemerkungen
	 */
	private List<AufzaehlungPrint> splitBemerkungen(String bemerkungen) {

		List<AufzaehlungPrint> list = new ArrayList<>();
		// Leere Zeile werden mit diese Annotation [\\r\\n]+ entfernt
		String[] splitBemerkungenNewLine = bemerkungen.split("[" + System.getProperty("line.separator") + "]+");
		for (String bemerkung : splitBemerkungenNewLine) {
			list.add(new AufzaehlungPrintImpl(bemerkung));
		}
		return list;
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


	@Override
	public boolean isVorgaengerVerfuegt() {
		return letzteVerfuegungDatum != null;
	}

	@Override
	public boolean isPrintManuellebemerkung() {

		return !getManuelleBemerkungen().isEmpty();
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
	public String getDateCreate() {
		final String date_pattern = ServerMessageUtil.getMessage("date_pattern");
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(date_pattern);

		return date.format(formatter);
	}
}
