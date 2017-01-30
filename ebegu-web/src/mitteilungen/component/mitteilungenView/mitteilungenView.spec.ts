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
import TestDataUtil from '../../../utils/TestDataUtil';

describe('mitteilungenView', function () {

    let mitteilungRS: MitteilungRS;
    let authServiceRS: AuthServiceRS;
    let stateParams: IMitteilungenStateParams;
    let fallRS: FallRS;
    let fall: TSFall;
    let $rootScope: IScope;
    let $q: IQService;
    let controller: MitteilungenViewController;
    let besitzer: TSUser;
    let verantwortlicher: TSUser;


    beforeEach(angular.mock.module(EbeguWebMitteilungen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        mitteilungRS = $injector.get('MitteilungRS');
        authServiceRS = $injector.get('AuthServiceRS');
        fallRS = $injector.get('FallRS');
        stateParams = $injector.get('$stateParams');
        $rootScope = $injector.get('$rootScope');
        $q = $injector.get('$q');

        // prepare fall
        stateParams.fallId = '123';
        fall = new TSFall();
        fall.id = stateParams.fallId;
        besitzer = new TSUser();
        besitzer.nachname = 'Romualdo Besitzer';
        fall.besitzer = besitzer;
        verantwortlicher = new TSUser();
        verantwortlicher.nachname = 'Arnaldo Verantwortlicher';
        fall.verantwortlicher = verantwortlicher;

        spyOn(mitteilungRS, 'getEntwurfForCurrentRolleForFall').and.returnValue($q.when(undefined));
    }));

    describe('loading initial data', function () {
        it('should create an empty TSMItteilung for GS', function () {
            let gesuchsteller: TSUser = new TSUser();
            gesuchsteller.role = TSRole.GESUCHSTELLER;
            spyOn(authServiceRS, 'isRole').and.returnValue(true);

            createMitteilungForUser(gesuchsteller);

            compareCommonAttributes(gesuchsteller);
            expect(controller.getCurrentMitteilung().empfaenger).toBe(verantwortlicher);
            expect(controller.getCurrentMitteilung().senderTyp).toBe(TSMitteilungTeilnehmerTyp.GESUCHSTELLER);
            expect(controller.getCurrentMitteilung().empfaengerTyp).toBe(TSMitteilungTeilnehmerTyp.JUGENDAMT);
        });
        it('should create an empty TSMItteilung for JA', function () {
            let sachbearbeiter_ja: TSUser = new TSUser();
            sachbearbeiter_ja.role = TSRole.SACHBEARBEITER_JA;
            spyOn(authServiceRS, 'isOneOfRoles').and.callFake((roles: Array<TSRole>) => {
                return roles.indexOf(TSRole.SACHBEARBEITER_JA) >= 0;
            });

            createMitteilungForUser(sachbearbeiter_ja);

            compareCommonAttributes(sachbearbeiter_ja);
            expect(controller.getCurrentMitteilung().empfaenger).toBe(besitzer);
            expect(controller.getCurrentMitteilung().empfaengerTyp).toBe(TSMitteilungTeilnehmerTyp.GESUCHSTELLER);
            expect(controller.getCurrentMitteilung().senderTyp).toBe(TSMitteilungTeilnehmerTyp.JUGENDAMT);
        });
        it('should create an empty TSMItteilung for Institution', function () {
            let sachbearbeiter_inst: TSUser = new TSUser();
            sachbearbeiter_inst.role = TSRole.SACHBEARBEITER_INSTITUTION;
            spyOn(authServiceRS, 'isOneOfRoles').and.callFake((roles: Array<TSRole>) => {
                return roles.indexOf(TSRole.SACHBEARBEITER_INSTITUTION) >= 0;
            });

            createMitteilungForUser(sachbearbeiter_inst);

            compareCommonAttributes(sachbearbeiter_inst);
            expect(controller.getCurrentMitteilung().empfaenger).toBe(verantwortlicher);
            expect(controller.getCurrentMitteilung().empfaengerTyp).toBe(TSMitteilungTeilnehmerTyp.JUGENDAMT);
            expect(controller.getCurrentMitteilung().senderTyp).toBe(TSMitteilungTeilnehmerTyp.INSTITUTION);
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
            savedMitteilung.mitteilungStatus = TSMitteilungStatus.NEU;
            spyOn(mitteilungRS, 'sendMitteilung').and.returnValue($q.when(savedMitteilung));
            controller.getCurrentMitteilung().subject = 'subject';
            controller.getCurrentMitteilung().message = 'message';

            controller.form = TestDataUtil.createDummyForm();
            controller.form.$dirty = true;
            controller.sendMitteilung();

            expect(controller.getCurrentMitteilung().mitteilungStatus).toBe(TSMitteilungStatus.ENTWURF);
            expect(controller.getCurrentMitteilung().sentDatum).toBeUndefined();
            expect(controller.getCurrentMitteilung().id).toBeUndefined();
        });
    });
    describe('setErledigt', function () {
        it('should change the status from GELESEN to ERLEDIGT and save the mitteilung', function () {
            let gesuchsteller: TSUser = new TSUser();
            gesuchsteller.role = TSRole.GESUCHSTELLER;
            spyOn(authServiceRS, 'isRole').and.returnValue(true);

            createMitteilungForUser(gesuchsteller);

            let mitteilung: TSMitteilung = new TSMitteilung();
            mitteilung.id = '123';
            spyOn(mitteilungRS, 'setMitteilungErledigt').and.returnValue($q.when(mitteilung));

            mitteilung.mitteilungStatus = TSMitteilungStatus.ENTWURF;
            controller.setErledigt(mitteilung);
            expect(mitteilung.mitteilungStatus).toBe(TSMitteilungStatus.ENTWURF); // Status ENTWURF wird nicht geaendert
            expect(mitteilungRS.setMitteilungErledigt).not.toHaveBeenCalled();

            mitteilung.mitteilungStatus = TSMitteilungStatus.NEU;
            controller.setErledigt(mitteilung);
            expect(mitteilung.mitteilungStatus).toBe(TSMitteilungStatus.NEU); // Status NEU wird nicht geaendert
            expect(mitteilungRS.setMitteilungErledigt).not.toHaveBeenCalled();

            mitteilung.mitteilungStatus = TSMitteilungStatus.GELESEN;
            controller.setErledigt(mitteilung);
            expect(mitteilung.mitteilungStatus).toBe(TSMitteilungStatus.ERLEDIGT); // von GELESEN auf ERLEDIGT
            expect(mitteilungRS.setMitteilungErledigt).toHaveBeenCalledWith('123');

            controller.setErledigt(mitteilung);
            expect(mitteilung.mitteilungStatus).toBe(TSMitteilungStatus.GELESEN); // von ERLEDIGT auf GELESEN
            expect(mitteilungRS.setMitteilungErledigt).toHaveBeenCalledWith('123');
        });
    });



    function compareCommonAttributes(currentUser: TSUser): void {
        expect(controller.getCurrentMitteilung()).toBeDefined();
        expect(controller.getCurrentMitteilung().fall).toBe(fall);
        expect(controller.getCurrentMitteilung().mitteilungStatus).toBe(TSMitteilungStatus.ENTWURF);
        expect(controller.getCurrentMitteilung().sender).toBe(currentUser);
        expect(controller.getCurrentMitteilung().subject).toBeUndefined();
        expect(controller.getCurrentMitteilung().message).toBeUndefined();
    }

    function createMitteilungForUser(user: TSUser): void {
        spyOn(authServiceRS, 'getPrincipal').and.returnValue(user);
        spyOn(fallRS, 'findFall').and.returnValue($q.when(fall));
        spyOn(mitteilungRS, 'getMitteilungenForCurrentRolleForFall').and.returnValue($q.when([{}]));
        spyOn(mitteilungRS, 'setAllNewMitteilungenOfFallGelesen').and.returnValue($q.when([{}]));

        controller = new MitteilungenViewController(stateParams, mitteilungRS, authServiceRS, fallRS, $q);
        $rootScope.$apply();
    }

});
