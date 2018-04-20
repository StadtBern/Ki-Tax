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

export enum TSZuschlagsgrund {
    UNREGELMAESSIGE_ARBEITSZEITEN = <any> 'UNREGELMAESSIGE_ARBEITSZEITEN',
    UEBERLAPPENDE_ARBEITSZEITEN = <any> 'UEBERLAPPENDE_ARBEITSZEITEN',
    FIXE_ARBEITSZEITEN = <any> 'FIXE_ARBEITSZEITEN',
    LANGER_ARBWEITSWEG = <any> 'LANGER_ARBWEITSWEG',
    ANDERE = <any> 'ANDERE'

}

export function getTSZuschlagsgrunde(): Array<TSZuschlagsgrund> {
    return [
        TSZuschlagsgrund.UNREGELMAESSIGE_ARBEITSZEITEN,
        TSZuschlagsgrund.UEBERLAPPENDE_ARBEITSZEITEN,
        TSZuschlagsgrund.FIXE_ARBEITSZEITEN,
        TSZuschlagsgrund.LANGER_ARBWEITSWEG,
        TSZuschlagsgrund.ANDERE,

    ];
}
/**
 * Gesuchsteller duerfen nicht alle Gruende auswaehlen
 * @returns {TSZuschlagsgrund[]}
 */
export function getTSZuschlagsgruendeForGS() {
    return [
        TSZuschlagsgrund.UNREGELMAESSIGE_ARBEITSZEITEN,
        TSZuschlagsgrund.UEBERLAPPENDE_ARBEITSZEITEN,
        TSZuschlagsgrund.FIXE_ARBEITSZEITEN,
        TSZuschlagsgrund.LANGER_ARBWEITSWEG,
    ];
}
