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
* Ersteller: zeab am: 22.08.2016
*/

/**
 * Definiert noetige Daten fuer FinanzielleSituationPrint
 */
public interface FinanzielleSituationPrint extends FinanzDatenPrint {

	/**
	 * @return die Referenznummer
	 */
	String getFallNummer();

	/**
	 * @return das Jahr der FinanzielleSituation
	 */
	String getFinanzielleSituationJahr();
}
