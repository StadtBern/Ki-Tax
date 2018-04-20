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

package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "file")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFile extends JaxAbstractDTO {

	private static final long serialVersionUID = 1118235796540488553L;

	private String filename;

	private String filepfad;

	private String filesize;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepfad() {
		return filepfad;
	}

	public void setFilepfad(String filepfad) {
		this.filepfad = filepfad;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		JaxFile that = (JaxFile) o;

		if (filename != null ? !filename.equals(that.filename) : that.filename != null) {
			return false;
		}
		if (filepfad != null ? !filepfad.equals(that.filepfad) : that.filepfad != null) {
			return false;
		}
		return filesize != null ? filesize.equals(that.filesize) : that.filesize == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (filename != null ? filename.hashCode() : 0);
		result = 31 * result + (filepfad != null ? filepfad.hashCode() : 0);
		result = 31 * result + (filesize != null ? filesize.hashCode() : 0);
		return result;
	}
}
