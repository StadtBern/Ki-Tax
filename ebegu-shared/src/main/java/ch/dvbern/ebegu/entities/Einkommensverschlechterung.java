package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.MathUtil;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Objects;

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

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus1;

	public Einkommensverschlechterung() {
	}


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

	public BigDecimal getGeschaeftsgewinnBasisjahrMinus1() {
		return geschaeftsgewinnBasisjahrMinus1;
	}

	public void setGeschaeftsgewinnBasisjahrMinus1(BigDecimal geschaeftsgewinnBasisjahrMinus1) {
		this.geschaeftsgewinnBasisjahrMinus1 = geschaeftsgewinnBasisjahrMinus1;
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
			nettolohnOkt, nettolohnNov, nettolohnDez, nettolohnZus);
	}

	public Einkommensverschlechterung copyForMutation(Einkommensverschlechterung mutation) {
		super.copyForMutation(mutation);
		mutation.setNettolohnJan(this.getNettolohnJan());
		mutation.setNettolohnFeb(this.getNettolohnFeb());
		mutation.setNettolohnMrz(this.getNettolohnMrz());
		mutation.setNettolohnApr(this.getNettolohnApr());
		mutation.setNettolohnMai(this.getNettolohnMai());
		mutation.setNettolohnJun(this.getNettolohnJun());
		mutation.setNettolohnJul(this.getNettolohnJul());
		mutation.setNettolohnAug(this.getNettolohnAug());
		mutation.setNettolohnSep(this.getNettolohnSep());
		mutation.setNettolohnOkt(this.getNettolohnOkt());
		mutation.setNettolohnNov(this.getNettolohnNov());
		mutation.setNettolohnDez(this.getNettolohnDez());
		mutation.setNettolohnZus(this.getNettolohnZus());
		mutation.setGeschaeftsgewinnBasisjahrMinus1(this.getGeschaeftsgewinnBasisjahrMinus1());
		return mutation;
	}

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!super.isSame(other)) {
			return false;
		}
		final Einkommensverschlechterung otherEinkommensverschlechterung = (Einkommensverschlechterung) other;
		return Objects.equals(getNettolohnJan(), otherEinkommensverschlechterung.getNettolohnJan()) &&
			Objects.equals(getNettolohnFeb(), otherEinkommensverschlechterung.getNettolohnFeb()) &&
			Objects.equals(getNettolohnMrz(), otherEinkommensverschlechterung.getNettolohnMrz()) &&
			Objects.equals(getNettolohnApr(), otherEinkommensverschlechterung.getNettolohnApr()) &&
			Objects.equals(getNettolohnMai(), otherEinkommensverschlechterung.getNettolohnMai()) &&
			Objects.equals(getNettolohnJun(), otherEinkommensverschlechterung.getNettolohnJun()) &&
			Objects.equals(getNettolohnJul(), otherEinkommensverschlechterung.getNettolohnJul()) &&
			Objects.equals(getNettolohnAug(), otherEinkommensverschlechterung.getNettolohnAug()) &&
			Objects.equals(getNettolohnSep(), otherEinkommensverschlechterung.getNettolohnSep()) &&
			Objects.equals(getNettolohnOkt(), otherEinkommensverschlechterung.getNettolohnOkt()) &&
			Objects.equals(getNettolohnNov(), otherEinkommensverschlechterung.getNettolohnNov()) &&
			Objects.equals(getNettolohnDez(), otherEinkommensverschlechterung.getNettolohnDez()) &&
			Objects.equals(getNettolohnZus(), otherEinkommensverschlechterung.getNettolohnZus()) &&
			Objects.equals(getGeschaeftsgewinnBasisjahrMinus1(), otherEinkommensverschlechterung.getGeschaeftsgewinnBasisjahrMinus1());
	}
}
