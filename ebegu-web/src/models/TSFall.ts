import TSAbstractEntity from './TSAbstractEntity';

export default class TSFall extends TSAbstractEntity {

    private _fallNummer: number;

    constructor(fallNummer?: number) {
        super();
        this._fallNummer = fallNummer;
    }


    get fallNummer(): number {
        return this._fallNummer;
    }

    set fallNummer(value: number) {
        this._fallNummer = value;
    }
}
