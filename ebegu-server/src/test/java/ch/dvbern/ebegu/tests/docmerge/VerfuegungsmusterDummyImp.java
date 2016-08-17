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

import java.util.ArrayList;
import java.util.List;

import ch.dvbern.ebegu.services.vorlagen.VerfuegungZeitabschnittPrint;
import ch.dvbern.ebegu.services.vorlagen.VerfuegungPrint;

public class VerfuegungsmusterDummyImp implements VerfuegungPrint {

	@Override
	public String getGesuchstellerName() {

		return "Estefania Anon";
	}

	@Override
	public String getGesuchstellerStrasse() {

		return "Lentulusstrasse 33";
	}

	@Override
	public String getGesuchstellerPLZStadt() {

		return "3007 Bern";
	}

	@Override
	public String getReferenzNummer() {

		return "16.000024.2.1";
	}

	@Override
	public String getVerfuegungsdatum() {

		return "31.03.2016";
	}

	@Override
	public String getGesuchsteller1() {

		return "Pedro Liam";
	}

	@Override
	public String getGesuchsteller2() {

		return "Pedra Liam";
	}

	@Override
	public String getKindNameVorname() {

		return "Liam Leandro Junius";
	}

	@Override
	public String getKindGeburtsdatum() {

		return "25.01.2016";
	}

	@Override
	public String getKitaBezeichnung() {

		return "Kita Rappard";
	}

	@Override
	public String getAnspruchAb() {

		return "1. August 2016";
	}

	@Override
	public String getAnspruchBis() {

		return "31. Juli 2017";
	}

	@Override
	public List<VerfuegungZeitabschnittPrint> getVerfuegungZeitabschnitt() {

		List<VerfuegungZeitabschnittPrint> list = new ArrayList<>();

		list.add(new VerfuegungZeitabschnittPrintDTODummy());
		list.add(new VerfuegungZeitabschnittPrintDTODummy());
		list.add(new VerfuegungZeitabschnittPrintDTODummy());
		list.add(new VerfuegungZeitabschnittPrintDTODummy());
		list.add(new VerfuegungZeitabschnittPrintDTODummy());
		list.add(new VerfuegungZeitabschnittPrintDTODummy());
		list.add(new VerfuegungZeitabschnittPrintDTODummy());

		return list;
	}

	@Override
	public String getBemerkung() {

		return "- Kein Wohnort";
	}

	@Override
	public boolean existGesuchsteller2() {

		return true;
	}

	@Override
	public boolean isPensumGrosser0() {

		return true;
	}

	@Override
	public boolean isMutation() {

		return true;
	}
}
