import TSErwerbspensumContainer from '../models/TSErwerbspensumContainer';
import TSErwerbspensum from '../models/TSErwerbspensum';
import {TSTaetigkeit} from '../models/enums/TSTaetigkeit';
import DateUtil from './DateUtil';
import {IHttpBackendService} from 'angular';
import {TSDateRange} from '../models/types/TSDateRange';
import {TSZuschlagsgrund} from '../models/enums/TSZuschlagsgrund';
import TSAbstractEntity from '../models/TSAbstractEntity';
import {TSAbstractDateRangedEntity} from '../models/TSAbstractDateRangedEntity';
import TSWizardStep from '../models/TSWizardStep';
import {TSWizardStepStatus} from '../models/enums/TSWizardStepStatus';
import {TSWizardStepName} from '../models/enums/TSWizardStepName';
import TSVerfuegung from '../models/TSVerfuegung';
import * as moment from 'moment';
import TSGesuchsperiode from '../models/TSGesuchsperiode';
import TSAntragDTO from '../models/TSAntragDTO';
import {TSAntragTyp} from '../models/enums/TSAntragTyp';
import TSGesuchsteller from '../models/TSGesuchsteller';
import TSAdresse from '../models/TSAdresse';
import TSGesuchstellerContainer from '../models/TSGesuchstellerContainer';
import TSAdresseContainer from '../models/TSAdresseContainer';
import Moment = moment.Moment;
import {TSGesuchsperiodeStatus} from '../models/enums/TSGesuchsperiodeStatus';

export default class TestDataUtil {


    public static setAbstractFieldsUndefined(abstractEntity: TSAbstractEntity) {
        abstractEntity.id = undefined;
        abstractEntity.timestampErstellt = undefined;
        abstractEntity.timestampMutiert = undefined;
        abstractEntity.vorgaengerId = undefined;
    }

    /**
     * @param {string} localDateTimeString string with format YYYY-MM-DDTHH:mm:ss.SSS
     * @returns {?Moment}
     */
    public static createErwerbspensumContainer(): TSErwerbspensumContainer {

        let dummyErwerbspensumContainer: TSErwerbspensumContainer = new TSErwerbspensumContainer();
        dummyErwerbspensumContainer.erwerbspensumGS = this.createErwerbspensum();
        dummyErwerbspensumContainer.erwerbspensumJA = this.createErwerbspensum();
        this.setAbstractFieldsUndefined(dummyErwerbspensumContainer);
        return dummyErwerbspensumContainer;
    }

    static createErwerbspensum(): TSErwerbspensum {
        let dummyErwerbspensum = new TSErwerbspensum();
        dummyErwerbspensum.taetigkeit = TSTaetigkeit.ANGESTELLT;
        dummyErwerbspensum.pensum = 80;
        dummyErwerbspensum.gueltigkeit = new TSDateRange(DateUtil.today(), DateUtil.today().add(7, 'months'));
        dummyErwerbspensum.zuschlagZuErwerbspensum = true;
        dummyErwerbspensum.zuschlagsprozent = 20;
        dummyErwerbspensum.zuschlagsgrund = TSZuschlagsgrund.FIXE_ARBEITSZEITEN;
        dummyErwerbspensum.bezeichnung = undefined;
        this.setAbstractFieldsUndefined(dummyErwerbspensum);
        return dummyErwerbspensum;
    }

    static checkGueltigkeitAndSetIfSame(first: TSAbstractDateRangedEntity, second: TSAbstractDateRangedEntity) {
        // Dieses hack wird gebraucht weil um 2 Moment zu vergleichen kann man nicht einfach equal() benutzen sondern isSame
        expect(first.gueltigkeit.gueltigAb.isSame(second.gueltigkeit.gueltigAb)).toBe(true);
        expect(first.gueltigkeit.gueltigBis.isSame(second.gueltigkeit.gueltigBis)).toBe(true);
        first.gueltigkeit.gueltigAb = second.gueltigkeit.gueltigAb;
        first.gueltigkeit.gueltigBis = second.gueltigkeit.gueltigBis;
    }


