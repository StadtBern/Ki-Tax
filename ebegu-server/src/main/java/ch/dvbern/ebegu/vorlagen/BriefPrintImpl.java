package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;

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
public class BriefPrintImpl implements BriefPrint {

	protected Gesuch gesuch;

	public BriefPrintImpl(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	@Override
	public String getZustellAdresse() {

		String newlineMSWord = "\n";
		String zustellAdresse = "";
		String organisation = PrintUtil.getOrganisation(gesuch);

		if (StringUtils.isNotEmpty(organisation)) {
			zustellAdresse += organisation;
		} else {
			zustellAdresse += ServerMessageUtil.getMessage("BriefPrintImpl_FAMILIE");
			zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerName(gesuch);
		}

		zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerStrasse(gesuch);

		String adrZusatz = PrintUtil.getAdresszusatz(gesuch);

		if (StringUtils.isNotEmpty(adrZusatz)) {
			zustellAdresse += newlineMSWord + adrZusatz;
		}

		zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerPLZStadt(gesuch);

		return zustellAdresse;

	}

	@Override
	public String getZustellDatum() {
		return Constants.DATE_FORMATTER.format(LocalDate.now());
	}

	@Override
	public String getPeriode() {
		return getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()
			+ "/" + getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigBis().getYear();
	}

	@Override
	public String getFallNummer() {
		return PrintUtil.createFallNummerString(getGesuch());
	}

	@Override
	public String getGesuchEingangsDatum() {
		return Constants.DATE_FORMATTER.format(gesuch.getEingangsdatum());
	}

	@Override
	public String getUnterzeichner() {
		return gesuch.getFall().getVerantwortlicher().getFullName();
	}
}
