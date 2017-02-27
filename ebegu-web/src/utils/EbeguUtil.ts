import TSGesuchsperiode from '../models/TSGesuchsperiode';
import {IFilterService} from 'angular';
import TSAbstractEntity from '../models/TSAbstractEntity';
import TSFall from '../models/TSFall';
import DateUtil from './DateUtil';
import {TSAntragTyp} from '../models/enums/TSAntragTyp';
import TSBetreuungsnummerParts from '../models/dto/TSBetreuungsnummerParts';
import ITranslateService = angular.translate.ITranslateService;
import Moment = moment.Moment;

/**
 * Klasse die allgemeine utils Methoden implementiert
 */
export default class EbeguUtil {

    static $inject = ['$filter', 'CONSTANTS', '$translate'];
    /* @ngInject */
    constructor(private $filter: IFilterService, private CONSTANTS: any, private $translate: ITranslateService) {
    }

    /**
     * Returns the first day of the given Period in the format DD.MM.YYYY
     * @param gesuchsperiode
     * @returns {string}
     */
    public getFirstDayGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        if (gesuchsperiode && gesuchsperiode.gueltigkeit && gesuchsperiode.gueltigkeit.gueltigAb) {
            return DateUtil.momentToLocalDateFormat(gesuchsperiode.gueltigkeit.gueltigAb, 'DD.MM.YYYY');
        }
        return '';
    }

    public getAntragTextDateAsString(tsAntragTyp: TSAntragTyp, eingangsdatum: Moment, laufnummer: number): string {
        if (tsAntragTyp) {
            if (tsAntragTyp === TSAntragTyp.MUTATION && eingangsdatum) {
                return this.$translate.instant('TOOLBAR_' + TSAntragTyp[tsAntragTyp], {
                    nummer: laufnummer,
                    date: eingangsdatum.format('DD.MM.YYYY')
                });
            }
            return this.$translate.instant('TOOLBAR_' + TSAntragTyp[tsAntragTyp] + '_NO_DATE');
        }
        return '';
    }

    /**
     * Takes the given Gesuchsperiode and returns a string with the format "gueltigAb.year/gueltigBis.year"
     * @returns {any}
     */
    public getBasisJahrPlusAsString(gesuchsperiode: TSGesuchsperiode, plusJahr: number): string {
        if (gesuchsperiode && gesuchsperiode.gueltigkeit) {
            return String(gesuchsperiode.gueltigkeit.gueltigAb.year() + plusJahr);
        }
        return undefined;
    }

    /**
     * Translates the given string using the angular-translate filter
     * @param toTranslate word to translate
     * @returns {any} translated word
     */
    public translateString(toTranslate: string): string {
        return this.$filter('translate')(toTranslate).toString();
    }

    /**
     * Translates the given list using the angular translate filter
     * @param translationList list of words that will be translated
     * @returns {any} A List of Objects with key and value, where value is the translated word.
     */
    public translateStringList(translationList: Array<any>): Array<any> {
        let listResult: Array<any> = [];
        translationList.forEach((item) => {
            listResult.push({key: item, value: this.translateString(item)});
        });
        return listResult;
    }

    /**
     * Die Methode fuegt 0s (links) hinzu bis die gegebene Nummer, die gegebene Laenge hat und dann gibt die nummer als string zurueck
     * @param number
     * @param length
     * @returns {any}
     */
    public addZerosToNumber(number: number, length: number): string {
        if (number != null) {
            let fallnummerString = '' + number;
            while (fallnummerString.length < length) {
                fallnummerString = '0' + fallnummerString;
            }
            return fallnummerString;
        }
        return undefined;
    }


    public static getIndexOfElementwithID(entityToSearch: TSAbstractEntity, listToSearchIn: Array<any>): number {
        let idToSearch = entityToSearch.id;
        for (let i = 0; i < listToSearchIn.length; i++) {
            if (listToSearchIn[i].id === idToSearch) {
                return i;
            }
        }
        return -1;
    }

    public calculateBetreuungsId(gesuchsperiode: TSGesuchsperiode, fall: TSFall, kindContainerNumber: number, betreuungNumber: number): string {
        let betreuungsId: string = '';
        if (gesuchsperiode && fall) {
            betreuungsId =
                gesuchsperiode.gueltigkeit.gueltigAb.year().toString().substring(2)
                + '.' + this.addZerosToNumber(fall.fallNummer, this.CONSTANTS.FALLNUMMER_LENGTH)
                + '.' + kindContainerNumber
                + '.' + betreuungNumber;
        }
        return betreuungsId;
    }

    /**
     * hilfsmethode um die betreuungsnummer in ihre einzelteile zu zerlegen. gibt ein objekt zurueck welches die werte einzeln enthaelt
     * @param betreuungsnummer im format JJ.Fallnr.kindnr.betrnr
     */
    public splitBetreuungsnummer(betreuungsnummer: string): TSBetreuungsnummerParts {
        let parts: Array<string> = betreuungsnummer.split('.');
        if (!parts || parts.length !== 4) {
            return undefined;
        }
        return new TSBetreuungsnummerParts(parts[0], parts[1], parts[2], parts[3]);
    }

    public handleSmarttablesUpdateBug(aList: any[]) {
        // Ugly Fix:
        // Because of a bug in smarttables, the table will only be refreshed if the reverence or the first element
        // changes in table. To resolve this bug, we overwrite the first element by a copy of itself.
        aList[0] = angular.copy(aList[0]);
    }

    /**
     * Erzeugt einen random String mit einer Laenge von numberOfCharacters
     * @param numberOfCharacters
     * @returns {string}
     */
    public static generateRandomName(numberOfCharacters: number): string {
        let text = '';
        let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

        for (let i = 0; i < numberOfCharacters; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }
        return text;
    }

}