    static mockDefaultGesuchModelManagerHttpCalls($httpBackend: IHttpBackendService) {

        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/0621fb5d-a187-5a91-abaf-8a813c4d263a').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/wizard-steps').respond({});
        $httpBackend.when('POST', '/ebegu/api/v1/wizard-steps').respond({});
    }

    public static mockLazyGesuchModelManagerHttpCalls($httpBackend: IHttpBackendService) {
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/fachstellen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/institutionstammdaten/date/active?date=' + DateUtil.momentToLocalDate(DateUtil.today())).respond({});
    }

    public static createWizardStep(gesuchId: string): TSWizardStep {
        let wizardStep: TSWizardStep = new TSWizardStep();
        TestDataUtil.setAbstractFieldsUndefined(wizardStep);
        wizardStep.gesuchId = gesuchId;
        wizardStep.bemerkungen = 'bemerkung';
        wizardStep.wizardStepStatus = TSWizardStepStatus.IN_BEARBEITUNG;
        wizardStep.wizardStepName = TSWizardStepName.BETREUUNG;
        return wizardStep;
    }

    public static createVerfuegung(): TSVerfuegung {
        let verfuegung: TSVerfuegung = new TSVerfuegung();
        TestDataUtil.setAbstractFieldsUndefined(verfuegung);
        verfuegung.id = '123321';
        verfuegung.zeitabschnitte = [];
        return verfuegung;
    }

    public static createGesuchsperiode20162017(): TSGesuchsperiode {
        let gueltigkeit: TSDateRange = new TSDateRange(moment('01.07.2016', 'DD.MM.YYYY'), moment('31.08.2017', 'DD.MM.YYYY'));
        return new TSGesuchsperiode(TSGesuchsperiodeStatus.AKTIV, gueltigkeit);
    }

    public static createTSAntragDTO(antragTyp: TSAntragTyp, eingangsdatum: Moment): TSAntragDTO {
        let antrag: TSAntragDTO = new TSAntragDTO();
        antrag.verfuegt = true;
        antrag.antragTyp = antragTyp;
        antrag.eingangsdatum = eingangsdatum;
        let gesuchsperiode: TSGesuchsperiode = TestDataUtil.createGesuchsperiode20162017();
        antrag.gesuchsperiodeGueltigAb = gesuchsperiode.gueltigkeit.gueltigAb;
        antrag.gesuchsperiodeGueltigBis = gesuchsperiode.gueltigkeit.gueltigBis;
        return antrag;
    }

    public static createGesuchsteller(vorname: string, nachname: string): TSGesuchstellerContainer {
        let gesuchstellerCont: TSGesuchstellerContainer = new TSGesuchstellerContainer();
        let gesuchsteller: TSGesuchsteller = new TSGesuchsteller();
        gesuchsteller.vorname = vorname;
        gesuchsteller.nachname = nachname;
        gesuchstellerCont.gesuchstellerJA = gesuchsteller;
        gesuchstellerCont.adressen = [];
        return gesuchstellerCont;
    }

    public static createAdresse(strasse: string, nummer: string): TSAdresseContainer {
        let adresseCont: TSAdresseContainer = new TSAdresseContainer();
        let adresse: TSAdresse = new TSAdresse();
        adresse.strasse = strasse;
        adresse.hausnummer = nummer;
        adresse.gueltigkeit = TestDataUtil.createGesuchsperiode20162017().gueltigkeit;
        adresseCont.showDatumVon = true;
        adresseCont.adresseJA = adresse;
        return adresseCont;
    }

    public static createDummyForm(): any {
        let form: any = {};
        form.$valid = true;
        form.$setPristine = () => {};
        form.$setUntouched = () => {};
        return form;
    }

    public static createValidationReport(): any {
        return {
            status: 400,
            data: {
                parameterViolations: [],
                classViolations: [],
                fieldViolations: [],
                propertyViolations: [{
                    constraintType: 'PARAMETER',
                    path: 'markAsRead.arg1',
                    message: 'Die Länge des Feldes muss zwischen 36 und 36 sein',
                    value: '8a146418-ab12-456f-9b17-aad6990f51'
                }],
                returnValueViolations: []
            }
        };
    }

    public static createExceptionReport(): any {
        return {
            status: 500,
            data: {
                errorCodeEnum: 'ERROR_ENTITY_NOT_FOUND',
                exceptionName: 'EbeguRuntimeException',
                methodName: 'doTest',
                stackTrace: null,
                translatedMessage: '',
                customMessage: 'test',
                objectId: '44-55-66-77',
                argumentList: null,
            }
        };
    }
}
