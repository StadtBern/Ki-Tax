import TSApplicationProperty from '../models/TSApplicationProperty';
import TSAbstractEntity from '../models/TSAbstractEntity';
import TSAdresse from '../models/TSAdresse';
import {TSAdressetyp} from '../models/enums/TSAdressetyp';
import TSGesuchsteller from '../models/TSGesuchsteller';
import TSGesuch from '../models/TSGesuch';
import TSFall from '../models/TSFall';
import DateUtil from './DateUtil';
import TSLand from '../models/types/TSLand';
import TSFamiliensituation from '../models/TSFamiliensituation';
import {TSFachstelle} from '../models/TSFachstelle';
import TSFinanzielleSituation from '../models/TSFinanzielleSituation';
import TSFinanzielleSituationContainer from '../models/TSFinanzielleSituationContainer';
import {TSMandant} from '../models/TSMandant';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
import TSInstitution from '../models/TSInstitution';
import TSInstitutionStammdaten from '../models/TSInstitutionStammdaten';
import {TSDateRange} from '../models/types/TSDateRange';
import {TSAbstractDateRangedEntity} from '../models/TSAbstractDateRangedEntity';
import TSKind from '../models/TSKind';
import TSAbstractPersonEntity from '../models/TSAbstractPersonEntity';
import {TSPensumFachstelle} from '../models/TSPensumFachstelle';
import TSErwerbspensumContainer from '../models/TSErwerbspensumContainer';
import TSErwerbspensum from '../models/TSErwerbspensum';
import {TSAbstractPensumEntity} from '../models/TSAbstractPensumEntity';
import TSFinanzielleSituationResultateDTO from '../models/dto/TSFinanzielleSituationResultateDTO';
import TSBetreuung from '../models/TSBetreuung';
import TSBetreuungspensumContainer from '../models/TSBetreuungspensumContainer';
import TSBetreuungspensum from '../models/TSBetreuungspensum';
import TSEbeguParameter from '../models/TSEbeguParameter';
import TSGesuchsperiode from '../models/TSGesuchsperiode';
import TSAbstractAntragEntity from '../models/TSAbstractAntragEntity';
import TSPendenzJA from '../models/TSPendenzJA';
import EbeguUtil from './EbeguUtil';
import TSKindContainer from '../models/TSKindContainer';
import TSUser from '../models/TSUser';

export default class EbeguRestUtil {
    static $inject = ['EbeguUtil'];

    /* @ngInject */
    constructor(private ebeguUtil: EbeguUtil) {
    }

