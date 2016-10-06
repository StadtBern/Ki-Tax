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
            $state, $stateParams, $scope);
    }));

});
