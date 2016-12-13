import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';

describe('einkommensverschlechterungSteuernView', function () {

    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    var component : any;
    var scope : angular.IScope;
    var $componentController : any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false, TSEingangsart.PAPIER);
        gesuchModelManager.getGesuch().familiensituation = new TSFamiliensituation();
        gesuchModelManager.getGesuch().gesuchsteller1 = new TSGesuchstellerContainer(new TSGesuchsteller());
    });

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        var bindings: {};
        component = $componentController('einkommensverschlechterungSteuernView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
