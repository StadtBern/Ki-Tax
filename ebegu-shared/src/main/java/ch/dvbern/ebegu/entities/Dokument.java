package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von Dokumente in der Datenbank.
 */
@Audited
@Entity
public class Dokument extends AbstractEntity {


	private static final long serialVersionUID = -895840426585785097L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String dokumentName;

	//Dokument soll nicht in DB gespeichert werden, sondern in File-System. Wie genau ist noch nicht klar und muss noch evaluiert werden!
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String dokumentPfad;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String dokumentSize;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_dokument_dokumentgrund_id"), nullable = false)
	private DokumentGrund dokumentGrund;

	public Dokument() {
	}

	public Dokument(DokumentGrund dokumentGrund) {
		this.dokumentGrund = dokumentGrund;
	}

	@Nullable
	public String getDokumentName() {
		return dokumentName;
	}

	public void setDokumentName(@Nullable String dokumentName) {
		this.dokumentName = dokumentName;
	}

	public DokumentGrund getDokumentGrund() {
		return dokumentGrund;
	}

	public void setDokumentGrund(DokumentGrund dokumentGrund) {
		this.dokumentGrund = dokumentGrund;
	}

	public String getDokumentPfad() {
		return dokumentPfad;
	}

	public void setDokumentPfad(String dokumentPfad) {
		this.dokumentPfad = dokumentPfad;
	}

	public String getDokumentSize() {
		return dokumentSize;
	}

	public void setDokumentSize(String dokumentSize) {
		this.dokumentSize = dokumentSize;
	}

	@Override
	public String toString() {
		return "Dokument{" +
			"dokumentName='" + dokumentName + '\'' +
			", dokumentPfad='" + dokumentPfad + '\'' +
			", dokumentSize='" + dokumentSize + '\'' +
			'}';
	}
}
