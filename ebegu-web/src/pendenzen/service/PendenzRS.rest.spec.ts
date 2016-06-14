import TSPendenzJA from '../../models/TSPendenzJA';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService} from 'angular';
import {EbeguWebPendenzen} from '../pendenzen.module';
import PendenzRS from './PendenzRS.rest';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';

describe('pendenzRS', function () {

    var pendenzRS: PendenzRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPendenz: TSPendenzJA;
    let mockPendenzRest: any;

    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzRS = $injector.get('PendenzRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockPendenz = new TSPendenzJA('id1', 123, 'name', TSAntragTyp.GESUCH, undefined,
            undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2']);
        mockPendenzRest = ebeguRestUtil.pendenzToRestObject({}, mockPendenz);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(pendenzRS.getServiceName()).toBe('PendenzRS');
        });
        it('should include a getPendenzenList() function', function () {
            expect(pendenzRS.getPendenzenList).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findBetreuung', () => {
            it('should return all pending Antraege', () => {
                let arrayResult: Array<any> = [mockPendenzRest];
                $httpBackend.expectGET(pendenzRS.serviceURL).respond(arrayResult);

                let foundPendenzen: Array<TSPendenzJA>;
                pendenzRS.getPendenzenList().then((result) => {
                    foundPendenzen = result;
                });
                $httpBackend.flush();
                expect(foundPendenzen).toBeDefined();
                expect(foundPendenzen.length).toBe(1);
                expect(foundPendenzen[0]).toEqual(mockPendenz);
            });
        });
    });

});
