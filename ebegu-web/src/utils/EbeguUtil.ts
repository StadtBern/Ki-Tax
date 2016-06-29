import TSGesuchsperiode from '../models/TSGesuchsperiode';
import {IFilterService} from 'angular';
import TSAbstractEntity from '../models/TSAbstractEntity';

/**
 * Klasse die allgemeine utils Methoden implementiert
 */
export default class EbeguUtil {

    static $inject = ['$filter'];
    /* @ngInject */
    constructor(private $filter: IFilterService) {
    }

    /**
     * Takes the given Gesuchsperiode and returns a string with the format "gueltigAb.year/gueltigBis.year"
     * @returns {any}
     */
    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        if (gesuchsperiode && gesuchsperiode.gueltigkeit) {
            return gesuchsperiode.gueltigkeit.gueltigAb.year() + '/'
                + gesuchsperiode.gueltigkeit.gueltigBis.year();
        }
        return undefined;
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
        var idToSearch = entityToSearch.id;
        for (var i = 0; i < listToSearchIn.length; i++) {
            if (listToSearchIn[i].id === idToSearch) {
                return i;
            }
        }
        return -1;

    }
}
