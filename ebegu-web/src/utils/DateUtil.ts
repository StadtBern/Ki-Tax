module ebeguWeb.utils {
    'use strict';

    export class DateUtil {

        static $inject = ['moment'];

        /**
         * @param {string} localDateTimeString string with format YYYY-MM-DDTHH:mm:ss.SSS
         * @returns {?moment.Moment}
         */
        public static localDateTimeToMoment(localDateTimeString: string): moment.Moment {
            var theMoment: moment.Moment = moment(localDateTimeString, 'YYYY-MM-DDTHH:mm:ss.SSS', true);
            return theMoment.isValid() ? theMoment : null;
        }

    }

}
