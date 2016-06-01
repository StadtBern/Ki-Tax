package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Entitaet zum Speichern von Gesuch in der Datenbank.
 */
@Audited
@Entity
//todo team die FK kann irgendwie nicht ueberschrieben werden. Folgender Code sollte gehen aber es ueberschreibt den Namen nicht
//@AssociationOverride(name = "gesuchsperiode", joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsperiode_id")))
public class Gesuch extends AbstractAntragEntity {

	private static final long serialVersionUID = -8403487439884700618L;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	private Gesuchsteller gesuchsteller1;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	private Gesuchsteller gesuchsteller2;

	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuch")
	private Set<KindContainer> kindContainers = new HashSet<>();

	@Column(nullable = true)
	private Boolean einkommensverschlechterung;



	@Nullable
	public Gesuchsteller getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable Gesuchsteller gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public Gesuchsteller getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable Gesuchsteller gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	public Set<KindContainer> getKindContainers() {
		return kindContainers;
	}

	public void setKindContainers(Set<KindContainer> kindContainers) {
		this.kindContainers = kindContainers;
	}

	public Boolean getEinkommensverschlechterung() {
		return einkommensverschlechterung;
	}

	public void setEinkommensverschlechterung(Boolean einkommensverschlechterung) {
		this.einkommensverschlechterung = einkommensverschlechterung;
	}

	public boolean addKindContainer(@NotNull KindContainer kindContainer) {
		kindContainer.setGesuch(this);
		return !this.kindContainers.contains(kindContainer) && this.kindContainers.add(kindContainer);
	}

}