    /**
     * Wandelt Data in einen TSApplicationProperty Array um, welches danach zurueckgeliefert wird
     * @param data
     * @returns {TSApplicationProperty[]}
     */
    public parseApplicationProperties(data: any): TSApplicationProperty[] {
        var appProperties: TSApplicationProperty[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                appProperties[i] = this.parseApplicationProperty(new TSApplicationProperty('', ''), data[i]);
            }
        } else {
            appProperties[0] = this.parseApplicationProperty(new TSApplicationProperty('', ''), data);
        }
        return appProperties;
    }

    /**
     * Wandelt die receivedAppProperty in einem parsedAppProperty um.
     * @param parsedAppProperty
     * @param receivedAppProperty
     * @returns {TSApplicationProperty}
     */
    public parseApplicationProperty(parsedAppProperty: TSApplicationProperty, receivedAppProperty: any): TSApplicationProperty {
        this.parseAbstractEntity(parsedAppProperty, receivedAppProperty);
        parsedAppProperty.name = receivedAppProperty.name;
        parsedAppProperty.value = receivedAppProperty.value;
        return parsedAppProperty;
    }

    public parseEbeguParameters(data: any): TSEbeguParameter[] {
        var ebeguParameters: TSEbeguParameter[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                ebeguParameters[i] = this.parseEbeguParameter(new TSEbeguParameter(), data[i]);
            }
        } else {
            ebeguParameters[0] = this.parseEbeguParameter(new TSEbeguParameter(), data);
        }
        return ebeguParameters;
    }

    public parseEbeguParameter(ebeguParameterTS: TSEbeguParameter, receivedEbeguParameter: any): TSEbeguParameter {
        if (receivedEbeguParameter) {
            this.parseDateRangeEntity(ebeguParameterTS, receivedEbeguParameter);
            ebeguParameterTS.name = receivedEbeguParameter.name;
            ebeguParameterTS.value = receivedEbeguParameter.value;
            ebeguParameterTS.proGesuchsperiode = receivedEbeguParameter.proGesuchsperiode;
            return ebeguParameterTS;
        }
        return undefined;
    }

    public ebeguParameterToRestObject(restEbeguParameter: any, ebeguParameter: TSEbeguParameter): TSEbeguParameter {
        if (ebeguParameter) {
            this.abstractDateRangeEntityToRestObject(restEbeguParameter, ebeguParameter);
            restEbeguParameter.name = ebeguParameter.name;
            restEbeguParameter.value = ebeguParameter.value;
            return restEbeguParameter;
        }
        return undefined;
    }

    private parseAbstractEntity(parsedAbstractEntity: TSAbstractEntity, receivedAbstractEntity: any): void {
        parsedAbstractEntity.id = receivedAbstractEntity.id;
        parsedAbstractEntity.timestampErstellt = DateUtil.localDateTimeToMoment(receivedAbstractEntity.timestampErstellt);
        parsedAbstractEntity.timestampMutiert = DateUtil.localDateTimeToMoment(receivedAbstractEntity.timestampMutiert);
    }

    private abstractEntityToRestObject(restObject: any, typescriptObject: TSAbstractEntity) {
        restObject.id = typescriptObject.id;
        if (typescriptObject.timestampErstellt) {
            restObject.timestampErstellt = DateUtil.momentToLocalDateTime(typescriptObject.timestampErstellt);
        }
        if (typescriptObject.timestampMutiert) {
            restObject.timestampMutiert = DateUtil.momentToLocalDateTime(typescriptObject.timestampMutiert);
        }
    }

    private parseAbstractPersonEntity(personObjectTS: TSAbstractPersonEntity, receivedPersonObject: any): void {
        this.parseAbstractEntity(personObjectTS, receivedPersonObject);
        personObjectTS.vorname = receivedPersonObject.vorname;
        personObjectTS.nachname = receivedPersonObject.nachname;
        personObjectTS.geburtsdatum = DateUtil.localDateToMoment(receivedPersonObject.geburtsdatum);
        personObjectTS.geschlecht = receivedPersonObject.geschlecht;
    }

    private abstractPersonEntitytoRestObject(restPersonObject: any, personObject: TSAbstractPersonEntity): void {
        this.abstractEntityToRestObject(restPersonObject, personObject);
        restPersonObject.vorname = personObject.vorname;
        restPersonObject.nachname = personObject.nachname;
        restPersonObject.geburtsdatum = DateUtil.momentToLocalDate(personObject.geburtsdatum);
        restPersonObject.geschlecht = personObject.geschlecht;
    }

    private abstractDateRangeEntityToRestObject(restObj: any, dateRangedEntity: TSAbstractDateRangedEntity) {
        this.abstractEntityToRestObject(restObj, dateRangedEntity);
        if (dateRangedEntity && dateRangedEntity.gueltigkeit) {
            restObj.gueltigAb = DateUtil.momentToLocalDate(dateRangedEntity.gueltigkeit.gueltigAb);
            restObj.gueltigBis = DateUtil.momentToLocalDate(dateRangedEntity.gueltigkeit.gueltigBis);
        }
    }

    private parseDateRangeEntity(parsedObject: TSAbstractDateRangedEntity, receivedAppProperty: any) {
        this.parseAbstractEntity(parsedObject, receivedAppProperty);
        parsedObject.gueltigkeit = new TSDateRange(DateUtil.localDateToMoment(receivedAppProperty.gueltigAb), DateUtil.localDateToMoment(receivedAppProperty.gueltigBis));
    }

    private abstractPensumEntityToRestObject(restObj: any, pensumEntity: TSAbstractPensumEntity) {
        this.abstractDateRangeEntityToRestObject(restObj, pensumEntity);
        restObj.pensum = pensumEntity.pensum;
    }

    private parseAbstractPensumEntity(betreuungspensumTS: TSAbstractPensumEntity, betreuungspensumFromServer: any) {
        this.parseDateRangeEntity(betreuungspensumTS, betreuungspensumFromServer);
        betreuungspensumTS.pensum = betreuungspensumFromServer.pensum;
    }

    private abstractAntragEntityToRestObject(restObj: any, antragEntity: TSAbstractAntragEntity) {
        this.abstractEntityToRestObject(restObj, antragEntity);
        restObj.fall = this.fallToRestObject({}, antragEntity.fall);
        restObj.gesuchsperiode = this.gesuchsperiodeToRestObject({}, antragEntity.gesuchsperiode);
        restObj.eingangsdatum = DateUtil.momentToLocalDate(antragEntity.eingangsdatum);
    }

    private parseAbstractAntragEntity(antragTS: TSAbstractAntragEntity, antragFromServer: any) {
        this.parseAbstractEntity(antragTS, antragFromServer);
        antragTS.fall = this.parseFall(new TSFall(), antragFromServer.fall);
        antragTS.gesuchsperiode = this.parseGesuchsperiode(new TSGesuchsperiode(), antragFromServer.gesuchsperiode);
        antragTS.eingangsdatum = DateUtil.localDateToMoment(antragFromServer.eingangsdatum);
    }

    public adresseToRestObject(restAdresse: any, adresse: TSAdresse): TSAdresse {
        if (adresse) {
            this.abstractDateRangeEntityToRestObject(restAdresse, adresse);
            restAdresse.strasse = adresse.strasse;
            restAdresse.hausnummer = adresse.hausnummer;
            restAdresse.zusatzzeile = adresse.zusatzzeile;
            restAdresse.plz = adresse.plz;
            restAdresse.ort = adresse.ort;
            restAdresse.land = adresse.land;
            restAdresse.gemeinde = adresse.gemeinde;
            restAdresse.adresseTyp = TSAdressetyp[adresse.adresseTyp];
            return restAdresse;
        }
        return undefined;

    }

    public parseAdresse(adresseTS: TSAdresse, receivedAdresse: any): TSAdresse {
        if (receivedAdresse) {
            this.parseDateRangeEntity(adresseTS, receivedAdresse);
            adresseTS.strasse = receivedAdresse.strasse;
            adresseTS.hausnummer = receivedAdresse.hausnummer;
            adresseTS.zusatzzeile = receivedAdresse.zusatzzeile;
            adresseTS.plz = receivedAdresse.plz;
            adresseTS.ort = receivedAdresse.ort;
            adresseTS.land = (this.landCodeToTSLand(receivedAdresse.land)) ? this.landCodeToTSLand(receivedAdresse.land).code : undefined;
            adresseTS.gemeinde = receivedAdresse.gemeinde;
            adresseTS.adresseTyp = receivedAdresse.adresseTyp;
            return adresseTS;
        }
        return undefined;
    }

    /**
     * Nimmt den eingegebenen Code und erzeugt ein TSLand Objekt mit dem Code und
     * seine Uebersetzung.
     * @param landCode
     * @returns {any}
     */
    public landCodeToTSLand(landCode: string): TSLand {
        if (landCode) {
            let translationKey = this.landCodeToTSLandCode(landCode);
            return new TSLand(landCode, this.ebeguUtil.translateString(translationKey));
        }
        return undefined;
    }

    /**
     * Fügt das 'Land_' dem eingegebenen Landcode hinzu.
     * @param landCode
     * @returns {any}
     */
    public landCodeToTSLandCode(landCode: string): string {
        if (landCode) {
            if (landCode.lastIndexOf('Land_', 0) !== 0) {
                return 'Land_' + landCode;
            }
        }
        return undefined;
    }


    public gesuchstellerToRestObject(restGesuchsteller: any, gesuchsteller: TSGesuchsteller): any {
        if (gesuchsteller) {
            this.abstractPersonEntitytoRestObject(restGesuchsteller, gesuchsteller);
            restGesuchsteller.mail = gesuchsteller.mail;
            restGesuchsteller.mobile = gesuchsteller.mobile;
            restGesuchsteller.telefon = gesuchsteller.telefon;
            restGesuchsteller.telefonAusland = gesuchsteller.telefonAusland;
            restGesuchsteller.umzug = gesuchsteller.umzug;
            restGesuchsteller.wohnAdresse = this.adresseToRestObject({}, gesuchsteller.adresse); //achtung heisst im jax wohnadresse nicht adresse
            restGesuchsteller.alternativeAdresse = this.adresseToRestObject({}, gesuchsteller.korrespondenzAdresse);
            restGesuchsteller.umzugAdresse = this.adresseToRestObject({}, gesuchsteller.umzugAdresse);
            if (gesuchsteller.finanzielleSituationContainer) {
                restGesuchsteller.finanzielleSituationContainer = this.finanzielleSituationContainerToRestObject({}, gesuchsteller.finanzielleSituationContainer);
            }
            if (gesuchsteller.erwerbspensenContainer) {
                let erwPensenCont: Array<any> = [];
                for (var i = 0; i < gesuchsteller.erwerbspensenContainer.length; i++) {
                    erwPensenCont.push(this.erwerbspensumContainerToRestObject({}, gesuchsteller.erwerbspensenContainer[i]));
                }
                restGesuchsteller.erwerbspensenContainers = erwPensenCont;

            }
            restGesuchsteller.diplomatenstatus = gesuchsteller.diplomatenstatus;
            return restGesuchsteller;
        }
        return undefined;
    }

    public parseGesuchsteller(gesuchstellerTS: TSGesuchsteller, gesuchstellerFromServer: any): TSGesuchsteller {
        if (gesuchstellerFromServer) {
            this.parseAbstractPersonEntity(gesuchstellerTS, gesuchstellerFromServer);
            gesuchstellerTS.mail = gesuchstellerFromServer.mail;
            gesuchstellerTS.mobile = gesuchstellerFromServer.mobile;
            gesuchstellerTS.telefon = gesuchstellerFromServer.telefon;
            gesuchstellerTS.telefonAusland = gesuchstellerFromServer.telefonAusland;
            gesuchstellerTS.umzug = gesuchstellerFromServer.umzug;
            gesuchstellerTS.adresse = this.parseAdresse(new TSAdresse(), gesuchstellerFromServer.wohnAdresse);
            gesuchstellerTS.korrespondenzAdresse = this.parseAdresse(new TSAdresse(), gesuchstellerFromServer.alternativeAdresse);
            gesuchstellerTS.umzugAdresse = this.parseAdresse(new TSAdresse(), gesuchstellerFromServer.umzugAdresse);
            gesuchstellerTS.finanzielleSituationContainer = this.parseFinanzielleSituationContainer(new TSFinanzielleSituationContainer(), gesuchstellerFromServer.finanzielleSituationContainer);
            gesuchstellerTS.erwerbspensenContainer = this.parseErwerbspensenContainers(gesuchstellerFromServer.erwerbspensenContainers);
            gesuchstellerTS.diplomatenstatus = gesuchstellerFromServer.diplomatenstatus;
            return gesuchstellerTS;
        }
        return undefined;

    }

    public parseErwerbspensumContainer(erwerbspensumContainer: TSErwerbspensumContainer, ewpContFromServer: any): TSErwerbspensumContainer {
        if (ewpContFromServer) {
            this.parseAbstractEntity(erwerbspensumContainer, ewpContFromServer);
            erwerbspensumContainer.erwerbspensumGS = this.parseErwerbspensum(erwerbspensumContainer.erwerbspensumGS || new TSErwerbspensum(), ewpContFromServer.erwerbspensumGS);
            erwerbspensumContainer.erwerbspensumJA = this.parseErwerbspensum(erwerbspensumContainer.erwerbspensumJA || new TSErwerbspensum(), ewpContFromServer.erwerbspensumJA);
            return erwerbspensumContainer;
        }
        return undefined;
    }

    public erwerbspensumContainerToRestObject(restEwpContainer: any, erwerbspensumContainer: TSErwerbspensumContainer): any {
        if (erwerbspensumContainer) {
            this.abstractEntityToRestObject(restEwpContainer, erwerbspensumContainer);
            restEwpContainer.erwerbspensumGS = this.erwerbspensumToRestObject({}, erwerbspensumContainer.erwerbspensumGS);
            restEwpContainer.erwerbspensumJA = this.erwerbspensumToRestObject({}, erwerbspensumContainer.erwerbspensumJA);
            return restEwpContainer;
        }
        return undefined;
    }

    public parseErwerbspensum(erwerbspensum: TSErwerbspensum, erwerbspensumFromServer: any): TSErwerbspensum {
        if (erwerbspensumFromServer) {
            this.parseAbstractPensumEntity(erwerbspensum, erwerbspensumFromServer);
            erwerbspensum.gesundheitlicheEinschraenkungen = erwerbspensumFromServer.gesundheitlicheEinschraenkungen;
            erwerbspensum.taetigkeit = erwerbspensumFromServer.taetigkeit;
            erwerbspensum.zuschlagsgrund = erwerbspensumFromServer.zuschlagsgrund;
            erwerbspensum.zuschlagsprozent = erwerbspensumFromServer.zuschlagsprozent;
            erwerbspensum.zuschlagZuErwerbspensum = erwerbspensumFromServer.zuschlagZuErwerbspensum;
            return erwerbspensum;
        } else {
            return undefined;
        }
    }

    public erwerbspensumToRestObject(restErwerbspensum: any, erwerbspensum: TSErwerbspensum): any {
        if (erwerbspensum) {
            this.abstractPensumEntityToRestObject(restErwerbspensum, erwerbspensum);
            restErwerbspensum.gesundheitlicheEinschraenkungen = erwerbspensum.gesundheitlicheEinschraenkungen;
            restErwerbspensum.taetigkeit = erwerbspensum.taetigkeit;
            restErwerbspensum.zuschlagsgrund = erwerbspensum.zuschlagsgrund;
            restErwerbspensum.zuschlagsprozent = erwerbspensum.zuschlagsprozent;
            restErwerbspensum.zuschlagZuErwerbspensum = erwerbspensum.zuschlagZuErwerbspensum;
            return restErwerbspensum;
        }
        return undefined;
    }

    public familiensituationToRestObject(restFamiliensituation: any, familiensituation: TSFamiliensituation): TSFamiliensituation {
        if (familiensituation) {
            this.abstractEntityToRestObject(restFamiliensituation, familiensituation);
            restFamiliensituation.familienstatus = familiensituation.familienstatus;
            restFamiliensituation.gesuchstellerKardinalitaet = familiensituation.gesuchstellerKardinalitaet;
            restFamiliensituation.bemerkungen = familiensituation.bemerkungen;
            restFamiliensituation.gemeinsameSteuererklaerung = familiensituation.gemeinsameSteuererklaerung;

            return restFamiliensituation;
        }
        return undefined;
    }

    public parseFamiliensituation(familiensituation: TSFamiliensituation, familiensituationFromServer: any): TSFamiliensituation {
        if (familiensituationFromServer) {
            this.parseAbstractEntity(familiensituation, familiensituationFromServer);
            familiensituation.bemerkungen = familiensituationFromServer.bemerkungen;
            familiensituation.familienstatus = familiensituationFromServer.familienstatus;
            familiensituation.gesuchstellerKardinalitaet = familiensituationFromServer.gesuchstellerKardinalitaet;
            familiensituation.gemeinsameSteuererklaerung = familiensituationFromServer.gemeinsameSteuererklaerung;
            return familiensituation;
        }
        return undefined;
    }

    public fallToRestObject(restFall: any, fall: TSFall): TSFall {
        if (fall) {
            this.abstractEntityToRestObject(restFall, fall);
            restFall.fallNummer = fall.fallNummer;
            restFall.verantwortlicher = this.userToRestObject({}, fall.verantwortlicher);
            return restFall;
        }
        return undefined;

    }

    public parseFall(fallTS: TSFall, fallFromServer: any): TSFall {
        if (fallFromServer) {
            this.parseAbstractEntity(fallTS, fallFromServer);
            fallTS.fallNummer = fallFromServer.fallNummer;
            fallTS.verantwortlicher = this.parseUser(new TSUser(), fallFromServer.verantwortlicher);
            return fallTS;
        }
        return undefined;
    }


    public gesuchToRestObject(restGesuch: any, gesuch: TSGesuch): TSGesuch {
        this.abstractAntragEntityToRestObject(restGesuch, gesuch);
        restGesuch.einkommensverschlechterung = gesuch.einkommensverschlechterung;
        restGesuch.gesuchsteller1 = this.gesuchstellerToRestObject({}, gesuch.gesuchsteller1);
        restGesuch.gesuchsteller2 = this.gesuchstellerToRestObject({}, gesuch.gesuchsteller2);
        restGesuch.familiensituation = this.familiensituationToRestObject({}, gesuch.familiensituation);
        return restGesuch;
    }

    public parseGesuch(gesuchTS: TSGesuch, gesuchFromServer: any): TSGesuch {
        if (gesuchFromServer) {
            this.parseAbstractAntragEntity(gesuchTS, gesuchFromServer);
            gesuchTS.einkommensverschlechterung = gesuchFromServer.einkommensverschlechterung;
            gesuchTS.gesuchsteller1 = this.parseGesuchsteller(new TSGesuchsteller(), gesuchFromServer.gesuchsteller1);
            gesuchTS.gesuchsteller2 = this.parseGesuchsteller(new TSGesuchsteller(), gesuchFromServer.gesuchsteller2);
            gesuchTS.familiensituation = this.parseFamiliensituation(new TSFamiliensituation(), gesuchFromServer.familiensituation);
            gesuchTS.kindContainers = this.parseKindContainerList(gesuchFromServer.kindContainers);
            return gesuchTS;
        }
        return undefined;
    }


    public fachstelleToRestObject(restFachstelle: any, fachstelle: TSFachstelle): any {
        this.abstractEntityToRestObject(restFachstelle, fachstelle);
        restFachstelle.name = fachstelle.name;
        restFachstelle.beschreibung = fachstelle.beschreibung;
        restFachstelle.behinderungsbestaetigung = fachstelle.behinderungsbestaetigung;
        return restFachstelle;
    }

    public parseFachstellen(data: any): TSFachstelle[] {
        var fachstellen: TSFachstelle[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                fachstellen[i] = this.parseFachstelle(new TSFachstelle(), data[i]);
            }
        } else {
            fachstellen[0] = this.parseFachstelle(new TSFachstelle(), data);
        }
        return fachstellen;
    }

    public parseFachstelle(parsedFachstelle: TSFachstelle, receivedFachstelle: any): TSFachstelle {
        this.parseAbstractEntity(parsedFachstelle, receivedFachstelle);
        parsedFachstelle.name = receivedFachstelle.name;
        parsedFachstelle.beschreibung = receivedFachstelle.beschreibung;
        parsedFachstelle.behinderungsbestaetigung = receivedFachstelle.behinderungsbestaetigung;
        return parsedFachstelle;
    }

    public mandantToRestObject(restMandant: any, mandant: TSMandant): any {
        if (mandant) {
            this.abstractEntityToRestObject(restMandant, mandant);
            restMandant.name = mandant.name;
            return restMandant;
        }
        return undefined;
    }

    public parseMandant(mandantTS: TSMandant, mandantFromServer: any): TSMandant {
        if (mandantFromServer) {
            this.parseAbstractEntity(mandantTS, mandantFromServer);
            mandantTS.name = mandantFromServer.name;
            return mandantTS;
        }
        return undefined;
    }

    public traegerschaftToRestObject(restTragerschaft: any, traegerschaft: TSTraegerschaft): any {
        if (traegerschaft) {
            this.abstractEntityToRestObject(restTragerschaft, traegerschaft);
            restTragerschaft.name = traegerschaft.name;
            return restTragerschaft;
        }
        return undefined;
    }

    public parseTraegerschaften(data: Array<any>): TSTraegerschaft[] {
        var traegerschaftenen: TSTraegerschaft[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                traegerschaftenen[i] = this.parseTraegerschaft(new TSTraegerschaft(), data[i]);
            }
        } else {
            traegerschaftenen[0] = this.parseTraegerschaft(new TSTraegerschaft(), data);
        }
        return traegerschaftenen;
    }

    public parseTraegerschaft(traegerschaftTS: TSTraegerschaft, traegerschaftFromServer: any): TSTraegerschaft {
        if (traegerschaftFromServer) {
            this.parseAbstractEntity(traegerschaftTS, traegerschaftFromServer);
            traegerschaftTS.name = traegerschaftFromServer.name;
            return traegerschaftTS;
        }
        return undefined;
    }

    public institutionToRestObject(restInstitution: any, institution: TSInstitution): any {
        if (institution) {
            this.abstractEntityToRestObject(restInstitution, institution);
            restInstitution.name = institution.name;
            restInstitution.mandant = this.mandantToRestObject({}, institution.mandant);
            restInstitution.traegerschaft = this.traegerschaftToRestObject({}, institution.traegerschaft);
            return restInstitution;
        }
        return undefined;
    }

    public parseInstitution(institutionTS: TSInstitution, institutionFromServer: any): TSInstitution {
        if (institutionFromServer) {
            this.parseAbstractEntity(institutionTS, institutionFromServer);
            institutionTS.name = institutionFromServer.name;
            institutionTS.mandant = this.parseMandant(new TSMandant(), institutionFromServer.mandant);
            institutionTS.traegerschaft = this.parseTraegerschaft(new TSTraegerschaft(), institutionFromServer.traegerschaft);
            return institutionTS;
        }
        return undefined;
    }

    public parseInstitutionen(data: Array<any>): TSInstitution[] {
        var institutionen: TSInstitution[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                institutionen[i] = this.parseInstitution(new TSInstitution(), data[i]);
            }
        } else {
            institutionen[0] = this.parseInstitution(new TSInstitution(), data);
        }
        return institutionen;
    }

    public institutionStammdatenToRestObject(restInstitutionStammdaten: any, institutionStammdaten: TSInstitutionStammdaten): any {
        if (institutionStammdaten) {
            this.abstractDateRangeEntityToRestObject(restInstitutionStammdaten, institutionStammdaten);
            restInstitutionStammdaten.iban = institutionStammdaten.iban;
            restInstitutionStammdaten.oeffnungsstunden = institutionStammdaten.oeffnungsstunden;
            restInstitutionStammdaten.oeffnungstage = institutionStammdaten.oeffnungstage;
            restInstitutionStammdaten.betreuungsangebotTyp = institutionStammdaten.betreuungsangebotTyp;
            restInstitutionStammdaten.institution = this.institutionToRestObject({}, institutionStammdaten.institution);
            restInstitutionStammdaten.adresse = this.adresseToRestObject({}, institutionStammdaten.adresse);
            return restInstitutionStammdaten;
        }
        return undefined;
    }

    public parseInstitutionStammdaten(institutionStammdatenTS: TSInstitutionStammdaten, institutionStammdatenFromServer: any): TSInstitutionStammdaten {
        if (institutionStammdatenFromServer) {
            this.parseDateRangeEntity(institutionStammdatenTS, institutionStammdatenFromServer);
            institutionStammdatenTS.iban = institutionStammdatenFromServer.iban;
            institutionStammdatenTS.oeffnungsstunden = institutionStammdatenFromServer.oeffnungsstunden;
            institutionStammdatenTS.oeffnungstage = institutionStammdatenFromServer.oeffnungstage;
            institutionStammdatenTS.betreuungsangebotTyp = institutionStammdatenFromServer.betreuungsangebotTyp;
            institutionStammdatenTS.institution = this.parseInstitution(new TSInstitution(), institutionStammdatenFromServer.institution);
            institutionStammdatenTS.adresse = this.parseAdresse(new TSAdresse(), institutionStammdatenFromServer.adresse);
            return institutionStammdatenTS;
        }
        return undefined;
    }

    public parseInstitutionStammdatenArray(data: Array<any>): TSInstitutionStammdaten[] {
        var institutionStammdaten: TSInstitutionStammdaten[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                institutionStammdaten[i] = this.parseInstitutionStammdaten(new TSInstitutionStammdaten(), data[i]);
            }
        } else {
            institutionStammdaten[0] = this.parseInstitutionStammdaten(new TSInstitutionStammdaten(), data);
        }
        return institutionStammdaten;
    }

    public finanzielleSituationContainerToRestObject(restFinanzielleSituationContainer: any, finanzielleSituationContainer: TSFinanzielleSituationContainer): TSFinanzielleSituationContainer {
        this.abstractEntityToRestObject(restFinanzielleSituationContainer, finanzielleSituationContainer);
        restFinanzielleSituationContainer.jahr = finanzielleSituationContainer.jahr;
        if (finanzielleSituationContainer.finanzielleSituationGS) {
            restFinanzielleSituationContainer.finanzielleSituationGS = this.finanzielleSituationToRestObject({}, finanzielleSituationContainer.finanzielleSituationGS);
        }
        if (finanzielleSituationContainer.finanzielleSituationJA) {
            restFinanzielleSituationContainer.finanzielleSituationJA = this.finanzielleSituationToRestObject({}, finanzielleSituationContainer.finanzielleSituationJA);
        }
        if (finanzielleSituationContainer.finanzielleSituationSV) {
            restFinanzielleSituationContainer.finanzielleSituationSV = this.finanzielleSituationToRestObject({}, finanzielleSituationContainer.finanzielleSituationSV);
        }
        return restFinanzielleSituationContainer;
    }

    public parseFinanzielleSituationContainer(containerTS: TSFinanzielleSituationContainer, containerFromServer: any): TSFinanzielleSituationContainer {
        if (containerFromServer) {
            this.parseAbstractEntity(containerTS, containerFromServer);
            containerTS.jahr = containerFromServer.jahr;
            //todo hefr nur initialisieren wenn noetig?
            containerTS.finanzielleSituationGS = this.parseFinanzielleSituation(containerTS.finanzielleSituationGS || new TSFinanzielleSituation(), containerFromServer.finanzielleSituationGS);
            containerTS.finanzielleSituationJA = this.parseFinanzielleSituation(containerTS.finanzielleSituationJA || new TSFinanzielleSituation(), containerFromServer.finanzielleSituationJA);
            containerTS.finanzielleSituationSV = this.parseFinanzielleSituation(containerTS.finanzielleSituationSV || new TSFinanzielleSituation(), containerFromServer.finanzielleSituationSV);
            return containerTS;
        }
        return undefined;
    }

    public finanzielleSituationToRestObject(restFinanzielleSituation: any, finanzielleSituation: TSFinanzielleSituation): TSFinanzielleSituation {
        this.abstractEntityToRestObject(restFinanzielleSituation, finanzielleSituation);
        restFinanzielleSituation.steuerveranlagungErhalten = finanzielleSituation.steuerveranlagungErhalten;
        restFinanzielleSituation.steuererklaerungAusgefuellt = finanzielleSituation.steuererklaerungAusgefuellt || false;
        restFinanzielleSituation.nettolohn = finanzielleSituation.nettolohn;
        restFinanzielleSituation.familienzulage = finanzielleSituation.familienzulage;
        restFinanzielleSituation.ersatzeinkommen = finanzielleSituation.ersatzeinkommen;
        restFinanzielleSituation.erhalteneAlimente = finanzielleSituation.erhalteneAlimente;
        restFinanzielleSituation.bruttovermoegen = finanzielleSituation.bruttovermoegen;
        restFinanzielleSituation.schulden = finanzielleSituation.schulden;
        restFinanzielleSituation.selbstaendig = finanzielleSituation.selbstaendig;
        restFinanzielleSituation.geschaeftsgewinnBasisjahrMinus2 = finanzielleSituation.geschaeftsgewinnBasisjahrMinus2;
        restFinanzielleSituation.geschaeftsgewinnBasisjahrMinus1 = finanzielleSituation.geschaeftsgewinnBasisjahrMinus1;
        restFinanzielleSituation.geschaeftsgewinnBasisjahr = finanzielleSituation.geschaeftsgewinnBasisjahr;
        restFinanzielleSituation.geleisteteAlimente = finanzielleSituation.geleisteteAlimente;
        return restFinanzielleSituation;
    }

    public parseFinanzielleSituation(finanzielleSituationTS: TSFinanzielleSituation, finanzielleSituationFromServer: any): TSFinanzielleSituation {
        if (finanzielleSituationFromServer) {
            this.parseAbstractEntity(finanzielleSituationTS, finanzielleSituationFromServer);
            finanzielleSituationTS.steuerveranlagungErhalten = finanzielleSituationFromServer.steuerveranlagungErhalten;
            finanzielleSituationTS.steuererklaerungAusgefuellt = finanzielleSituationFromServer.steuererklaerungAusgefuellt;
            finanzielleSituationTS.nettolohn = finanzielleSituationFromServer.nettolohn;
            finanzielleSituationTS.familienzulage = finanzielleSituationFromServer.familienzulage;
            finanzielleSituationTS.ersatzeinkommen = finanzielleSituationFromServer.ersatzeinkommen;
            finanzielleSituationTS.erhalteneAlimente = finanzielleSituationFromServer.erhalteneAlimente;
            finanzielleSituationTS.bruttovermoegen = finanzielleSituationFromServer.bruttovermoegen;
            finanzielleSituationTS.schulden = finanzielleSituationFromServer.schulden;
            finanzielleSituationTS.selbstaendig = finanzielleSituationFromServer.selbstaendig;
            finanzielleSituationTS.geschaeftsgewinnBasisjahrMinus2 = finanzielleSituationFromServer.geschaeftsgewinnBasisjahrMinus2;
            finanzielleSituationTS.geschaeftsgewinnBasisjahrMinus1 = finanzielleSituationFromServer.geschaeftsgewinnBasisjahrMinus1;
            finanzielleSituationTS.geschaeftsgewinnBasisjahr = finanzielleSituationFromServer.geschaeftsgewinnBasisjahr;
            finanzielleSituationTS.geleisteteAlimente = finanzielleSituationFromServer.geleisteteAlimente;
            return finanzielleSituationTS;
        }
        return undefined;
    }

    public finanzielleSituationResultateToRestObject(restFinanzielleSituationResultate: any, finanzielleSituationResultateDTO: TSFinanzielleSituationResultateDTO): TSFinanzielleSituationResultateDTO {
        restFinanzielleSituationResultate.geschaeftsgewinnDurchschnittGesuchsteller1 = finanzielleSituationResultateDTO.geschaeftsgewinnDurchschnittGesuchsteller1;
        restFinanzielleSituationResultate.geschaeftsgewinnDurchschnittGesuchsteller2 = finanzielleSituationResultateDTO.geschaeftsgewinnDurchschnittGesuchsteller2;
        restFinanzielleSituationResultate.einkommenBeiderGesuchsteller = finanzielleSituationResultateDTO.einkommenBeiderGesuchsteller;
        restFinanzielleSituationResultate.nettovermoegenFuenfProzent = finanzielleSituationResultateDTO.nettovermoegenFuenfProzent;
        restFinanzielleSituationResultate.anrechenbaresEinkommen = finanzielleSituationResultateDTO.anrechenbaresEinkommen;
        restFinanzielleSituationResultate.abzuegeBeiderGesuchsteller = finanzielleSituationResultateDTO.abzuegeBeiderGesuchsteller;
        restFinanzielleSituationResultate.abzugAufgrundFamiliengroesse = finanzielleSituationResultateDTO.abzugAufgrundFamiliengroesse;
        restFinanzielleSituationResultate.totalAbzuege = finanzielleSituationResultateDTO.totalAbzuege;
        restFinanzielleSituationResultate.massgebendesEinkommen = finanzielleSituationResultateDTO.massgebendesEinkommen;
        restFinanzielleSituationResultate.familiengroesse = finanzielleSituationResultateDTO.familiengroesse;
        return restFinanzielleSituationResultate;
    }

    public parseFinanzielleSituationResultate(finanzielleSituationResultateDTO: TSFinanzielleSituationResultateDTO, finanzielleSituationResultateFromServer: any): TSFinanzielleSituationResultateDTO {
        if (finanzielleSituationResultateFromServer) {
            finanzielleSituationResultateDTO.geschaeftsgewinnDurchschnittGesuchsteller1 = finanzielleSituationResultateFromServer.geschaeftsgewinnDurchschnittGesuchsteller1;
            finanzielleSituationResultateDTO.geschaeftsgewinnDurchschnittGesuchsteller2 = finanzielleSituationResultateFromServer.geschaeftsgewinnDurchschnittGesuchsteller2;
            finanzielleSituationResultateDTO.einkommenBeiderGesuchsteller = finanzielleSituationResultateFromServer.einkommenBeiderGesuchsteller;
            finanzielleSituationResultateDTO.nettovermoegenFuenfProzent = finanzielleSituationResultateFromServer.nettovermoegenFuenfProzent;
            finanzielleSituationResultateDTO.anrechenbaresEinkommen = finanzielleSituationResultateFromServer.anrechenbaresEinkommen;
            finanzielleSituationResultateDTO.abzuegeBeiderGesuchsteller = finanzielleSituationResultateFromServer.abzuegeBeiderGesuchsteller;
            finanzielleSituationResultateDTO.abzugAufgrundFamiliengroesse = finanzielleSituationResultateFromServer.abzugAufgrundFamiliengroesse;
            finanzielleSituationResultateDTO.totalAbzuege = finanzielleSituationResultateFromServer.totalAbzuege;
            finanzielleSituationResultateDTO.massgebendesEinkommen = finanzielleSituationResultateFromServer.massgebendesEinkommen;
            finanzielleSituationResultateDTO.familiengroesse = finanzielleSituationResultateFromServer.familiengroesse;
            return finanzielleSituationResultateDTO;
        }
        return undefined;
    }

    public kindContainerToRestObject(restKindContainer: any, kindContainer: TSKindContainer): any {
        this.abstractEntityToRestObject(restKindContainer, kindContainer);
        if (kindContainer.kindGS) {
            restKindContainer.kindGS = this.kindToRestObject({}, kindContainer.kindGS);
        }
        if (kindContainer.kindJA) {
            restKindContainer.kindJA = this.kindToRestObject({}, kindContainer.kindJA);
        }
        restKindContainer.betreuungen = this.betreuungListToRestObject(kindContainer.betreuungen);
        return restKindContainer;
    }

    private kindToRestObject(restKind: any, kind: TSKind): any {
        this.abstractPersonEntitytoRestObject(restKind, kind);
        restKind.kinderabzug = kind.kinderabzug;
        restKind.mutterspracheDeutsch = kind.mutterspracheDeutsch;
        restKind.familienErgaenzendeBetreuung = kind.familienErgaenzendeBetreuung;
        if (kind.pensumFachstelle) {
            restKind.pensumFachstelle = this.pensumFachstelleToRestObject({}, kind.pensumFachstelle);
        }
        restKind.bemerkungen = kind.bemerkungen;
        return restKind;
    }

    public parseKindContainerList(data: Array<any>): TSKindContainer[] {
        var kindContainerList: TSKindContainer[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                kindContainerList[i] = this.parseKindContainer(new TSKindContainer(), data[i]);
            }
        } else {
            kindContainerList[0] = this.parseKindContainer(new TSKindContainer(), data);
        }
        return kindContainerList;
    }

    public parseKindContainer(kindContainerTS: TSKindContainer, kindContainerFromServer: any): TSKindContainer {
        if (kindContainerFromServer) {
            this.parseAbstractEntity(kindContainerTS, kindContainerFromServer);
            kindContainerTS.kindGS = this.parseKind(new TSKind(), kindContainerFromServer.kindGS);
            kindContainerTS.kindJA = this.parseKind(new TSKind(), kindContainerFromServer.kindJA);
            kindContainerTS.betreuungen = this.parseBetreuungList(kindContainerFromServer.betreuungen);
            return kindContainerTS;
        }
        return undefined;
    }

    private parseKind(kindTS: TSKind, kindFromServer: any): TSKind {
        if (kindFromServer) {
            this.parseAbstractPersonEntity(kindTS, kindFromServer);
            kindTS.kinderabzug = kindFromServer.kinderabzug;
            kindTS.mutterspracheDeutsch = kindFromServer.mutterspracheDeutsch;
            kindTS.familienErgaenzendeBetreuung = kindFromServer.familienErgaenzendeBetreuung;
            if (kindFromServer.pensumFachstelle) {
                kindTS.pensumFachstelle = this.parsePensumFachstelle(new TSPensumFachstelle(), kindFromServer.pensumFachstelle);
            }
            kindTS.bemerkungen = kindFromServer.bemerkungen;
            return kindTS;
        }
        return undefined;
    }

    private pensumFachstelleToRestObject(restPensumFachstelle: any, pensumFachstelle: TSPensumFachstelle): any {
        this.abstractDateRangeEntityToRestObject(restPensumFachstelle, pensumFachstelle);
        restPensumFachstelle.pensum = pensumFachstelle.pensum;
        if (pensumFachstelle.fachstelle) {
            restPensumFachstelle.fachstelle = this.fachstelleToRestObject({}, pensumFachstelle.fachstelle);
        }
        return restPensumFachstelle;
    }

    private parsePensumFachstelle(pensumFachstelleTS: TSPensumFachstelle, pensumFachstelleFromServer: any): TSPensumFachstelle {
        if (pensumFachstelleFromServer) {
            this.parseDateRangeEntity(pensumFachstelleTS, pensumFachstelleFromServer);
            pensumFachstelleTS.pensum = pensumFachstelleFromServer.pensum;
            if (pensumFachstelleFromServer.fachstelle) {
                pensumFachstelleTS.fachstelle = this.parseFachstelle(new TSFachstelle(), pensumFachstelleFromServer.fachstelle);
            }
            return pensumFachstelleTS;
        }
        return undefined;
    }

    private betreuungListToRestObject(betreuungen: Array<TSBetreuung>): Array<any> {
        let list: any[] = [];
        if (betreuungen) {
            for (var i = 0; i < betreuungen.length; i++) {
                list[i] = this.betreuungToRestObject({}, betreuungen[i]);
            }
        }
        return list;
    }

    public betreuungToRestObject(restBetreuung: any, betreuung: TSBetreuung): any {
        this.abstractEntityToRestObject(restBetreuung, betreuung);
        restBetreuung.betreuungsstatus = betreuung.betreuungsstatus;
        restBetreuung.bemerkungen = betreuung.bemerkungen;
        restBetreuung.schulpflichtig = betreuung.schulpflichtig;
        if (betreuung.institutionStammdaten) {
            restBetreuung.institutionStammdaten = this.institutionStammdatenToRestObject({}, betreuung.institutionStammdaten);
        }
        if (betreuung.betreuungspensumContainers) {
            restBetreuung.betreuungspensumContainers = [];
            betreuung.betreuungspensumContainers.forEach((betPensCont: TSBetreuungspensumContainer) => {
                restBetreuung.betreuungspensumContainers.push(this.betreuungspensumContainerToRestObject({}, betPensCont));
            });
        }
        return restBetreuung;
    }

    public betreuungspensumContainerToRestObject(restBetPensCont: any, betPensCont: TSBetreuungspensumContainer): any {
        this.abstractEntityToRestObject(restBetPensCont, betPensCont);
        if (betPensCont.betreuungspensumGS) {
            restBetPensCont.betreuungspensumGS = this.betreuungspensumToRestObject({}, betPensCont.betreuungspensumGS);
        }
        if (betPensCont.betreuungspensumJA) {
            restBetPensCont.betreuungspensumJA = this.betreuungspensumToRestObject({}, betPensCont.betreuungspensumJA);
        }
        return restBetPensCont;
    }

    public betreuungspensumToRestObject(restBetreuungspensum: any, betreuungspensum: TSBetreuungspensum): any {
        this.abstractPensumEntityToRestObject(restBetreuungspensum, betreuungspensum);
        return restBetreuungspensum;
    }

    private parseBetreuungList(betreuungen: Array<any>): TSBetreuung[] {
        let resultList: TSBetreuung[] = [];
        if (betreuungen && Array.isArray(betreuungen)) {
            for (var i = 0; i < betreuungen.length; i++) {
                resultList[i] = this.parseBetreuung(new TSBetreuung(), betreuungen[i]);
            }
        } else {
            resultList[0] = this.parseBetreuung(new TSBetreuung(), betreuungen);
        }
        return resultList;
    }

    public parseBetreuung(betreuungTS: TSBetreuung, betreuungFromServer: any): TSBetreuung {
        if (betreuungFromServer) {
            this.parseAbstractEntity(betreuungTS, betreuungFromServer);
            betreuungTS.bemerkungen = betreuungFromServer.bemerkungen;
            betreuungTS.schulpflichtig = betreuungFromServer.schulpflichtig;
            betreuungTS.betreuungsstatus = betreuungFromServer.betreuungsstatus;
            betreuungTS.institutionStammdaten = this.parseInstitutionStammdaten(new TSInstitutionStammdaten(), betreuungFromServer.institutionStammdaten);
            betreuungTS.betreuungspensumContainers = this.parseBetreuungspensumContainers(betreuungFromServer.betreuungspensumContainers);
            return betreuungTS;
        }
        return undefined;
    }

    public parseBetreuungspensumContainers(data: Array<any>): TSBetreuungspensumContainer[] {
        let betPensContainers: TSBetreuungspensumContainer[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                betPensContainers[i] = this.parseBetreuungspensumContainer(new TSBetreuungspensumContainer(), data[i]);
            }
        } else {
            betPensContainers[0] = this.parseBetreuungspensumContainer(new TSBetreuungspensumContainer(), data);
        }
        return betPensContainers;
    }

    public parseBetreuungspensumContainer(betPensContainerTS: TSBetreuungspensumContainer, betPensContFromServer: any): TSBetreuungspensumContainer {
        if (betPensContFromServer) {
            this.parseAbstractEntity(betPensContainerTS, betPensContFromServer);
            if (betPensContFromServer.betreuungspensumGS) {
                betPensContainerTS.betreuungspensumGS = this.parseBetreuungspensum(new TSBetreuungspensum(), betPensContFromServer.betreuungspensumGS);
            }
            if (betPensContFromServer.betreuungspensumJA) {
                betPensContainerTS.betreuungspensumJA = this.parseBetreuungspensum(new TSBetreuungspensum(), betPensContFromServer.betreuungspensumJA);
            }
            return betPensContainerTS;
        }
        return undefined;
    }

    public parseBetreuungspensum(betreuungspensumTS: TSBetreuungspensum, betreuungspensumFromServer: any): TSBetreuungspensum {
        if (betreuungspensumFromServer) {
            this.parseAbstractPensumEntity(betreuungspensumTS, betreuungspensumFromServer);
            return betreuungspensumTS;
        }
        return undefined;
    }


    private parseErwerbspensenContainers(data: Array<any>): TSErwerbspensumContainer[] {
        let erwerbspensen: TSErwerbspensumContainer[] = [];
        if (data !== null && data !== undefined) {
            if (Array.isArray(data)) {
                for (var i = 0; i < data.length; i++) {
                    erwerbspensen[i] = this.parseErwerbspensumContainer(new TSErwerbspensumContainer(), data[i]);
                }
            } else {
                erwerbspensen[0] = this.parseErwerbspensumContainer(new TSErwerbspensumContainer(), data);
            }
        }
        return erwerbspensen;
    }

    public gesuchsperiodeToRestObject(restGesuchsperiode: any, gesuchsperiode: TSGesuchsperiode): any {
        if (gesuchsperiode) {
            this.abstractDateRangeEntityToRestObject(restGesuchsperiode, gesuchsperiode);
            restGesuchsperiode.active = gesuchsperiode.active;
            return restGesuchsperiode;
        }
        return undefined;
    }

    public parseGesuchsperiode(gesuchsperiodeTS: TSGesuchsperiode, gesuchsperiodeFromServer: any): TSGesuchsperiode {
        if (gesuchsperiodeFromServer) {
            this.parseDateRangeEntity(gesuchsperiodeTS, gesuchsperiodeFromServer);
            gesuchsperiodeTS.active = gesuchsperiodeFromServer.active;
            return gesuchsperiodeTS;
        }
        return undefined;
    }

    public parseGesuchsperioden(data: any) {
        var gesuchsperioden: TSGesuchsperiode[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                gesuchsperioden[i] = this.parseGesuchsperiode(new TSGesuchsperiode(), data[i]);
            }
        } else {
            gesuchsperioden[0] = this.parseGesuchsperiode(new TSGesuchsperiode(), data);
        }
        return gesuchsperioden;
    }

    public pendenzToRestObject(restPendenz: any, pendenz: TSPendenzJA): any {
        restPendenz.antragId = pendenz.antragId;
        restPendenz.fallNummer = pendenz.fallNummer;
        restPendenz.familienName = pendenz.familienName;
        restPendenz.angebote = pendenz.angebote;
        restPendenz.antragTyp = pendenz.antragTyp;
        restPendenz.eingangsdatum = DateUtil.momentToLocalDate(pendenz.eingangsdatum);
        restPendenz.gesuchsperiode = this.gesuchsperiodeToRestObject(new TSGesuchsperiode(), pendenz.gesuchsperiode);
        restPendenz.institutionen = pendenz.institutionen;
        return restPendenz;
    }

    public parsePendenz(pendenzTS: TSPendenzJA, pendenzFromServer: any): TSPendenzJA {
        pendenzTS.antragId = pendenzFromServer.antragId;
        pendenzTS.fallNummer = pendenzFromServer.fallNummer;
        pendenzTS.familienName = pendenzFromServer.familienName;
        pendenzTS.angebote = pendenzFromServer.angebote;
        pendenzTS.antragTyp = pendenzFromServer.antragTyp;
        pendenzTS.eingangsdatum = DateUtil.localDateToMoment(pendenzFromServer.eingangsdatum);
        pendenzTS.gesuchsperiode = this.parseGesuchsperiode(new TSGesuchsperiode(), pendenzFromServer.gesuchsperiode);
        pendenzTS.institutionen = pendenzFromServer.institutionen;
        return pendenzTS;
    }

    public parsePendenzen(data: any): TSPendenzJA[] {
        var pendenzen: TSPendenzJA[] = [];
        if (data && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                pendenzen[i] = this.parsePendenz(new TSPendenzJA(), data[i]);
            }
        } else {
            pendenzen[0] = this.parsePendenz(new TSPendenzJA(), data);
        }
        return pendenzen;
    }

    public userToRestObject(user: any, userTS: TSUser): any {
        if (userTS) {
            user.username = userTS.username;
            user.password = userTS.password;
            user.nachname = userTS.nachname;
            user.vorname = userTS.vorname;
            user.email = userTS.email;
            user.role = userTS.role;
            user.mandant = this.mandantToRestObject({}, userTS.mandant);
            return user;
        }
        return undefined;
    }

    private parseUser(userTS: TSUser, userFromServer: any): TSUser {
        if (userFromServer) {
            userTS.username = userFromServer.username;
            userTS.password = userFromServer.password;
            userTS.nachname = userFromServer.nachname;
            userTS.vorname = userFromServer.vorname;
            userTS.email = userFromServer.email;
            userTS.role = userFromServer.role;
            userTS.mandant = this.parseMandant(new TSMandant(), userFromServer.mandant);
            return userTS;
        }
        return undefined;
    }
}
