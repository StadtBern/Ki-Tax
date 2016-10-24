package ch.dvbern.ebegu.vorlagen.verfuegung;

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

	String getTitel();

	String getAngebot();

	String getInstitution();

	String getReferenznummer();

	String getVerfuegungsdatum();

	String getKindNameVorname();

	String getKindGeburtsdatum();

	String getKitaBezeichnung();

	String getAnspruchAb();

	String getAnspruchBis();

	List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt();

	/**
	 * @return die Bemerkung
	 */
	List<BemerkungPrint> getManuelleBemerkungen();

	/**
	 * @return true falls Pensum groesser 0 ist
	 */
	boolean isPensumGrosser0();

	/**
	 * @return true wenn Pensum gleich 0 ist
	 */
	boolean isPensumIst0();

	/**
	 * @return true falls eine Vorgänger-Verfügung besteht
	 */
	boolean isVorgaengerVerfuegt();

	/**
	 * @return true ob die Bermerkungen ausgedruckt werden muessen
	 */
	boolean isPrintManuellebemerkung();

}
