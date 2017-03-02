import TSGesuchsperiode from '../models/TSGesuchsperiode';
import {TSDateRange} from '../models/types/TSDateRange';
import {EbeguWebCore} from '../core/core.module';
import EbeguUtil from './EbeguUtil';
import * as moment from 'moment';
import TSFall from '../models/TSFall';
import TestDataUtil from './TestDataUtil';

describe('EbeguUtil', function () {

    let ebeguUtil: EbeguUtil;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    // Das wird nur fuer tests gebraucht in denen etwas uebersetzt wird. Leider muss man dieses erstellen
    // bevor man den Injector erstellt hat. Deshalb muss es fuer alle Tests definiert werden
    beforeEach(angular.mock.module(function($provide: any) {
        let mockTranslateFilter = function(value: any) {
            if (value === 'FIRST') {
                return 'Erster';
            }
            if (value === 'SECOND') {
                return 'Zweiter';
            }
            return value;
        };
        $provide.value('translateFilter', mockTranslateFilter);
    }));

    beforeEach(angular.mock.inject(function ($injector: any) {
        ebeguUtil = $injector.get('EbeguUtil');
    }));

    describe('translateStringList', () => {
        it('should translate the given list of words', () => {
            let list: Array<string> = ['FIRST', 'SECOND'];
            let returnedList: Array<any> = ebeguUtil.translateStringList(list);
            expect(returnedList.length).toEqual(2);
            expect(returnedList[0].key).toEqual('FIRST');
            expect(returnedList[0].value).toEqual('Erster');
            expect(returnedList[1].key).toEqual('SECOND');
            expect(returnedList[1].value).toEqual('Zweiter');
        });
    });
    describe('addZerosToNumber', () => {
        it('returns a string with 6 chars starting with 0s and ending with the given number', () => {
            expect(ebeguUtil.addZerosToNumber(0, 2)).toEqual('00');
            expect(ebeguUtil.addZerosToNumber(1, 2)).toEqual('01');
            expect(ebeguUtil.addZerosToNumber(12, 2)).toEqual('12');
        });
        it('returns undefined if the number is undefined', () => {
            expect(ebeguUtil.addZerosToNumber(undefined, 2)).toBeUndefined();
            expect(ebeguUtil.addZerosToNumber(null, 2)).toBeUndefined();
        });
        it('returns the given number as string if its length is greather than 6', () => {
            expect(ebeguUtil.addZerosToNumber(1234567, 6)).toEqual('1234567');
        });
    });
    describe('calculateBetreuungsId', () => {
        it ('it returns empty string for undefined objects', () => {
            expect(ebeguUtil.calculateBetreuungsId(undefined, undefined, 0, 0)).toBe('');
        });
        it ('it returns empty string for undefined kindContainer', () => {
            let fall: TSFall = new TSFall();
            expect(ebeguUtil.calculateBetreuungsId(undefined, fall, 0, 0)).toBe('');
        });
        it ('it returns empty string for undefined betreuung', () => {
            let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode();
            expect(ebeguUtil.calculateBetreuungsId(gesuchsperiode, undefined, 0, 0)).toBe('');
        });
        it ('it returns the right ID: YY(gesuchsperiodeBegin).fallNummer.Kind.Betreuung', () => {
            let fall: TSFall = new TSFall(254);
            let gesuchsperiode = TestDataUtil.createGesuchsperiode20162017();
            expect(ebeguUtil.calculateBetreuungsId(gesuchsperiode, fall, 1, 1)).toBe('16.000254.1.1');
        });
    });
    describe('getFirstDayGesuchsperiodeAsString', () => {
        it ('it returns empty string for undefined Gesuchsperiode', () => {
            expect(ebeguUtil.getFirstDayGesuchsperiodeAsString(undefined)).toBe('');
        });
        it ('it returns empty string for undefined daterange in the Gesuchsperiode', () => {
            let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode(true, undefined);
            expect(ebeguUtil.getFirstDayGesuchsperiodeAsString(undefined)).toBe('');
        });
        it ('it returns empty string for undefined gueltigAb', () => {
            let daterange: TSDateRange = new TSDateRange(undefined, moment('31.07.2017', 'DD.MM.YYYY'));
            let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode(true, daterange);
            expect(ebeguUtil.getFirstDayGesuchsperiodeAsString(gesuchsperiode)).toBe('');
        });
        it ('it returns 01.08.2016', () => {
            let daterange: TSDateRange = new TSDateRange(moment('01.08.2016', 'DD.MM.YYYY'), moment('31.07.2017', 'DD.MM.YYYY'));
            let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode(true, daterange);
            expect(ebeguUtil.getFirstDayGesuchsperiodeAsString(gesuchsperiode)).toBe('01.08.2016');
        });
    });
    describe('generateRandomName', () => {
        it ('it returns a string with 5 characters', () => {
            expect(EbeguUtil.generateRandomName(5).length).toBe(5);
        });
        it ('it returns a string with 0 characters', () => {
            expect(EbeguUtil.generateRandomName(0).length).toBe(0);
        });
        it ('it returns a string with 52 characters', () => {
            expect(EbeguUtil.generateRandomName(52).length).toBe(52);
        });
        it ('it returns a string with 0 characters', () => {
            expect(EbeguUtil.generateRandomName(-1).length).toBe(0);
        });
    });
});
