package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.MathUtil;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Entität für die Einkommensverschlechterung
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@Entity
public class Einkommensverschlechterung extends AbstractFinanzielleSituation {

	private static final long serialVersionUID = -8959552696602183511L;

	@Column(nullable = true)
	private BigDecimal nettolohnJan;

	@Column(nullable = true)
	private BigDecimal nettolohnFeb;

	@Column(nullable = true)
	private BigDecimal nettolohnMrz;

	@Column(nullable = true)
	private BigDecimal nettolohnApr;

	@Column(nullable = true)
	private BigDecimal nettolohnMai;

	@Column(nullable = true)
	private BigDecimal nettolohnJun;

	@Column(nullable = true)
	private BigDecimal nettolohnJul;

	@Column(nullable = true)
	private BigDecimal nettolohnAug;

	@Column(nullable = true)
	private BigDecimal nettolohnSep;

	@Column(nullable = true)
	private BigDecimal nettolohnOkt;

	@Column(nullable = true)
	private BigDecimal nettolohnNov;

	@Column(nullable = true)
	private BigDecimal nettolohnDez;

	@Column(nullable = true)
	private BigDecimal nettolohnZus;

	public BigDecimal getNettolohnJan() {
		return nettolohnJan;
	}

	public void setNettolohnJan(final BigDecimal nettolohnJan) {
		this.nettolohnJan = nettolohnJan;
	}

	public BigDecimal getNettolohnFeb() {
		return nettolohnFeb;
	}

	public void setNettolohnFeb(final BigDecimal nettolohnFeb) {
		this.nettolohnFeb = nettolohnFeb;
	}

	public BigDecimal getNettolohnMrz() {
		return nettolohnMrz;
	}

	public void setNettolohnMrz(final BigDecimal nettolohnMrz) {
		this.nettolohnMrz = nettolohnMrz;
	}

	public BigDecimal getNettolohnApr() {
		return nettolohnApr;
	}

	public void setNettolohnApr(final BigDecimal nettolohnApr) {
		this.nettolohnApr = nettolohnApr;
	}

	public BigDecimal getNettolohnMai() {
		return nettolohnMai;
	}

	public void setNettolohnMai(final BigDecimal nettolohnMai) {
		this.nettolohnMai = nettolohnMai;
	}

	public BigDecimal getNettolohnJun() {
		return nettolohnJun;
	}

	public void setNettolohnJun(final BigDecimal nettolohnJun) {
		this.nettolohnJun = nettolohnJun;
	}

	public BigDecimal getNettolohnJul() {
		return nettolohnJul;
	}

	public void setNettolohnJul(final BigDecimal nettolohnJul) {
		this.nettolohnJul = nettolohnJul;
	}

	public BigDecimal getNettolohnAug() {
		return nettolohnAug;
	}

	public void setNettolohnAug(final BigDecimal nettolohnAug) {
		this.nettolohnAug = nettolohnAug;
	}

	public BigDecimal getNettolohnSep() {
		return nettolohnSep;
	}

	public void setNettolohnSep(final BigDecimal nettolohnSep) {
		this.nettolohnSep = nettolohnSep;
	}

	public BigDecimal getNettolohnOkt() {
		return nettolohnOkt;
	}

	public void setNettolohnOkt(final BigDecimal nettolohnOkt) {
		this.nettolohnOkt = nettolohnOkt;
	}

	public BigDecimal getNettolohnNov() {
		return nettolohnNov;
	}

	public void setNettolohnNov(final BigDecimal nettolohnNov) {
		this.nettolohnNov = nettolohnNov;
	}

	public BigDecimal getNettolohnDez() {
		return nettolohnDez;
	}

	public void setNettolohnDez(final BigDecimal nettolohnDez) {
		this.nettolohnDez = nettolohnDez;
	}

	public BigDecimal getNettolohnZus() {

		return nettolohnZus;
	}

	public void setNettolohnZus(BigDecimal nettolohnZus) {

		this.nettolohnZus = nettolohnZus;
	}

	@Override
	public BigDecimal getNettolohn() {

		return MathUtil.DEFAULT.add(nettolohnJan, nettolohnFeb, nettolohnMrz, nettolohnApr,
			nettolohnMai, nettolohnJun, nettolohnJul, nettolohnAug, nettolohnSep,
			nettolohnOkt, nettolohnNov, nettolohnDez);
	}
}
