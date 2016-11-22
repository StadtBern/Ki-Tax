import {EbeguWebCore} from '../core/core.module';
import GesuchModelManager from './service/gesuchModelManager';
import TestDataUtil from '../utils/TestDataUtil';
import * as moment from 'moment';
import {GesuchRouteController} from './gesuch';
import TSGesuch from '../models/TSGesuch';

describe('fallCreationView', function () {

    let gesuchRouteController: GesuchRouteController;
    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($injector.get('$httpBackend'));
        gesuchRouteController = new GesuchRouteController(gesuchModelManager, $injector.get('BerechnungsManager'),
            $injector.get('WizardStepManager'), $injector.get('EbeguUtil'), $injector.get('AntragStatusHistoryRS'), $injector.get('$translate'));
    }));

    describe('getGesuchErstellenStepTitle', () => {
        it('should return Art der Mutation', () => {
            spyOn(gesuchModelManager, 'isErstgesuch').and.returnValue(false);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Erstellen einer Mutation');
        });
        it('should return Art der Mutation', () => {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.eingangsdatum = moment('01.07.2016', 'DD.MM.YYYY');
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            spyOn(gesuchModelManager, 'isErstgesuch').and.returnValue(false);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Mutation vom 01.07.2016');
        });
        it('should return Erstgesuch der Periode', () => {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.eingangsdatum = moment('01.07.2016', 'DD.MM.YYYY');
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            spyOn(gesuchModelManager, 'isErstgesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Erstgesuch vom 01.07.2016');
        });
        it('should return Erstgesuch', () => {
            spyOn(gesuchModelManager, 'isErstgesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            expect(gesuchRouteController.getGesuchErstellenStepTitle()).toBe('Erstgesuch');
        });
    });
});
