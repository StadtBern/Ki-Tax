import '../bootstrap.ts';
import 'angular-mocks';
import {IFilterService} from 'angular';
import EbeguRestUtil from './EbeguRestUtil';
import TSAdresse from '../models/TSAdresse';
import {EbeguWebCore} from '../core/core.module';
import TSGesuchsteller from '../models/TSGesuchsteller';
import {TSGeschlecht} from '../models/enums/TSGeschlecht';
import {TSAdressetyp} from '../models/enums/TSAdressetyp';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import {TSFachstelle} from '../models/TSFachstelle';
import TSAbstractEntity from '../models/TSAbstractEntity';
import {TSMandant} from '../models/TSMandant';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
import {TSInstitution} from '../models/TSInstitution';
import {TSInstitutionStammdaten} from '../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../models/enums/TSBetreuungsangebotTyp';
import DateUtil from './DateUtil';
import {TSDateRange} from '../models/types/TSDateRange';
import TSBetreuung from '../models/TSBetreuung';
import {TSBetreuungsstatus} from '../models/enums/TSBetreuungsstatus';
import TSBetreuungspensumContainer from '../models/TSBetreuungspensumContainer';
import TSBetreuungspensum from '../models/TSBetreuungspensum';
import {TSAbstractDateRangedEntity} from '../models/TSAbstractDateRangedEntity';

