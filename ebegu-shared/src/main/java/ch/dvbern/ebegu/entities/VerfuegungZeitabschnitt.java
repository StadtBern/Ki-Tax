package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Nonnull;
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
	private int fachstellenpensum;
	private int anspruchspensumRest;
	private int anspruchspensumOriginal; // = Gesamtanspruch für alle Kitas TODO (hefr) brauchts wohl eher nicht...
	private int anspruchberechtigtesPensum; // = Anpsruch für diese Kita, bzw. Tageseltern Kleinkinder
	private BigDecimal vollkosten = BigDecimal.ZERO;
	private BigDecimal elternbeitrag = BigDecimal.ZERO;
	private BigDecimal verguenstigung = BigDecimal.ZERO;
	private BigDecimal abzugFamGroesse = BigDecimal.ZERO;
	private BigDecimal massgebendesEinkommen = BigDecimal.ZERO;

	@Nonnull
	private List<String> bemerkungen = new ArrayList<>();

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

	public int getFachstellenpensum() {
		return fachstellenpensum;
	}

	public void setFachstellenpensum(int fachstellenpensum) {
		this.fachstellenpensum = fachstellenpensum;
	}

	public int getAnspruchspensumRest() {
		return anspruchspensumRest;
	}

	public void setAnspruchspensumRest(int anspruchspensumRest) {
		this.anspruchspensumRest = anspruchspensumRest;
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

	@Nonnull
	public List<String> getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nonnull List<String> bemerkungen) {
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
		this.setFachstellenpensum(this.getFachstellenpensum() + other.getFachstellenpensum());
		this.setAnspruchspensumOriginal(this.getAnspruchspensumOriginal() + other.getAnspruchspensumOriginal());
		this.setAnspruchspensumRest(this.getAnspruchspensumRest() + other.getAnspruchspensumRest());
		this.setAnspruchberechtigtesPensum(this.getAnspruchberechtigtesPensum() + other.getAnspruchberechtigtesPensum());
		this.setErwerbspensumGS1(this.getErwerbspensumGS1() + other.getErwerbspensumGS1());
		this.setErwerbspensumGS2(this.getErwerbspensumGS2() + other.getErwerbspensumGS2());
		BigDecimal massgebendesEinkommen = BigDecimal.ZERO;
		if (this.getMassgebendesEinkommen() != null) {
			massgebendesEinkommen = massgebendesEinkommen.add(this.getMassgebendesEinkommen());
		}
		if (other.getMassgebendesEinkommen() != null) {
			massgebendesEinkommen = massgebendesEinkommen.add(other.getMassgebendesEinkommen());
		}
		this.setMassgebendesEinkommen(massgebendesEinkommen);
	}

	/**
	 * Fügt eine Bemerkung zur Liste hinzu
     */
	public void addBemerkung(String bemerkung) {
		bemerkungen.add(bemerkung);
	}

	/**
	 * Fügt mehrere Bemerkungen zur Liste hinzu
	 */
	public void addAllBemerkungen(@Nullable List<String> bemerkungenList) {
		if (bemerkungenList != null) {
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
			fachstellenpensum == that.fachstellenpensum &&
			anspruchspensumOriginal == that.anspruchspensumOriginal &&
			anspruchspensumRest == that.anspruchspensumRest &&
			anspruchberechtigtesPensum == that.anspruchberechtigtesPensum &&
			Objects.equals(abzugFamGroesse, that.abzugFamGroesse) &&
			Objects.equals(massgebendesEinkommen, that.massgebendesEinkommen);
	}
}
