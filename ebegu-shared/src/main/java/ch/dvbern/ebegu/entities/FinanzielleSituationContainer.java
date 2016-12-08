package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Container-Entity für die Finanzielle Situation: Diese muss für jeden Benutzertyp (GS, JA, SV) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "gesuchsteller_id", name = "UK_finanzielle_situation_container_gesuchsteller")
)
public class FinanzielleSituationContainer extends AbstractEntity {

	private static final long serialVersionUID = -6504985266190035840L;

	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_finanzielleSituationContainer_gesuchsteller_id"), nullable = false)
	private Gesuchsteller gesuchsteller;

	@NotNull
	@Column(nullable = false)
	private Integer jahr;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_finanzielleSituationContainer_finanzielleSituationGS_id"), nullable = true)
	private FinanzielleSituation finanzielleSituationGS;

	@Valid
	@OneToOne (optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_finanzielleSituationContainer_finanzielleSituationJA_id"), nullable = true)
	private FinanzielleSituation finanzielleSituationJA;


	public FinanzielleSituationContainer() {
	}


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

	public FinanzielleSituationContainer copyForMutation(FinanzielleSituationContainer mutation, @Nonnull Gesuchsteller gesuchstellerMutation) {
		super.copyForMutation(mutation);
		mutation.setGesuchsteller(gesuchstellerMutation);
		mutation.setJahr(this.getJahr());
		mutation.setFinanzielleSituationGS(null);
		mutation.setFinanzielleSituationJA(this.getFinanzielleSituationJA().copyForMutation(new FinanzielleSituation()));
		return mutation;
	}
}
