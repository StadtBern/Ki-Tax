import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebCore} from '../../core.module';
import {DVShowElementController} from './dv-show-element';
import {EbeguAuthentication} from '../../../authentication/authentication.module';
import {TSRole} from '../../../models/enums/TSRole';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import IInjectorService = angular.auto.IInjectorService;

describe('DVShowElementController', function () {

    let authServiceRS: AuthServiceRS;
    let cvShowElementController: DVShowElementController;

    beforeEach(angular.mock.module(EbeguAuthentication.name));
    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: IInjectorService) {
        authServiceRS = <AuthServiceRS>$injector.get('AuthServiceRS');
        spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.GESUCHSTELLER);
        cvShowElementController = new DVShowElementController(authServiceRS);

    }));

    describe('checkRoles', function() {
        it('should return true for the same role as the user and no expression', function () {
            cvShowElementController.dvShowAllowedRoles = [TSRole.GESUCHSTELLER];
            cvShowElementController.dvShowExpression = undefined;
            expect(cvShowElementController.checkValidity()).toBe(true);
        });
        it('should return true for the same role as the user and true expression', function () {
            cvShowElementController.dvShowAllowedRoles = [TSRole.GESUCHSTELLER];
            cvShowElementController.dvShowExpression = true;
            expect(cvShowElementController.checkValidity()).toBe(true);
        });
        it('should return false for the same role as the user and false expression', function () {
            cvShowElementController.dvShowAllowedRoles = [TSRole.GESUCHSTELLER];
            cvShowElementController.dvShowExpression = false;
            expect(cvShowElementController.checkValidity()).toBe(false);
        });
        it('should return false for a different role as the user and no expression', function () {
            cvShowElementController.dvShowAllowedRoles = [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.ADMIN];
            cvShowElementController.dvShowExpression = undefined;
            expect(cvShowElementController.checkValidity()).toBe(false);
        });
        it('should return false for a different role as the user and true expression', function () {
            cvShowElementController.dvShowAllowedRoles = [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.ADMIN];
            cvShowElementController.dvShowExpression = undefined;
            expect(cvShowElementController.checkValidity()).toBe(false);
        });
        it('should return false for a different role as the user and false expression', function () {
            cvShowElementController.dvShowAllowedRoles = [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.ADMIN];
            cvShowElementController.dvShowExpression = undefined;
            expect(cvShowElementController.checkValidity()).toBe(false);
        });
    });
});
