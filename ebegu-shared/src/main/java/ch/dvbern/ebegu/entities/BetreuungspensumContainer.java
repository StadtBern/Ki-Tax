package ch.dvbern.ebegu.entities;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.Validate;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Container-Entity für die Betreuungspensen: Diese muss für jeden Benutzertyp (GS, JA) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
public class BetreuungspensumContainer extends AbstractEntity implements Comparable<BetreuungspensumContainer> {

	private static final long serialVersionUID = -6784987861150035840L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuungspensum_container_betreuung_id"), nullable = false)
	private Betreuung betreuung;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuungspensum_container_betreuungspensum_gs"))
	private Betreuungspensum betreuungspensumGS;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_betreuungspensum_container_betreuungspensum_ja"))
	private Betreuungspensum betreuungspensumJA;


	public BetreuungspensumContainer() {
	}


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

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(BetreuungspensumContainer otherBetreuungspensumContainer) {
		if (this == otherBetreuungspensumContainer) {
			return true;
		}
		if (otherBetreuungspensumContainer == null || getClass() != otherBetreuungspensumContainer.getClass()) {
			return false;
		}

		return getBetreuungspensumGS().isSame(otherBetreuungspensumContainer.getBetreuungspensumGS()) &&
			getBetreuungspensumJA().isSame(otherBetreuungspensumContainer.getBetreuungspensumJA());
	}

	/**
	 *
	 * @return geht durch die internen Datenstrukturen hoch bis zur Gesuchsperiode und gibt diese zureuck
	 * @throws IllegalArgumentException wenn einer der benoetigten Pfade null ist
	 */
	@Transient
	public Gesuchsperiode extractGesuchsperiode(){
		Validate.notNull(this.getBetreuung(), "Can not extract Gesuchsperiode because Betreuung is null");
		Validate.notNull(this.getBetreuung().getKind(), "Can not extract Gesuchsperiode because Kind is null");
		Validate.notNull(this.getBetreuung().getKind().getGesuch(), "Can not extract Gesuchsperiode because Gesuch is null");
		return this.getBetreuung().getKind().getGesuch().getGesuchsperiode();
	}

	@Transient
	public Gesuch extractGesuch(){
		return this.getBetreuung().getKind().getGesuch();
	}


	@Override
	public int compareTo(BetreuungspensumContainer o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getBetreuungspensumJA(), o.getBetreuungspensumJA());
		builder.append(this.getBetreuungspensumJA().getId(), o.getBetreuungspensumJA().getId());
		return builder.toComparison();
	}

	public BetreuungspensumContainer copyForMutation(@Nonnull BetreuungspensumContainer mutation, @Nonnull Betreuung betreuungMutation) {
		super.copyForMutation(mutation);
		mutation.setBetreuung(betreuungMutation);
		mutation.setBetreuungspensumGS(null);
		mutation.setBetreuungspensumJA(this.getBetreuungspensumJA().copyForMutation(new Betreuungspensum()));
		return mutation;
	}
}
