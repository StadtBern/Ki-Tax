package ch.dvbern.ebegu.vorlagen.mahnung;

import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;

import java.util.List;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 16/11/2016.
 */
public interface ManhungPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	String getAngebotFull();

	String getAngebotShort();

	String getFallNummer();

	String getFallDatum();

	List<AufzaehlungPrint> getFehlendeUnterlagen();

	String getMahnFristDatum();

	String getErsteMahnDatum();

	String getKontaktStelle();

}
