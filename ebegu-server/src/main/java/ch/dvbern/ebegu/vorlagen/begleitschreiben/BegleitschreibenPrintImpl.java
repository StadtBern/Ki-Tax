package ch.dvbern.ebegu.vorlagen.begleitschreiben;
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

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Transferobjekt
 */
public class BegleitschreibenPrintImpl implements BegleitschreibenPrint {

	private Gesuch gesuch;

	/**
	 * @param gesuch
	 */
	public BegleitschreibenPrintImpl(Gesuch gesuch) {

		this.gesuch = gesuch;
	}

	/**
	 * @return GesuchstellerName
	 */
	@Override
	public String getGesuchstellerNameOderOrganisation() {

		String bezeichnung = PrintUtil.getOrganisation(gesuch);
		if (StringUtils.isNotEmpty(bezeichnung)) {
			return bezeichnung;
		}
		return PrintUtil.getGesuchstellerName(gesuch);
	}

	/**
	 * @return Gesuchsteller-Strasse
	 */
	@Override
	public String getGesuchstellerStrasse() {

		return PrintUtil.getGesuchstellerStrasse(gesuch);
	}

	/**
	 * @return Gesuchsteller-PLZ Stadt
	 */
	@Override
	public String getGesuchstellerPLZStadt() {

		return PrintUtil.getGesuchstellerPLZStadt(gesuch);
	}

	/**
	 * @return Gesuchsteller-ReferenzNummer
	 */
	@Override
	public String getFallNummer() {

		return PrintUtil.createFallNummerString(gesuch);
	}

	@Override
	public String getDateCreate() {
		final String date_pattern = ServerMessageUtil.getMessage("date_letter_pattern");
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(date_pattern);

		return date.format(formatter);
	}

	@Override
	public boolean isPrintTextFamilie() {

		if (StringUtils.isNotEmpty(PrintUtil.getOrganisation(gesuch))) {
			// Text Familie darf nicht gedruckt werden
			return false;
		}
		return true;
	}

	/**
	 * @return true wenn adresszusatz vorhanden
	 */
	@Override
	public boolean isPrintAdresszusatz() {
		if (StringUtils.isNotEmpty(PrintUtil.getAdresszusatz(gesuch))) {
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public String getAdresszusatz() {
		return PrintUtil.getAdresszusatz(gesuch);
	}
}
