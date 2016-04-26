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
            var actual = DateUtil.localDateTimeToMoment('1995-12-25T16:06:34.564');
            var expected = moment('1995-12-25T16:06:34.564', 'YYYY-MM-DDTHH:mm:ss.SSS', true);
            expect(expected.isSame(actual)).toBeTruthy();
        });
    });
});
