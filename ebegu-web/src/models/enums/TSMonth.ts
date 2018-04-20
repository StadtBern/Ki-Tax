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

export enum TSMonth {
    VORJAHR = <any> 'VORJAHR',
    JANURAY = <any> 'JANUARY',
    FEBRUARY = <any> 'FEBRUARY',
    MARCH = <any> 'MARCH',
    APRIL = <any> 'APRIL',
    MAY = <any> 'MAY',
    JUNE = <any> 'JUNE',
    JULY = <any> 'JULY',
    AUGUST = <any> 'AUGUST',
    SEPTEMBER = <any> 'SEPTEMBER',
    OCTOBER = <any> 'OCTOBER',
    NOVEMBER = <any> 'NOVEMBER',
    DECEMBER = <any> 'DECEMBER',
}

export function getTSMonthValues(): Array<TSMonth> {
    return [
        TSMonth.JANURAY,
        TSMonth.FEBRUARY,
        TSMonth.MARCH,
        TSMonth.APRIL,
        TSMonth.MAY,
        TSMonth.JUNE,
        TSMonth.JULY,
        TSMonth.AUGUST,
        TSMonth.SEPTEMBER,
        TSMonth.OCTOBER,
        TSMonth.NOVEMBER,
        TSMonth.DECEMBER
    ];
}

export function getTSMonthWithVorjahrValues(): Array<TSMonth> {
    return [
        TSMonth.VORJAHR,
        TSMonth.JANURAY,
        TSMonth.FEBRUARY,
        TSMonth.MARCH,
        TSMonth.APRIL,
        TSMonth.MAY,
        TSMonth.JUNE,
        TSMonth.JULY,
        TSMonth.AUGUST,
        TSMonth.SEPTEMBER,
        TSMonth.OCTOBER,
        TSMonth.NOVEMBER,
        TSMonth.DECEMBER
    ];
}


