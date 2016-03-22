/// <reference path="../../typings/browser.d.ts" />
describe('dateUtil', function () {

    beforeEach(angular.mock.module('ebeguWeb.admin'));


    describe('localDateToMoment()', function () {
        it('should return null for invalid input', function () {
            expect(ebeguWeb.utils.DateUtil.localDateTimeToMoment(undefined)).toEqual(null);
            expect(ebeguWeb.utils.DateUtil.localDateTimeToMoment(null)).toEqual(null);
            expect(ebeguWeb.utils.DateUtil.localDateTimeToMoment('')).toEqual(null);
            expect(ebeguWeb.utils.DateUtil.localDateTimeToMoment('invalid format')).toEqual(null);
            expect(ebeguWeb.utils.DateUtil.localDateTimeToMoment('1995-12-25')).toEqual(null);
        });

        it('should return a valid moment', function () {
            var actual = ebeguWeb.utils.DateUtil.localDateTimeToMoment('1995-12-25T16:06:34.564');
            var expected = moment('1995-12-25T16:06:34.564', 'YYYY-MM-DDTHH:mm:ss.SSS', true);
            expect(expected.isSame(actual)).toBeTruthy();
        });
    });
});
