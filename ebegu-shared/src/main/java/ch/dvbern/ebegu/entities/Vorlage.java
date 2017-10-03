package ch.dvbern.ebegu.entities;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von Dokumente in der Datenbank.
 */
@Audited
@Entity
public class Vorlage extends FileMetadata {

	private static final long serialVersionUID = -895840426585785097L;

	public Vorlage() {
	}

	public Vorlage copy() {

		Vorlage copied = new Vorlage();
		copied.setFilename(this.getFilename());
		copied.setFilesize(this.getFilesize());
		copied.setFilepfad(this.getFilepfad());
		return copied;
	}

	@Override
	public String toString() {
		return "Vorlage{" +
			"dokumentName='" + getFilename() + '\'' +
			", dokumentPfad='" + getFilepfad() + '\'' +
			", dokumentSize='" + getFilesize() + '\'' +
			'}';
	}
}
