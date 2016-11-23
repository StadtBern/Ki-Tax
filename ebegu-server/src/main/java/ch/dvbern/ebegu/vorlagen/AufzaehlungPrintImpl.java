package ch.dvbern.ebegu.vorlagen;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 28.09.2016
*/

public class AufzaehlungPrintImpl implements AufzaehlungPrint {

	private String text;

	public AufzaehlungPrintImpl(String text) {
		this.text = text;
	}

	@Override
	public String getText() {

		return text;
	}
}
