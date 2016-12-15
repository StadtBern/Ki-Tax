import '../../../bootstrap.ts';
import 'angular-mocks';
import GesuchModelManager from '../../service/gesuchModelManager';
import {EbeguWebGesuch} from '../../gesuch.module';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import IQService = angular.IQService;
import IScope = angular.IScope;
import TSGesuchsteller from '../../../models/TSGesuchsteller';

describe('einkommensverschlechterungView', function () {


    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    var component: any;
    var scope: angular.IScope;
    var $componentController: any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));


    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
        gesuchModelManager.initFamiliensituation();
        gesuchModelManager.getGesuch().gesuchsteller1 = new TSGesuchstellerContainer(new TSGesuchsteller());
        gesuchModelManager.getGesuch().gesuchsteller2 = new TSGesuchstellerContainer(new TSGesuchsteller());
        gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
        gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();

    });

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        var bindings: {};
        component = $componentController('einkommensverschlechterungView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });

});
