package ch.dvbern.ebegu.vorlagen.freigabequittung;

import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.lib.doctemplate.docx.DocxImage;

import java.io.IOException;
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
public interface FreigabequittungPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	DocxImage getBarcodeImage() throws IOException;

	boolean isAdresseJugendamt();

	boolean isAdresseSchulamt();

	String getPeriode();

	String getFallNummer();

	String getFallDatum();

	String getAdresseGS1();

	boolean isAddresseGS2();

	String getAdresseGS2();

	List<BetreuungsTabellePrint> getBetreuungen();

	List<AufzaehlungPrint> getUnterlagen();

	boolean isWithoutUnterlagen();

}
