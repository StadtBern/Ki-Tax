import TSAdresse from './TSAdresse';
import {TSBetroffene} from './enums/TSBetroffene';
import TSAdresseContainer from './TSAdresseContainer';

export default class TSUmzugAdresse {

    private _betroffene: TSBetroffene;
    private _adresse: TSAdresseContainer;

    // nur zum speichern der anderen GS adresse
    private _adresseGS2: TSAdresseContainer;

    constructor(betroffene?: TSBetroffene, adresse?: TSAdresseContainer) {
        this._betroffene = betroffene;
        this._adresse = adresse;
    }

    get betroffene(): TSBetroffene {
        return this._betroffene;
    }

    set betroffene(value: TSBetroffene) {
        this._betroffene = value;
    }

    get adresse(): TSAdresseContainer {
        return this._adresse;
    }

    set adresse(value: TSAdresseContainer) {
        this._adresse = value;
    }

    get adresseGS2(): TSAdresseContainer {
        return this._adresseGS2;
    }

    set adresseGS2(value: TSAdresseContainer) {
        this._adresseGS2 = value;
    }
}
