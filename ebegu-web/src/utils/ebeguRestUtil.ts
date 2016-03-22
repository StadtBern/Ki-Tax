/// <reference path="../../typings/browser.d.ts" />
/// <reference path="./DateUtil.ts" />
module ebeguWeb.utils {
    'use strict';

    export class EbeguRestUtil {

        /**
         * Wndelt Data in einen TSApplicationProperty Array um, welches danach zurueckgeliefert wird
         * @param data
         * @returns {any}
         */
        public static parseApplicationProperties(data: any) : ebeguWeb.API.TSApplicationProperty[] {
            var appProperties: ebeguWeb.API.TSApplicationProperty[] = new Array<ebeguWeb.API.TSApplicationProperty>();
            if (data !== null && Array.isArray(data)) {
                for (var i = 0; i < data.length; i++) {
                    appProperties[i] = EbeguRestUtil.parseApplicationProperty(new ebeguWeb.API.TSApplicationProperty('', ''), data[i]);
                }
            }
            else {
                appProperties[0] = EbeguRestUtil.parseApplicationProperty(new ebeguWeb.API.TSApplicationProperty('', ''), data);
            }
            return appProperties;
        }

        /**
         * Wandelt die receivedAppProperty in einem parsedAppProperty um.
         * @param parsedAppProperty
         * @param receivedAppProperty
         * @returns {ebeguWeb.API.TSApplicationProperty}
         */
        public static parseApplicationProperty(parsedAppProperty: ebeguWeb.API.TSApplicationProperty, receivedAppProperty: any): ebeguWeb.API.TSApplicationProperty {
            parsedAppProperty.name = receivedAppProperty.name;
            parsedAppProperty.value = receivedAppProperty.value;
            parsedAppProperty.timestampErstellt = DateUtil.localDateTimeToMoment(receivedAppProperty.timestampErstellt);
            parsedAppProperty.timestampMutiert = DateUtil.localDateTimeToMoment(receivedAppProperty.timestampMutiert);
            return parsedAppProperty;
        }

    }

}
