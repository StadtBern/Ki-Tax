import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService} from 'angular';
import {EbeguWebPendenzenInstitution} from '../pendenzenInstitution.module';
import PendenzInstitutionRS from './PendenzInstitutionRS.rest';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TSPendenzInstitution from '../../models/TSPendenzInstitution';

describe('pendenzInstitutionRS', function () {

    let pendenzInstitutionRS: PendenzInstitutionRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPendenzInstitution: TSPendenzInstitution;
    let mockPendenzInstitutionRest: any;

    beforeEach(angular.mock.module(EbeguWebPendenzenInstitution.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzInstitutionRS = $injector.get('PendenzInstitutionRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockPendenzInstitution = new TSPendenzInstitution('123.12.12', '123',  '123',  '123', 'Kind', 'Kilian', undefined, 'Platzbestaetigung', undefined,
            undefined, TSBetreuungsangebotTyp.KITA, undefined);
        mockPendenzInstitutionRest = ebeguRestUtil.pendenzInstitutionToRestObject({}, mockPendenzInstitution);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(pendenzInstitutionRS.getServiceName()).toBe('PendenzInstitutionRS');
        });
        it('should include a getPendenzenList() function', function () {
            expect(pendenzInstitutionRS.getPendenzenList).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findBetreuung', () => {
            it('should return all pending Antraege', () => {
                let arrayResult: Array<any> = [mockPendenzInstitutionRest];
                $httpBackend.expectGET(pendenzInstitutionRS.serviceURL).respond(arrayResult);

                let foundPendenzen: Array<TSPendenzInstitution>;
                pendenzInstitutionRS.getPendenzenList().then((result) => {
                    foundPendenzen = result;
                });
                $httpBackend.flush();
                expect(foundPendenzen).toBeDefined();
                expect(foundPendenzen.length).toBe(1);
                expect(foundPendenzen[0]).toEqual(mockPendenzInstitution);
            });
        });
    });
});
