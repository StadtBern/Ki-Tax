package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entitaet zum Speichern von Dokumente in der Datenbank.
 */
@Audited
@Entity
public class Dokument extends FileMetadata {

	private static final long serialVersionUID = -895840426585785097L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_dokument_dokumentgrund_id"), nullable = false)
	private DokumentGrund dokumentGrund;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime timestampUpload;


	public Dokument() {
	}


	public Dokument(DokumentGrund dokumentGrund) {
		this.dokumentGrund = dokumentGrund;
	}

	public DokumentGrund getDokumentGrund() {
		return dokumentGrund;
	}

	public void setDokumentGrund(DokumentGrund dokumentGrund) {
		this.dokumentGrund = dokumentGrund;
	}

	public LocalDateTime getTimestampUpload() {
		return timestampUpload;
	}

	public void setTimestampUpload(LocalDateTime timestampUpload) {
		this.timestampUpload = timestampUpload;
	}

	@Override
	public String toString() {
		return "Dokument{" +
			"dokumentName='" + getFilename() + "\'" +
			", dokumentPfad='" + getFilepfad() + "\'" +
			", dokumentSize='" + getFilesize() + "\'" +
			"}";
	}

	public Dokument copyForMutation(Dokument mutation, DokumentGrund dokumentGrundMutation) {
		super.copyForMutation(mutation);
		mutation.setDokumentGrund(dokumentGrundMutation);
		mutation.setTimestampUpload(timestampUpload);
		return mutation;
	}

}
