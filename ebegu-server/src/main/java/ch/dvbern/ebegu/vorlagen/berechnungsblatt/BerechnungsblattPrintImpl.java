package ch.dvbern.ebegu.vorlagen.berechnungsblatt;
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

import java.math.BigDecimal;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.util.Constants;

public class BerechnungsblattPrintImpl implements BerechnungsblattPrint {

	private VerfuegungZeitabschnitt verfuegungZeitabschnitt;

	public BerechnungsblattPrintImpl(VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
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
	public BigDecimal getMassgebendesEinkommenVorAbzFamgr() {

		return verfuegungZeitabschnitt.getMassgebendesEinkommenVorAbzFamgr();
	}

	@Override
	public String getFamiliengroesse() {

		BigDecimal value = verfuegungZeitabschnitt.getFamiliengroesse() != null ? verfuegungZeitabschnitt.getFamiliengroesse() : BigDecimal.ZERO;
		if (value.compareTo(BigDecimal.valueOf(value.intValue())) > 0) {
			value = value.setScale(2, BigDecimal.ROUND_DOWN);
			return value.toString();
		} else {
			return Integer.toString(value.intValue());
		}
	}

	@Override
	public BigDecimal getAbzugFamGroesse() {

		return verfuegungZeitabschnitt.getAbzugFamGroesse();
	}

	@Override
	public BigDecimal getMassgebendesEinkommenNachAbzugFamgr() {

		return verfuegungZeitabschnitt.getMassgebendesEinkommenNachAbzugFamgr();
	}
}
