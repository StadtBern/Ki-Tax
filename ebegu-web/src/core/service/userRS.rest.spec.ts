import {EbeguWebCore} from '../core.module';
import UserRS from './userRS.rest';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSUser from '../../models/TSUser';

describe('userRS', function () {

    let userRS: UserRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockUser: TSUser;
    let mockUserRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
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
