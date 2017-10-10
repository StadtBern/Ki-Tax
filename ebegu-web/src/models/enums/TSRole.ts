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

export enum TSRole {
    SUPER_ADMIN = <any> 'SUPER_ADMIN',
    ADMIN = <any> 'ADMIN',
    SACHBEARBEITER_JA= <any> 'SACHBEARBEITER_JA',
    SACHBEARBEITER_INSTITUTION= <any> 'SACHBEARBEITER_INSTITUTION',
    SACHBEARBEITER_TRAEGERSCHAFT= <any> 'SACHBEARBEITER_TRAEGERSCHAFT',
    GESUCHSTELLER= <any> 'GESUCHSTELLER',
    JURIST= <any> 'JURIST',
    REVISOR= <any> 'REVISOR',
    STEUERAMT= <any> 'STEUERAMT',
    ADMINISTRATOR_SCHULAMT= <any> 'ADMINISTRATOR_SCHULAMT',
    SCHULAMT= <any> 'SCHULAMT'
}

export function getTSRoleValues(): Array<TSRole> {
    return [
        TSRole.SUPER_ADMIN,
        TSRole.ADMIN,
        TSRole.SACHBEARBEITER_JA,
        TSRole.SACHBEARBEITER_INSTITUTION,
        TSRole.SACHBEARBEITER_TRAEGERSCHAFT,
        TSRole.GESUCHSTELLER,
        TSRole.JURIST,
        TSRole.REVISOR,
        TSRole.STEUERAMT,
        TSRole.ADMINISTRATOR_SCHULAMT,
        TSRole.SCHULAMT,
    ];
}

export function rolePrefix(): string {
    return 'TSRole_';
}


