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
import * as moment from 'moment';

export enum TSFristverlaengerung {
    KEINE_FRISTVERLAENGERUNG = <any> 'KEINE_FRISTVERLAENGERUNG',
    FRISTVERLAENGERUNG_SEPTEMBER = <any> 'FRISTVERLAENGERUNG_SEPTEMBER',
    FRISTVERLAENGERUNG_NOVEMBER = <any> 'FRISTVERLAENGERUNG_NOVEMBER',
}

export function getTSFristverlaengerungValuesForSCH(): Array<TSFristverlaengerung> {
    return [
        TSFristverlaengerung.KEINE_FRISTVERLAENGERUNG,
        TSFristverlaengerung.FRISTVERLAENGERUNG_SEPTEMBER,
        TSFristverlaengerung.FRISTVERLAENGERUNG_NOVEMBER
    ];
}

export function getTSFristverlaengerungValuesForGS(): Array<TSFristverlaengerung> {
    return [
        TSFristverlaengerung.KEINE_FRISTVERLAENGERUNG,
        TSFristverlaengerung.FRISTVERLAENGERUNG_SEPTEMBER,
    ];
}

export function getFristverlaengerungAsMoment(fristverlaengerung: TSFristverlaengerung, gesuchsperiodeyear: number): moment.Moment {
    //TODO imanol review: sollte das Datum hier anders hinterlegt werden?
    switch (fristverlaengerung) {
        case TSFristverlaengerung.FRISTVERLAENGERUNG_SEPTEMBER:
            return moment(gesuchsperiodeyear + '-09-15');
        case TSFristverlaengerung.FRISTVERLAENGERUNG_NOVEMBER:
            return moment(gesuchsperiodeyear + '-11-15');
        case TSFristverlaengerung.KEINE_FRISTVERLAENGERUNG:
            return null;
        default:
            return null;
    }
}

export function getFristverlaengerungFromMoment(fristverlaengerungMoment: moment.Moment): TSFristverlaengerung {
    if (fristverlaengerungMoment === undefined || fristverlaengerungMoment === null) {
        return TSFristverlaengerung.KEINE_FRISTVERLAENGERUNG;
    }
    //TODO imanol review: sollte das Datum hier anders hinterlegt werden?
    if (fristverlaengerungMoment.get('month') === 8) {
        return TSFristverlaengerung.FRISTVERLAENGERUNG_SEPTEMBER;
    } else if (fristverlaengerungMoment.get('month') === 10) {
        return TSFristverlaengerung.FRISTVERLAENGERUNG_NOVEMBER;
    } else {
        return TSFristverlaengerung.KEINE_FRISTVERLAENGERUNG;
    }
}

