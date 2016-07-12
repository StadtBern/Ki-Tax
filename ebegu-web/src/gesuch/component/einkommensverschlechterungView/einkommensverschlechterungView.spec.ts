import '../../../bootstrap.ts';
import 'angular-mocks';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
import {EbeguWebCore} from '../../../core/core.module';
import DateUtil from '../../../utils/DateUtil';
import {EinkommensverschlechterungViewController} from './einkommensverschlechterungView';
import {IEinkommensverschlechterungStateParams} from '../../gesuch.route';
import ErrorService from '../../../core/errors/service/ErrorService';
import BerechnungsManager from '../../service/berechnungsManager';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import IQService = angular.IQService;
import IScope = angular.IScope;

describe('einkommensverschlechterungView', function () {


    let gesuchModelManager: GesuchModelManager;
    let stateParams: IEinkommensverschlechterungStateParams;
    let $state: IStateService;
    let berechnungsmanager: BerechnungsManager;
    let $q: IQService;
    let betreuung: TSBetreuung;
    let $rootScope: IScope;
    let $httpBackend: IHttpBackendService;
    let $errorService: ErrorService;
    let einkommensverschlechterungViewController: EinkommensverschlechterungViewController;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        berechnungsmanager = $injector.get('BerechnungsManager');
        $httpBackend = $injector.get('$httpBackend');
        $errorService = $injector.get('ErrorService');
        $q = $injector.get('$q');
        betreuung = new TSBetreuung();
        betreuung.timestampErstellt = DateUtil.today();
        spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(betreuung);
        $rootScope = $injector.get('$rootScope');
        stateParams = new IEinkommensverschlechterungStateParams;
        stateParams.basisjahrPlus = '1';
        stateParams.gesuchstellerNumber = '1';
        einkommensverschlechterungViewController = new EinkommensverschlechterungViewController(stateParams, $state, gesuchModelManager, berechnungsmanager, $injector.get('CONSTANTS'),
            $injector.get('ErrorService'));
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
    });

});
