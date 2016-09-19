import {EbeguWebCore} from '../core.module';
import AntragStatusHistoryRS from './antragStatusHistoryRS.rest';
import TSAntragStatusHistory from '../../models/TSAntragStatusHistory';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import DateUtil from '../../utils/DateUtil';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IHttpBackendService = angular.IHttpBackendService;
import TestDataUtil from '../../utils/TestDataUtil';
import TSGesuch from '../../models/TSGesuch';

describe('betreuungRS', function () {

    let antragStatusHistoryRS: AntragStatusHistoryRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        antragStatusHistoryRS = $injector.get('AntragStatusHistoryRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    describe('Public API', function () {
        it('check URI', function () {
            expect(antragStatusHistoryRS.serviceURL).toContain('antragStatusHistory');
        });
        it('check Service name', function () {
            expect(antragStatusHistoryRS.getServiceName()).toBe('AntragStatusHistoryRS');
        });
        it('should include a findLastStatusChange() function', function () {
            expect(antragStatusHistoryRS.findLastStatusChange).toBeDefined();
        });
    });

    describe('findLastStatusChange', () => {
        it('should return the last status change for the given gesuch', () => {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.id = '123456';
            let antragStatusHistory: TSAntragStatusHistory = new TSAntragStatusHistory(gesuch.id, undefined, DateUtil.today(), TSAntragStatus.VERFUEGEN);
            TestDataUtil.setAbstractFieldsUndefined(antragStatusHistory);
            let restAntStatusHistory: any = ebeguRestUtil.antragStatusHistoryToRestObject({}, antragStatusHistory);
            $httpBackend.expectGET(antragStatusHistoryRS.serviceURL + '/' + encodeURIComponent(gesuch.id)).respond(restAntStatusHistory);

            let lastStatusChange: TSAntragStatusHistory;
            antragStatusHistoryRS.findLastStatusChange(gesuch).then((response) => {
                lastStatusChange = response;
            });
            $httpBackend.flush();

            expect(lastStatusChange).toBeDefined();
            expect(lastStatusChange.datum.isSame(antragStatusHistory.datum)).toBe(true);
            lastStatusChange.datum = antragStatusHistory.datum;
            expect(lastStatusChange).toEqual(antragStatusHistory);
        });
        it('should return undefined if the gesuch is undefined', () => {
            antragStatusHistoryRS.findLastStatusChange(undefined);
            expect(antragStatusHistoryRS.lastChange).toBeUndefined();
        });
        it('should return undefined if the gesuch id is undefined', () => {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.id = undefined;
            antragStatusHistoryRS.findLastStatusChange(gesuch);
            expect(antragStatusHistoryRS.lastChange).toBeUndefined();
        });
    });

});
