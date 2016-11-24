package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.util.Constants;
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

	private Gesuch gesuch;

	public BriefPrintImpl(Gesuch gesuch){
		this.gesuch = gesuch;
	}

	@Override
	public String getZustellAdresse() {

		String newlineMSWord = "\n";
		String zustellAdresse = "";
		String organisation = PrintUtil.getOrganisation(gesuch);

		if (StringUtils.isNotEmpty(organisation)) {
			zustellAdresse += organisation;
		}
		else {
			zustellAdresse += "Familie";  //TODO: resources/localisation?
		}

		zustellAdresse += newlineMSWord + PrintUtil.getGesuchstellerName(gesuch);
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
	public String getUnterzeichner() {
		return gesuch.getUserMutiert();
	}
}
