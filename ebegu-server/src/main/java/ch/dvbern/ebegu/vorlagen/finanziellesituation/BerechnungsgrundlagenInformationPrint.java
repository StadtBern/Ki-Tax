package ch.dvbern.ebegu.vorlagen.finanziellesituation;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 23.08.2016
*/

public interface BerechnungsgrundlagenInformationPrint {
	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	/**
	 * @return Name des Gesuchsteller1
	 */
	String getGesuchsteller1Name();

	/**
	 * @return Name des Gesuchsteller2
	 */
	String getGesuchsteller2Name();

	/**
	 * @return true falls das Gesuchsteller 2 vorhanden ist
	 */
	boolean isExistGesuchsteller2();

	/**
	 * @return true das Einkommenverschleschterung 1 existiert
	 */
	boolean isExistEv1();

	/**
	 * @return true das Einkommenverschleschterung 2 existiert
	 */
	boolean isExistEv2();

	/**
	 * @return die Finanzsituation {@link FinanzielleSituationPrint}
	 */
	FinanzielleSituationPrint getFinanz();

	/**
	 * @return das Einkommenverschleschterung {@link EinkommensverschlechterungPrint}
	 */
	EinkommensverschlechterungPrint getEv1();

	/**
	 * @return das Einkommenverschleschterung {@link EinkommensverschlechterungPrint}
	 */
	EinkommensverschlechterungPrint getEv2();

}
