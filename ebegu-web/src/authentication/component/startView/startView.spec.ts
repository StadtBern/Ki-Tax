import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguAuthentication} from '../../authentication.module';
import {TSAuthEvent} from '../../../models/enums/TSAuthEvent';
import {StartViewController} from './startView';
import {EbeguWebCore} from '../../../core/core.module';
import AuthServiceRS from '../../service/AuthServiceRS.rest';
import TSUser from '../../../models/TSUser';
import {TSRole} from '../../../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
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
    let authService: AuthServiceRS;
    let mockPrincipal: TSUser;
    let state: IStateService;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        authService = $injector.get('AuthServiceRS');
        state = $injector.get('$state');
        startViewController = new StartViewController(state, $rootScope, authService);

    }));
    beforeEach(function () {
        mockPrincipal = new TSUser();
        mockPrincipal.nachname = 'mockprincipal';
        mockPrincipal.vorname = 'tester';
        mockPrincipal.role = TSRole.GESUCHSTELLER;
    });

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        expect(startViewController).toBeDefined();
    });

    it('should  broadcast "AUTH_EVENTS.notAuthenticated" if no principal is available', function () {
        let broadcast = spyOn($rootScope, '$broadcast');
        startViewController.$onInit();
        expect(broadcast).toHaveBeenCalledWith(TSAuthEvent.NOT_AUTHENTICATED, 'not logged in on startpage');
    });

    describe('should  redirect based on role of principal', function () {
        it('should go to gesuchstellerDashboard if role is gesuchsteller', function () {
            spyOn(authService, 'getPrincipal').and.returnValue(mockPrincipal);
            spyOn(state, 'go');
            startViewController.$onInit();
            expect(state.go).toHaveBeenCalledWith('gesuchstellerDashboard');
        });
        it('should go to pendenzen if role is sachbearbeiter ja', function () {
            mockPrincipal.role = TSRole.SACHBEARBEITER_JA;
               spyOn(authService, 'getPrincipal').and.returnValue(mockPrincipal);
               spyOn(state, 'go');
               startViewController.$onInit();
               expect(state.go).toHaveBeenCalledWith('pendenzen');
           });
    });
});
