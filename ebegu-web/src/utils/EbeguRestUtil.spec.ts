import '../bootstrap.ts';
import 'angular-mocks';
import EbeguRestUtil from './EbeguRestUtil';
import TSAdresse from '../models/TSAdresse';
import {EbeguWebCore} from '../core/core.module';
import TSPerson from '../models/TSPerson';
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

        it('should include a parsePerson() function', function () {
            expect(ebeguRestUtil.parsePerson).toBeDefined();
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
                addresse.hausnummer = 1;
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
        describe('parsePerson()', () => {
            it('should transfrom TSPerson to REST Obj and back', () => {
                let myPerson =  new TSPerson();
                myPerson.vorname = 'Til';
                myPerson.nachname = 'Testperson';
                myPerson.id = 'mytestid';
                myPerson.geschlecht = TSGeschlecht.MAENNLICH;
                myPerson.telefon = '+41 76 300 12 34';
                myPerson.mobile = '+41 76 300 12 34';
                myPerson.umzug = false;
                myPerson.mail = 'Til.Testperson@example.com';
                let restPerson =  ebeguRestUtil.personToRestObject({}, myPerson);
                expect(restPerson).toBeDefined();
                let transformedPers: TSPerson = ebeguRestUtil.parsePerson(new TSPerson(), restPerson);
                expect(transformedPers).toBeDefined();
                expect(myPerson.nachname).toEqual(transformedPers.nachname);
                expect(myPerson, transformedPers);

            });

        });
    });


});
