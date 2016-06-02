import * as moment from 'moment';
import Moment = moment.Moment; // TODO kann das über ein anderes Import Format gelöst werden (import ... from 'moment')?

export default class DateUtil {



    /**
     * @param {string} localDateTimeString string with format YYYY-MM-DDTHH:mm:ss.SSS
     * @returns {?Moment}
     */
    public static localDateTimeToMoment(localDateTimeString: string): Moment {
        var theMoment: Moment = moment(localDateTimeString, ['YYYY-MM-DDTHH:mm:ss.SSS', 'YYYY-MM-DDTHH:mm:ss', 'YYYY-MM-DDTHH:mm:ss.SSSZ'], true);
        return theMoment.isValid() ? theMoment : undefined;
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
     * @returns {?string} a Date (YYYY-MM-DD) representation of the given moment. NULL when aMoment is invalid
     */
    public static momentToLocalDateTime(aMoment: Moment): string {
        return moment(aMoment).format('YYYY-MM-DDTHH:mm:ss.SSS');
    }

    /**
     * @param {string} localDateString string with format YYYY-MM-DD
     * @returns {?moment}
     */
    public static localDateToMoment(localDateString: string) { // TODO how to annotate optional Moment return type?
        var theMoment = moment(localDateString, 'YYYY-MM-DD', true);
        return theMoment.isValid() ? theMoment : undefined;
    }

    public static today(): Moment {
        return moment().startOf('day');
    }
}
