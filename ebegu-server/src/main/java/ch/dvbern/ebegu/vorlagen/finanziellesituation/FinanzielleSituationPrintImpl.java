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
* Ersteller: zeab am: 22.08.2016
*/

/**
 * DTO fuer den den FinanzielleSituation Print. Ergenzt FinanzDatenPrintImpl um einige Funktionen
 */
public class FinanzielleSituationPrintImpl extends FinanzDatenPrintImpl implements FinanzielleSituationPrint {

	private String finanzielleSituationJahr;
	private String fallNummer;

	/**
	 * Konstruktor
	 *
	 * @param finanzielleSituationG1
	 * @param finanzielleSituationG2
	 * @param fallNummer
	 */
	public FinanzielleSituationPrintImpl(FinanzSituationPrintGesuchsteller fsGesuchsteller1, FinanzSituationPrintGesuchsteller fsGesuchsteller2, String finanzielleSituationJahr,
			String fallNummer) {

		super(fsGesuchsteller1, fsGesuchsteller2);
		this.finanzielleSituationJahr = finanzielleSituationJahr;
		this.fallNummer = fallNummer;

	}

	@Override
	public String getFallNummer() {

		return fallNummer;
	}

	@Override
	public String getFinanzielleSituationJahr() {

		return finanzielleSituationJahr;
	}
}
