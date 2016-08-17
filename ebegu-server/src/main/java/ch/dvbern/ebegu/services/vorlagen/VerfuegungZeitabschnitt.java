package ch.dvbern.ebegu.services.vorlagen;

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
* Ersteller: zeab am: 12.08.2016
*/
public interface VerfuegungZeitabschnitt {

	String getVon();

	String getBis();

	int getBetreuung();

	int getAnspruch();

	int getBGPensum();

	BigDecimal getVollkosten();

	BigDecimal getElternbeitrag();

	BigDecimal getVerguenstigung();
}
