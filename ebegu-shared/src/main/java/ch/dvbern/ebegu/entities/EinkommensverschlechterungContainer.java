package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.util.EbeguUtil;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Container-Entity für dieEinkommensverschlechterung: Diese muss für jeden
 * Benutzertyp (GS, JA) sowie für beide Halbjahre (Basisjahr + 1 und Basisjahr +2 ) einzeln geführt werden,
 * damit die Veränderungen Korrekturen angezeigt werden können.
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "gesuchsteller_container_id", name = "UK_einkommensverschlechterungcontainer_gesuchsteller")
)
public class EinkommensverschlechterungContainer extends AbstractEntity {


	private static final long serialVersionUID = -2685774428336265818L;

	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_einkommensverschlechterungcontainer_gesuchstellerContainer_id"), nullable = false)
	private GesuchstellerContainer gesuchstellerContainer;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_einkommensverschlechterungcontainer_ekvGSBasisJahrPlus1_id"), nullable = true)
	private Einkommensverschlechterung ekvGSBasisJahrPlus1;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_einkommensverschlechterungcontainer_ekvGSBasisJahrPlus2_id"), nullable = true)
	private Einkommensverschlechterung ekvGSBasisJahrPlus2;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_einkommensverschlechterungcontainer_ekvJABasisJahrPlus1_id"), nullable = true)
	private Einkommensverschlechterung ekvJABasisJahrPlus1;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_einkommensverschlechterungcontainer_ekvJABasisJahrPlus2_id"), nullable = true)
	private Einkommensverschlechterung ekvJABasisJahrPlus2;


	public EinkommensverschlechterungContainer() {
	}


	public Einkommensverschlechterung getEkvJABasisJahrPlus2() {
		return ekvJABasisJahrPlus2;
	}

	public void setEkvJABasisJahrPlus2(final Einkommensverschlechterung ekvJABasisJahrPlus2) {
		this.ekvJABasisJahrPlus2 = ekvJABasisJahrPlus2;
	}

	public Einkommensverschlechterung getEkvJABasisJahrPlus1() {
		return ekvJABasisJahrPlus1;
	}

	public void setEkvJABasisJahrPlus1(final Einkommensverschlechterung ekvJABasisJahrPlus1) {
		this.ekvJABasisJahrPlus1 = ekvJABasisJahrPlus1;
	}

	public Einkommensverschlechterung getEkvGSBasisJahrPlus2() {
		return ekvGSBasisJahrPlus2;
	}

	public void setEkvGSBasisJahrPlus2(final Einkommensverschlechterung ekvGSBasisJahrPlus2) {
		this.ekvGSBasisJahrPlus2 = ekvGSBasisJahrPlus2;
	}

	public Einkommensverschlechterung getEkvGSBasisJahrPlus1() {
		return ekvGSBasisJahrPlus1;
	}

	public void setEkvGSBasisJahrPlus1(final Einkommensverschlechterung ekvGSBasisJahrPlus1) {
		this.ekvGSBasisJahrPlus1 = ekvGSBasisJahrPlus1;
	}

	public GesuchstellerContainer getGesuchsteller() {
		return gesuchstellerContainer;
	}

	public void setGesuchsteller(GesuchstellerContainer gesuchsteller) {
		this.gesuchstellerContainer = gesuchsteller;
	}

	public EinkommensverschlechterungContainer copyForMutation(EinkommensverschlechterungContainer mutation, @Nonnull GesuchstellerContainer gesuchstellerMutation) {
		super.copyForMutation(mutation);
		mutation.setGesuchsteller(gesuchstellerMutation);
		mutation.setEkvGSBasisJahrPlus1(null);
		mutation.setEkvGSBasisJahrPlus2(null);
		if (this.getEkvJABasisJahrPlus1() != null) {
			mutation.setEkvJABasisJahrPlus1(this.getEkvJABasisJahrPlus1().copyForMutation(new Einkommensverschlechterung()));
		}
		if (this.getEkvJABasisJahrPlus2() != null) {
			mutation.setEkvJABasisJahrPlus2(this.getEkvJABasisJahrPlus2().copyForMutation(new Einkommensverschlechterung()));
		}
		return mutation;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		final EinkommensverschlechterungContainer otherEKVContainer = (EinkommensverschlechterungContainer) other;
		return EbeguUtil.isSameObject(getEkvGSBasisJahrPlus1(), otherEKVContainer.getEkvGSBasisJahrPlus1()) &&
			EbeguUtil.isSameObject(getEkvGSBasisJahrPlus2(), otherEKVContainer.getEkvGSBasisJahrPlus2()) &&
			EbeguUtil.isSameObject(getEkvJABasisJahrPlus1(), otherEKVContainer.getEkvJABasisJahrPlus1()) &&
			EbeguUtil.isSameObject(getEkvJABasisJahrPlus2(), otherEKVContainer.getEkvJABasisJahrPlus2());
	}
}
