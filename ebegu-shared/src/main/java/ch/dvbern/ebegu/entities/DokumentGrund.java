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
 * Entitaet zum Speichern von Institution in der Datenbank.
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

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullname) {
		this.dokumentGrundTyp = dokumentGrundTyp;
		this.fullname = fullname;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullname, String tag) {
		this.dokumentGrundTyp = dokumentGrundTyp;
		this.fullname = fullname;
		this.tag = tag;
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp);
		this.dokumente = new HashSet<Dokument>();
		this.dokumente.add(new Dokument(this, dokumentTyp));
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullname, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp, fullname);
		this.dokumente = new HashSet<Dokument>();
		this.dokumente.add(new Dokument(this, dokumentTyp));
	}

	public DokumentGrund(DokumentGrundTyp dokumentGrundTyp, String fullname, String tag, DokumentTyp dokumentTyp) {
		this(dokumentGrundTyp, fullname, tag);
		this.dokumente = new HashSet<Dokument>();
		this.dokumente.add(new Dokument(this, dokumentTyp));
	}

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_dokumentGrund_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private DokumentGrundTyp dokumentGrundTyp;

	//TODO: Es wäre besser dies als Person zu speichern!
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String fullname;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String tag;

	@Nullable
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dokumentGrund")
	private Set<Dokument> dokumente = new HashSet<>();


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
	public String getFullname() {
		return fullname;
	}

	public void setFullname(@Nullable String fullname) {
		this.fullname = fullname;
	}

	@Nullable
	public String getTag() {
		return tag;
	}

	public void setTag(@Nullable String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "DokumentGrund{" +
			"dokumentGrundTyp=" + dokumentGrundTyp +
			", fullname='" + fullname + '\'' +
			", year='" + tag + '\'' +
			", dokumente=" + dokumente +
			'}';
	}
}
