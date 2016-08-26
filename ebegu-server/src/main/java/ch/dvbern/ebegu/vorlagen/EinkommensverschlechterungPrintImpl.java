package ch.dvbern.ebegu.vorlagen;
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
 * Implementiert den {@link EinkommensverschlechterungPrint}
 */
public class EinkommensverschlechterungPrintImpl extends FinanzDatenPrintImpl implements EinkommensverschlechterungPrint {

	private String einkommensverschlechterungJahr;
	private String ereigniseintritt;
	private String grund;

	/**
	 * Konstruktor
	 *
	 * @param fsGesuchsteller1 das {@link FinanzSituationGesuchsteller1}
	 * @param fsGesuchsteller2 das {@link FinanzSituationGesuchsteller2}
	 * @param einkommensverschlechterungJahr das Jahr des Einkommenverschleschterung
	 * @param ereigniseintritt Ereingis datum
	 * @param grund Grund
	 */
	public EinkommensverschlechterungPrintImpl(FinanzSituationPrintGesuchsteller fsGesuchsteller1, FinanzSituationPrintGesuchsteller fsGesuchsteller2, String einkommensverschlechterungJahr,
											   String ereigniseintritt, String grund) {

		super(fsGesuchsteller1, fsGesuchsteller2);

		this.einkommensverschlechterungJahr = einkommensverschlechterungJahr;
		this.ereigniseintritt = ereigniseintritt;
		this.grund = grund;
	}

	@Override
	public String getEinkommensverschlechterungJahr() {
		return einkommensverschlechterungJahr;
	}

	@Override
	public String getEreigniseintritt() {
		return ereigniseintritt;
	}

	@Override
	public String getGrund() {
		return grund;
	}
}