describe('EbeguRestUtil', function () {

    let ebeguRestUtil: EbeguRestUtil;
    let filter: IFilterService;
    let today: moment.Moment;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    // Das wird nur fuer tests gebraucht in denen etwas uebersetzt wird. Leider muss man dieses erstellen
    // bevor man den Injector erstellt hat. Deshalb muss es fuer alle Tests definiert werden
    beforeEach(angular.mock.module(function($provide: any) {
        let mockTranslateFilter = function(value: any) {
            if (value === 'FIRST') {
                return 'Erster';
            }
            if (value === 'SECOND') {
                return 'Zweiter';
            }
            return value;
        };
        $provide.value('translateFilter', mockTranslateFilter);
    }));

    beforeEach(angular.mock.inject(function ($injector: any) {
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        filter = $injector.get('$filter');
        today = DateUtil.today();
    }));

    describe('publicAPI', () => {
        it('should include a parseAdresse() function', function () {
            expect(ebeguRestUtil.parseAdresse).toBeDefined();
        });
        it('should include a parseGesuchsteller() function', function () {
            expect(ebeguRestUtil.parseGesuchsteller).toBeDefined();
        });
        it('should include a fachstelleToRestObject() function', function () {
            expect(ebeguRestUtil.fachstelleToRestObject).toBeDefined();
        });
        it('should include a parseFachstelle() function', function () {
            expect(ebeguRestUtil.parseFachstelle).toBeDefined();
        });
        it('should include a mandantToRestObject() function', function () {
            expect(ebeguRestUtil.mandantToRestObject).toBeDefined();
        });
        it('should include a parseMandant() function', function () {
            expect(ebeguRestUtil.parseMandant).toBeDefined();
        });
        it('should include a traegerschaftToRestObject() function', function () {
            expect(ebeguRestUtil.traegerschaftToRestObject).toBeDefined();
        });
        it('should include a parseTraegerschaft() function', function () {
            expect(ebeguRestUtil.parseTraegerschaft).toBeDefined();
        });
        it('should include a institutionToRestObject() function', function () {
            expect(ebeguRestUtil.institutionToRestObject).toBeDefined();
        });
        it('should include a parseInstitution() function', function () {
            expect(ebeguRestUtil.parseInstitution).toBeDefined();
        });
        it('should include a institutionStammdatenToRestObject() function', function () {
            expect(ebeguRestUtil.institutionStammdatenToRestObject).toBeDefined();
        });
        it('should include a parseInstitutionStammdaten() function', function () {
            expect(ebeguRestUtil.parseInstitutionStammdaten).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('parseAdresse()', () => {
            it('should transfrom Adresse Rest Objects', () => {
                let adresse = new TSAdresse();
                adresse.adresseTyp = TSAdressetyp.WOHNADRESSE;
                setAbstractFieldsUndefined(adresse);
                adresse.gemeinde = 'Testingen';
                adresse.land = 'CH';
                adresse.ort = 'Testort';
                adresse.strasse = 'Teststrasse';
                adresse.hausnummer = '1';
                adresse.zusatzzeile = 'co test';
                adresse.plz = '3014';
                adresse.id = '1234567';
                adresse.gueltigkeit = new TSDateRange(today, today);

                let restAdresse: any =  ebeguRestUtil.adresseToRestObject({}, adresse);
                expect(restAdresse).toBeDefined();
                let adr: TSAdresse = ebeguRestUtil.parseAdresse(new TSAdresse(), restAdresse);
                expect(adr).toBeDefined();
                expect(adresse.gemeinde).toEqual(adr.gemeinde);
                checkAndCopyDates(adr, adresse);
                expect(adresse).toEqual(adr);

            });
        });
        describe('parseGesuchsteller()', () => {
            it('should transfrom TSGesuchsteller to REST Obj and back', () => {
                let myGesuchsteller =  new TSGesuchsteller();
                myGesuchsteller.vorname = 'Til';
                myGesuchsteller.nachname = 'TestGesuchsteller';
                myGesuchsteller.id = 'mytestid';
                myGesuchsteller.geschlecht = TSGeschlecht.MAENNLICH;
                myGesuchsteller.telefon = '+41 76 300 12 34';
                myGesuchsteller.mobile = '+41 76 300 12 34';
                myGesuchsteller.umzug = false;
                myGesuchsteller.mail = 'Til.Testgesuchsteller@example.com';
                myGesuchsteller.korrespondenzAdresse = undefined;
                myGesuchsteller.umzugAdresse = undefined;
                myGesuchsteller.adresse = undefined;
                myGesuchsteller.timestampErstellt = undefined;
                myGesuchsteller.timestampMutiert = undefined;
                myGesuchsteller.finanzielleSituationContainer = undefined;
                let restGesuchsteller =  ebeguRestUtil.gesuchstellerToRestObject({}, myGesuchsteller);
                expect(restGesuchsteller).toBeDefined();
                let transformedPers: TSGesuchsteller = ebeguRestUtil.parseGesuchsteller(new TSGesuchsteller(), restGesuchsteller);
                expect(transformedPers).toBeDefined();
                expect(myGesuchsteller.nachname).toEqual(transformedPers.nachname);
                expect(myGesuchsteller).toEqual(transformedPers);

            });
        });
        describe('parseFachstelle()', () => {
           it('should transform TSFachstelle to REST object and back', () => {
               let myFachstelle = new TSFachstelle('Fachstelle_name', 'Beschreibung', true);
               setAbstractFieldsUndefined(myFachstelle);

               let restFachstelle = ebeguRestUtil.fachstelleToRestObject({}, myFachstelle);
               expect(restFachstelle).toBeDefined();
               expect(restFachstelle.name).toEqual(myFachstelle.name);
               expect(restFachstelle.beschreibung).toEqual(myFachstelle.beschreibung);
               expect(restFachstelle.behinderungsbestaetigung).toEqual(myFachstelle.behinderungsbestaetigung);

               let transformedFachstelle = ebeguRestUtil.parseFachstelle(new TSFachstelle(), restFachstelle);
               expect(transformedFachstelle).toBeDefined();
               expect(transformedFachstelle).toEqual(myFachstelle);
           });
        });
        describe('parseMandant()', () => {
            it('should transform TSMandant to REST object and back', () => {
                let myMandant = new TSMandant('myMandant');
                setAbstractFieldsUndefined(myMandant);

                let restMandant = ebeguRestUtil.mandantToRestObject({}, myMandant);
                expect(restMandant).toBeDefined();
                expect(restMandant.name).toEqual(myMandant.name);

                let transformedMandant = ebeguRestUtil.parseMandant(new TSMandant(), restMandant);
                expect(transformedMandant).toBeDefined();
                expect(transformedMandant).toEqual(myMandant);
            });
        });
        describe('parseTraegerschaft()', () => {
            it('should transform TSTraegerschaft to REST object and back', () => {
                let myTraegerschaft = new TSTraegerschaft('myTraegerschaft');
                setAbstractFieldsUndefined(myTraegerschaft);

                let restTraegerschaft = ebeguRestUtil.traegerschaftToRestObject({}, myTraegerschaft);
                expect(restTraegerschaft).toBeDefined();
                expect(restTraegerschaft.name).toEqual(myTraegerschaft.name);

                let transformedTraegerschaft = ebeguRestUtil.parseTraegerschaft(new TSTraegerschaft(), restTraegerschaft);
                expect(transformedTraegerschaft).toBeDefined();
                expect(transformedTraegerschaft).toEqual(myTraegerschaft);
            });
        });
        describe('parseInstitution()', () => {
            it('should transform TSInstitution to REST object and back', () => {
                var myInstitution = createInstitution();

                let restInstitution = ebeguRestUtil.institutionToRestObject({}, myInstitution);
                expect(restInstitution).toBeDefined();
                expect(restInstitution.name).toEqual(myInstitution.name);
                expect(restInstitution.traegerschaft.name).toEqual(myInstitution.traegerschaft.name);
                expect(restInstitution.mandant.name).toEqual(myInstitution.mandant.name);

                let transformedInstitution = ebeguRestUtil.parseInstitution(new TSInstitution(), restInstitution);
                expect(transformedInstitution).toBeDefined();
                expect(transformedInstitution).toEqual(myInstitution);
            });
        });
        describe('parseBetreuung()', () => {
            it('should transform TSBetreuung to REST object and back', () => {
                let instStam: TSInstitutionStammdaten = new TSInstitutionStammdaten('iban', 250, 12, TSBetreuungsangebotTyp.KITA, createInstitution(),
                    new TSDateRange(DateUtil.today(), DateUtil.today()));
                setAbstractFieldsUndefined(instStam);

                let tsBetreuungspensumGS: TSBetreuungspensum = new TSBetreuungspensum(25, new TSDateRange(DateUtil.today(), DateUtil.today()));
                setAbstractFieldsUndefined(tsBetreuungspensumGS);
                let tsBetreuungspensumJA: TSBetreuungspensum = new TSBetreuungspensum(50, new TSDateRange(DateUtil.today(), DateUtil.today()));
                setAbstractFieldsUndefined(tsBetreuungspensumJA);
                let tsBetreuungspensumContainer: TSBetreuungspensumContainer = new TSBetreuungspensumContainer(tsBetreuungspensumGS, tsBetreuungspensumJA);
                setAbstractFieldsUndefined(tsBetreuungspensumContainer);
                let betContainers: Array<TSBetreuungspensumContainer> = [tsBetreuungspensumContainer];
                let betreuung: TSBetreuung = new TSBetreuung(instStam, TSBetreuungsstatus.AUSSTEHEND, betContainers, 'bemerkungen');
                setAbstractFieldsUndefined(betreuung);

                let restBetreuung = ebeguRestUtil.betreuungToRestObject({}, betreuung);

                expect(restBetreuung).toBeDefined();
                expect(restBetreuung.bemerkungen).toEqual('bemerkungen');
                expect(restBetreuung.betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                expect(restBetreuung.institutionStammdaten.iban).toEqual(betreuung.institutionStammdaten.iban);
                expect(restBetreuung.betreuungspensumContainers).toBeDefined();
                expect(restBetreuung.betreuungspensumContainers.length).toEqual(betreuung.betreuungspensumContainers.length);
                expect(restBetreuung.betreuungspensumContainers[0].betreuungspensumGS.pensum).toBe(betreuung.betreuungspensumContainers[0].betreuungspensumGS.pensum);
                expect(restBetreuung.betreuungspensumContainers[0].betreuungspensumJA.pensum).toBe(betreuung.betreuungspensumContainers[0].betreuungspensumJA.pensum);

                let transformedBetreuung: TSBetreuung = ebeguRestUtil.parseBetreuung(new TSBetreuung(), restBetreuung);

                expect(transformedBetreuung).toBeDefined();
                checkAndCopyDates(transformedBetreuung.betreuungspensumContainers[0].betreuungspensumGS, betreuung.betreuungspensumContainers[0].betreuungspensumGS);
                checkAndCopyDates(transformedBetreuung.betreuungspensumContainers[0].betreuungspensumJA, betreuung.betreuungspensumContainers[0].betreuungspensumJA);
                checkAndCopyDates(transformedBetreuung.institutionStammdaten, betreuung.institutionStammdaten);
                expect(transformedBetreuung.bemerkungen).toEqual(betreuung.bemerkungen);
                expect(transformedBetreuung.betreuungsstatus).toEqual(betreuung.betreuungsstatus);
                expect(transformedBetreuung.betreuungspensumContainers[0]).toEqual(betreuung.betreuungspensumContainers[0]);
            });
        });
        describe('parseBetreuungspensum', () => {
            it('should transform TSBetreuungspensum to REST object and back', () => {
                let betreuungspensum: TSBetreuungspensum = new TSBetreuungspensum(25, new TSDateRange(DateUtil.today(), DateUtil.today()));
                setAbstractFieldsUndefined(betreuungspensum);

                let restBetreuungspensum: TSBetreuungspensum = ebeguRestUtil.betreuungspensumToRestObject({}, betreuungspensum);
                expect(restBetreuungspensum).toBeDefined();
                expect(restBetreuungspensum.pensum).toEqual(betreuungspensum.pensum);

                let transformedBetreuungspensum: TSBetreuungspensum = ebeguRestUtil.parseBetreuungspensum(new TSBetreuungspensum(), restBetreuungspensum);

                expect(transformedBetreuungspensum).toBeDefined();
                checkAndCopyDates(transformedBetreuungspensum, betreuungspensum);
                expect(transformedBetreuungspensum).toEqual(betreuungspensum);
            });
        });
        describe('parseInstitutionStammdaten()', () => {
            it('should transform TSInstitutionStammdaten to REST object and back', () => {
                var myInstitution = createInstitution();
                let myInstitutionStammdaten = new TSInstitutionStammdaten('iban', 250, 12, TSBetreuungsangebotTyp.KITA, myInstitution,
                    new TSDateRange(DateUtil.today(), DateUtil.today()));
                setAbstractFieldsUndefined(myInstitutionStammdaten);

                let restInstitutionStammdaten = ebeguRestUtil.institutionStammdatenToRestObject({}, myInstitutionStammdaten);
                expect(restInstitutionStammdaten).toBeDefined();
                expect(restInstitutionStammdaten.iban).toEqual(myInstitutionStammdaten.iban);
                expect(restInstitutionStammdaten.oeffnungsstunden).toEqual(myInstitutionStammdaten.oeffnungsstunden);
                expect(restInstitutionStammdaten.oeffnungstage).toEqual(myInstitutionStammdaten.oeffnungstage);
                expect(restInstitutionStammdaten.gueltigAb).toEqual(DateUtil.momentToLocalDate(myInstitutionStammdaten.gueltigkeit.gueltigAb));
                expect(restInstitutionStammdaten.gueltigBis).toEqual(DateUtil.momentToLocalDate(myInstitutionStammdaten.gueltigkeit.gueltigBis));
                expect(restInstitutionStammdaten.betreuungsangebotTyp).toEqual(myInstitutionStammdaten.betreuungsangebotTyp);
                expect(restInstitutionStammdaten.institution.name).toEqual(myInstitutionStammdaten.institution.name);

                let transformedInstitutionStammdaten = ebeguRestUtil.parseInstitutionStammdaten(new TSInstitutionStammdaten(), restInstitutionStammdaten);

                // Dieses hack wird gebraucht weil um 2 Moment zu vergleichen kann man nicht einfach equal() benutzen sondern isSame
                expect(myInstitutionStammdaten.gueltigkeit.gueltigAb.isSame(transformedInstitutionStammdaten.gueltigkeit.gueltigAb)).toBe(true);
                expect(myInstitutionStammdaten.gueltigkeit.gueltigBis.isSame(transformedInstitutionStammdaten.gueltigkeit.gueltigBis)).toBe(true);
                myInstitutionStammdaten.gueltigkeit.gueltigAb = transformedInstitutionStammdaten.gueltigkeit.gueltigAb;
                myInstitutionStammdaten.gueltigkeit.gueltigBis = transformedInstitutionStammdaten.gueltigkeit.gueltigBis;
                expect(transformedInstitutionStammdaten).toEqual(myInstitutionStammdaten);
            });
        });
        describe('translateStringList', () => {
            it('should translate the given list of words', () => {
                let list: Array<string> = ['FIRST', 'SECOND'];
                let returnedList: Array<any> = ebeguRestUtil.translateStringList(list);
                expect(returnedList.length).toEqual(2);
                expect(returnedList[0].key).toEqual('FIRST');
                expect(returnedList[0].value).toEqual('Erster');
                expect(returnedList[1].key).toEqual('SECOND');
                expect(returnedList[1].value).toEqual('Zweiter');
            });
        });
    });

    function setAbstractFieldsUndefined(abstractEntity: TSAbstractEntity) {
        abstractEntity.id = undefined;
        abstractEntity.timestampErstellt = undefined;
        abstractEntity.timestampMutiert = undefined;
    }

    function createInstitution(): TSInstitution {
        let traegerschaft = new TSTraegerschaft('myTraegerschaft');
        setAbstractFieldsUndefined(traegerschaft);
        let mandant = new TSMandant('myMandant');
        setAbstractFieldsUndefined(mandant);
        let myInstitution = new TSInstitution('myInstitution', traegerschaft, mandant);
        setAbstractFieldsUndefined(myInstitution);
        return myInstitution;
    }

    function checkAndCopyDates(abstrTocopyTo: TSAbstractDateRangedEntity, abstrToCopyFrom: TSAbstractDateRangedEntity): void {
        expect(abstrTocopyTo.gueltigkeit.gueltigAb.isSame(abstrToCopyFrom.gueltigkeit.gueltigAb)).toBe(true);
        expect(abstrTocopyTo.gueltigkeit.gueltigBis.isSame(abstrToCopyFrom.gueltigkeit.gueltigBis)).toBe(true);
        abstrTocopyTo.gueltigkeit = abstrToCopyFrom.gueltigkeit;
    }

});
