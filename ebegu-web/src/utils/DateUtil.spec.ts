import DateUtil from './DateUtil';
import * as moment from 'moment';
import Moment = moment.Moment;

describe('dateUtil', function () {

   // beforeEach(angular.mock.module(EbeguWebCore));


    describe('localDateToMoment()', function () {
        it('should return null for invalid input', function () {
            expect(DateUtil.localDateTimeToMoment(undefined)).toEqual(undefined);
            expect(DateUtil.localDateTimeToMoment(null)).toEqual(undefined);
            expect(DateUtil.localDateTimeToMoment('')).toEqual(undefined);
            expect(DateUtil.localDateTimeToMoment('invalid format')).toEqual(undefined);
            expect(DateUtil.localDateTimeToMoment('1995-12-25')).toEqual(undefined);
        });

        it('should return a valid moment', function () {
            let actual = DateUtil.localDateTimeToMoment('1995-12-25T16:06:34.564');
            let expected = moment('1995-12-25T16:06:34.564', 'YYYY-MM-DDTHH:mm:ss.SSS', true);
            expect(expected.isSame(actual)).toBeTruthy();
        });
    });
    describe('compareDateTime()', function () {
        it('DATETIME: a date should be before b date', function () {
            let a = DateUtil.localDateTimeToMoment('1995-12-24T14:06:34.564');
            let b = DateUtil.localDateTimeToMoment('1995-12-24T16:06:34.564');
            expect(DateUtil.compareDateTime(a, b)).toBe(-1);
        });
        it('DATETIME: a date should be the same as b date', function () {
            let a = DateUtil.localDateTimeToMoment('1995-12-24T16:06:34.564');
            let b = DateUtil.localDateTimeToMoment('1995-12-24T16:06:34.564');
            expect(DateUtil.compareDateTime(a, b)).toBe(0);
        });
        it('DATETIME: a date should be after b date', function () {
            let a = DateUtil.localDateTimeToMoment('1995-12-24T18:06:34.564');
            let b = DateUtil.localDateTimeToMoment('1995-12-24T16:06:34.564');
            expect(DateUtil.compareDateTime(a, b)).toBe(1);
        });

        it('DATE: a date should be before b date', function () {
            let a = DateUtil.localDateToMoment('1995-12-23');
            let b = DateUtil.localDateToMoment('1995-12-24');
            expect(DateUtil.compareDateTime(a, b)).toBe(-1);
        });
        it('DATE: a date should be the same as b date', function () {
            let a = DateUtil.localDateToMoment('1995-12-24');
            let b = DateUtil.localDateToMoment('1995-12-24');
            expect(DateUtil.compareDateTime(a, b)).toBe(0);
        });
        it('DATE: a date should be after b date', function () {
            let a = DateUtil.localDateToMoment('1995-12-25');
            let b = DateUtil.localDateToMoment('1995-12-24');
            expect(DateUtil.compareDateTime(a, b)).toBe(1);
        });
    });
});
