/// <reference path="../../typings/browser.d.ts" />
/// <reference path="./DateUtil.ts" />
module ebeguWeb.utils {
    'use strict';
    import TSAdressetyp = ebeguWeb.API.TSAdressetyp;
    import TSPerson = ebeguWeb.API.TSPerson;
    import TSAdresse = ebeguWeb.API.TSAdresse;

    export class EbeguRestUtil {

        /**
         * Wndelt Data in einen TSApplicationProperty Array um, welches danach zurueckgeliefert wird
         * @param data
         * @returns {any}
         */
        public static parseApplicationProperties(data:any):ebeguWeb.API.TSApplicationProperty[] {
            var appProperties:ebeguWeb.API.TSApplicationProperty[] = new Array<ebeguWeb.API.TSApplicationProperty>();
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
        public static parseApplicationProperty(parsedAppProperty:ebeguWeb.API.TSApplicationProperty, receivedAppProperty:any):ebeguWeb.API.TSApplicationProperty {
            parsedAppProperty.name = receivedAppProperty.name;
            parsedAppProperty.value = receivedAppProperty.value;
            this.parseAbstractEntity(parsedAppProperty, receivedAppProperty);
            return parsedAppProperty;
        }

        private static parseAbstractEntity(parsedAppProperty:ebeguWeb.API.TSAbstractEntity, receivedAppProperty:any) {
            parsedAppProperty.timestampErstellt = DateUtil.localDateTimeToMoment(receivedAppProperty.timestampErstellt);
            parsedAppProperty.timestampMutiert = DateUtil.localDateTimeToMoment(receivedAppProperty.timestampMutiert);
            parsedAppProperty.id = receivedAppProperty.id;
        }

        private static abstractEntityToRestObject(restObject:any, typescriptObject:ebeguWeb.API.TSAbstractEntity) {
            restObject.id = typescriptObject.id;
            restObject.timestampErstellt = DateUtil.momentToLocalDateTime(typescriptObject.timestampErstellt);
            restObject.timestampMutiert = DateUtil.momentToLocalDateTime(typescriptObject.timestampMutiert);
        }

        public static adresseToRestObject(restAdresse:any, adresse:ebeguWeb.API.TSAdresse):ebeguWeb.API.TSAdresse {
            if (adresse) {
                EbeguRestUtil.abstractEntityToRestObject(restAdresse, adresse);
                restAdresse.strasse = adresse.strasse;
                restAdresse.hausnummer = adresse.hausnummer;
                restAdresse.zusatzzeile = adresse.zusatzzeile;
                restAdresse.plz = adresse.plz;
                restAdresse.ort = adresse.ort;
                restAdresse.land = adresse.land;
                restAdresse.gemeinde = adresse.gemeinde;
                restAdresse.gueltigAb =  DateUtil.momentToLocalDate(adresse.gueltigAb);
                restAdresse.gueltigBis = DateUtil.momentToLocalDate(adresse.gueltigBis);
                restAdresse.adresseTyp = TSAdressetyp[adresse.adresseTyp];
                return restAdresse;
            }
            return undefined;

        }

        public static parseAdresse(adresseTS:TSAdresse, receivedAdresse:any):TSAdresse {
            if (receivedAdresse) {
                EbeguRestUtil.abstractEntityToRestObject(adresseTS, receivedAdresse);
                adresseTS.strasse = receivedAdresse.strasse;
                adresseTS.hausnummer = receivedAdresse.hausnummer;
                adresseTS.zusatzzeile = receivedAdresse.zusatzzeile;
                adresseTS.plz = receivedAdresse.plz;
                adresseTS.ort = receivedAdresse.ort;
                adresseTS.land = receivedAdresse.land;
                adresseTS.gemeinde = receivedAdresse.gemeinde;
                adresseTS.gueltigAb = DateUtil.localDateToMoment(receivedAdresse.gueltigAb);
                adresseTS.gueltigBis = DateUtil.localDateToMoment(receivedAdresse.gueltigBis);
                adresseTS.adresseTyp = TSAdressetyp[<string>receivedAdresse.adresseTyp];
                return adresseTS;
            }
            return undefined;

        }

        public static personToRestObject(restPerson:any, person:ebeguWeb.API.TSPerson):any {

            if (person) {
                EbeguRestUtil.abstractEntityToRestObject(restPerson, person);

                restPerson.vorname = person.vorname;
                restPerson.nachname = person.nachname;
                restPerson.geburtsdatum = DateUtil.momentToLocalDate(person.geburtsdatum);
                restPerson.mail = person.mail;
                restPerson.mobile = person.mobile;
                restPerson.telefon = person.telefon;
                restPerson.telefonAusland = person.telefonAusland;
                restPerson.umzug = person.umzug;
                restPerson.geschlecht = person.geschlecht;
                restPerson.wohnAdresse = EbeguRestUtil.adresseToRestObject({}, person.adresse);
                restPerson.alternativeAdresse = EbeguRestUtil.adresseToRestObject({}, person.korrespondenzAdresse);
                restPerson.umzugAdresse = EbeguRestUtil.adresseToRestObject({}, person.umzugAdresse);
                return restPerson
            }
            return undefined;
        }


        public static parsePerson(personTS:TSPerson, personFromServer:any):TSPerson {
            if (personFromServer) {

                EbeguRestUtil.parseAbstractEntity(personTS, personFromServer);
                personTS.vorname = personFromServer.vorname;
                personTS.nachname = personFromServer.nachname;
                personTS.geburtsdatum = DateUtil.localDateTimeToMoment(personFromServer.geburtsdatum);
                personTS.mail = personFromServer.mail;
                personTS.mobile = personFromServer.mobile;
                personTS.telefon = personFromServer.telefon;
                personTS.telefonAusland = personFromServer.telefonAusland;
                personTS.umzug = personFromServer.umzug;
                personTS.geschlecht = personFromServer.geschlecht;
                personTS.adresse = EbeguRestUtil.parseAdresse(new TSAdresse(), personFromServer.wohnAdresse);
                personTS.korrespondenzAdresse = EbeguRestUtil.parseAdresse(new TSAdresse(), personFromServer.alternativeAdresse);
                personTS.umzugAdresse = EbeguRestUtil.parseAdresse(new TSAdresse(), personFromServer.umzugAdresse);
                return personTS
            }
            return undefined;

        }

    }
}
