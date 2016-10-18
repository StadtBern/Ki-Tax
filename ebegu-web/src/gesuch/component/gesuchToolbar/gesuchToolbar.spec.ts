import {EbeguWebCore} from '../../../core/core.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {GesuchToolbarController} from './gesuchToolbar';
import UserRS from '../../../core/service/userRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import EbeguUtil from '../../../utils/EbeguUtil';
import GesuchRS from '../../service/gesuchRS.rest';
import BerechnungsManager from '../../service/berechnungsManager';
import {IStateService} from 'angular-ui-router';
import {IGesuchStateParams} from '../../gesuch.route';
import IScope = angular.IScope;

describe('betreuungView', function () {

    let gesuchModelManager: GesuchModelManager;
    let gesuchToolbarController: GesuchToolbarController;
    let userRS: UserRS;
    let authServiceRS: AuthServiceRS;
    let ebeguUtil: EbeguUtil;
    let CONSTANTS: any;
    let gesuchRS: GesuchRS;
    let berechnungsManager: BerechnungsManager;
    let $state: IStateService;
    let $stateParams: IGesuchStateParams;
    let $scope: IScope;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        userRS = $injector.get('UserRS');
        authServiceRS = $injector.get('AuthServiceRS');
        ebeguUtil = $injector.get('EbeguUtil');
        CONSTANTS = $injector.get('CONSTANTS');
        gesuchRS = $injector.get('GesuchRS');
        berechnungsManager = $injector.get('BerechnungsManager');
        $state = $injector.get('$state');
        ebeguUtil = $injector.get('EbeguUtil');
        $stateParams = $injector.get('$stateParams');
        $scope = null;
        gesuchToolbarController = new GesuchToolbarController(userRS, ebeguUtil,
            CONSTANTS, gesuchRS,
            $state, $stateParams, $scope, gesuchModelManager);
    }));

    // todo homa gapa fragen warum die nicht mehr klappen
    /* describe('getVerantwortlicherFullName', () => {
     it('returns empty string for empty verantwortlicher', () => {
     expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('');
     });
     it('returns the fullname of the verantwortlicher', () => {
     let verantwortlicher: TSUser = new TSUser('Emiliano', 'Camacho');
     spyOn(authServiceRS, 'getPrincipal').and.returnValue(verantwortlicher);
     gesuchModelManager.initGesuch(true);
     expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('Emiliano Camacho');
     });
     });
     describe('setVerantwortlicher()', () => {
     it('does nothing if the passed user is empty', () => {
     spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
     spyOn(gesuchModelManager, 'updateFall');
     gesuchToolbarController.setVerantwortlicher(undefined);
     expect(gesuchModelManager.setUserAsFallVerantwortlicher).not.toHaveBeenCalled();
     expect(gesuchModelManager.updateFall).not.toHaveBeenCalled();
     });
     it('sets the user as the verantwortlicher of the current fall', () => {
     spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
     spyOn(gesuchModelManager, 'updateFall');
     let user: TSUser = new TSUser('Emiliano', 'Camacho');
     gesuchToolbarController.setVerantwortlicher(user);
     expect(gesuchModelManager.setUserAsFallVerantwortlicher).toHaveBeenCalledWith(user);
     expect(gesuchModelManager.updateFall).toHaveBeenCalled();
     });
     });*/

});
