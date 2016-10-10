package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.util.Constants;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * DTO fuer Verfuegung Zeitabschnitte. Gehoert immer zu einer Verfuegung welche weiderum zu einen Betreuung gehoert
 */
@XmlRootElement(name = "pensumFachstelle")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxVerfuegungZeitabschnitt extends JaxAbstractDateRangedDTO {


	private static final long serialVersionUID = 5116358042804364490L;

	@Max(100)
	@Min(0)
	@NotNull
	private int erwerbspensumGS1;

	@Max(100)
	@Min(0)
	@NotNull
	private int erwerbspensumGS2;

	@Max(100)
	@Min(0)
	@NotNull
	private int betreuungspensum;

	@Max(100)
	@Min(0)
	@NotNull
	private int fachstellenpensum;

	@Max(100)
	@Min(0)
	@NotNull
	private int anspruchspensumRest;

	@Max(100)
	@Min(0)
	@NotNull
	private int anspruchberechtigtesPensum; // = Anpsruch für diese Kita, bzw. Tageseltern Kleinkinder

	private int bgPensum; //min von anspruchberechtigtesPensum und betreuungspensum

	public int getBgPensum() {
		return bgPensum;
	}

	public void setBgPensum(int bgPensum) {
		this.bgPensum = bgPensum;
	}

	private BigDecimal betreuungsstunden;

	private BigDecimal vollkosten = BigDecimal.ZERO;

	private BigDecimal elternbeitrag = BigDecimal.ZERO;

	private BigDecimal abzugFamGroesse = BigDecimal.ZERO;

	private BigDecimal famGroesse;

	private BigDecimal massgebendesEinkommenVorAbzugFamgr = BigDecimal.ZERO;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	private String bemerkungen ;

	@Transient
	private String status;

//	@NotNull
//	@ManyToOne(optional = false)
//	@JoinColumn(foreignKey = @ForeignKey(name = "FK_verfuegung_zeitabschnitt_verfuegung_id"), nullable = false)
//	private Verfuegung verfuegung;


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

	public int getAnspruchberechtigtesPensum() {
		return anspruchberechtigtesPensum;
	}

	public void setAnspruchberechtigtesPensum(int anspruchberechtigtesPensum) {
		this.anspruchberechtigtesPensum = anspruchberechtigtesPensum;
	}

	public BigDecimal getBetreuungsstunden() {
		return betreuungsstunden;
	}

	public void setBetreuungsstunden(BigDecimal betreuungsstunden) {
		this.betreuungsstunden = betreuungsstunden;
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

	public BigDecimal getMassgebendesEinkommenVorAbzugFamgr() {
		return massgebendesEinkommenVorAbzugFamgr;
	}

	public void setMassgebendesEinkommenVorAbzugFamgr(BigDecimal massgebendesEinkommenVorAbzugFamgr) {
		this.massgebendesEinkommenVorAbzugFamgr = massgebendesEinkommenVorAbzugFamgr;
	}

	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getFamGroesse() {
		return famGroesse;
	}

	public void setFamGroesse(BigDecimal famGroesse) {
		this.famGroesse = famGroesse;
	}
}
