package ch.dvbern.ebegu.entities;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Entity fuer AbwesenheitContainer
 */
@Audited
@Entity
public class AbwesenheitContainer extends AbstractEntity implements Comparable<AbwesenheitContainer> {

	private static final long serialVersionUID = -8876987863152535840L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_container_betreuung_id"), nullable = false)
	private Betreuung betreuung;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_container_abwesenheit_gs"))
	private Abwesenheit abwesenheitGS;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_abwesenheit_container_abwesenheit_ja"))
	private Abwesenheit abwesenheitJA;


	public AbwesenheitContainer() {

	}

	public AbwesenheitContainer(@Nonnull AbwesenheitContainer toCopy, @Nonnull Betreuung betreuung) {
		this.setVorgaengerId(toCopy.getId());
		this.betreuung = betreuung;
		this.abwesenheitGS = null;
		this.abwesenheitJA = new Abwesenheit(toCopy.getAbwesenheitJA());
	}


	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}

	public Abwesenheit getAbwesenheitGS() {
		return abwesenheitGS;
	}

	public void setAbwesenheitGS(Abwesenheit abwesenheitGS) {
		this.abwesenheitGS = abwesenheitGS;
	}

	public Abwesenheit getAbwesenheitJA() {
		return abwesenheitJA;
	}

	public void setAbwesenheitJA(Abwesenheit abwesenheitJA) {
		this.abwesenheitJA = abwesenheitJA;
	}

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(AbwesenheitContainer otherAbwesenheitContainer) {
		if (this == otherAbwesenheitContainer) {
			return true;
		}
		if (otherAbwesenheitContainer == null || getClass() != otherAbwesenheitContainer.getClass()) {
			return false;
		}

		return getAbwesenheitGS().isSame(otherAbwesenheitContainer.getAbwesenheitGS()) &&
			getAbwesenheitJA().isSame(otherAbwesenheitContainer.getAbwesenheitJA());
	}

	@Override
	public int compareTo(AbwesenheitContainer o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getAbwesenheitJA(), o.getAbwesenheitJA());
		builder.append(this.getAbwesenheitJA().getId(), o.getAbwesenheitJA().getId());
		return builder.toComparison();
	}
}
