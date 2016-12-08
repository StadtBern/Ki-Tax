package ch.dvbern.ebegu.entities;


import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Entity für die Erfassung von Einkommensverschlechterungen für das Gesuch
 * Speichern der Entscheidung ob eine Einkommensverschlechterung geltend gemacht werden möchte sowie die Auswahl der
 * Jahreshälfte, Monat des Ereignisses sowie deren Grund
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@Entity
public class EinkommensverschlechterungInfoContainer extends AbstractEntity {

	private static final long serialVersionUID = 7458803905310712257L;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ekvinfocontainer_einkommensverschlechterunginfogs_id"), nullable = true)
	private EinkommensverschlechterungInfo einkommensverschlechterungInfoGS;

	@Valid
	@Nonnull
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ekvinfocontainer_einkommensverschlechterunginfoja_id"), nullable = true)
	private EinkommensverschlechterungInfo einkommensverschlechterungInfoJA = new EinkommensverschlechterungInfo();

	@NotNull
	@Valid
	@OneToOne(optional = false, mappedBy = "einkommensverschlechterungInfoContainer")
	private Gesuch gesuch;

	public EinkommensverschlechterungInfoContainer() {
	}

	/**
	 * Copy Constructor. ACHTUNG kopiert nur daten die in dieser Klasse definiert sind
	 */
	public EinkommensverschlechterungInfoContainer(@Nonnull EinkommensverschlechterungInfoContainer that) {
		this.setVorgaengerId(that.getId());
		this.einkommensverschlechterungInfoGS = null;
		this.einkommensverschlechterungInfoJA = new EinkommensverschlechterungInfo(that.einkommensverschlechterungInfoJA);
	}


	public EinkommensverschlechterungInfo getEinkommensverschlechterungInfoGS() {
		return einkommensverschlechterungInfoGS;
	}

	public void setEinkommensverschlechterungInfoGS(EinkommensverschlechterungInfo einkommensverschlechterungInfoGS) {
		this.einkommensverschlechterungInfoGS = einkommensverschlechterungInfoGS;
	}

	public EinkommensverschlechterungInfo getEinkommensverschlechterungInfoJA() {
		return einkommensverschlechterungInfoJA;
	}

	public void setEinkommensverschlechterungInfoJA(EinkommensverschlechterungInfo einkommensverschlechterungInfoJA) {
		this.einkommensverschlechterungInfoJA = einkommensverschlechterungInfoJA;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;

		if (gesuch != null &&
			(gesuch.getEinkommensverschlechterungInfoContainer() == null || !gesuch.getEinkommensverschlechterungInfoContainer().equals(this))) {
			gesuch.setEinkommensverschlechterungInfoContainer(this);
		}
	}

}
