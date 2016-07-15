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
//todo team die FK kann irgendwie nicht ueberschrieben werden. Folgende 2 Moeglichkeiten sollten gehen aber es ueberschreibt den Namen nicht --> Problem mit hibernate-maven-plugin??
//@AssociationOverride(name = "gesuchsperiode", joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsperiode_id")))
//@AssociationOverride(name = "gesuchsperiode", foreignKey = @ForeignKey(name="FK_gesuch_gesuchsperiode_id"))
public class Gesuch extends AbstractAntragEntity {

	private static final long serialVersionUID = -8403487439884700618L;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsteller1_id"))
	private Gesuchsteller gesuchsteller1;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_gesuchsteller2_id"))
	private Gesuchsteller gesuchsteller2;

	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuch")
	private Set<KindContainer> kindContainers = new HashSet<>();

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_familiensituation_id"))
	private Familiensituation familiensituation;

	@Valid
	@Nullable
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_einkommensverschlechterungInfo_id"))
	private EinkommensverschlechterungInfo einkommensverschlechterungInfo;


	@Nullable
	public Gesuchsteller getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable final Gesuchsteller gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public Gesuchsteller getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable final Gesuchsteller gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	public Set<KindContainer> getKindContainers() {
		return kindContainers;
	}

	public void setKindContainers(final Set<KindContainer> kindContainers) {
		this.kindContainers = kindContainers;
	}

	@Nullable
	public Familiensituation getFamiliensituation() {
		return familiensituation;
	}

	public void setFamiliensituation(@Nullable final Familiensituation familiensituation) {
		this.familiensituation = familiensituation;
	}

	@Nullable
	public EinkommensverschlechterungInfo getEinkommensverschlechterungInfo() {
		return einkommensverschlechterungInfo;
	}

	public void setEinkommensverschlechterungInfo(@Nullable final EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		this.einkommensverschlechterungInfo = einkommensverschlechterungInfo;
		if (this.einkommensverschlechterungInfo != null) {
			this.einkommensverschlechterungInfo.setGesuch(this);
		}
	}

	public boolean addKindContainer(@NotNull final KindContainer kindContainer) {
		kindContainer.setGesuch(this);
		return !this.kindContainers.contains(kindContainer) && this.kindContainers.add(kindContainer);
	}
}
