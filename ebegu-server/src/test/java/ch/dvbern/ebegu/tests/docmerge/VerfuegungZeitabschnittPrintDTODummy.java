package ch.dvbern.ebegu.tests.docmerge;
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

import ch.dvbern.ebegu.services.vorlagen.VerfuegungZeitabschnittPrint;

import java.math.BigDecimal;

public class VerfuegungZeitabschnittPrintDTODummy implements VerfuegungZeitabschnittPrint {

	@Override
	public String getVon() {

		return "01.04.2016";
	}

	@Override
	public String getBis() {

		return "31.12.2016";
	}

	@Override
	public int getBetreuung() {

		return 9;
	}

	@Override
	public int getAnspruch() {

		return 99;
	}

	@Override
	public int getBGPensum() {

		return 61;
	}

	@Override
	public BigDecimal getVollkosten() {

		return new BigDecimal(555.12545);
	}

	@Override
	public BigDecimal getElternbeitrag() {

		return new BigDecimal(555.12545);
	}

	@Override
	public BigDecimal getVerguenstigung() {

		return new BigDecimal(555.12545);
	}
}
