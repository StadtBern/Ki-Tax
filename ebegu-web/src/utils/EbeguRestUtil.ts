import TSApplicationProperty from '../models/TSApplicationProperty';
import TSAbstractEntity from '../models/TSAbstractEntity';
import TSAdresse from '../models/TSAdresse';
import {TSAdressetyp} from '../models/enums/TSAdressetyp';
import TSGesuchsteller from '../models/TSGesuchsteller';
import TSGesuch from '../models/TSGesuch';
import TSFall from '../models/TSFall';
import DateUtil from './DateUtil';
import {IFilterService} from 'angular';
import TSLand from '../models/types/TSLand';
import TSFamiliensituation from '../models/TSFamiliensituation';
import {TSFachstelle} from '../models/TSFachstelle';
import {TSMandant} from '../models/TSMandant';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
import {TSInstitution} from '../models/TSInstitution';
import {TSInstitutionStammdaten} from '../models/TSInstitutionStammdaten';
import {TSDateRange} from '../models/types/TSDateRange';
import {TSAbstractDateRangedEntity} from '../models/TSAbstractDateRangedEntity';

export default class EbeguRestUtil {
    static $inject = ['$filter'];
    public filter: any;

    /* @ngInject */
    constructor($filter: IFilterService) {
        this.filter = $filter;
    }

