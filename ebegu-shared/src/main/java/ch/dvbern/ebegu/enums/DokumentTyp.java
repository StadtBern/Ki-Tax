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
 * Enum fuer den Dokumenten Typ
 */
public enum DokumentTyp {
	NACHWEIS_TRENNUNG, //mutation
	FACHSTELLENBEST_SOZ,
	FACHSTELLENBEST_BEH,
	NACHWEIS_ERWERBSPENSUM,
	NACHWEIS_SELBSTAENDIGKEIT,
	NACHWEIS_AUSBILDUNG,
	NACHWEIS_RAV,
	BESTAETIGUNG_ARZT,
	NACHWEIS_UNREG_ARBEITSZ,
	NACHWEIS_LANG_ARBEITSWEG,
	NACHWEIS_SONSTIGEN_ZUSCHLAG,
	NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT,
	NACHWEIS_FIXE_ARBEITSZEITEN,
	STEUERVERANLAGUNG,
	STEUERERKLAERUNG,
	JAHRESLOHNAUSWEISE,
	NACHWEIS_FAMILIENZULAGEN,
	NACHWEIS_ERSATZEINKOMMEN,
	NACHWEIS_ERHALTENE_ALIMENTE,
	NACHWEIS_GELEISTETE_ALIMENTE,
	NACHWEIS_VERMOEGEN,
	NACHWEIS_SCHULDEN,
	ERFOLGSRECHNUNGEN_JAHR,
	ERFOLGSRECHNUNGEN_JAHR_MINUS1,
	ERFOLGSRECHNUNGEN_JAHR_MINUS2,
	UNTERSTUETZUNGSBESTAETIGUNG,
	//EKV
	NACHWEIS_EINKOMMENSSITUATION_MONAT,
	DIV,
	ORIGINAL_PAPIERGESUCH,
	ORIGINAL_FREIGABEQUITTUNG;
}
