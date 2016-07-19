package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.DokumentTyp;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von Institution in der Datenbank.
 */
@Audited
@Entity
public class AnlageDokument extends AbstractEntity {


	private static final long serialVersionUID = -895840426585785097L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String dokumentName;


	//Dokument soll nicht in DB gespeichert werden, sondern in File-System. Wie genau ist noch nicht klar und muss noch evaluiert werden!
//	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
//	@Column(nullable = true)
//	@Nullable
//	private String dokumentPfad;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private DokumentTyp dokumentTyp;


	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_anlagedokument_anlagegrund_id"), nullable = false)
	private DokumentGrund dokumentGrund;

	public AnlageDokument() {
	}

	public AnlageDokument(DokumentGrund dokumentGrund, DokumentTyp dokumentTyp) {
		this.dokumentGrund = dokumentGrund;
		this.dokumentTyp = dokumentTyp;

	}

	@Nullable
	public String getDokumentName() {
		return dokumentName;
	}

	public void setDokumentName(@Nullable String dokumentName) {
		this.dokumentName = dokumentName;
	}

	public DokumentTyp getDokumentTyp() {
		return dokumentTyp;
	}

	public void setDokumentTyp(DokumentTyp dokumentTyp) {
		this.dokumentTyp = dokumentTyp;
	}

	public DokumentGrund getDokumentGrund() {
		return dokumentGrund;
	}

	public void setDokumentGrund(DokumentGrund dokumentGrund) {
		this.dokumentGrund = dokumentGrund;
	}

	@Override
	public String toString() {
		return "AnlageDokument{" +
			"dokumentName='" + dokumentName + '\'' +
			", dokumentTyp=" + dokumentTyp +
			'}';
	}
}
