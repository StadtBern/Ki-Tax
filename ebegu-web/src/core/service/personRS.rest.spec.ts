import {PersonRS} from "./personRS.rest";

describe('PersonRS', function () {

    let personRS: PersonRS;

    beforeEach(angular.mock.module('ebeguWeb.core'));

    beforeEach(angular.mock.inject(function (_personRS_: PersonRS) {
        personRS = _personRS_;
    }));

    describe('Public API', function () {

    });

    describe('API Usage', function () {

    });
});
