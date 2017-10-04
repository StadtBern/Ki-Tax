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

export enum TSBetreuungsangebotTyp {
    KITA = <any> 'KITA',
    TAGESELTERN_KLEINKIND = <any> 'TAGESELTERN_KLEINKIND',
    TAGESELTERN_SCHULKIND = <any> 'TAGESELTERN_SCHULKIND',
    TAGI = <any> 'TAGI',
    TAGESSCHULE = <any> 'TAGESSCHULE'
}

export function getTSBetreuungsangebotTypValues(): Array<TSBetreuungsangebotTyp> {
    return [
        TSBetreuungsangebotTyp.KITA,
        TSBetreuungsangebotTyp.TAGI,
        TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND,
        TSBetreuungsangebotTyp.TAGESELTERN_SCHULKIND,
        TSBetreuungsangebotTyp.TAGESSCHULE
    ];
}

export function isSchulamt(status: TSBetreuungsangebotTyp): boolean {
    return status === TSBetreuungsangebotTyp.TAGESSCHULE;
}

export function isJugendamt(status: TSBetreuungsangebotTyp): boolean {
    return !isSchulamt(status);
}
