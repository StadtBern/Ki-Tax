/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.util.UploadFileInfo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Objects;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Gemeinsame Basisklasse für speichern von Files. Der Content wird dabei nicht gespeichert sondern
 * nur die Metainformationen.
 *
 * @author gapa
 * @version 1.0
 */
@Audited
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FileMetadata extends AbstractEntity {

	private static final long serialVersionUID = -4502262818759522627L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String filename;

	//Dokument soll nicht in DB gespeichert werden, sondern in File-System. Wie genau ist noch nicht klar und muss noch evaluiert werden!
	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String filepfad;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String filesize;

	// copy
	public FileMetadata(FileMetadata fileMetadata) {
		this.filename = fileMetadata.filename;
		this.filepfad = fileMetadata.filepfad;
		this.filesize = fileMetadata.filesize;
	}

	public FileMetadata() {
	}

	public FileMetadata(UploadFileInfo uploadFileInfo) {
		this.filename = uploadFileInfo.getFilename();
		this.filepfad = uploadFileInfo.getPath();
		this.filesize = uploadFileInfo.getSizeString();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String dokumentName) {
		this.filename = dokumentName;
	}

	public String getFilepfad() {
		return filepfad;
	}

	public void setFilepfad(String dokumentPfad) {
		this.filepfad = dokumentPfad;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String dokumentSize) {
		this.filesize = dokumentSize;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("filename", filename)
			.append("filepfad", filepfad)
			.toString();
	}

	public FileMetadata copyForMutation(FileMetadata mutation) {
		super.copyForMutation(mutation);
		mutation.setFilename(this.filename);
		mutation.setFilepfad(this.filepfad);
		mutation.setFilesize(this.filesize);
		return mutation;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!(other instanceof FileMetadata)) {
			return false;
		}
		final FileMetadata otherFileMetadata = (FileMetadata) other;
		return Objects.equals(getFilename(), otherFileMetadata.getFilename()) &&
			Objects.equals(getFilepfad(), otherFileMetadata.getFilepfad()) &&
			Objects.equals(getFilesize(), otherFileMetadata.getFilesize());
	}
}
