package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von DokumentGrund in der Datenbank.
 */
@Audited
@Entity
public class DokumentGrund extends AbstractEntity {

	private static final long serialVersionUID = 5417585258130227434L;

	public DokumentGrund() {
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp) {
		this.dokumentGrundTyp = dokumentGrundTyp;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullName) {
		this.dokumentGrundTyp = dokumentGrundTyp;
		this.fullName = fullName;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullName, String tag) {
		this.dokumentGrundTyp = dokumentGrundTyp;
		this.fullName = fullName;
		this.tag = tag;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp);
		this.dokumente = new HashSet<>();
		this.dokumentTyp = dokumentTyp;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullName, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp, fullName);
		this.dokumente = new HashSet<>();
		this.dokumentTyp = dokumentTyp;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullName, String tag, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp, fullName, tag);
		this.dokumente = new HashSet<>();
		this.dokumentTyp = dokumentTyp;
	}

	//Copy
	public DokumentGrund(DokumentGrund toCopy) {
		this.setVorgaengerId(toCopy.getId());
		this.dokumentGrundTyp = toCopy.dokumentGrundTyp;
		this.fullName = toCopy.fullName;
		this.tag = toCopy.tag;
		this.dokumentTyp = toCopy.dokumentTyp;
		if(toCopy.dokumente != null) {
			for (Dokument dokument : toCopy.dokumente) {
				this.dokumente.add(new Dokument(dokument, this));
			}
		}
		this.needed = toCopy.needed;
	}

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
	@NotNull
	private DokumentTyp dokumentTyp;

	@Nullable
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dokumentGrund")
	private Set<Dokument> dokumente = new HashSet<>();

	// Marker, ob Dokument benötigt wird oder nicht. Nicht in DB
	@Transient
	private boolean needed = true;



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

	@Nullable
	public String getFullName() {
		return fullName;
	}

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

	@Override
	public String toString() {
		return "DokumentGrund{" +
			"dokumentGrundTyp=" + dokumentGrundTyp +
			", fullName='" + fullName + '\'' +
			", year='" + tag + '\'' +
			", dokumente=" + dokumente +
			'}';
	}

	public boolean isEmpty() {
		return getDokumente() == null || getDokumente().size() <= 0;
	}
}
