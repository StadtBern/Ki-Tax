import TSAdresse from './TSAdresse';
import {TSBetroffene} from './enums/TSBetroffene';

export default class TSUmzugAdresse {

    private _betroffene: TSBetroffene;
    private _adresse: TSAdresse;

    constructor(betroffene?: TSBetroffene, adresse?: TSAdresse) {
        this._betroffene = betroffene;
        this._adresse = adresse;
    }

    get betroffene(): TSBetroffene {
        return this._betroffene;
    }

    set betroffene(value: TSBetroffene) {
        this._betroffene = value;
    }

    get adresse(): TSAdresse {
        return this._adresse;
    }

    set adresse(value: TSAdresse) {
        this._adresse = value;
    }
}
