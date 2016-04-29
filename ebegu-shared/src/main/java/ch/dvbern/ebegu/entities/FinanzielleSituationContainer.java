package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Container-Entity für die Finanzielle Situation: Diese muss für jeden Benutzertyp (GS, JA, SV) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
public class FinanzielleSituationContainer extends AbstractEntity {

	private static final long serialVersionUID = -6504985266190035840L;

	@NotNull
	@OneToOne(optional = false)
	private Person gesuchsteller;

	@NotNull
	@Column(nullable = false)
	private Integer jahr;

	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private FinanzielleSituation finanzielleSituationGS;

	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private FinanzielleSituation finanzielleSituationJA;

	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private FinanzielleSituation finanzielleSituationSV;


	public Person getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(Person gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
		if (gesuchsteller != null &&
				(gesuchsteller.getFinanzielleSituationContainer() == null || !gesuchsteller.getFinanzielleSituationContainer().equals(this))) {
			gesuchsteller.setFinanzielleSituationContainer(this);
		}
	}

	public Integer getJahr() {
		return jahr;
	}

	public void setJahr(Integer jahr) {
		this.jahr = jahr;
	}

	public FinanzielleSituation getFinanzielleSituationGS() {
		return finanzielleSituationGS;
	}

	public void setFinanzielleSituationGS(FinanzielleSituation finanzielleSituationGS) {
		this.finanzielleSituationGS = finanzielleSituationGS;
	}

	public FinanzielleSituation getFinanzielleSituationJA() {
		return finanzielleSituationJA;
	}

	public void setFinanzielleSituationJA(FinanzielleSituation finanzielleSituationJA) {
		this.finanzielleSituationJA = finanzielleSituationJA;
	}

	public FinanzielleSituation getFinanzielleSituationSV() {
		return finanzielleSituationSV;
	}

	public void setFinanzielleSituationSV(FinanzielleSituation finanzielleSituationSV) {
		this.finanzielleSituationSV = finanzielleSituationSV;
	}
}
