import '../../bootstrap.ts';
import 'angular-mocks';
import IInjectorService = angular.auto.IInjectorService;
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {DVRoleElementController} from './DVRoleElementController';
import {EbeguAuthentication} from '../../authentication/authentication.module';
import {EbeguWebCore} from '../core.module';
import {TSRole} from '../../models/enums/TSRole';

describe('DVElementController', function () {

    let authServiceRS: AuthServiceRS;
    let cvElementController: DVRoleElementController;

    beforeEach(angular.mock.module(EbeguAuthentication.name));
    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: IInjectorService) {
        authServiceRS = <AuthServiceRS>$injector.get('AuthServiceRS');
        spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.GESUCHSTELLER);
        cvElementController = new DVRoleElementController(authServiceRS);

    }));

    describe('checkRoles', function() {
        it('should return true for the same role as the user and no expression', function () {
            cvElementController.dvAllowedRoles = [TSRole.GESUCHSTELLER];
            cvElementController.dvExpression = undefined;
            expect(cvElementController.checkValidity()).toBe(true);
        });
        it('should return true for the same role as the user and true expression', function () {
            cvElementController.dvAllowedRoles = [TSRole.GESUCHSTELLER];
            cvElementController.dvExpression = true;
            expect(cvElementController.checkValidity()).toBe(true);
        });
        it('should return false for the same role as the user and false expression', function () {
            cvElementController.dvAllowedRoles = [TSRole.GESUCHSTELLER];
            cvElementController.dvExpression = false;
            expect(cvElementController.checkValidity()).toBe(false);
        });
        it('should return false for a different role as the user and no expression', function () {
            cvElementController.dvAllowedRoles = [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.ADMIN];
            cvElementController.dvExpression = undefined;
            expect(cvElementController.checkValidity()).toBe(false);
        });
        it('should return false for a different role as the user and true expression', function () {
            cvElementController.dvAllowedRoles = [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.ADMIN];
            cvElementController.dvExpression = undefined;
            expect(cvElementController.checkValidity()).toBe(false);
        });
        it('should return false for a different role as the user and false expression', function () {
            cvElementController.dvAllowedRoles = [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.ADMIN];
            cvElementController.dvExpression = undefined;
            expect(cvElementController.checkValidity()).toBe(false);
        });
    });
});
