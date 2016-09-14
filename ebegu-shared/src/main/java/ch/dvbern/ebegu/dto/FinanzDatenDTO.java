package ch.dvbern.ebegu.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO für die Resultate der Berechnungen der Finanziellen Situation und eventueller Einkommensverschlechterungen.
 *
 * die Werte massgebendesEinkommenBasisjahrPlus1, massgebendesEinkommenBasisjahrPlus1 und datumVonBasisjahrPlus1 sowie
 * datumVonBasisjahrPlus2 sind nur gesetzt wenn die jeweilige Einkommensverschlechterung akzeptiert wurde
 */
public class FinanzDatenDTO {

	private BigDecimal massgebendesEinkommenBasisjahr = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkommenBasisjahrPlus1 = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkommenBasisjahrPlus2 = BigDecimal.ZERO;

	private LocalDate datumVonBasisjahr = null; // Start Gesuchsperiode
	private LocalDate datumVonBasisjahrPlus1 = null; // 1. des ausgewählten Monats
	private LocalDate datumVonBasisjahrPlus2 = null; // 1. des ausgewählten Monats


	public BigDecimal getMassgebendesEinkommenBasisjahr() {
		return massgebendesEinkommenBasisjahr;
	}

	public void setMassgebendesEinkommenBasisjahr(BigDecimal massgebendesEinkommenBasisjahr) {
		this.massgebendesEinkommenBasisjahr = massgebendesEinkommenBasisjahr;
	}

	public BigDecimal getMassgebendesEinkommenBasisjahrPlus1() {
		return massgebendesEinkommenBasisjahrPlus1;
	}

	public void setMassgebendesEinkommenBasisjahrPlus1(BigDecimal massgebendesEinkommenBasisjahrPlus1) {
		this.massgebendesEinkommenBasisjahrPlus1 = massgebendesEinkommenBasisjahrPlus1;
	}

	public BigDecimal getMassgebendesEinkommenBasisjahrPlus2() {
		return massgebendesEinkommenBasisjahrPlus2;
	}

	public void setMassgebendesEinkommenBasisjahrPlus2(BigDecimal massgebendesEinkommenBasisjahrPlus2) {
		this.massgebendesEinkommenBasisjahrPlus2 = massgebendesEinkommenBasisjahrPlus2;
	}

	public LocalDate getDatumVonBasisjahr() {
		return datumVonBasisjahr;
	}

	public void setDatumVonBasisjahr(LocalDate datumVonBasisjahr) {
		this.datumVonBasisjahr = datumVonBasisjahr;
	}

	public LocalDate getDatumVonBasisjahrPlus1() {
		return datumVonBasisjahrPlus1;
	}

	public void setDatumVonBasisjahrPlus1(LocalDate datumVonBasisjahrPlus1) {
		this.datumVonBasisjahrPlus1 = datumVonBasisjahrPlus1;
	}

	public LocalDate getDatumVonBasisjahrPlus2() {
		return datumVonBasisjahrPlus2;
	}

	public void setDatumVonBasisjahrPlus2(LocalDate datumVonBasisjahrPlus2) {
		this.datumVonBasisjahrPlus2 = datumVonBasisjahrPlus2;
	}
}
