package ch.dvbern.ebegu.vorlagen.finanziellesituation;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 23.08.2016
*/

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.FinanzielleSituation;

/**
 * Klasse um die relevante Daten des Gesuch fuer Berechnung und Darstellung festzuhalten
 */
public class FinanzSituationPrintGesuchsteller {

	private FinanzielleSituation finanzielleSituation;
	private Einkommensverschlechterung einkommensverschlechterung1;
	private Einkommensverschlechterung einkommensverschlechterung2;
	private EinkommensverschlechterungInfo einkommensverschlechterungInfo;

	public FinanzSituationPrintGesuchsteller(FinanzielleSituation finanzielleSituation, Einkommensverschlechterung einkommensverschlechterung1,
											 Einkommensverschlechterung einkommensverschlechterung2, EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		this.finanzielleSituation = finanzielleSituation;
		this.einkommensverschlechterung1 = einkommensverschlechterung1;
		this.einkommensverschlechterung2 = einkommensverschlechterung2;
		this.einkommensverschlechterungInfo = einkommensverschlechterungInfo;
	}

	public EinkommensverschlechterungInfo getEinkommensverschlechterungInfo() {

		return einkommensverschlechterungInfo;
	}

	/**
	 * @return FinanzielleSituation
	 */
	public FinanzielleSituation getFinanzielleSituation() {

		return finanzielleSituation;
	}

	/**
	 * @return Einkommensverschlechterung erste einkommensverschlechterung
	 */
	public Einkommensverschlechterung getEinkommensverschlechterung1() {

		return einkommensverschlechterung1;
	}

	/**
	 * @return Einkommensverschlechterung  zweite einkommensverschlechterung
	 */
	public Einkommensverschlechterung getEinkommensverschlechterung2() {

		return einkommensverschlechterung2;
	}

}
