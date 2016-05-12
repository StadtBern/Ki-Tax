package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * Container-Entity für die Betreuungspensen: Diese muss für jeden Benutzertyp (GS, JA) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
public class BetreuungspensumContainer extends AbstractEntity {

	private static final long serialVersionUID = -6784987861150035840L;

	@NotNull
	@ManyToOne(optional = false)
	private Betreuung betreuung;

	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private Betreuungspensum betreuungspensumGS;

	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private Betreuungspensum betreuungspensumJA;



	public Betreuung getBetreuung() {
		return this.betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}

	public Betreuungspensum getBetreuungspensumGS() {
		return betreuungspensumGS;
	}

	public void setBetreuungspensumGS(Betreuungspensum betreuungspensumGS) {
		this.betreuungspensumGS = betreuungspensumGS;
	}

	public Betreuungspensum getBetreuungspensumJA() {
		return betreuungspensumJA;
	}

	public void setBetreuungspensumJA(Betreuungspensum betreuungspensumJA) {
		this.betreuungspensumJA = betreuungspensumJA;
	}
}
