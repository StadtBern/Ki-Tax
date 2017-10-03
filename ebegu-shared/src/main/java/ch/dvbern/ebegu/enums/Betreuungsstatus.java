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

	@Deprecated //wir glauben das gibts gar nicht mehr
	AUSSTEHEND,
	WARTEN,
	SCHULAMT,
	ABGEWIESEN,
	NICHT_EINGETRETEN,
	STORNIERT,
	BESTAETIGT,
	VERFUEGT,
	GESCHLOSSEN_OHNE_VERFUEGUNG;

	private static final Set<Betreuungsstatus> all = EnumSet.allOf(Betreuungsstatus.class);
	private static final Set<Betreuungsstatus> none = EnumSet.noneOf(Betreuungsstatus.class);

	private static final Set<Betreuungsstatus> forSachbearbeiterInstitutionRole = EnumSet.of(WARTEN, VERFUEGT, BESTAETIGT, ABGEWIESEN, NICHT_EINGETRETEN, STORNIERT, GESCHLOSSEN_OHNE_VERFUEGUNG);
	public static final Set<Betreuungsstatus> hasVerfuegung = EnumSet.of(VERFUEGT, NICHT_EINGETRETEN);

	private static final Set<Betreuungsstatus> forSachbearbeiterTraegerschaftRole = forSachbearbeiterInstitutionRole;


	public boolean isGeschlossen() {
		return VERFUEGT.equals(this) || GESCHLOSSEN_OHNE_VERFUEGUNG.equals(this) || NICHT_EINGETRETEN.equals(this);
	}

	public boolean isAnyStatusOfVerfuegt() {
		return VERFUEGT.equals(this) || STORNIERT.equals(this) || SCHULAMT.equals(this);
	}

	public boolean isSendToInstitution(){
		return ABGEWIESEN.equals(this) || BESTAETIGT.equals(this)|| WARTEN.equals(this);
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
				return forSachbearbeiterInstitutionRole;
			case SACHBEARBEITER_JA:
				return all;
			case SACHBEARBEITER_TRAEGERSCHAFT:
				return forSachbearbeiterTraegerschaftRole;
			case SCHULAMT:
				return all;
			case STEUERAMT:
				return all;
			default:
				return none;
		}
	}
}
