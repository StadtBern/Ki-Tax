package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;

/**
 * Container-Entity für das Erwerbspensum: Diese muss für die  Benutzertypen (GS, JA) einzeln geführt werden,
 * damit die Veränderungen / Korrekturen angezeigt werden können.
 */
@Audited
@Entity
public class ErwerbspensumContainer extends AbstractEntity {


	private static final long serialVersionUID = -3084333639027795652L;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ErwerbspensumContainer_gesuchsteller_id"))
	private Gesuchsteller gesuchsteller;

	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ErwerbspensumContainer_erwerbspensumgs_id"))
	private Erwerbspensum erwerbspensumGS;

	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ErwerbspensumContainer_erwerbspensumja_id"))
	private Erwerbspensum erwerbspensumJA;

	public Gesuchsteller getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(Gesuchsteller gesuchsteller) {
		this.gesuchsteller = gesuchsteller;

	}

	public Erwerbspensum getErwerbspensumGS() {
		return erwerbspensumGS;
	}

	public void setErwerbspensumGS(Erwerbspensum erwerbspensumGS) {
		this.erwerbspensumGS = erwerbspensumGS;
	}

	public Erwerbspensum getErwerbspensumJA() {
		return erwerbspensumJA;
	}

	public void setErwerbspensumJA(Erwerbspensum erwerbspensumJA) {
		this.erwerbspensumJA = erwerbspensumJA;
	}

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(ErwerbspensumContainer otherErwerbspensum) {
		if (this == otherErwerbspensum) {
			return true;
		}
		if (otherErwerbspensum == null || getClass() != otherErwerbspensum.getClass()) {
			return false;
		}
		return getErwerbspensumGS().isSame(otherErwerbspensum.getErwerbspensumGS()) &&
			getErwerbspensumJA().isSame(otherErwerbspensum.getErwerbspensumJA());

	}
}
