package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
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
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_FinanzielleSituationContainer_gesuchsteller_id"), nullable = false)
	private Gesuchsteller gesuchsteller;

	@NotNull
	@Column(nullable = false)
	private Integer jahr;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private FinanzielleSituation finanzielleSituationGS;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private FinanzielleSituation finanzielleSituationJA;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	private FinanzielleSituation finanzielleSituationSV;


	public Gesuchsteller getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(Gesuchsteller gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
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
