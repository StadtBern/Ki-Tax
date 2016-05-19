import TSAbstractEntity from './TSAbstractEntity';
import TSErwerbspensum from './TSErwerbspensum';
export default class TSErwerbspensumContainer extends TSAbstractEntity {


    private _erwerbspensumGS: TSErwerbspensum;
    private _erwerbspensumJA: TSErwerbspensum;


    get erwerbspensumGS(): TSErwerbspensum {
        return this._erwerbspensumGS;
    }

    set erwerbspensumGS(value: TSErwerbspensum) {
        this._erwerbspensumGS = value;
    }

    get erwerbspensumJA(): TSErwerbspensum {
        return this._erwerbspensumJA;
    }

    set erwerbspensumJA(value: TSErwerbspensum) {
        this._erwerbspensumJA = value;
    }
}
