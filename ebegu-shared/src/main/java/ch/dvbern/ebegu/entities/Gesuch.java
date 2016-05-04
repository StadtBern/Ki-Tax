package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Entitaet zum Speichern von Gesuch in der Datenbank.
 */
@Audited
@Entity
public class Gesuch extends AbstractEntity {

	private static final long serialVersionUID = -8403487439884700618L;
	@ManyToOne(optional = false)
	private Fall fall;

	@Nullable
	@OneToOne(optional = true)
	private Gesuchsteller gesuchsteller1;

	@Nullable
	@OneToOne(optional = true)
	private Gesuchsteller gesuchsteller2;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "gesuch")
	private Set<KindContainer> kindContainer = new HashSet<>();


	public Fall getFall() {
		return fall;
	}

	public void setFall(Fall fall) {
		this.fall = fall;
	}

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

	public Set<KindContainer> getKindContainer() {
		return kindContainer;
	}

	public void setKindContainer(Set<KindContainer> kindContainer) {
		this.kindContainer = kindContainer;
	}

	public void addKindContainer(@NotNull KindContainer kindContainer) {
		this.kindContainer.add(kindContainer);
	}
}
