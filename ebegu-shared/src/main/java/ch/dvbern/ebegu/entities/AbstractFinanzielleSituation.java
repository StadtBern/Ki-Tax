package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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

	public AbstractFinanzielleSituation(@Nonnull AbstractFinanzielleSituation toCopy) {
		this.steuerveranlagungErhalten = toCopy.steuerveranlagungErhalten;
		this.steuererklaerungAusgefuellt = toCopy.steuererklaerungAusgefuellt;
		this.familienzulage = toCopy.familienzulage;
		this.ersatzeinkommen = toCopy.ersatzeinkommen;
		this.erhalteneAlimente = toCopy.erhalteneAlimente;
		this.bruttovermoegen = toCopy.bruttovermoegen;
		this.schulden = toCopy.schulden;
		this.geschaeftsgewinnBasisjahr = toCopy.geschaeftsgewinnBasisjahr;
		this.geleisteteAlimente = toCopy.geleisteteAlimente;
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

}
