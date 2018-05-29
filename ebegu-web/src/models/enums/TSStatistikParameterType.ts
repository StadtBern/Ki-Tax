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

export enum TSStatistikParameterType {
    GESUCH_STICHTAG = <any>'GESUCH_STICHTAG',
    GESUCH_ZEITRAUM = <any>'GESUCH_ZEITRAUM',
    KINDER = <any>'KINDER',
    GESUCHSTELLER = <any>'GESUCHSTELLER',
    KANTON = <any>'KANTON',
    MITARBEITERINNEN = <any>'MITARBEITERINNEN',
    GESUCHSTELLER_KINDER_BETREUUNG = <any>'GESUCHSTELLER_KINDER_BETREUUNG',
    ZAHLUNGEN_PERIODE = <any>'ZAHLUNGEN_PERIODE',
    BENUTZER = <any>'BENUTZER'
}

export function getTSStatistikParameterKeyValues(): Array<TSStatistikParameterType> {
    return [
        TSStatistikParameterType.GESUCH_STICHTAG,
        TSStatistikParameterType.GESUCH_ZEITRAUM,
        TSStatistikParameterType.KINDER,
        TSStatistikParameterType.GESUCHSTELLER,
        TSStatistikParameterType.KANTON,
        TSStatistikParameterType.MITARBEITERINNEN,
        TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG,
        TSStatistikParameterType.ZAHLUNGEN_PERIODE,
        TSStatistikParameterType.BENUTZER,
    ];
}
