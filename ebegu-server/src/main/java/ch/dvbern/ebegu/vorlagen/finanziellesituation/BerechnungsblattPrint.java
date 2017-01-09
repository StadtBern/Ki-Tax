package ch.dvbern.ebegu.vorlagen.finanziellesituation;

import java.math.BigDecimal;

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
public interface BerechnungsblattPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	String getVon();

	String getBis();

	String getJahr();

	BigDecimal getMassgebendesEinkommenVorAbzFamgr();

	String getFamiliengroesse();

	BigDecimal getAbzugFamGroesse();

	BigDecimal getMassgebendesEinkommenNachAbzugFamgr();
}
