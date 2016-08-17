package ch.dvbern.ebegu.services.vorlagen;
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

import java.math.BigDecimal;

import ch.dvbern.ebegu.util.Constants;

public class VerfuegungZeitabschnittImpl implements VerfuegungZeitabschnitt {

	private ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt verfuegungZeitabschnitt;

	public VerfuegungZeitabschnittImpl(ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		this.verfuegungZeitabschnitt = verfuegungZeitabschnitt;
	}

	@Override
	public String getVon() {

		return Constants.DATE_FORMATTER.format(verfuegungZeitabschnitt.getGueltigkeit().getGueltigAb());
	}

	@Override
	public String getBis() {

		return Constants.DATE_FORMATTER.format(verfuegungZeitabschnitt.getGueltigkeit().getGueltigBis());
	}

	@Override
	public int getBetreuung() {

		return verfuegungZeitabschnitt.getBetreuungspensum();
	}

	@Override
	public int getAnspruch() {

		return verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();
	}

	@Override
	public int getBGPensum() {

		return verfuegungZeitabschnitt.getAnspruchberechtigtesPensum();//// TODO (hefr) spaeter das
	}

	@Override
	public BigDecimal getVollkosten() {

		return verfuegungZeitabschnitt.getVollkosten();
	}

	@Override
	public BigDecimal getElternbeitrag() {

		return verfuegungZeitabschnitt.getElternbeitrag();
	}

	@Override
	public BigDecimal getVerguenstigung() {

		return verfuegungZeitabschnitt.getVerguenstigung();
	}
}
