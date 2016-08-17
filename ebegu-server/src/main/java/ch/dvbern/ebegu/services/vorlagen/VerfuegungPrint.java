package ch.dvbern.ebegu.services.vorlagen;

import java.util.List;

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
public interface VerfuegungPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	String getGesuchstellerName();

	String getGesuchstellerStrasse();

	String getGesuchstellerPLZStadt();

	String getReferenzNummer();

	String getVerfuegungsdatum();

	String getGesuchsteller1();

	String getGesuchsteller2();

	String getKindNameVorname();

	String getKindGeburtsdatum();

	String getKitaBezeichnung();

	String getAnspruchAb();

	String getAnspruchBis();

	List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt();

	/**
	 * @return die Bemerkung
	 */
	String getBemerkung();

	/**
	 * @return true falls Gesuchstelller 2 existiert
	 */
	boolean existGesuchsteller2();

	/**
	 * @return true falls Pensum groesser 0 ist
	 */
	boolean isPensumGrosser0();

	/**
	 * @return true falls eine Mutation ist
	 */
	boolean isMutation();
}
