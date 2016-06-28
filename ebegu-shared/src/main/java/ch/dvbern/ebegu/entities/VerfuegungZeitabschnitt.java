package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Dieses Objekt repraesentiert einen Zeitabschnitt wahrend eines Betreeungsgutscheinantrags waehrend dem die Faktoren
 * die fuer die Berechnung des Gutscheins der Betreuung relevant sind konstant geblieben sind.
 *
 */
public class VerfuegungZeitabschnitt extends AbstractDateRangedEntity {

	private static final long serialVersionUID = 7250339356897563374L;

	private int erwerbspensumGS1;
	private int erwerbspensumGS2;
	private int betreuungspensum;
	private int anspruchspensumOriginal;
	private int anspruchberechtigtesPensum;
	private BigDecimal vollkosten;
	private BigDecimal elternbeitrag;
	private BigDecimal verguenstigung;
	private BigDecimal abzugFamGroesse;
	private BigDecimal massgebendesEinkommen;

	private List<String> bemerkungen;

	private String status;


	/**
	 * Erstellt einen Zeitabschnitt mit der gegebenen gueltigkeitsdauer
	 */
	public VerfuegungZeitabschnitt(DateRange gueltigkeit) {
		this.setGueltigkeit(new DateRange(gueltigkeit));
	}

	public int getErwerbspensumGS1() {
		return erwerbspensumGS1;
	}

	public void setErwerbspensumGS1(int erwerbspensumGS1) {
		this.erwerbspensumGS1 = erwerbspensumGS1;
	}

	public int getErwerbspensumGS2() {
		return erwerbspensumGS2;
	}

	public void setErwerbspensumGS2(int erwerbspensumGS2) {
		this.erwerbspensumGS2 = erwerbspensumGS2;
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

	public BigDecimal getVerguenstigung() {
		return verguenstigung;
	}

	public void setVerguenstigung(BigDecimal verguenstigung) {
		this.verguenstigung = verguenstigung;
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

	/**
	 * Addiert die Daten von "other" zu diesem VerfuegungsZeitabschnitt
     */
	public void add(VerfuegungZeitabschnitt other) {
		this.setBetreuungspensum(this.getBetreuungspensum() + other.getBetreuungspensum());
		this.setAnspruchspensumOriginal(this.getAnspruchspensumOriginal() + other.getAnspruchspensumOriginal());
		this.setErwerbspensumGS1(this.getErwerbspensumGS1() + other.getErwerbspensumGS1());
		this.setErwerbspensumGS2(this.getErwerbspensumGS2() + other.getErwerbspensumGS2());
	}

	/**
	 * Fügt eine Bemerkung zur Liste hinzu
     */
	public void addBemerkung(String bemerkung) {
		if (bemerkungen == null) {
			bemerkungen = new ArrayList<>();
		}
		bemerkungen.add(bemerkung);
	}

	/**
	 * Fügt mehrere Bemerkungen zur Liste hinzu
	 */
	public void addAllBemerkungen(@Nullable List<String> bemerkungenList) {
		if (bemerkungenList != null) {
			if (bemerkungen == null) {
				bemerkungen = new ArrayList<>();
			}
			bemerkungen.addAll(bemerkungenList);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("[").append(Constants.DATE_FORMATTER.format(getGueltigkeit().getGueltigAb())).append(" - ").append(Constants.DATE_FORMATTER.format(getGueltigkeit().getGueltigBis())).append("] ")
			.append("erwerbspensumGS1", erwerbspensumGS1)
			.append("erwerbspensumGS2", erwerbspensumGS2)
			.append("betreuungspensum", betreuungspensum)
			.append("anspruchspensumOriginal", anspruchspensumOriginal)
			.append("bemerkungen", bemerkungen)
			.toString();
	}

	public boolean isSame(VerfuegungZeitabschnitt that) {
		if (this == that) return true;
		return erwerbspensumGS1 == that.erwerbspensumGS1 &&
			erwerbspensumGS2 == that.erwerbspensumGS2 &&
			betreuungspensum == that.betreuungspensum &&
			anspruchspensumOriginal == that.anspruchspensumOriginal &&
			anspruchberechtigtesPensum == that.anspruchberechtigtesPensum &&
			Objects.equals(abzugFamGroesse, that.abzugFamGroesse) &&
			Objects.equals(massgebendesEinkommen, that.massgebendesEinkommen);
	}
}
