/// <reference path="../../typings/browser.d.ts" />
/// <reference path="./DateUtil.ts" />
module ebeguWeb.utils {
    'use strict';
    import TSAdressetyp = ebeguWeb.API.TSAdressetyp;

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

        public static adresseToRestObject(restAdresse: any, adresse: ebeguWeb.API.TSAdresse): ebeguWeb.API.TSAdresse {
            if(adresse) {

                restAdresse.id = adresse.id;
                restAdresse.strasse = adresse.strasse;
                restAdresse.hausnummer = adresse.hausnummer;
                restAdresse.zusatzzeile = adresse.zusatzzeile;
                restAdresse.plz = adresse.plz;
                restAdresse.ort = adresse.ort;
                restAdresse.land = adresse.land;
                restAdresse.gemeinde = adresse.gemeinde;
                restAdresse.gueltigAb = DateUtil.momentToLocalDate(adresse.gueltigAb);
                restAdresse.gueltigAb = DateUtil.momentToLocalDate(adresse.gueltigBis);
                restAdresse.timestampErstellt = DateUtil.momentToLocalDateTime(adresse.timestampErstellt);
                restAdresse.timestampMutiert = DateUtil.momentToLocalDateTime(adresse.timestampMutiert);
                //todo homa wie kann man das hier transformieren ohne das ein compilefehler entsteht
                // restAdresse.adresseTyp = adresse.adresseTyp ? TSAdressetyp[adresse.adresseTyp] : undefined;
                console.log("adresseToRest");
                console.log(adresse);
            }

            return restAdresse;
        }

    }

}
