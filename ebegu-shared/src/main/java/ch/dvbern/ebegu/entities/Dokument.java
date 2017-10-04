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

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

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
