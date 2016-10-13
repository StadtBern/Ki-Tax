package ch.dvbern.ebegu.entities;


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
	uniqueConstraints = @UniqueConstraint(columnNames = "gesuchsteller_id", name = "UK_einkommensverschlechterungcontainer_gesuchsteller")
)
public class EinkommensverschlechterungContainer extends AbstractEntity {


	private static final long serialVersionUID = -2685774428336265818L;

	@NotNull
	@OneToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_einkommensverschlechterungcontainer_gesuchsteller_id"), nullable = false)
	private Gesuchsteller gesuchsteller;

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

	public EinkommensverschlechterungContainer(@Nonnull EinkommensverschlechterungContainer toCopy, @Nonnull Gesuchsteller gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
		this.ekvGSBasisJahrPlus1 = null;
		this.ekvGSBasisJahrPlus2 = null;
		if (toCopy.ekvJABasisJahrPlus1 != null) {
			this.ekvJABasisJahrPlus1 = new Einkommensverschlechterung(toCopy.ekvJABasisJahrPlus1);
		}
		if (toCopy.ekvJABasisJahrPlus2 != null) {
			this.ekvJABasisJahrPlus2 = new Einkommensverschlechterung(toCopy.ekvJABasisJahrPlus2);
		}
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


	public Gesuchsteller getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(Gesuchsteller gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
	}
}
