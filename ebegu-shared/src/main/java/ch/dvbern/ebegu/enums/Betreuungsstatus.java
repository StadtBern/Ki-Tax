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

package ch.dvbern.ebegu.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum fuers Feld status in einer Betreuung.
 */
public enum Betreuungsstatus {

	// Ablauf beim Jugendamt
	WARTEN,
	SCHULAMT, //TODO (team) Diesen Status später entfernen?
	ABGEWIESEN,
	NICHT_EINGETRETEN,
	STORNIERT,
	BESTAETIGT,
	VERFUEGT,
	GESCHLOSSEN_OHNE_VERFUEGUNG,

	// Ablauf beim Schulamt
	SCHULAMT_ANMELDUNG_ERFASST,
	SCHULAMT_ANMELDUNG_AUSGELOEST,
	SCHULAMT_ANMELDUNG_UEBERNOMMEN,
	SCHULAMT_ANMELDUNG_ABGELEHNT,
	SCHULAMT_FALSCHE_INSTITUTION;

	private static final Set<Betreuungsstatus> all = EnumSet.allOf(Betreuungsstatus.class);
	private static final Set<Betreuungsstatus> none = EnumSet.noneOf(Betreuungsstatus.class);

	public static final Set<Betreuungsstatus> hasVerfuegung = EnumSet.of(VERFUEGT, NICHT_EINGETRETEN);
	public static final Set<Betreuungsstatus> forPendenzInstitution = EnumSet.of(WARTEN, SCHULAMT_ANMELDUNG_AUSGELOEST);
	public static final Set<Betreuungsstatus> forPendenzSchulamt = EnumSet.of(SCHULAMT_ANMELDUNG_AUSGELOEST, SCHULAMT_FALSCHE_INSTITUTION);
	public static final Set<Betreuungsstatus> betreuungsstatusAusgeloest = EnumSet.of(SCHULAMT_ANMELDUNG_AUSGELOEST,
		SCHULAMT_ANMELDUNG_UEBERNOMMEN, SCHULAMT_ANMELDUNG_ABGELEHNT, SCHULAMT_FALSCHE_INSTITUTION);

	public boolean isGeschlossenJA() {
		return VERFUEGT == this || GESCHLOSSEN_OHNE_VERFUEGUNG == this || NICHT_EINGETRETEN == this;
	}

	/**
	 * Alle SCH-Status, die ausgeloest sind, gelten als geschlossen, da sie im Verfuegungsprozess nicht beruecksichtigt werden.
	 */
	public boolean isGeschlossen() {
		return VERFUEGT == this || GESCHLOSSEN_OHNE_VERFUEGUNG == this || NICHT_EINGETRETEN == this || SCHULAMT == this
			|| SCHULAMT_ANMELDUNG_UEBERNOMMEN == this || SCHULAMT_ANMELDUNG_ABGELEHNT == this || SCHULAMT_ANMELDUNG_AUSGELOEST == this
			|| SCHULAMT_FALSCHE_INSTITUTION == this;
	}

	public boolean isAnyStatusOfVerfuegt() {
		return VERFUEGT == this || STORNIERT == this || SCHULAMT == this
			|| SCHULAMT_ANMELDUNG_UEBERNOMMEN == this || SCHULAMT_ANMELDUNG_ABGELEHNT == this;
	}

	public boolean isSendToInstitution() {
		return ABGEWIESEN == this || BESTAETIGT == this || WARTEN == this ;
	}

	public boolean isSchulamt() {
		return SCHULAMT == this || SCHULAMT_ANMELDUNG_ERFASST  == this || SCHULAMT_ANMELDUNG_AUSGELOEST == this
			|| SCHULAMT_ANMELDUNG_UEBERNOMMEN == this|| SCHULAMT_ANMELDUNG_ABGELEHNT == this  || SCHULAMT_FALSCHE_INSTITUTION == this;
	}

	public boolean isStorniert() {
		return STORNIERT == this;
	}

	@SuppressWarnings("Duplicates")
	public static Set<Betreuungsstatus> allowedRoles(UserRole userRole) {
		switch (userRole) {
		case SUPER_ADMIN:
			return all;
		case ADMIN:
			return all;
		case GESUCHSTELLER:
			return all;
		case JURIST:
			return all;
		case REVISOR:
			return all;
		case SACHBEARBEITER_INSTITUTION:
			return all;
		case SACHBEARBEITER_JA:
			return all;
		case SACHBEARBEITER_TRAEGERSCHAFT:
			return all;
		case SCHULAMT:
			return all;
		case STEUERAMT:
			return all;
		default:
			return none;
		}
	}
}
