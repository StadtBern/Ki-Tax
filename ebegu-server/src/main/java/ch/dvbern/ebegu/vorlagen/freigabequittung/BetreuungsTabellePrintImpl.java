package ch.dvbern.ebegu.vorlagen.freigabequittung;
/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 05/12/2016.
 */

import ch.dvbern.ebegu.entities.Betreuung;

public class BetreuungsTabellePrintImpl implements BetreuungsTabellePrint {

	private Betreuung betreuungen;

	public BetreuungsTabellePrintImpl(Betreuung betreuungen) {
		this.betreuungen = betreuungen;
	}

	@Override
	public String getKind() {
		return betreuungen.getKind().getKindJA().getFullName();
	}

	@Override
	public String getBetreuung() {
		return betreuungen.getInstitutionStammdaten().getInstitution().getName();
	}

	@Override
	public String getIDNummer() {
		return betreuungen.getBGNummer();
	}
}
