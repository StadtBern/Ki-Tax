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

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import ch.dvbern.ebegu.util.UploadFileInfo;

/**
 * Entitaet zum Speichern von DownloadFile in der Datenbank.
 */
@Entity
public class DownloadFile extends FileMetadata {

	private static final long serialVersionUID = 5960979521430438226L;

	@Column(length = 36, nullable = false, updatable = false)
	private final String accessToken;

	@Column(length = 45, nullable = false, updatable = false)
	private final String ip;

	public DownloadFile() {
		this.accessToken = UUID.randomUUID().toString();
		this.ip = "";
	}

	public DownloadFile(@Nonnull FileMetadata file, @Nonnull String ip) {
		super(file);
		this.accessToken = UUID.randomUUID().toString();
		this.ip = ip;

	}

	public DownloadFile(UploadFileInfo uploadFileInfo, @Nonnull String ip) {
		super(uploadFileInfo);
		this.accessToken = UUID.randomUUID().toString();
		this.ip = ip;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getIp() {
		return ip;
	}
}
