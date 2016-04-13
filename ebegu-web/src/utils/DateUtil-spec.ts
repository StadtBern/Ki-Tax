import DateUtil from './DateUtil';

describe('dateUtil', function () {

    beforeEach(angular.mock.module('ebeguWeb.admin'));


    describe('localDateToMoment()', function () {
        it('should return null for invalid input', function () {
            expect(DateUtil.localDateTimeToMoment(undefined)).toEqual(null);
            expect(DateUtil.localDateTimeToMoment(null)).toEqual(null);
            expect(DateUtil.localDateTimeToMoment('')).toEqual(null);
            expect(DateUtil.localDateTimeToMoment('invalid format')).toEqual(null);
            expect(DateUtil.localDateTimeToMoment('1995-12-25')).toEqual(null);
        });

        it('should return a valid moment', function () {
            var actual = DateUtil.localDateTimeToMoment('1995-12-25T16:06:34.564');
            var expected = moment('1995-12-25T16:06:34.564', 'YYYY-MM-DDTHH:mm:ss.SSS', true);
            expect(expected.isSame(actual)).toBeTruthy();
        });
    });
});
