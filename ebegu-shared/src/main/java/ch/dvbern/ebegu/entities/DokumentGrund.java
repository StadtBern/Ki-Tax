package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AnlageGrundTyp;
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

	public DokumentGrund(AnlageGrundTyp anlageGrundTyp) {
		this.anlageGrundTyp = anlageGrundTyp;
	}

	public DokumentGrund(AnlageGrundTyp anlageGrundTyp, String tag1) {
		this.anlageGrundTyp = anlageGrundTyp;
		this.tag1 = tag1;
	}

	public DokumentGrund(AnlageGrundTyp anlageGrundTyp, String tag1, String tag2) {
		this.anlageGrundTyp = anlageGrundTyp;
		this.tag1 = tag1;
		this.tag2 = tag2;
	}

	public DokumentGrund(AnlageGrundTyp anlageGrundTyp, DokumentTyp dokumentTyp) {
		this(anlageGrundTyp);
		this.anlageDokumente = new HashSet<AnlageDokument>();
		this.anlageDokumente.add(new AnlageDokument(this, dokumentTyp));
	}

	public DokumentGrund(AnlageGrundTyp anlageGrundTyp, String tag1, DokumentTyp dokumentTyp) {
		this(anlageGrundTyp, tag1);
		this.anlageDokumente = new HashSet<AnlageDokument>();
		this.anlageDokumente.add(new AnlageDokument(this, dokumentTyp));
	}

	public DokumentGrund(AnlageGrundTyp anlageGrundTyp, String tag1, String tag2, DokumentTyp dokumentTyp) {
		this(anlageGrundTyp, tag1, tag2);
		this.anlageDokumente = new HashSet<AnlageDokument>();
		this.anlageDokumente.add(new AnlageDokument(this, dokumentTyp));
	}

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_analge_grund_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private AnlageGrundTyp anlageGrundTyp;

	//TODO: Es wäre besser dies als Person zu speichern!
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String tag1;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String tag2;

	@Nullable
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "anlageGrund")
	private Set<AnlageDokument> anlageDokumente = new HashSet<>();


	@Nullable
	public Set<AnlageDokument> getAnlageDokumente() {
		return anlageDokumente;
	}

	public void setAnlageDokumente(@Nullable Set<AnlageDokument> anlageDokumente) {
		this.anlageDokumente = anlageDokumente;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public AnlageGrundTyp getAnlageGrundTyp() {
		return anlageGrundTyp;
	}

	public void setAnlageGrundTyp(AnlageGrundTyp anlageGrundTyp) {
		this.anlageGrundTyp = anlageGrundTyp;
	}

	@Nullable
	public String getTag1() {
		return tag1;
	}

	public void setTag1(@Nullable String tag1) {
		this.tag1 = tag1;
	}

	@Nullable
	public String getTag2() {
		return tag2;
	}

	public void setTag2(@Nullable String tag2) {
		this.tag2 = tag2;
	}

	@Override
	public String toString() {
		return "DokumentGrund{" +
			"anlageGrundTyp=" + anlageGrundTyp +
			", tag1='" + tag1 + '\'' +
			", tag2='" + tag2 + '\'' +
			", anlageDokumente=" + anlageDokumente +
			'}';
	}
}
