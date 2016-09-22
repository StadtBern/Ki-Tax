import '../bootstrap.ts';
import 'angular-mocks';
import {IFilterService} from 'angular';
import EbeguRestUtil from './EbeguRestUtil';
import TSAdresse from '../models/TSAdresse';
import {EbeguWebCore} from '../core/core.module';
import TSGesuchsteller from '../models/TSGesuchsteller';
import {TSGeschlecht} from '../models/enums/TSGeschlecht';
import {TSAdressetyp} from '../models/enums/TSAdressetyp';
import {TSFachstelle} from '../models/TSFachstelle';
import {TSMandant} from '../models/TSMandant';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
import TSInstitution from '../models/TSInstitution';
import TSInstitutionStammdaten from '../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../models/enums/TSBetreuungsangebotTyp';
import DateUtil from './DateUtil';
import {TSDateRange} from '../models/types/TSDateRange';
import TSErwerbspensum from '../models/TSErwerbspensum';
import TestDataUtil from './TestDataUtil';
import TSBetreuung from '../models/TSBetreuung';
import {TSBetreuungsstatus} from '../models/enums/TSBetreuungsstatus';
import TSBetreuungspensumContainer from '../models/TSBetreuungspensumContainer';
import TSBetreuungspensum from '../models/TSBetreuungspensum';
import TSGesuch from '../models/TSGesuch';
import TSGesuchsperiode from '../models/TSGesuchsperiode';
import TSFall from '../models/TSFall';
import TSAntragDTO from '../models/TSAntragDTO';
import {TSAntragTyp} from '../models/enums/TSAntragTyp';
import {EbeguWebPendenzen} from '../pendenzen/pendenzen.module';
import TSFamiliensituation from '../models/TSFamiliensituation';
import TSVerfuegung from '../models/TSVerfuegung';
import TSVerfuegungZeitabschnitt from '../models/TSVerfuegungZeitabschnitt';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('EbeguRestUtil', function () {

    let ebeguRestUtil: EbeguRestUtil;
    let filter: IFilterService;
    let today: moment.Moment;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

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
        it('should include a parseGesuch() function', function () {
            expect(ebeguRestUtil.parseGesuch).toBeDefined();
        });
        it('should include a gesuchToRestObject() function', function () {
            expect(ebeguRestUtil.gesuchToRestObject).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('parseAdresse()', () => {
            it('should transfrom Adresse Rest Objects', () => {
                let adresse = new TSAdresse();
                adresse.adresseTyp = TSAdressetyp.WOHNADRESSE;
                TestDataUtil.setAbstractFieldsUndefined(adresse);
                adresse.gemeinde = 'Testingen';
                adresse.land = 'CH';
                adresse.ort = 'Testort';
                adresse.strasse = 'Teststrasse';
                adresse.hausnummer = '1';
                adresse.zusatzzeile = 'co test';
                adresse.plz = '3014';
                adresse.id = '1234567';
                adresse.organisation = 'Test AG';
                adresse.gueltigkeit = new TSDateRange(today, today);

                let restAdresse: any = ebeguRestUtil.adresseToRestObject({}, adresse);
                expect(restAdresse).toBeDefined();
                let adr: TSAdresse = ebeguRestUtil.parseAdresse(new TSAdresse(), restAdresse);
                expect(adr).toBeDefined();
                expect(adresse.gemeinde).toEqual(adr.gemeinde);
                TestDataUtil.checkGueltigkeitAndSetIfSame(adr, adresse);
                expect(adresse).toEqual(adr);

            });
        });
        describe('parseGesuchsteller()', () => {
            it('should transfrom TSGesuchsteller to REST Obj and back', () => {
                var myGesuchsteller = createGesuchsteller();
                myGesuchsteller.telefon = ''; // Ein leerer String im Telefon muss auch behandelt werden
                let restGesuchsteller = ebeguRestUtil.gesuchstellerToRestObject({}, myGesuchsteller);
                expect(restGesuchsteller).toBeDefined();
                let transformedPers: TSGesuchsteller = ebeguRestUtil.parseGesuchsteller(new TSGesuchsteller(), restGesuchsteller);
                expect(transformedPers).toBeDefined();
                expect(myGesuchsteller.nachname).toEqual(transformedPers.nachname);

                expect(transformedPers.telefon).toBeUndefined(); // der leere String wurde in undefined umgewandelt deswegen muessen wir hier undefined zurueckbekommen
                transformedPers.telefon = ''; // um das Objekt zu validieren, muessen wird das Telefon wieder auf '' setzen

                expect(myGesuchsteller).toEqual(transformedPers);

            });
        });
        describe('parseFachstelle()', () => {
            it('should transform TSFachstelle to REST object and back', () => {
                let myFachstelle = new TSFachstelle('Fachstelle_name', 'Beschreibung', true);
                TestDataUtil.setAbstractFieldsUndefined(myFachstelle);

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
        describe('parseGesuch()', () => {
            it('should transform TSGesuch to REST object and back', () => {
                let myGesuch = new TSGesuch();
                TestDataUtil.setAbstractFieldsUndefined(myGesuch);
                myGesuch.einkommensverschlechterungInfo = undefined;
                let fall: TSFall = new TSFall();
                TestDataUtil.setAbstractFieldsUndefined(fall);
                fall.nextNumberKind = 2;
                myGesuch.fall = fall;
                let gesuchsteller: TSGesuchsteller = createGesuchsteller();
                myGesuch.gesuchsteller1 = gesuchsteller;
                myGesuch.gesuchsteller2 = gesuchsteller;
                let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode();
                TestDataUtil.setAbstractFieldsUndefined(gesuchsperiode);
                gesuchsperiode.gueltigkeit = new TSDateRange(undefined, undefined);
                myGesuch.gesuchsperiode = gesuchsperiode;
                let familiensituation: TSFamiliensituation = new TSFamiliensituation();
                TestDataUtil.setAbstractFieldsUndefined(familiensituation);
                myGesuch.familiensituation = familiensituation;
                myGesuch.kindContainers = [undefined];
                myGesuch.einkommensverschlechterungInfo = undefined;  //todo createEinkommensverschlechterungInfo
                myGesuch.bemerkungen = undefined;

                let restGesuch = ebeguRestUtil.gesuchToRestObject({}, myGesuch);
                expect(restGesuch).toBeDefined();

                let transformedGesuch = ebeguRestUtil.parseGesuch(new TSGesuch(), restGesuch);
                expect(transformedGesuch).toBeDefined();
                expect(transformedGesuch).toEqual(myGesuch);
            });
        });
        describe('parseMandant()', () => {
            it('should transform TSMandant to REST object and back', () => {
                let myMandant = new TSMandant('myMandant');
                TestDataUtil.setAbstractFieldsUndefined(myMandant);

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
                TestDataUtil.setAbstractFieldsUndefined(myTraegerschaft);

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
                let instStam: TSInstitutionStammdaten = new TSInstitutionStammdaten('iban', 250, 12, TSBetreuungsangebotTyp.KITA,
                    createInstitution(), undefined, new TSDateRange(DateUtil.today(), DateUtil.today()));
                TestDataUtil.setAbstractFieldsUndefined(instStam);

                let tsBetreuungspensumGS: TSBetreuungspensum = new TSBetreuungspensum(25, new TSDateRange(DateUtil.today(), DateUtil.today()));
                TestDataUtil.setAbstractFieldsUndefined(tsBetreuungspensumGS);
                let tsBetreuungspensumJA: TSBetreuungspensum = new TSBetreuungspensum(50, new TSDateRange(DateUtil.today(), DateUtil.today()));
                TestDataUtil.setAbstractFieldsUndefined(tsBetreuungspensumJA);
                let tsBetreuungspensumContainer: TSBetreuungspensumContainer = new TSBetreuungspensumContainer(tsBetreuungspensumGS, tsBetreuungspensumJA);
                TestDataUtil.setAbstractFieldsUndefined(tsBetreuungspensumContainer);
                let betContainers: Array<TSBetreuungspensumContainer> = [tsBetreuungspensumContainer];
                let betreuung: TSBetreuung = new TSBetreuung(instStam, TSBetreuungsstatus.AUSSTEHEND, betContainers, 2);
                TestDataUtil.setAbstractFieldsUndefined(betreuung);

                let restBetreuung = ebeguRestUtil.betreuungToRestObject({}, betreuung);

                expect(restBetreuung).toBeDefined();
                expect(restBetreuung.betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                expect(restBetreuung.institutionStammdaten.iban).toEqual(betreuung.institutionStammdaten.iban);
                expect(restBetreuung.betreuungspensumContainers).toBeDefined();
                expect(restBetreuung.betreuungspensumContainers.length).toEqual(betreuung.betreuungspensumContainers.length);
                expect(restBetreuung.betreuungspensumContainers[0].betreuungspensumGS.pensum).toBe(betreuung.betreuungspensumContainers[0].betreuungspensumGS.pensum);
                expect(restBetreuung.betreuungspensumContainers[0].betreuungspensumJA.pensum).toBe(betreuung.betreuungspensumContainers[0].betreuungspensumJA.pensum);

                let transformedBetreuung: TSBetreuung = ebeguRestUtil.parseBetreuung(new TSBetreuung(), restBetreuung);

                expect(transformedBetreuung).toBeDefined();
                TestDataUtil.checkGueltigkeitAndSetIfSame(transformedBetreuung.betreuungspensumContainers[0].betreuungspensumGS, betreuung.betreuungspensumContainers[0].betreuungspensumGS);
                TestDataUtil.checkGueltigkeitAndSetIfSame(transformedBetreuung.betreuungspensumContainers[0].betreuungspensumJA, betreuung.betreuungspensumContainers[0].betreuungspensumJA);
                TestDataUtil.checkGueltigkeitAndSetIfSame(transformedBetreuung.institutionStammdaten, betreuung.institutionStammdaten);
                expect(transformedBetreuung.betreuungsstatus).toEqual(betreuung.betreuungsstatus);
                expect(transformedBetreuung.betreuungNummer).toEqual(betreuung.betreuungNummer);
                expect(transformedBetreuung.betreuungspensumContainers[0]).toEqual(betreuung.betreuungspensumContainers[0]);
            });
        });
        describe('parseBetreuungspensum', () => {
            it('should transform TSBetreuungspensum to REST object and back', () => {
                let betreuungspensum: TSBetreuungspensum = new TSBetreuungspensum(25, new TSDateRange(DateUtil.today(), DateUtil.today()));
                TestDataUtil.setAbstractFieldsUndefined(betreuungspensum);

                let restBetreuungspensum: TSBetreuungspensum = ebeguRestUtil.betreuungspensumToRestObject({}, betreuungspensum);
                expect(restBetreuungspensum).toBeDefined();
                expect(restBetreuungspensum.pensum).toEqual(betreuungspensum.pensum);

                let transformedBetreuungspensum: TSBetreuungspensum = ebeguRestUtil.parseBetreuungspensum(new TSBetreuungspensum(), restBetreuungspensum);

                expect(transformedBetreuungspensum).toBeDefined();
                TestDataUtil.checkGueltigkeitAndSetIfSame(transformedBetreuungspensum, betreuungspensum);
                expect(transformedBetreuungspensum).toEqual(betreuungspensum);
            });
        });
        describe('parseInstitutionStammdaten()', () => {
            it('should transform TSInstitutionStammdaten to REST object and back', () => {
                var myInstitution = createInstitution();
                let myInstitutionStammdaten = new TSInstitutionStammdaten('iban', 250, 12, TSBetreuungsangebotTyp.KITA, myInstitution, undefined,
                    new TSDateRange(DateUtil.today(), DateUtil.today()));
                TestDataUtil.setAbstractFieldsUndefined(myInstitutionStammdaten);

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

                TestDataUtil.checkGueltigkeitAndSetIfSame(transformedInstitutionStammdaten, myInstitutionStammdaten);
                expect(transformedInstitutionStammdaten).toEqual(myInstitutionStammdaten);
            });
        });
        describe('parseErwerbspensenContainer()', () => {
            it('should transform TSErwerbspensum to REST object and back', () => {
                var erwerbspensumContainer = TestDataUtil.createErwerbspensumContainer();
                let erwerbspensumJA = erwerbspensumContainer.erwerbspensumJA;

                let restErwerbspensum = ebeguRestUtil.erwerbspensumToRestObject({}, erwerbspensumContainer.erwerbspensumJA);
                expect(restErwerbspensum).toBeDefined();
                expect(restErwerbspensum.taetigkeit).toEqual(erwerbspensumJA.taetigkeit);
                expect(restErwerbspensum.pensum).toEqual(erwerbspensumJA.pensum);
                expect(restErwerbspensum.gueltigAb).toEqual(DateUtil.momentToLocalDate(erwerbspensumJA.gueltigkeit.gueltigAb));
                expect(restErwerbspensum.gueltigBis).toEqual(DateUtil.momentToLocalDate(erwerbspensumJA.gueltigkeit.gueltigBis));
                expect(restErwerbspensum.zuschlagZuErwerbspensum).toEqual(erwerbspensumJA.zuschlagZuErwerbspensum);
                expect(restErwerbspensum.zuschlagsprozent).toEqual(erwerbspensumJA.zuschlagsprozent);
                expect(restErwerbspensum.zuschlagsgrund).toEqual(erwerbspensumJA.zuschlagsgrund);

                let transformedErwerbspensum = ebeguRestUtil.parseErwerbspensum(new TSErwerbspensum(), restErwerbspensum);

                TestDataUtil.checkGueltigkeitAndSetIfSame(transformedErwerbspensum, erwerbspensumJA);
                expect(transformedErwerbspensum).toEqual(erwerbspensumJA);
            });
        });
        describe('parseGesuchsperiode()', () => {
            it('should transfrom TSGesuchsperiode to REST Obj and back', () => {
                let myGesuchsperiode = new TSGesuchsperiode(true, new TSDateRange(undefined, undefined));
                TestDataUtil.setAbstractFieldsUndefined(myGesuchsperiode);

                let restGesuchsperiode = ebeguRestUtil.gesuchsperiodeToRestObject({}, myGesuchsperiode);
                expect(restGesuchsperiode).toBeDefined();

                let transformedGesuchsperiode: TSGesuchsperiode = ebeguRestUtil.parseGesuchsperiode(new TSGesuchsperiode(), restGesuchsperiode);
                expect(transformedGesuchsperiode).toBeDefined();
                expect(myGesuchsperiode.active).toBe(true);
                expect(myGesuchsperiode).toEqual(transformedGesuchsperiode);

            });
        });
        describe('parseAntragDTO()', () => {
            it('should transform TSAntragDTO to REST Obj and back', () => {
                let tsGesuchsperiode = new TSGesuchsperiode(true, new TSDateRange(undefined, undefined));
                TestDataUtil.setAbstractFieldsUndefined(tsGesuchsperiode);
                let myPendenz = new TSAntragDTO('id1', 123, 'name', TSAntragTyp.GESUCH, tsGesuchsperiode,
                    DateUtil.today(), DateUtil.now(), [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado');

                let restPendenz = ebeguRestUtil.antragDTOToRestObject({}, myPendenz);
                expect(restPendenz).toBeDefined();

                let transformedPendenz = ebeguRestUtil.parseAntragDTO(new TSAntragDTO(), restPendenz);
                expect(transformedPendenz).toBeDefined();
                expect(transformedPendenz.eingangsdatum.isSame(myPendenz.eingangsdatum)).toBe(true);
                transformedPendenz.eingangsdatum = myPendenz.eingangsdatum;
                expect(transformedPendenz.aenderungsdatum.isSame(myPendenz.aenderungsdatum)).toBe(true);
                transformedPendenz.aenderungsdatum = myPendenz.aenderungsdatum;
                expect(transformedPendenz).toEqual(myPendenz);
            });
        });
        describe('parseVerfuegung()', () => {
            it('should transform a REST Verfuegung to TS Obj', () => {
                let restVerfuegung: any = {};
                restVerfuegung.generatedBemerkungen = 'generated';
                restVerfuegung.manuelleBemerkungen = 'manuell';
                restVerfuegung.zeitabschnitte = {};

                let verfuegungTS = ebeguRestUtil.parseVerfuegung(new TSVerfuegung(), restVerfuegung);

                expect(verfuegungTS).toBeDefined();
                expect(verfuegungTS.generatedBemerkungen).toEqual(restVerfuegung.generatedBemerkungen);
                expect(verfuegungTS.manuelleBemerkungen).toEqual(restVerfuegung.manuelleBemerkungen);
                expect(verfuegungTS.zeitabschnitte).toBeDefined();
            });
        });
        describe('parseVerfuegungZeitabschnitt()', () => {
            it('should transform a REST VerfuegungZeitabschnitt to TS Obj', () => {
                let restVerfuegungZeitabschnitt: any = {};
                restVerfuegungZeitabschnitt.abzugFamGroesse = 1;
                restVerfuegungZeitabschnitt.anspruchberechtigtesPensum = 2;
                restVerfuegungZeitabschnitt.anspruchspensumRest = 3;
                restVerfuegungZeitabschnitt.betreuungspensum = 5;
                restVerfuegungZeitabschnitt.betreuungsstunden = 6;
                restVerfuegungZeitabschnitt.elternbeitrag = 7;
                restVerfuegungZeitabschnitt.erwerbspensumGS1 = 8;
                restVerfuegungZeitabschnitt.erwerbspensumGS2 = 9;
                restVerfuegungZeitabschnitt.fachstellenpensum = 10;
                restVerfuegungZeitabschnitt.massgebendesEinkommenVorAbzugFamgr = 11;
                restVerfuegungZeitabschnitt.vollkosten = 12;
                restVerfuegungZeitabschnitt.bemerkungen = 'bemerkung1';
                restVerfuegungZeitabschnitt.status = 'status1';

                let verfuegungTS = ebeguRestUtil.parseVerfuegungZeitabschnitt(new TSVerfuegungZeitabschnitt(), restVerfuegungZeitabschnitt);

                expect(verfuegungTS).toBeDefined();
                expect(verfuegungTS.abzugFamGroesse).toEqual(restVerfuegungZeitabschnitt.abzugFamGroesse);
                expect(verfuegungTS.anspruchberechtigtesPensum).toEqual(restVerfuegungZeitabschnitt.anspruchberechtigtesPensum);
                expect(verfuegungTS.anspruchspensumRest).toEqual(restVerfuegungZeitabschnitt.anspruchspensumRest);
                expect(verfuegungTS.betreuungspensum).toEqual(restVerfuegungZeitabschnitt.betreuungspensum);
                expect(verfuegungTS.betreuungsstunden).toEqual(restVerfuegungZeitabschnitt.betreuungsstunden);
                expect(verfuegungTS.elternbeitrag).toEqual(restVerfuegungZeitabschnitt.elternbeitrag);
                expect(verfuegungTS.erwerbspensumGS1).toEqual(restVerfuegungZeitabschnitt.erwerbspensumGS1);
                expect(verfuegungTS.erwerbspensumGS2).toEqual(restVerfuegungZeitabschnitt.erwerbspensumGS2);
                expect(verfuegungTS.fachstellenpensum).toEqual(restVerfuegungZeitabschnitt.fachstellenpensum);
                expect(verfuegungTS.massgebendesEinkommenVorAbzugFamgr).toEqual(restVerfuegungZeitabschnitt.massgebendesEinkommenVorAbzugFamgr);
                expect(verfuegungTS.vollkosten).toEqual(restVerfuegungZeitabschnitt.vollkosten);
                expect(verfuegungTS.bemerkungen).toEqual(restVerfuegungZeitabschnitt.bemerkungen);
                expect(verfuegungTS.status).toEqual(restVerfuegungZeitabschnitt.status);
            });
        });
    });

    function createInstitution(): TSInstitution {
        let traegerschaft = new TSTraegerschaft('myTraegerschaft');
        TestDataUtil.setAbstractFieldsUndefined(traegerschaft);
        let mandant = new TSMandant('myMandant');
        TestDataUtil.setAbstractFieldsUndefined(mandant);
        let myInstitution = new TSInstitution('myInstitution', traegerschaft, mandant);
        TestDataUtil.setAbstractFieldsUndefined(myInstitution);
        return myInstitution;
    }

    function createGesuchsteller(): TSGesuchsteller {
        let myGesuchsteller = new TSGesuchsteller();
        myGesuchsteller.vorname = 'Til';
        myGesuchsteller.nachname = 'TestGesuchsteller';
        myGesuchsteller.id = 'mytestid';
        myGesuchsteller.timestampErstellt = undefined;
        myGesuchsteller.timestampMutiert = undefined;
        myGesuchsteller.geschlecht = TSGeschlecht.MAENNLICH;
        myGesuchsteller.telefon = '+41 76 300 12 34';
        myGesuchsteller.mobile = '+41 76 300 12 34';
        myGesuchsteller.mail = 'Til.Testgesuchsteller@example.com';
        myGesuchsteller.korrespondenzAdresse = undefined;
        myGesuchsteller.umzugAdresse = undefined;
        myGesuchsteller.adresse = undefined;
        myGesuchsteller.finanzielleSituationContainer = undefined;
        myGesuchsteller.einkommensverschlechterungContainer = undefined;
        return myGesuchsteller;
    }
});
