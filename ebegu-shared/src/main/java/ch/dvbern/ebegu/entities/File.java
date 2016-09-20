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

	public File(File file) {
		this.fileName = file.fileName;
		this.filePfad = file.filePfad;
		this.fileSize = file.fileSize;
	}

	public File() {
	}

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String fileName;

	//Dokument soll nicht in DB gespeichert werden, sondern in File-System. Wie genau ist noch nicht klar und muss noch evaluiert werden!
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String filePfad;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String fileSize;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String dokumentName) {
		this.fileName = dokumentName;
	}

	public String getFilePfad() {
		return filePfad;
	}

	public void setFilePfad(String dokumentPfad) {
		this.filePfad = dokumentPfad;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String dokumentSize) {
		this.fileSize = dokumentSize;
	}
}
