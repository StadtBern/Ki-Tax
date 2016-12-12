package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;

/**
 * Entitaet zum Speichern von FamiliensituationContainer in der Datenbank.
 */
@Audited
@Entity
public class FamiliensituationContainer extends AbstractEntity {

	private static final long serialVersionUID = 6696130722316500745L;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_JA_id"))
	private Familiensituation familiensituationJA;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_GS_id"))
	private Familiensituation familiensituationGS;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_familiensituation_container_familiensituation_erstgesuch_id"))
	private Familiensituation familiensituationErstgesuch;

	public FamiliensituationContainer() {
	}

	public FamiliensituationContainer(FamiliensituationContainer that, boolean toCopyisMutation) {
		this.setVorgaengerId(that.getId());
		this.setFamiliensituationJA(new Familiensituation(that.getFamiliensituationJA()));
		if (toCopyisMutation) {
			this.familiensituationErstgesuch = that.getFamiliensituationErstgesuch();
		} else { // beim ErstGesuch holen wir direkt die normale Familiensituation
			this.familiensituationErstgesuch = that.getFamiliensituationJA();
		}
	}

	@Nullable
	public Familiensituation getFamiliensituationJA() {
		return familiensituationJA;
	}

	public void setFamiliensituationJA(@Nullable Familiensituation familiensituationJA) {
		this.familiensituationJA = familiensituationJA;
	}

	@Nullable
	public Familiensituation getFamiliensituationGS() {
		return familiensituationGS;
	}

	public void setFamiliensituationGS(@Nullable Familiensituation familiensituationGS) {
		this.familiensituationGS = familiensituationGS;
	}

	@Nullable
	public Familiensituation getFamiliensituationErstgesuch() {
		return familiensituationErstgesuch;
	}

	public void setFamiliensituationErstgesuch(@Nullable Familiensituation familiensituationErstgesuch) {
		this.familiensituationErstgesuch = familiensituationErstgesuch;
	}

	@Nullable
	public Familiensituation extractFamiliensituation() {
		return familiensituationJA;
	}
}
