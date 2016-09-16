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


	private BigDecimal massgebendesEinkBjVorAbzFamGr = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkBjP1VorAbzFamGr = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkBjP2VorAbzFamGr = BigDecimal.ZERO;

	private LocalDate datumVonBasisjahr = null; // Start Gesuchsperiode
	private LocalDate datumVonBasisjahrPlus1 = null; // 1. des ausgewählten Monats
	private LocalDate datumVonBasisjahrPlus2 = null; // 1. des ausgewählten Monats


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

	public BigDecimal getMassgebendesEinkBjVorAbzFamGr() {
		return massgebendesEinkBjVorAbzFamGr;
	}

	public void setMassgebendesEinkBjVorAbzFamGr(BigDecimal massgebendesEinkBjVorAbzFamGr) {
		this.massgebendesEinkBjVorAbzFamGr = massgebendesEinkBjVorAbzFamGr;
	}

	public BigDecimal getMassgebendesEinkBjP1VorAbzFamGr() {
		return massgebendesEinkBjP1VorAbzFamGr;
	}

	public void setMassgebendesEinkBjP1VorAbzFamGr(BigDecimal massgebendesEinkBjP1VorAbzFamGr) {
		this.massgebendesEinkBjP1VorAbzFamGr = massgebendesEinkBjP1VorAbzFamGr;
	}

	public BigDecimal getMassgebendesEinkBjP2VorAbzFamGr() {
		return massgebendesEinkBjP2VorAbzFamGr;
	}

	public void setMassgebendesEinkBjP2VorAbzFamGr(BigDecimal massgebendesEinkBjP2VorAbzFamGr) {
		this.massgebendesEinkBjP2VorAbzFamGr = massgebendesEinkBjP2VorAbzFamGr;
	}
}
