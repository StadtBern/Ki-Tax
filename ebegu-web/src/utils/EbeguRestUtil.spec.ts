import '../bootstrap.ts';
import 'angular-mocks';
import EbeguRestUtil from './EbeguRestUtil';
import TSAdresse from '../models/TSAdresse';
import {EbeguWebCore} from '../core/core.module';
import TSGesuchsteller from '../models/TSGesuchsteller';
import {TSGeschlecht} from '../models/enums/TSGeschlecht';
import {TSAdressetyp} from '../models/enums/TSAdressetyp';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('EbeguRestUtil', function () {

    let ebeguRestUtil: EbeguRestUtil;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    describe('publicAPI', () => {
        it('should include a parseAdresse() function', function () {
            expect(ebeguRestUtil.parseAdresse).toBeDefined();
        });

        it('should include a parseGesuchsteller() function', function () {
            expect(ebeguRestUtil.parseGesuchsteller).toBeDefined();
        });

    });

    describe('API Usage', function () {
        describe('parseAdresse()', () => {
            it('should transfrom Adresse Rest Objects', () => {
                let addresse = new TSAdresse();
                addresse.adresseTyp = TSAdressetyp.WOHNADRESSE;
                addresse.gemeinde = 'Testingen';
                addresse.land = 'CH';
                addresse.ort = 'Testort';
                addresse.strasse = 'Teststrasse';
                addresse.hausnummer = '1';
                addresse.zusatzzeile = 'co test';
                addresse.plz = '3014';
                addresse.id = '1234567';

                let restAdresse: any =  ebeguRestUtil.adresseToRestObject({}, addresse);
                expect(restAdresse).toBeDefined();
                let adr: TSAdresse = ebeguRestUtil.parseAdresse(new TSAdresse(), restAdresse);
                expect(adr).toBeDefined();
                expect(addresse.gemeinde).toEqual(adr.gemeinde);
                expect(addresse).toEqual(adr);

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
                let restGesuchsteller =  ebeguRestUtil.gesuchstellerToRestObject({}, myGesuchsteller);
                expect(restGesuchsteller).toBeDefined();
                let transformedPers: TSGesuchsteller = ebeguRestUtil.parseGesuchsteller(new TSGesuchsteller(), restGesuchsteller);
                expect(transformedPers).toBeDefined();
                expect(myGesuchsteller.nachname).toEqual(transformedPers.nachname);
                expect(myGesuchsteller).toEqual(transformedPers);

            });

        });
    });


});
