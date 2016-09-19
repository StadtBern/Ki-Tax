import {EbeguWebCore} from '../core.module';
import AntragStatusHistoryRS from './antragStatusHistoryRS.rest';
import TSAntragStatusHistory from '../../models/TSAntragStatusHistory';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import DateUtil from '../../utils/DateUtil';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IHttpBackendService = angular.IHttpBackendService;
import TestDataUtil from '../../utils/TestDataUtil';

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
            let antragStatusHistory: TSAntragStatusHistory = new TSAntragStatusHistory('123456', undefined, DateUtil.today(), TSAntragStatus.VERFUEGEN);
            TestDataUtil.setAbstractFieldsUndefined(antragStatusHistory);
            let restAntStatusHistory: any = ebeguRestUtil.antragStatusHistoryToRestObject({}, antragStatusHistory);
            $httpBackend.expectGET(antragStatusHistoryRS.serviceURL + '/123456').respond(restAntStatusHistory);

            let lastStatusChange: TSAntragStatusHistory;
            antragStatusHistoryRS.findLastStatusChange('123456').then((response) => {
                lastStatusChange = response;
            });
            $httpBackend.flush();

            expect(lastStatusChange).toBeDefined();
            expect(lastStatusChange.datum.isSame(antragStatusHistory.datum)).toBe(true);
            lastStatusChange.datum = antragStatusHistory.datum;
            expect(lastStatusChange).toEqual(antragStatusHistory);
        });
    });

});
