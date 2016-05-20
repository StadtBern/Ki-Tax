import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {BetreuungViewController} from './betreuungView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSBetreuung from '../../../models/TSBetreuung';
import DateUtil from '../../../utils/DateUtil';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {TSInstitutionStammdaten} from '../../../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import IQService = angular.IQService;

describe('betreuungView', function () {

    let betreuungView: BetreuungViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let ebeguRestUtil: EbeguRestUtil;
    let $q: IQService;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        $q = $injector.get('$q');
        betreuungView = new BetreuungViewController($state, gesuchModelManager, ebeguRestUtil);
    }));

    describe('Public API', function () {
        it('should include a cancel() function', function () {
            expect(betreuungView.cancel).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('cancel existing object', () => {
            it('should not remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.timestampErstellt = DateUtil.today();
                spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(betreuung);
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');

                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).not.toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
            });
        });
        describe('cancel non-existing object', () => {
            it('should remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(betreuung);
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');

                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
            });
        });
        describe('getInstitutionenSDList', () => {
            beforeEach(function() {
                gesuchModelManager.institutionenList = [];
                gesuchModelManager.institutionenList.push(createInstitutionStammdaten('1', TSBetreuungsangebotTyp.KITA));
                gesuchModelManager.institutionenList.push(createInstitutionStammdaten('2', TSBetreuungsangebotTyp.KITA));
                gesuchModelManager.institutionenList.push(createInstitutionStammdaten('3', TSBetreuungsangebotTyp.TAGESELTERN));
                gesuchModelManager.institutionenList.push(createInstitutionStammdaten('4', TSBetreuungsangebotTyp.TAGESSCHULE));
            });
            it('should return an empty list if betreuungsangebot is not yet defined', () => {
                 let list: Array<TSInstitutionStammdaten> = betreuungView.getInstitutionenSDList();
                expect(list).toBeDefined();
                expect(list.length).toBe(0);
            });
            it('should return a list with 2 Institutions of type TSBetreuungsangebotTyp.KITA', () => {
                betreuungView.betreuungsangebot = {key: 'KITA', value: 'kita'};
                let list: Array<TSInstitutionStammdaten> = betreuungView.getInstitutionenSDList();
                expect(list).toBeDefined();
                expect(list.length).toBe(2);
                expect(list[0].iban).toBe('1');
                expect(list[1].iban).toBe('2');
            });
        });
        describe('createBetreuungspensum', () => {
            it('creates the first betreuungspensum in empty list and then a second one', () => {
                spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(new TSBetreuung());
                betreuungView.createBetreuungspensum();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers).toBeDefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers.length).toBe(1);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumGS).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA).toBeDefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA.pensum).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA.gueltigkeit.gueltigAb).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA.gueltigkeit.gueltigBis).toBeUndefined();

                betreuungView.createBetreuungspensum();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers.length).toBe(2);
            });
        });
        describe('submit', () => {
            it('Does not submit because form is invalid', () => {
                spyOn(gesuchModelManager, 'updateBetreuung').and.returnValue($q.when({}));
                let form: any = {};
                form.$valid = false;
                betreuungView.submit(form);
                expect(gesuchModelManager.updateBetreuung).not.toHaveBeenCalled();
            });
            it('submits all data of current Betreuung', () => {
                spyOn(gesuchModelManager, 'updateBetreuung').and.returnValue($q.when({}));
                let form: any = {};
                form.$valid = true;
                betreuungView.submit(form);
                expect(gesuchModelManager.updateBetreuung).toHaveBeenCalled();
            });
        });
    });

    function createInstitutionStammdaten(iban: string, betAngTyp: TSBetreuungsangebotTyp) {
        let instStam1: TSInstitutionStammdaten = new TSInstitutionStammdaten();
        instStam1.iban = iban;
        instStam1.betreuungsangebotTyp = betAngTyp;
        return instStam1;
    }

});
