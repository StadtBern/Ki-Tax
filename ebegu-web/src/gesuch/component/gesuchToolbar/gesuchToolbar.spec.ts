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
import TSUser from '../../../models/TSUser';
import TSGesuch from '../../../models/TSGesuch';
import TSFall from '../../../models/TSFall';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import IScope = angular.IScope;
import FallRS from '../../service/fallRS.rest';

describe('gesuchToolbar', function () {

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
    let $rootScope: IScope;
    let user: TSUser;
    let $mdSidenav: ng.material.ISidenavService;
    let gesuchsperiodeRS: GesuchsperiodeRS;
    let fallRS: FallRS;

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
        $rootScope = $injector.get('$rootScope');
        $mdSidenav = $injector.get('$mdSidenav');
        $scope = $rootScope.$new();
        user = new TSUser('Emiliano', 'Camacho');
        $stateParams.gesuchId = '123456789';
        gesuchsperiodeRS = $injector.get('GesuchsperiodeRS');
        fallRS = $injector.get('FallRS');

        gesuchToolbarController = new GesuchToolbarController(userRS, ebeguUtil,
            CONSTANTS, gesuchRS, $state, $stateParams, $scope, gesuchModelManager,
            authServiceRS, $mdSidenav, undefined, gesuchsperiodeRS, fallRS);
    }));

    describe('getVerantwortlicherFullName', () => {
        it('returns empty string for empty verantwortlicher', () => {
            expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('');
        });

        it('returns the fullname of the verantwortlicher', () => {
            let verantwortlicher: TSUser = new TSUser('Emiliano', 'Camacho');
            spyOn(authServiceRS, 'getPrincipal').and.returnValue(verantwortlicher);
            spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
            gesuchModelManager.initGesuch(true, TSEingangsart.PAPIER);
            expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('Emiliano Camacho');
        });
    });
    describe('setVerantwortlicher()', () => {
        it('does nothing if the passed user is empty, verantwortlicher remains as it was before', () => {
            createGesuch();
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            spyOn(gesuchModelManager, 'updateFall');

            gesuchToolbarController.setVerantwortlicher(undefined);
            expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(user);
        });
        it('sets the user as the verantwortlicher of the current fall', () => {
            createGesuch();
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            spyOn(gesuchModelManager, 'updateFall');

            let newUser: TSUser = new TSUser('Adolfo', 'Contreras');
            gesuchToolbarController.setVerantwortlicher(newUser);
            expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(newUser);
        });
    });

    function createGesuch() {
        let gesuch: TSGesuch = new TSGesuch();
        let fall: TSFall = new TSFall();
        fall.verantwortlicher = user;
        gesuch.fall = fall;
        gesuchModelManager.setGesuch(gesuch);
    }

});
