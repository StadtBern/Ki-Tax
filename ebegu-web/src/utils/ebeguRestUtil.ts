/// <reference path="../../typings/browser.d.ts" />
/// <reference path="./DateUtil.ts" />
module ebeguWeb.utils {
    'use strict';

    export class EbeguRestUtil {

        /**
         * Holt die Data aus response und wandelt sie in einen TSApplicationProperty Array um, welches danach zurueckliefert
         * @param response
         * @returns {any}
         */
        public static parseApplicationProperties(response: angular.IHttpPromiseCallbackArg<any>) : ebeguWeb.API.TSApplicationProperty[] {
            if (response != null) {
                var appProperties: ebeguWeb.API.TSApplicationProperty[] = new Array<ebeguWeb.API.TSApplicationProperty>(response.data.length);
                if (Array.isArray(response.data)) {
                    for (var i = 0; i < response.data.length; i++) {
                        appProperties[i] = EbeguRestUtil.parseApplicationProperty(new ebeguWeb.API.TSApplicationProperty('', ''), response.data[i]);
                    }
                }
                else {
                    appProperties[0] = EbeguRestUtil.parseApplicationProperty(new ebeguWeb.API.TSApplicationProperty('', ''), response.data);
                }
                return appProperties;
            }
            return [];
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
