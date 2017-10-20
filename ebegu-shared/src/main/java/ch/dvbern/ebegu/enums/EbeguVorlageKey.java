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

/**
 * Keys für die zeitabhängigen E-BEGU-Vorlagen
 */
public enum EbeguVorlageKey {

	// Die erste Mahnung, falls das Gesuch unvollständig eingereicht wurde
	VORLAGE_MAHNUNG_1("/vorlagen/1_Mahnung.docx"),

	// Die zweite Mahnung, falls das Gesuch unvollständig eingereicht wurde
	VORLAGE_MAHNUNG_2("/vorlagen/2_Mahnung.docx"),

	// Verfügung des Angebots KITA
	VORLAGE_VERFUEGUNG_KITA("/vorlagen/Verfuegungsmuster_kita.docx"),

	// Eine Angebotsübergreifende Verfügung Verfügung Kita Verfügung des Angebots Kita
	VORLAGE_NICHT_EINTRETENSVERFUEGUNG("/vorlagen/Nichteintretensverfuegung.docx"),

	// Vorlage fuer Nicht-Eintreten bei Schulkindern
	VORLAGE_INFOSCHREIBEN_MAXIMALTARIF("/vorlagen/Infoschreiben_Maxtarif.docx"),

	// Verfügung des Angebots Tageseltern Kleinkinder
	VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER("/vorlagen/Verfuegungsmuster_tageseltern_kleinkinder.docx"),

	// Verfügung (Brief) des Angebots Tagesstaette Schulkinder
	VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER("/vorlagen/Verfuegungsmuster_tagesstaette_schulkinder.docx"),

	// Verfügung (Brief) des Angebots Tageseltern Schulkinder
	VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER("/vorlagen/Verfuegungsmuster_tageseltern_schulkinder.docx"),

	// Die Freigabequittung die der Gesuchsteller ausdruckt
	VORLAGE_FREIGABEQUITTUNG("/vorlagen/Freigabequittung.docx"),

	// Ein Anhang der mit den Verfügungen mitgeschickt wird und aufzeigt, wie die
	// finanzielle Situation gerechnet wurde
	VORLAGE_FINANZIELLE_SITUATION("/vorlagen/Berechnungsgrundlagen.docx"),

	// Ein angebotsübergreifenden neutralen Begleitbrief zu den Verfügungen
	VORLAGE_BEGLEITSCHREIBEN("/vorlagen/Begleitschreiben.docx"),

	// Benutzerhandbuecher
	VORLAGE_BENUTZERHANDBUCH_ADMIN("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Administrator.pdf"),
	VORLAGE_BENUTZERHANDBUCH_INSTITUTION("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Institution.pdf"),
	VORLAGE_BENUTZERHANDBUCH_JUGENDAMT("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Jugendamt.pdf"),
	VORLAGE_BENUTZERHANDBUCH_JURIST("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Jurist.pdf"),
	VORLAGE_BENUTZERHANDBUCH_REVISOR("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Revisor.pdf"),
	VORLAGE_BENUTZERHANDBUCH_SCHULAMT("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Schulamt.pdf"),
	VORLAGE_BENUTZERHANDBUCH_STV("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Steuerverwaltung.pdf"),
	VORLAGE_BENUTZERHANDBUCH_TRAEGERSCHAFT("/benutzerhandbuch/Ki-Tax Benutzerhandbuch - Rolle Trägerschaft.pdf");

	private boolean proGesuchsperiode;

	private String defaultVorlagePath;

	EbeguVorlageKey() {
		this.proGesuchsperiode = true;
	}

	EbeguVorlageKey(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	EbeguVorlageKey(String defaultVorlagePath) {
		this.proGesuchsperiode = true;
		this.defaultVorlagePath = defaultVorlagePath;
	}

	EbeguVorlageKey(boolean proGesuchsperiode, String defaultVorlagePath) {
		this.proGesuchsperiode = proGesuchsperiode;
		this.defaultVorlagePath = defaultVorlagePath;
	}

	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	public String getDefaultVorlagePath() {
		return defaultVorlagePath;
	}

	public void setDefaultVorlagePath(String defaultVorlagePath) {
		this.defaultVorlagePath = defaultVorlagePath;
	}

	@SuppressWarnings("OverlyComplexMethod")
	public static EbeguVorlageKey getBenutzerHandbuchKeyForRole(UserRole userRole) {
		if (userRole != null) {
			switch (userRole) {
				case ADMIN:
				case SUPER_ADMIN:
					return VORLAGE_BENUTZERHANDBUCH_ADMIN;
				case SACHBEARBEITER_JA:
					return VORLAGE_BENUTZERHANDBUCH_JUGENDAMT;
				case SACHBEARBEITER_TRAEGERSCHAFT:
					return VORLAGE_BENUTZERHANDBUCH_TRAEGERSCHAFT;
				case SACHBEARBEITER_INSTITUTION:
					return VORLAGE_BENUTZERHANDBUCH_INSTITUTION;
				case JURIST:
					return VORLAGE_BENUTZERHANDBUCH_JURIST;
				case REVISOR:
					return VORLAGE_BENUTZERHANDBUCH_REVISOR;
				case STEUERAMT:
					return VORLAGE_BENUTZERHANDBUCH_STV;
				case SCHULAMT:
				case ADMINISTRATOR_SCHULAMT:
					return VORLAGE_BENUTZERHANDBUCH_SCHULAMT;
				default:
					return null;
			}
		}
		return null;
	}

	public static EbeguVorlageKey[] getAllKeysProGesuchsperiode() {
		return new EbeguVorlageKey[] {
			VORLAGE_MAHNUNG_1,
			VORLAGE_MAHNUNG_2,
			VORLAGE_VERFUEGUNG_KITA,
			VORLAGE_NICHT_EINTRETENSVERFUEGUNG,
			VORLAGE_INFOSCHREIBEN_MAXIMALTARIF,
			VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER,
			VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER,
			VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER,
			VORLAGE_FREIGABEQUITTUNG,
			VORLAGE_FINANZIELLE_SITUATION,
			VORLAGE_BEGLEITSCHREIBEN };
	}
}
