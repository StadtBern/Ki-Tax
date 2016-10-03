package ch.dvbern.ebegu.vorlagen.berechnungsblatt;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 03.10.2016
*/

public class BerechnungsblattPrintImpl implements BerechnungsblattPrint {


	@Override
	public String getVon() {

		return null;
	}

	@Override
	public String getBis() {
		return null;
	}

	@Override
	public int getEinkommenVorAbzug() {
		return 0;
	}

	@Override
	public int getFamiliengroesse() {
		return 0;
	}

	@Override
	public int getFamiliengroesseAbzug() {
		return 0;
	}

	@Override
	public int getEinkommenNachAbzug() {
		return 0;
	}
}
