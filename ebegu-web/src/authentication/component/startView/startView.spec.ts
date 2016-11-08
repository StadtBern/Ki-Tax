import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguAuthentication} from '../../authentication.module';
import {TSAuthEvent} from '../../../models/enums/TSAuthEvent';
import {StartViewController} from './startView';
import {EbeguWebCore} from '../../../core/core.module';
import IRootScopeService = angular.IRootScopeService;
import IScope = angular.IScope;

describe('startView', function () {

    //evtl ist modulaufteilung hier nicht ganz sauber, wir brauchen sowohl core als auch auth modul
    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguAuthentication.name));

    let $rootScope: IRootScopeService;
    let scope: IScope;
    let $componentController: any;
    let startViewController: StartViewController;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        startViewController = new StartViewController($injector.get('$state'), $rootScope, $injector.get('AuthServiceRS'));
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        expect(startViewController).toBeDefined();
    });

    it('should  broadcast "AUTH_EVENTS.notAuthenticated" if no principal is available', function () {
        let broadcast =  spyOn($rootScope, '$broadcast');
        startViewController.$onInit();
        expect(broadcast).toHaveBeenCalledWith(TSAuthEvent.NOT_AUTHENTICATED, 'not logged in on startpage');
    });
});
