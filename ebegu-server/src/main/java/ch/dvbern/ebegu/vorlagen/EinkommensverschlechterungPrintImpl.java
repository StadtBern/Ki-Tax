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

import java.math.BigDecimal;

import ch.dvbern.ebegu.entities.Einkommensverschlechterung;

/**
 * Implementiert den {@link EinkommensverschlechterungPrint}
 */
public class EinkommensverschlechterungPrintImpl extends FinanzDatenPrintImpl implements EinkommensverschlechterungPrint {

	private String einkommensverschlechterungJahr;
	private String ereigniseintritt;
	private String grund;
	private Einkommensverschlechterung ev1;
	private Einkommensverschlechterung ev2;

	/**
	 * Konstruktor
	 *
	 * @param fsGesuchsteller1 das {@link FinanzSituationGesuchsteller1}
	 * @param fsGesuchsteller2 das {@link FinanzSituationGesuchsteller2}
	 * @param einkommensverschlechterungJahr das Jahr des Einkommenverschleschterung
	 * @param ereigniseintritt Ereingis datum
	 * @param grund Grund
	 */
	public EinkommensverschlechterungPrintImpl(FinanzSituationPrintGesuchsteller fsGesuchsteller1, FinanzSituationPrintGesuchsteller fsGesuchsteller2,
			String einkommensverschlechterungJahr, String ereigniseintritt, String grund) {

		super(fsGesuchsteller1, fsGesuchsteller2);

		this.einkommensverschlechterungJahr = einkommensverschlechterungJahr;
		this.ereigniseintritt = ereigniseintritt;
		this.grund = grund;
		this.ev1 = fsGesuchsteller1.getEinkommensverschlechterung1();
		if (fsGesuchsteller2 != null && fsGesuchsteller2.getEinkommensverschlechterung2() != null) {
			this.ev2 = fsGesuchsteller2.getEinkommensverschlechterung2();
		}

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

	@Override
	public BigDecimal getNettolohnG1() {

		return ev1.getNettolohn();
	}

	@Override
	public BigDecimal getNettolohnG2() {

		return ev2 != null ? ev2.getNettolohn() : null;
	}

	@Override
	public BigDecimal getFamilienzulagenG1() {

		return ev1.getFamilienzulage();
	}

	@Override
	public BigDecimal getFamilienzulagenG2() {

		return ev2 != null ? ev2.getFamilienzulage() : null;
	}

	@Override
	public BigDecimal getErsatzeinkommenG1() {

		return ev1.getErsatzeinkommen();
	}

	@Override
	public BigDecimal getErsatzeinkommenG2() {

		return ev2 != null ? ev2.getErsatzeinkommen() : null;
	}

	@Override
	public BigDecimal getUnterhaltsbeitraegeG1() {

		return ev1.getErhalteneAlimente();
	}

	@Override
	public BigDecimal getUnterhaltsbeitraegeG2() {

		return ev2 != null ? ev2.getErhalteneAlimente() : null;
	}

	@Override
	public BigDecimal getGeschaeftsgewinnG1() {

		return ev1.getGeschaeftsgewinnBasisjahr();
	}

	@Override
	public BigDecimal getGeschaeftsgewinnG2() {

		return ev2 != null ? ev2.getGeschaeftsgewinnBasisjahr() : null;
	}

	@Override
	public BigDecimal getZwischentotalEinkuenfteG1() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getZwischentotalEinkuenfteG2() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getTotalEinkuenfte() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getBruttovermoegenG1() {

		return ev1.getBruttovermoegen();
	}

	@Override
	public BigDecimal getBruttovermoegenG2() {

		return ev2 != null ? ev2.getBruttovermoegen() : null;
	}

	@Override
	public BigDecimal getSchuldenG1() {

		return ev1.getSchulden();
	}

	@Override
	public BigDecimal getSchuldenG2() {

		return ev2 != null ? ev2.getSchulden() : null;
	}

	@Override
	public BigDecimal getZwischentotalNettovermoegenBeiderGesuchsteller1() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getZwischentotalNettovermoegenBeiderGesuchsteller2() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getZwischentotalNettovermoegenInsgesamt() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getNettovermoegen() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getAbzuegeBeiEinerFamiliengroesseVon5Personen() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public int getAnzahlPersonen() {

		// TODO Implementieren
		return 9;
	}

	@Override
	public BigDecimal getTotalAbzuege() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getZusammenzugTotaleinkuenfte() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getZusammenzugNettovermoegen() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getZusammenzugTotalAbzuege() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}

	@Override
	public BigDecimal getMassgebendesEinkommen() {

		// TODO Implementieren
		return new BigDecimal(11111);
	}
}
