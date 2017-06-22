package ch.dvbern.ebegu.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von DokumentGrund in der Datenbank.
 */
@Audited
@Entity
public class DokumentGrund extends AbstractEntity implements Comparable<DokumentGrund> {

	private static final long serialVersionUID = 5417585258130227434L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_dokumentGrund_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private DokumentGrundTyp dokumentGrundTyp;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String fullName;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String tag;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	private DokumentGrundPersonType personType;

	@Nullable
	private Integer personNumber;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private DokumentTyp dokumentTyp;

	@Nullable
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dokumentGrund")
	private Set<Dokument> dokumente = new HashSet<>();

	// Marker, ob Dokument benötigt wird oder nicht. Nicht in DB
	@Transient
	private boolean needed = true;


	public DokumentGrund() {
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp) {
		this.dokumentGrundTyp = dokumentGrundTyp;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, @Nullable String tag,
						 DokumentGrundPersonType personType, Integer personNumber) {
		this.dokumentGrundTyp = dokumentGrundTyp;
		this.tag = tag;
		this.personType = personType;
		this.personNumber = personNumber;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp);
		this.dokumente = new HashSet<>();
		this.dokumentTyp = dokumentTyp;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String tag,
						 DokumentGrundPersonType personType, Integer personNumber, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp, tag, personType, personNumber);
		this.dokumente = new HashSet<>();
		this.dokumentTyp = dokumentTyp;
	}

	@Nullable
	public Set<Dokument> getDokumente() {
		return dokumente;
	}

	public void setDokumente(@Nullable Set<Dokument> dokumente) {
		this.dokumente = dokumente;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public DokumentGrundTyp getDokumentGrundTyp() {
		return dokumentGrundTyp;
	}

	public void setDokumentGrundTyp(DokumentGrundTyp dokumentGrundTyp) {
		this.dokumentGrundTyp = dokumentGrundTyp;
	}

	@Deprecated
	@Nullable
	public String getFullName() {
		return fullName;
	}

	@Deprecated
	public void setFullName(@Nullable String fullname) {
		this.fullName = fullname;
	}

	@Nullable
	public String getTag() {
		return tag;
	}

	public void setTag(@Nullable String tag) {
		this.tag = tag;
	}

	public DokumentTyp getDokumentTyp() {
		return dokumentTyp;
	}

	public void setDokumentTyp(DokumentTyp dokumentTyp) {
		this.dokumentTyp = dokumentTyp;
	}

	public boolean isNeeded() {
		return needed;
	}

	public void setNeeded(boolean needed) {
		this.needed = needed;
	}

	@Nullable
	public DokumentGrundPersonType getPersonType() {
		return personType;
	}

	public void setPersonType(@Nullable DokumentGrundPersonType personType) {
		this.personType = personType;
	}

	@Nullable
	public Integer getPersonNumber() {
		return personNumber;
	}

	public void setPersonNumber(@Nullable Integer personNumber) {
		this.personNumber = personNumber;
	}

	@Override
	public String toString() {
		return "DokumentGrund{" +
			"dokumentGrundTyp=" + dokumentGrundTyp +
			", fullName='" + fullName + '\'' +
			", year='" + tag + '\'' +
			", dokumente=" + dokumente +
			'}';
	}

	/**
	 * This methode compares both objects with all their attributes.
	 * WARNING! Never use it when trying to compare old DokumentGrund with new DokumentGrund.
	 * Since in the old data the fields personType and personNumber didn't exist, the comparison
	 * cannot be done with this methode.
	 */
	@Override
	public int compareTo(DokumentGrund o) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(this.getDokumentGrundTyp(), o.getDokumentGrundTyp());
		builder.append(this.getDokumentTyp(), o.getDokumentTyp());
		if (this.getFullName() != null && o.getFullName() != null) {
			builder.append(this.getFullName(), o.getFullName());
		}
		if (this.getTag() != null && o.getTag() != null) {
			builder.append(this.getTag(), o.getTag());
		}
		if (this.getPersonNumber() != null && o.getPersonNumber() != null) {
			builder.append(this.getPersonNumber(), o.getPersonNumber());
		}
		if (this.getPersonType() != null && o.getPersonType() != null) {
			builder.append(this.getPersonType(), o.getPersonType());
		}
		return builder.toComparison();
	}

	public boolean isEmpty() {
		return getDokumente() == null || getDokumente().size() <= 0;
	}

	public DokumentGrund copyForMutation(DokumentGrund mutation) {
		super.copyForMutation(mutation);
		mutation.setDokumentGrundTyp(this.getDokumentGrundTyp());
		mutation.setFullName(this.getFullName());
		mutation.setTag(this.getTag());
		mutation.setPersonNumber(this.getPersonNumber());
		mutation.setPersonType(this.getPersonType());
		mutation.setDokumentTyp(this.getDokumentTyp());
		if (this.getDokumente() != null) {
			for (Dokument dokument : this.getDokumente()) {
				mutation.getDokumente().add(dokument.copyForMutation(new Dokument(), mutation));
			}
		}
		mutation.setNeeded(this.isNeeded());
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
		if (!(other instanceof DokumentGrund)) {
			return false;
		}
		final DokumentGrund otherDokumentGrund = (DokumentGrund) other;
		return getDokumentGrundTyp() == otherDokumentGrund.getDokumentGrundTyp() &&
//			Objects.equals(getFullName(), otherDokumentGrund.getFullName()) && // deprecated
			Objects.equals(getTag(), otherDokumentGrund.getTag()) &&
			getPersonType() == otherDokumentGrund.getPersonType() &&
			Objects.equals(getPersonNumber(), otherDokumentGrund.getPersonNumber()) &&
			getDokumentTyp() == otherDokumentGrund.getDokumentTyp() &&
			Objects.equals(isNeeded(), otherDokumentGrund.isNeeded());
	}
}
