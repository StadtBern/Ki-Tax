import {EbeguWebCore} from '../core/core.module';
import GesuchModelManager from './service/gesuchModelManager';
import TestDataUtil from '../utils/TestDataUtil';
import * as moment from 'moment';
import {GesuchRouteController} from './gesuch';
import TSGesuch from '../models/TSGesuch';
import {TSAntragTyp} from '../models/enums/TSAntragTyp';

describe('gesuch', function () {

    let gesuchRouteController: GesuchRouteController;
    let gesuchModelManager: GesuchModelManager;
    let gesuch: TSGesuch;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($injector.get('$httpBackend'));
        gesuchRouteController = new GesuchRouteController(gesuchModelManager, $injector.get('BerechnungsManager'),
            $injector.get('WizardStepManager'), $injector.get('EbeguUtil'), $injector.get('AntragStatusHistoryRS'),
            $injector.get('$translate'), $injector.get('AuthServiceRS'), $injector.get('$mdSidenav'), $injector.get('CONSTANTS'),
            undefined, undefined, undefined, undefined);
        gesuch = new TSGesuch();
        gesuch.typ = TSAntragTyp.ERSTGESUCH;
    }));

    describe('getGesuchErstellenStepTitle', () => {
        it('should return Art der Mutation', () => {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(false);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Erstellen einer Mutation');
        });
        it('should return Art der Mutation', () => {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.eingangsdatum = moment('01.07.2016', 'DD.MM.YYYY');
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(false);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Mutation vom 01.07.2016');
        });
        it('should return Erstgesuch der Periode', () => {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.eingangsdatum = moment('01.07.2016', 'DD.MM.YYYY');
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Erstgesuch vom 01.07.2016');
        });
        it('should return Erstgesuch', () => {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Erstgesuch');
        });
    });
});
