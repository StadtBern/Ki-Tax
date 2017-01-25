import {EbeguWebMitteilungen} from '../../mitteilungen.module';
import MitteilungRS from '../../../core/service/mitteilungRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {MitteilungenViewController} from './mitteilungenView';
import {TSMitteilungTeilnehmerTyp} from '../../../models/enums/TSMitteilungTeilnehmerTyp';
import {TSMitteilungStatus} from '../../../models/enums/TSMitteilungStatus';
import TSUser from '../../../models/TSUser';
import {TSRole} from '../../../models/enums/TSRole';
import {IMitteilungenStateParams} from '../../mitteilungen.route';
import FallRS from '../../../gesuch/service/fallRS.rest';
import TSFall from '../../../models/TSFall';
import IScope = angular.IScope;
import IQService = angular.IQService;
import TSMitteilung from '../../../models/TSMitteilung';

describe('mitteilungenView', function () {

    let mitteilungRS: MitteilungRS;
    let authServiceRS: AuthServiceRS;
    let stateParams: IMitteilungenStateParams;
    let fallRS: FallRS;
    let fall: TSFall;
    let $rootScope: IScope;
    let $q: IQService;
    let controller: MitteilungenViewController;


    beforeEach(angular.mock.module(EbeguWebMitteilungen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        mitteilungRS = $injector.get('MitteilungRS');
        authServiceRS = $injector.get('AuthServiceRS');
        fallRS = $injector.get('FallRS');
        stateParams = $injector.get('$stateParams');
        $rootScope = $injector.get('$rootScope');
        $q = $injector.get('$q');
        stateParams.fallId = '123';
        fall = new TSFall();
        fall.id = stateParams.fallId;
    }));

    describe('loading initial data', function () {
        it('should create an empty TSMItteilung for GS', function () {
            let gesuchsteller: TSUser = new TSUser();
            gesuchsteller.role = TSRole.GESUCHSTELLER;
            spyOn(authServiceRS, 'isRole').and.returnValue(true);

            createMitteilungForUser(gesuchsteller);

            compareCommonAttributes(controller, gesuchsteller);
            expect(controller.getCurrentMitteilung().senderTyp).toBe(TSMitteilungTeilnehmerTyp.GESUCHSTELLER);
            expect(controller.getCurrentMitteilung().empfaengerTyp).toBe(TSMitteilungTeilnehmerTyp.JUGENDAMT);
        });
        it('should create an empty TSMItteilung for JA', function () {
            let sachbearbeiter_ja: TSUser = new TSUser();
            sachbearbeiter_ja.role = TSRole.SACHBEARBEITER_JA;
            spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);

            createMitteilungForUser(sachbearbeiter_ja);

            compareCommonAttributes(controller, sachbearbeiter_ja);
            expect(controller.getCurrentMitteilung().empfaengerTyp).toBe(TSMitteilungTeilnehmerTyp.GESUCHSTELLER);
            expect(controller.getCurrentMitteilung().senderTyp).toBe(TSMitteilungTeilnehmerTyp.JUGENDAMT);
        });
    });
    describe('sendMitteilung', function () {
        it('should send the current mitteilung and update currentMitteilung with the new content', function () {
            let gesuchsteller: TSUser = new TSUser();
            gesuchsteller.role = TSRole.GESUCHSTELLER;
            spyOn(authServiceRS, 'isRole').and.returnValue(true);

            createMitteilungForUser(gesuchsteller);

            // mock saved mitteilung
            let savedMitteilung: TSMitteilung = new TSMitteilung();
            savedMitteilung.id = '321';
            spyOn(mitteilungRS, 'createMitteilung').and.returnValue($q.when(savedMitteilung));

            controller.sendMitteilung();
            expect(controller.getCurrentMitteilung().mitteilungStatus).toBe(TSMitteilungStatus.NEU);

            $rootScope.$apply();

            expect(controller.getCurrentMitteilung()).toBeDefined();
            expect(controller.getCurrentMitteilung().id).toBe(savedMitteilung.id);
        });
    });



    function compareCommonAttributes(controller: MitteilungenViewController, sachbearbeiter_ja: TSUser): void {
        expect(controller.getCurrentMitteilung()).toBeDefined();
        expect(controller.getCurrentMitteilung().empfaenger).toBeUndefined();
        expect(controller.getCurrentMitteilung().fall).toBe(fall);
        expect(controller.getCurrentMitteilung().mitteilungStatus).toBe(TSMitteilungStatus.ENTWURF);
        expect(controller.getCurrentMitteilung().sender).toBe(sachbearbeiter_ja);
        expect(controller.getCurrentMitteilung().subject).toBeUndefined();
        expect(controller.getCurrentMitteilung().message).toBeUndefined();
    }

    function createMitteilungForUser(user: TSUser): void {
        spyOn(authServiceRS, 'getPrincipal').and.returnValue(user);
        spyOn(fallRS, 'findFall').and.returnValue($q.when(fall));
        spyOn(mitteilungRS, 'getMitteilungenForCurrentRolle').and.returnValue($q.when([{}]));

        controller = new MitteilungenViewController(stateParams, mitteilungRS, authServiceRS, fallRS);
        $rootScope.$apply();
    }

});
