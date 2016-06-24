package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dieses Objekt repraesentiert einen Zeitabschnitt wahrend eines Betreeungsgutscheinantrags waehrend dem die Faktoren
 * die fuer die Berechnung des Gutscheins der Betreuung relevant sind konstant geblieben sind.
 *
 */
public class VerfuegungZeitabschnitt extends AbstractDateRangedEntity{

	private static final long serialVersionUID = 7250339356897563374L;


	private int betreuungspensum;
	private int anspruchspensumOriginal;
	private int anspruchberechtigtesPensum;
	private BigDecimal vollkosten;
	private BigDecimal elternbeitrag;
	private BigDecimal abzugFamGroesse;
	private BigDecimal massgebendesEinkommen;
	private BigDecimal betreuungsstunden;

	private List<String> bemerkungen;

	private String status;


	/**
	 * Erstellt einen Zeitabschnitt mit der gegebenen gueltigkeitsdauer
	 */
	public VerfuegungZeitabschnitt(DateRange gueltigkeit) {
		this.setGueltigkeit(new DateRange(gueltigkeit));
	}


	public int getBetreuungspensum() {
		return betreuungspensum;
	}

	public void setBetreuungspensum(int betreuungspensum) {
		this.betreuungspensum = betreuungspensum;
	}

	public int getAnspruchspensumOriginal() {
		return anspruchspensumOriginal;
	}

	public void setAnspruchspensumOriginal(int anspruchspensumOriginal) {
		this.anspruchspensumOriginal = anspruchspensumOriginal;
	}

	public int getAnspruchberechtigtesPensum() {
		return anspruchberechtigtesPensum;
	}

	public void setAnspruchberechtigtesPensum(int anspruchberechtigtesPensum) {
		this.anspruchberechtigtesPensum = anspruchberechtigtesPensum;
	}

	public BigDecimal getVollkosten() {
		return vollkosten;
	}

	public void setVollkosten(BigDecimal vollkosten) {
		this.vollkosten = vollkosten;
	}

	public BigDecimal getElternbeitrag() {
		return elternbeitrag;
	}

	public void setElternbeitrag(BigDecimal elternbeitrag) {
		this.elternbeitrag = elternbeitrag;
	}

	public BigDecimal getAbzugFamGroesse() {
		return abzugFamGroesse;
	}

	public void setAbzugFamGroesse(BigDecimal abzugFamGroesse) {
		this.abzugFamGroesse = abzugFamGroesse;
	}

	public BigDecimal getMassgebendesEinkommen() {
		return massgebendesEinkommen;
	}

	public void setMassgebendesEinkommen(BigDecimal massgebendesEinkommen) {
		this.massgebendesEinkommen = massgebendesEinkommen;
	}

	public List<String> getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(List<String> bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getBetreuungsstunden() {
		return betreuungsstunden;
	}

	public void setBetreuungsstunden(BigDecimal betreuungsstunden) {
		this.betreuungsstunden = betreuungsstunden;
	}

	/**
	 * Gibt den Betrag des Gutscheins zurück.
     */
	public BigDecimal getVerguenstigung() {
		return vollkosten.subtract(elternbeitrag);
	}
}
