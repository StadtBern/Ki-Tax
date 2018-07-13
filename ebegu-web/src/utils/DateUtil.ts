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
import Moment = moment.Moment; // kann das über ein anderes Import Format gelöst werden (import ... from 'moment')?

export default class DateUtil {

    /**
     * @param {string} localDateTimeString string with format YYYY-MM-DDTHH:mm:ss.SSS
     * @returns {?Moment}
     */
    public static localDateTimeToMoment(localDateTimeString: string): Moment {
        let theMoment: Moment = moment(localDateTimeString, ['YYYY-MM-DDTHH:mm:ss.SSS', 'YYYY-MM-DDTHH:mm:ss', 'YYYY-MM-DDTHH:mm:ss.SSSZ'], true);
        if (!theMoment.isValid()) {
            console.warn('Trying to parse a invalid date to moment', theMoment);
            return undefined;
        }
        return theMoment;
    }

    /**
     * Calls momentToLocalDateFormat with the format by default 'YYYY-MM-DD'
     * @param aMoment
     * @returns {string}
     */
    public static momentToLocalDate(aMoment: Moment): string {
        return DateUtil.momentToLocalDateFormat(aMoment, 'YYYY-MM-DD');
    }

    /**
     * @param {Moment} aMoment time instance
     * @param {string} format the format
     * @returns {?string} a Date (YYYY-MM-DD) representation of the given moment. NULL when aMoment is invalid
     */
    public static momentToLocalDateFormat(aMoment: Moment, format: string): string {
        if (!aMoment) {
            return undefined;
        }
        return moment(aMoment).startOf('day').format(format);
    }

    /**
     * @param {Moment} aMoment time instance
     * @param format format for the time
     * @returns {?string} a Date (YYYY-MM-DD) representation of the given moment. undefined when aMoment is invalid
     */
    public static momentToLocalDateTimeFormat(aMoment: Moment, format: string): string {
        if (!aMoment) {
            return undefined;
        }
        return moment(aMoment).format(format);
    }

    /**
     * @param {Moment} aMoment time instance
     * @returns {?string} a Date (YYYY-MM-DD) representation of the given moment. NULL when aMoment is invalid
     */
    public static momentToLocalDateTime(aMoment: Moment): string {
        return DateUtil.momentToLocalDateTimeFormat(aMoment, 'YYYY-MM-DDTHH:mm:ss.SSS');
    }

    /**
     * @param {string} localDateString string with format YYYY-MM-DD
     * @returns {?moment}
     */
    public static localDateToMoment(localDateString: string): Moment {
        let theMoment = moment(localDateString, 'YYYY-MM-DD', true);
        return theMoment.isValid() ? theMoment : undefined;
    }

    public static today(): Moment {
        return moment().startOf('day');
    }

    public static now(): Moment {
        return moment();
    }

    public static currentYear(): number {
        return moment().year();
    }

    /**
     * Vergleicht 2 Moments. Date und Time werden beruecksichtigt.
     */
    public static compareDateTime(a: Moment, b: Moment): number {
        if (a.isBefore(b)) {
            return -1;
        } else if (a.isSame(b)) {
            return 0;
        } else {
            return 1;
        }
    }
}
