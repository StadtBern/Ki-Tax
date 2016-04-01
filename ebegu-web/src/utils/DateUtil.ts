/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.utils {
    'use strict';

    export class DateUtil {

        static $inject = ['moment'];

        /**
         * @param {string} localDateTimeString string with format YYYY-MM-DDTHH:mm:ss.SSS
         * @returns {?moment.Moment}
         */
        public static localDateTimeToMoment(localDateTimeString:string):moment.Moment {
            var theMoment:moment.Moment = moment(localDateTimeString, 'YYYY-MM-DDTHH:mm:ss.SSS', true);
            return theMoment.isValid() ? theMoment : undefined;
        }

        /**
         * @param {moment.Moment} aMoment time instance
         * @returns {?string} a Date (YYYY-MM-DD) representation of the given moment. NULL when aMoment is invalid
         */
        public static momentToLocalDate(aMoment:moment.Moment):string {
            if(!aMoment){
                return undefined;
            }
            return moment(aMoment).startOf('day').format('YYYY-MM-DD');
        }

        /**
         * @param {moment.Moment} aMoment time instance
         * @returns {?string} a Date (YYYY-MM-DD) representation of the given moment. NULL when aMoment is invalid
         */
        public static momentToLocalDateTime(aMoment:moment.Moment):string {
            return moment(aMoment).format('YYYY-MM-DDTHH:mm:ss.SSS');
        }

        /**
         * @param {string} localDateString string with format YYYY-MM-DD
         * @returns {?moment}
         */
        public static localDateToMoment(localDateString) {
            var theMoment = moment(localDateString, 'YYYY-MM-DD', true);
            return theMoment.isValid() ? theMoment : undefined;
        }


        public static today() {
            return moment().startOf('day');

        }

    }

}
