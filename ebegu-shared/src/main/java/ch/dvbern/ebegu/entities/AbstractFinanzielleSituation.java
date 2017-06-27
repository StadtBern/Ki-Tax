package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.MathUtil;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Gemeinsame Basisklasse für FinanzielleSituation und Einkommensverschlechterung
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractFinanzielleSituation extends AbstractEntity {

	private static final long serialVersionUID = 2596930494846119259L;

	@NotNull
	@Column(nullable = false)
	private Boolean steuerveranlagungErhalten;

	@NotNull
	@Column(nullable = false)
	private Boolean steuererklaerungAusgefuellt;

	@Column(nullable = true)
	private BigDecimal familienzulage;

	@Column(nullable = true)
	private BigDecimal ersatzeinkommen;

	@Column(nullable = true)
	private BigDecimal erhalteneAlimente;

	@Column(nullable = true)
	private BigDecimal bruttovermoegen;

	@Column(nullable = true)
	private BigDecimal schulden;

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahr;

	@Column(nullable = true)
	private BigDecimal geleisteteAlimente;

	public AbstractFinanzielleSituation() {
	}


	public abstract BigDecimal getNettolohn();

	public Boolean getSteuerveranlagungErhalten() {
		return steuerveranlagungErhalten;
	}

	public void setSteuerveranlagungErhalten(final Boolean steuerveranlagungErhalten) {
		this.steuerveranlagungErhalten = steuerveranlagungErhalten;
	}

	public Boolean getSteuererklaerungAusgefuellt() {
		return steuererklaerungAusgefuellt;
	}

	public void setSteuererklaerungAusgefuellt(final Boolean steuererklaerungAusgefuellt) {
		this.steuererklaerungAusgefuellt = steuererklaerungAusgefuellt;
	}

	public BigDecimal getFamilienzulage() {
		return familienzulage;
	}

	public void setFamilienzulage(final BigDecimal familienzulage) {
		this.familienzulage = familienzulage;
	}

	public BigDecimal getErsatzeinkommen() {
		return ersatzeinkommen;
	}

	public void setErsatzeinkommen(final BigDecimal ersatzeinkommen) {
		this.ersatzeinkommen = ersatzeinkommen;
	}

	public BigDecimal getErhalteneAlimente() {
		return erhalteneAlimente;
	}

	public void setErhalteneAlimente(final BigDecimal erhalteneAlimente) {
		this.erhalteneAlimente = erhalteneAlimente;
	}

	public BigDecimal getBruttovermoegen() {
		return bruttovermoegen;
	}

	public void setBruttovermoegen(final BigDecimal bruttovermoegen) {
		this.bruttovermoegen = bruttovermoegen;
	}

	public BigDecimal getSchulden() {
		return schulden;
	}

	public void setSchulden(final BigDecimal schulden) {
		this.schulden = schulden;
	}

	public BigDecimal getGeschaeftsgewinnBasisjahr() {
		return geschaeftsgewinnBasisjahr;
	}

	public void setGeschaeftsgewinnBasisjahr(final BigDecimal geschaeftsgewinnBasisjahr) {
		this.geschaeftsgewinnBasisjahr = geschaeftsgewinnBasisjahr;
	}

	public BigDecimal getGeleisteteAlimente() {
		return geleisteteAlimente;
	}

	public void setGeleisteteAlimente(final BigDecimal geleisteteAlimente) {
		this.geleisteteAlimente = geleisteteAlimente;
	}

	public AbstractFinanzielleSituation copyForMutation(AbstractFinanzielleSituation mutation) {
		super.copyForMutation(mutation);
		mutation.setSteuerveranlagungErhalten(this.getSteuerveranlagungErhalten());
		mutation.setSteuererklaerungAusgefuellt(this.getSteuererklaerungAusgefuellt());
		mutation.setFamilienzulage(this.getFamilienzulage());
		mutation.setErsatzeinkommen(this.getErsatzeinkommen());
		mutation.setErhalteneAlimente(this.getErhalteneAlimente());
		mutation.setBruttovermoegen(this.getBruttovermoegen());
		mutation.setSchulden(this.getSchulden());
		mutation.setGeschaeftsgewinnBasisjahr(this.getGeschaeftsgewinnBasisjahr());
		mutation.setGeleisteteAlimente(this.getGeleisteteAlimente());
		return mutation;
	}

	@SuppressWarnings("OverlyComplexMethod")
	@Override
	public boolean isSame(AbstractEntity other) {
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof AbstractFinanzielleSituation)) {
			return false;
		}
		final AbstractFinanzielleSituation otherFinSituation = (AbstractFinanzielleSituation) other;
		return Objects.equals(getSteuerveranlagungErhalten(), otherFinSituation.getSteuerveranlagungErhalten()) &&
			Objects.equals(getSteuererklaerungAusgefuellt(), otherFinSituation.getSteuererklaerungAusgefuellt()) &&
			MathUtil.isSame(getFamilienzulage(), otherFinSituation.getFamilienzulage()) &&
			MathUtil.isSame(getErsatzeinkommen(), otherFinSituation.getErsatzeinkommen()) &&
			MathUtil.isSame(getErhalteneAlimente(), otherFinSituation.getErhalteneAlimente()) &&
			MathUtil.isSame(getBruttovermoegen(), otherFinSituation.getBruttovermoegen()) &&
			MathUtil.isSame(getSchulden(), otherFinSituation.getSchulden()) &&
			MathUtil.isSame(getGeschaeftsgewinnBasisjahr(), otherFinSituation.getGeschaeftsgewinnBasisjahr()) &&
			MathUtil.isSame(getGeleisteteAlimente(), otherFinSituation.getGeleisteteAlimente());
	}
}