    /**
     * Wandelt Data in einen TSApplicationProperty Array um, welches danach zurueckgeliefert wird
     * @param data
     * @returns {TSApplicationProperty[]}
     */
    public parseApplicationProperties(data: any): TSApplicationProperty[] {
        var appProperties: TSApplicationProperty[] = [];
        if (data !== null && Array.isArray(data)) {
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
        parsedAppProperty.name = receivedAppProperty.name;
        parsedAppProperty.value = receivedAppProperty.value;
        this.parseAbstractEntity(parsedAppProperty, receivedAppProperty);
        return parsedAppProperty;
    }

    private parseAbstractEntity(parsedAppProperty: TSAbstractEntity, receivedAppProperty: any): void {
        parsedAppProperty.id = receivedAppProperty.id;
        parsedAppProperty.timestampErstellt = DateUtil.localDateTimeToMoment(receivedAppProperty.timestampErstellt);
        parsedAppProperty.timestampMutiert = DateUtil.localDateTimeToMoment(receivedAppProperty.timestampMutiert);
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

    private dateRangeEntityToRestObject(dateRangedEntity: TSAbstractDateRangedEntity, restObj: any) {
        if (dateRangedEntity && dateRangedEntity.gueltigkeit) {
            restObj.gueltigAb = DateUtil.momentToLocalDate(dateRangedEntity.gueltigkeit.gueltigAb);
            restObj.gueltigBis = DateUtil.momentToLocalDate(dateRangedEntity.gueltigkeit.gueltigBis);
        }
    }
    private parseDateRangeEntity(parsedObject: TSAbstractDateRangedEntity, receivedAppProperty: any) {
            parsedObject.gueltigkeit = new TSDateRange(DateUtil.localDateToMoment(receivedAppProperty.gueltigAb), DateUtil.localDateToMoment(receivedAppProperty.gueltigBis));
    }

    public adresseToRestObject(restAdresse: any, adresse: TSAdresse): TSAdresse {
        if (adresse) {
            this.abstractEntityToRestObject(restAdresse, adresse);
            this.dateRangeEntityToRestObject(adresse, restAdresse);
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
            this.abstractEntityToRestObject(adresseTS, receivedAdresse);
            this.parseDateRangeEntity(adresseTS, receivedAdresse);
            adresseTS.strasse = receivedAdresse.strasse;
            adresseTS.hausnummer = receivedAdresse.hausnummer;
            adresseTS.zusatzzeile = receivedAdresse.zusatzzeile;
            adresseTS.plz = receivedAdresse.plz;
            adresseTS.ort = receivedAdresse.ort;
            adresseTS.land =  (this.landCodeToTSLand(receivedAdresse.land)) ? this.landCodeToTSLand(receivedAdresse.land).code : undefined;
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
            return new TSLand(landCode, this.filter('translate')(translationKey).toString());
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
            this.abstractEntityToRestObject(restGesuchsteller, gesuchsteller);
            restGesuchsteller.vorname = gesuchsteller.vorname;

            restGesuchsteller.nachname = gesuchsteller.nachname;
            restGesuchsteller.geburtsdatum = DateUtil.momentToLocalDate(gesuchsteller.geburtsdatum);
            restGesuchsteller.mail = gesuchsteller.mail;
            restGesuchsteller.mobile = gesuchsteller.mobile;
            restGesuchsteller.telefon = gesuchsteller.telefon;
            restGesuchsteller.telefonAusland = gesuchsteller.telefonAusland;
            restGesuchsteller.umzug = gesuchsteller.umzug;
            restGesuchsteller.geschlecht = gesuchsteller.geschlecht;
            restGesuchsteller.wohnAdresse = this.adresseToRestObject({}, gesuchsteller.adresse); //achtung heisst im jax wohnadresse nicht adresse
            restGesuchsteller.alternativeAdresse = this.adresseToRestObject({}, gesuchsteller.korrespondenzAdresse);
            restGesuchsteller.umzugAdresse = this.adresseToRestObject({}, gesuchsteller.umzugAdresse);
            return restGesuchsteller;
        }
        return undefined;
    }


    public parseGesuchsteller(gesuchstellerTS: TSGesuchsteller, gesuchstellerFromServer: any): TSGesuchsteller {
        if (gesuchstellerFromServer) {

            this.parseAbstractEntity(gesuchstellerTS, gesuchstellerFromServer);
            gesuchstellerTS.vorname = gesuchstellerFromServer.vorname;
            gesuchstellerTS.nachname = gesuchstellerFromServer.nachname;
            gesuchstellerTS.geburtsdatum = DateUtil.localDateToMoment(gesuchstellerFromServer.geburtsdatum);
            gesuchstellerTS.mail = gesuchstellerFromServer.mail;
            gesuchstellerTS.mobile = gesuchstellerFromServer.mobile;
            gesuchstellerTS.telefon = gesuchstellerFromServer.telefon;
            gesuchstellerTS.telefonAusland = gesuchstellerFromServer.telefonAusland;
            gesuchstellerTS.umzug = gesuchstellerFromServer.umzug;
            gesuchstellerTS.geschlecht = gesuchstellerFromServer.geschlecht;
            gesuchstellerTS.adresse = this.parseAdresse(new TSAdresse(), gesuchstellerFromServer.wohnAdresse);
            gesuchstellerTS.korrespondenzAdresse = this.parseAdresse(new TSAdresse(), gesuchstellerFromServer.alternativeAdresse);
            gesuchstellerTS.umzugAdresse = this.parseAdresse(new TSAdresse(), gesuchstellerFromServer.umzugAdresse);
            return gesuchstellerTS;
        }
        return undefined;

    }

    public familiensituationToRestObject(restFamiliensituation: any, familiensituation: TSFamiliensituation): TSFamiliensituation {
        restFamiliensituation.familienstatus = familiensituation.familienstatus;
        restFamiliensituation.gesuchstellerKardinalitaet = familiensituation.gesuchstellerKardinalitaet;
        restFamiliensituation.bemerkungen = familiensituation.bemerkungen;
        restFamiliensituation.gesuch = this.gesuchToRestObject({}, familiensituation.gesuch);
        this.abstractEntityToRestObject(restFamiliensituation, familiensituation);

        return restFamiliensituation;
    }

    public parseFamiliensituation(familiensituation: TSFamiliensituation, familiensituationFromServer: any): TSFamiliensituation {

        if (familiensituationFromServer) {
            this.parseAbstractEntity(familiensituation, familiensituationFromServer);
            familiensituation.bemerkungen = familiensituationFromServer.bemerkungen;
            familiensituation.familienstatus = familiensituationFromServer.familienstatus;
            familiensituation.gesuchstellerKardinalitaet = familiensituationFromServer.gesuchstellerKardinalitaet;
            familiensituation.gesuch = this.parseGesuch(familiensituation.gesuch, familiensituationFromServer.gesuch);
            return familiensituation;
        }
        return undefined;
    }

    public fallToRestObject(restFall: any, fall: TSFall): TSFall {
        this.abstractEntityToRestObject(restFall, fall);

        return restFall;
    }

    public parseFall(fallTS: TSFall, fallFromServer: any): TSFall {
        if (fallFromServer) {
            this.parseAbstractEntity(fallTS, fallFromServer);
            return fallTS;
        }
        return undefined;
    }


    public gesuchToRestObject(restGesuch: any, gesuch: TSGesuch): TSGesuch {
        this.abstractEntityToRestObject(restGesuch, gesuch);
        restGesuch.fall = this.fallToRestObject({}, gesuch.fall);
        restGesuch.gesuchsteller1 = this.gesuchstellerToRestObject({}, gesuch.gesuchsteller1);
        restGesuch.gesuchsteller2 = this.gesuchstellerToRestObject({}, gesuch.gesuchsteller2);

        return restGesuch;
    }

    public parseGesuch(gesuchTS: TSGesuch, gesuchFromServer: any): TSGesuch {
        if (gesuchFromServer) {
            this.parseAbstractEntity(gesuchTS, gesuchFromServer);
            gesuchTS.fall = this.parseFall(new TSFall(), gesuchFromServer.fall);
            gesuchTS.gesuchsteller1 = this.parseGesuchsteller(new TSGesuchsteller(), gesuchFromServer.gesuchsteller1);
            gesuchTS.gesuchsteller2 = this.parseGesuchsteller(new TSGesuchsteller(), gesuchFromServer.gesuchsteller2);
            return gesuchTS;
        }
        return undefined;
    }


    public fachstelleToRestObject(restFachstelle: any, fachstelle: TSFachstelle): any {
        restFachstelle.name = fachstelle.name;
        restFachstelle.beschreibung = fachstelle.beschreibung;
        restFachstelle.behinderungsbestaetigung = fachstelle.behinderungsbestaetigung;
        this.abstractEntityToRestObject(restFachstelle, fachstelle);

        return restFachstelle;
    }

    public parseFachstellen(data: any): TSFachstelle[] {
        var fachstellen: TSFachstelle[] = [];
        if (data !== null && Array.isArray(data)) {
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
        if (data !== null && Array.isArray(data)) {
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
            restInstitution.mandant = this.mandantToRestObject(new TSMandant(), institution.mandant);
            restInstitution.traegerschaft = this.traegerschaftToRestObject(new TSTraegerschaft(), institution.traegerschaft);
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
        if (data !== null && Array.isArray(data)) {
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
            this.abstractEntityToRestObject(restInstitutionStammdaten, institutionStammdaten);
            restInstitutionStammdaten.iban = institutionStammdaten.iban;
            restInstitutionStammdaten.oeffnungsstunden = institutionStammdaten.oeffnungsstunden;
            restInstitutionStammdaten.oeffnungstage = institutionStammdaten.oeffnungstage;
            restInstitutionStammdaten.betreuungsangebotTyp = institutionStammdaten.betreuungsangebotTyp;
            restInstitutionStammdaten.gueltigAb = DateUtil.momentToLocalDate(institutionStammdaten.gueltigkeit.gueltigAb);
            restInstitutionStammdaten.gueltigBis = DateUtil.momentToLocalDate(institutionStammdaten.gueltigkeit.gueltigBis);
            restInstitutionStammdaten.institution = this.institutionToRestObject(new TSInstitution(), institutionStammdaten.institution);
            return restInstitutionStammdaten;
        }
        return undefined;
    }

    public parseInstitutionStammdaten(institutionStammdatenTS: TSInstitutionStammdaten, institutionStammdatenFromServer: any): TSInstitutionStammdaten {
        if (institutionStammdatenFromServer) {
            this.parseAbstractEntity(institutionStammdatenTS, institutionStammdatenFromServer);
            institutionStammdatenTS.iban = institutionStammdatenFromServer.iban;
            institutionStammdatenTS.oeffnungsstunden = institutionStammdatenFromServer.oeffnungsstunden;
            institutionStammdatenTS.oeffnungstage = institutionStammdatenFromServer.oeffnungstage;
            institutionStammdatenTS.betreuungsangebotTyp = institutionStammdatenFromServer.betreuungsangebotTyp;
            institutionStammdatenTS.gueltigkeit = new TSDateRange(DateUtil.localDateToMoment(institutionStammdatenFromServer.gueltigAb),
                DateUtil.localDateToMoment(institutionStammdatenFromServer.gueltigBis));
            institutionStammdatenTS.institution = this.parseInstitution(new TSInstitution(), institutionStammdatenFromServer.institution);
            return institutionStammdatenTS;
        }
        return undefined;
    }

    public parseInstitutionStammdatenArray(data: Array<any>): TSInstitutionStammdaten[] {
        var institutionStammdaten: TSInstitutionStammdaten[] = [];
        if (data !== null && Array.isArray(data)) {
            for (var i = 0; i < data.length; i++) {
                institutionStammdaten[i] = this.parseInstitutionStammdaten(new TSInstitutionStammdaten(), data[i]);
            }
        } else {
            institutionStammdaten[0] = this.parseInstitutionStammdaten(new TSInstitutionStammdaten(), data);
        }
        return institutionStammdaten;
    }
}
