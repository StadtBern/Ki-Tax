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
import ch.dvbern.ebegu.vorlagen.PrintUtil;

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
	public String getGesuchstellerName() {

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
}
