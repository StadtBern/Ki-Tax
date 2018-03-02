/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.dto.suchfilter.lucene.EbeguLocalDateBridge;
import ch.dvbern.ebegu.enums.GesuchDeletionCause;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.GesuchDeletionLogEntityListener;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.bridge.builtin.LongBridge;

/**
 * Entity fuer die Protokollierung von Gesuch-Löschungen (durch BatchJob oder durch Admin)
 */
@Entity
@EntityListeners(GesuchDeletionLogEntityListener.class)
public class GesuchDeletionLog extends AbstractSimpleEntity {

	private static final long serialVersionUID = -8876987863152535840L;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GesuchDeletionCause cause;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false)
	private LocalDateTime timestampDeleted;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userDeleted;

	@NotNull
	@Column(nullable = false)
	@Field(bridge = @FieldBridge(impl = LongBridge.class))
	private long fallNummer;

	@NotNull
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String gesuchId;

	@Column(nullable = true)
	@Field()
	private String vorname;

	@Column(nullable = true)
	@Field()
	private String nachname;

	@Column(nullable = true)
	@FieldBridge(impl = EbeguLocalDateBridge.class)   //wir indizieren dates als string
	@Field(analyze = Analyze.NO) //datumsfelder nicht tokenizen etc
	private LocalDate geburtsdatum;


	public GesuchDeletionLog() {
	}

	public GesuchDeletionLog(@Nonnull Gesuch gesuch, GesuchDeletionCause deletionCause) {
		this.cause = deletionCause;
		this.fallNummer = gesuch.getFall().getFallNummer();
		this.gesuchId = gesuch.getId();
		Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
		if (gesuchsteller != null) {
			this.vorname = gesuchsteller.getVorname();
			this.nachname = gesuchsteller.getNachname();
			this.geburtsdatum = gesuchsteller.getGeburtsdatum();
		}
	}

	public GesuchDeletionCause getCause() {
		return cause;
	}

	public void setCause(GesuchDeletionCause typ) {
		this.cause = typ;
	}

	public LocalDateTime getTimestampDeleted() {
		return timestampDeleted;
	}

	public void setTimestampDeleted(LocalDateTime timestampDeleted) {
		this.timestampDeleted = timestampDeleted;
	}

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public String getUserDeleted() {
		return userDeleted;
	}

	public void setUserDeleted(String userDeleted) {
		this.userDeleted = userDeleted;
	}

	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}
}
