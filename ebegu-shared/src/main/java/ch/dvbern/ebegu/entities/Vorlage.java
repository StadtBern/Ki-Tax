package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Entitaet zum Speichern von Dokumente in der Datenbank.
 */
@Audited
@Entity
public class Vorlage extends File {

	private static final long serialVersionUID = -895840426585785097L;

	public Vorlage() {
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
