package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Entitaet zum Speichern von Dokumente in der Datenbank.
 */
@Audited
@Entity
public class Dokument extends File {

	private static final long serialVersionUID = -895840426585785097L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_dokument_dokumentgrund_id"), nullable = false)
	private DokumentGrund dokumentGrund;

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

	@Override
	public String toString() {
		return "Dokument{" +
			"dokumentName='" + getFilename() + '\'' +
			", dokumentPfad='" + getFilepfad() + '\'' +
			", dokumentSize='" + getFilesize() + '\'' +
			'}';
	}
}
