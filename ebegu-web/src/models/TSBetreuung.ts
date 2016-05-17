import TSAbstractEntity from './TSAbstractEntity';
import {TSInstitutionStammdaten} from './TSInstitutionStammdaten';
import {TSBetreuungsstatus} from './enums/TSBetreuungsstatus';
import TSBetreuungspensumContainer from './TSBetreuungspensumContainer';

export default class TSBetreuung extends TSAbstractEntity {

    private _institutionStammdaten: TSInstitutionStammdaten;
    private _betreuungsstatus: TSBetreuungsstatus;
    private _betreuungspensumContainers: Array<TSBetreuungspensumContainer> = [];
    private _bemerkungen: string;

    constructor(institutionStammdaten?: TSInstitutionStammdaten, betreuungsstatus?: TSBetreuungsstatus,
                betreuungspensumContainers?: Array<TSBetreuungspensumContainer>, bemerkungen?: string) {
        super();
        this._institutionStammdaten = institutionStammdaten;
        this._betreuungsstatus = betreuungsstatus;
        this._betreuungspensumContainers = betreuungspensumContainers;
        this._bemerkungen = bemerkungen;
    }

    get institutionStammdaten(): TSInstitutionStammdaten {
        return this._institutionStammdaten;
    }

    set institutionStammdaten(value: TSInstitutionStammdaten) {
        this._institutionStammdaten = value;
    }

    get betreuungsstatus(): TSBetreuungsstatus {
        return this._betreuungsstatus;
    }

    set betreuungsstatus(value: TSBetreuungsstatus) {
        this._betreuungsstatus = value;
    }

    get betreuungspensumContainers(): Array<TSBetreuungspensumContainer> {
        return this._betreuungspensumContainers;
    }

    set betreuungspensumContainers(value: Array<TSBetreuungspensumContainer>) {
        this._betreuungspensumContainers = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }
}
