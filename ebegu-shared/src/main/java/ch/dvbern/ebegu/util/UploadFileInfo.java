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

package ch.dvbern.ebegu.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.activation.MimeType;
import java.util.Arrays;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class UploadFileInfo {

	private String filename;

	private String actualFilename;

	private final MimeType contentType;

	private byte[] bytes;

	private String path;

	private Long size;

	public UploadFileInfo(
		String filename,
		MimeType contentType) {
		this.filename = filename;
		this.contentType = contentType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getActualFilename() {
		return actualFilename;
	}

	public void setActualFilename(String actualFilename) {
		this.actualFilename = actualFilename;
	}

	public MimeType getContentType() {
		return contentType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = Arrays.copyOf(bytes, bytes.length);
	}

	public String getPath() {
		return path;
	}

	public String getPathWithoutFileName() {
		return this.getPath().substring(0, this.getPath().lastIndexOf("/"));
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getSizeString() {

		int unit = 1000;
		if (size < unit) {
			return size + " B";
		}
		int exp = (int) (Math.log(size) / Math.log(unit));
		String pre = ("kMGTPE").charAt(exp - 1) + "";
		return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
	}
}
