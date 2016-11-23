package ch.dvbern.ebegu.vorlagen;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 18/11/2016.
 */

/**
 *****************************************************************************************************************
 Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
 mit den Platzhaltern im Word-Template!
 ****************************************************************************************************************
 */

public interface BriefPrint {

	String getZustellAdresse();

	String getZustellDatum();

	String getUnterzeichner();

}
