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
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;

import java.util.List;

/**
 * Transferobjekt
 */
public class BegleitschreibenPrintImpl extends BriefPrintImpl implements BegleitschreibenPrint {

	/**
	 * @param gesuch
	 */
	public BegleitschreibenPrintImpl(Gesuch gesuch) {

		super(gesuch);

	}

	@Override
	public List<AufzaehlungPrint> getUnterlagen() {
		return null;
	}

	@Override
	public boolean isWithoutUnterlagen() {
		return false;
	}
}
