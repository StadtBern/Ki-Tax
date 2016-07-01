package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * DTO fuer Familiensituationen
 */
@XmlRootElement(name = "einkommensverschlechterung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEinkommensverschlechterung extends JaxAbstractFinanzielleSituation {

	private static final long serialVersionUID = 3659631207762053261L;

	@Nullable
	private BigDecimal nettolohnJan;

	@Nullable
	private BigDecimal nettolohnFeb;

	@Nullable
	private BigDecimal nettolohnMrz;

	@Nullable
	private BigDecimal nettolohnApr;

	@Nullable
	private BigDecimal nettolohnMai;

	@Nullable
	private BigDecimal nettolohnJun;

	@Nullable
	private BigDecimal nettolohnJul;

	@Nullable
	private BigDecimal nettolohnAug;

	@Nullable
	private BigDecimal nettolohnSep;

	@Nullable
	private BigDecimal nettolohnOkt;

	@Nullable
	private BigDecimal nettolohnNov;

	@Nullable
	private BigDecimal nettolohnDez;

	@Nullable
	public BigDecimal getNettolohnJan() {
		return nettolohnJan;
	}

	public void setNettolohnJan(@Nullable final BigDecimal nettolohnJan) {
		this.nettolohnJan = nettolohnJan;
	}

	@Nullable
	public BigDecimal getNettolohnFeb() {
		return nettolohnFeb;
	}

	public void setNettolohnFeb(@Nullable final BigDecimal nettolohnFeb) {
		this.nettolohnFeb = nettolohnFeb;
	}

	@Nullable
	public BigDecimal getNettolohnMrz() {
		return nettolohnMrz;
	}

	public void setNettolohnMrz(@Nullable final BigDecimal nettolohnMrz) {
		this.nettolohnMrz = nettolohnMrz;
	}

	@Nullable
	public BigDecimal getNettolohnApr() {
		return nettolohnApr;
	}

	public void setNettolohnApr(@Nullable final BigDecimal nettolohnApr) {
		this.nettolohnApr = nettolohnApr;
	}

	@Nullable
	public BigDecimal getNettolohnMai() {
		return nettolohnMai;
	}

	public void setNettolohnMai(@Nullable final BigDecimal nettolohnMai) {
		this.nettolohnMai = nettolohnMai;
	}

	@Nullable
	public BigDecimal getNettolohnJun() {
		return nettolohnJun;
	}

	public void setNettolohnJun(@Nullable final BigDecimal nettolohnJun) {
		this.nettolohnJun = nettolohnJun;
	}

	@Nullable
	public BigDecimal getNettolohnJul() {
		return nettolohnJul;
	}

	public void setNettolohnJul(@Nullable final BigDecimal nettolohnJul) {
		this.nettolohnJul = nettolohnJul;
	}

	@Nullable
	public BigDecimal getNettolohnAug() {
		return nettolohnAug;
	}

	public void setNettolohnAug(@Nullable final BigDecimal nettolohnAug) {
		this.nettolohnAug = nettolohnAug;
	}

	@Nullable
	public BigDecimal getNettolohnSep() {
		return nettolohnSep;
	}

	public void setNettolohnSep(@Nullable final BigDecimal nettolohnSep) {
		this.nettolohnSep = nettolohnSep;
	}

	@Nullable
	public BigDecimal getNettolohnOkt() {
		return nettolohnOkt;
	}

	public void setNettolohnOkt(@Nullable final BigDecimal nettolohnOkt) {
		this.nettolohnOkt = nettolohnOkt;
	}

	@Nullable
	public BigDecimal getNettolohnNov() {
		return nettolohnNov;
	}

	public void setNettolohnNov(@Nullable final BigDecimal nettolohnNov) {
		this.nettolohnNov = nettolohnNov;
	}

	@Nullable
	public BigDecimal getNettolohnDez() {
		return nettolohnDez;
	}

	public void setNettolohnDez(@Nullable final BigDecimal nettolohnDez) {
		this.nettolohnDez = nettolohnDez;
	}
}
