package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entität für die Finanzielle Situation
 */
@Audited
@Entity
public class FinanzielleSituation extends AbstractEntity {

	private static final long serialVersionUID = -4401110366293613225L;

	@NotNull
	@Column(nullable = false)
	private Boolean steuerveranlagungErhalten;

	@NotNull
	@Column(nullable = false)
	private Boolean steuererklaerungAusgefuellt;

	@Column(nullable = true)
	private BigDecimal nettolohn;

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

	@NotNull
	@Column(nullable = false)
	private Boolean selbstaendig = Boolean.FALSE;

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus2;

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahrMinus1;

	@Column(nullable = true)
	private BigDecimal geschaeftsgewinnBasisjahr;

	@Column(nullable = true)
	private BigDecimal geleisteteAlimente;


	public Boolean getSteuerveranlagungErhalten() {
		return steuerveranlagungErhalten;
	}

	public void setSteuerveranlagungErhalten(Boolean steuerveranlagungErhalten) {
		this.steuerveranlagungErhalten = steuerveranlagungErhalten;
	}

	public Boolean getSteuererklaerungAusgefuellt() {
		return steuererklaerungAusgefuellt;
	}

	public void setSteuererklaerungAusgefuellt(Boolean steuererklaerungAusgefuellt) {
		this.steuererklaerungAusgefuellt = steuererklaerungAusgefuellt;
	}

	public BigDecimal getNettolohn() {
		return nettolohn;
	}

	public void setNettolohn(BigDecimal nettolohn) {
		this.nettolohn = nettolohn;
	}

	public BigDecimal getFamilienzulage() {
		return familienzulage;
	}

	public void setFamilienzulage(BigDecimal familienzulage) {
		this.familienzulage = familienzulage;
	}

	public BigDecimal getErsatzeinkommen() {
		return ersatzeinkommen;
	}

	public void setErsatzeinkommen(BigDecimal ersatzeinkommen) {
		this.ersatzeinkommen = ersatzeinkommen;
	}

	public BigDecimal getErhalteneAlimente() {
		return erhalteneAlimente;
	}

	public void setErhalteneAlimente(BigDecimal erhalteneAlimente) {
		this.erhalteneAlimente = erhalteneAlimente;
	}

	public BigDecimal getBruttovermoegen() {
		return bruttovermoegen;
	}

	public void setBruttovermoegen(BigDecimal bruttovermoegen) {
		this.bruttovermoegen = bruttovermoegen;
	}

	public BigDecimal getSchulden() {
		return schulden;
	}

	public void setSchulden(BigDecimal schulden) {
		this.schulden = schulden;
	}

	public Boolean getSelbstaendig() {
		return selbstaendig;
	}

	public void setSelbstaendig(Boolean selbstaendig) {
		this.selbstaendig = selbstaendig;
	}

	public BigDecimal getGeschaeftsgewinnBasisjahrMinus2() {
		return geschaeftsgewinnBasisjahrMinus2;
	}

	public void setGeschaeftsgewinnBasisjahrMinus2(BigDecimal geschaeftsgewinnBasisjahrMinus2) {
		this.geschaeftsgewinnBasisjahrMinus2 = geschaeftsgewinnBasisjahrMinus2;
	}

	public BigDecimal getGeschaeftsgewinnBasisjahrMinus1() {
		return geschaeftsgewinnBasisjahrMinus1;
	}

	public void setGeschaeftsgewinnBasisjahrMinus1(BigDecimal geschaeftsgewinnBasisjahrMinus1) {
		this.geschaeftsgewinnBasisjahrMinus1 = geschaeftsgewinnBasisjahrMinus1;
	}

	public BigDecimal getGeschaeftsgewinnBasisjahr() {
		return geschaeftsgewinnBasisjahr;
	}

	public void setGeschaeftsgewinnBasisjahr(BigDecimal geschaeftsgewinnBasisjahr) {
		this.geschaeftsgewinnBasisjahr = geschaeftsgewinnBasisjahr;
	}

	public BigDecimal getGeleisteteAlimente() {
		return geleisteteAlimente;
	}

	public void setGeleisteteAlimente(BigDecimal geleisteteAlimente) {
		this.geleisteteAlimente = geleisteteAlimente;
	}
}
