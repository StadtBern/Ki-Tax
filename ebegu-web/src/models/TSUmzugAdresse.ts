import TSAdresse from './TSAdresse';
import {TSBetroffene} from './enums/TSBetroffene';
import TSAdresseContainer from './TSAdresseContainer';

export default class TSUmzugAdresse {

    private _betroffene: TSBetroffene;
    private _adresse: TSAdresseContainer;

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
}
