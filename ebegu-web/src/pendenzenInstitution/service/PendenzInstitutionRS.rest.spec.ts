import TSPendenzJA from '../../models/TSPendenzJA';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService} from 'angular';
import {EbeguWebPendenzenInstitution} from '../pendenzenInstitution.module';
import PendenzInstitutionRS from './PendenzInstitutionRS.rest';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TSPendenzInstitution from '../../models/TSPendenzInstitution';

describe('pendenzRS', function () {

    var pendenzRS: PendenzInstitutionRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPendenz: TSPendenzInstitution;
    let mockPendenzRest: any;

    beforeEach(angular.mock.module(EbeguWebPendenzenInstitution.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzRS = $injector.get('PendenzRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockPendenz = new TSPendenzInstitution('123.12.12', 'Kind', 'Kilian', undefined, 'Platzbestaetigung', undefined,
            undefined, TSBetreuungsangebotTyp.KITA, undefined);
        mockPendenzRest = ebeguRestUtil.pendenzInstitutionToRestObject({}, mockPendenz);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(pendenzRS.getServiceName()).toBe('PendenzInstitutionRS');
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

                let foundPendenzen: Array<TSPendenzInstitution>;
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
