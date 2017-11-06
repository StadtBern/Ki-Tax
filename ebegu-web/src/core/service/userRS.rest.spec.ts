/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import TSUser from '../../models/TSUser';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import UserRS from './userRS.rest';

describe('userRS', function () {

    let userRS: UserRS;
    let $httpBackend: angular.IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockUser: TSUser;
    let mockUserRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        userRS = $injector.get('UserRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockUser = new TSUser('Pedro', 'Jimenez');
        mockUserRest = ebeguRestUtil.userToRestObject({}, mockUser);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(userRS.getServiceName()).toBe('UserRS');
        });
        it('should include a getAllTraegerschaften() function', function () {
            expect(userRS.getAllUsers).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('getAllUsers', () => {
            it('should return all users', () => {
                let usersRestArray: Array<any> = [mockUser, mockUser];
                $httpBackend.expectGET(userRS.serviceURL).respond(usersRestArray);

                let returnedUsers: Array<TSUser>;
                userRS.getAllUsers().then((result) => {
                    returnedUsers = result;
                });
                $httpBackend.flush();
                expect(returnedUsers).toBeDefined();
                expect(returnedUsers.length).toEqual(2);
                checkFieldValues(returnedUsers[0], usersRestArray[0]);
                checkFieldValues(returnedUsers[1], usersRestArray[1]);
            });
        });
    });

    function checkFieldValues(user1: TSUser, user2: TSUser) {
        expect(user1).toBeDefined();
        expect(user1.nachname).toEqual(user2.nachname);
        expect(user1.vorname).toEqual(user2.vorname);
    }
});
