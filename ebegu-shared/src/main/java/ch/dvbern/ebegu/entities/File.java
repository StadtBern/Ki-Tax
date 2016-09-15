package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Gemeinsame Basisklasse für speichern von Files
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class File extends AbstractEntity {

	private static final long serialVersionUID = -4502262818759522627L;

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

	public String getDokumentName() {
		return dokumentName;
	}

	public void setDokumentName(String dokumentName) {
		this.dokumentName = dokumentName;
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
}
