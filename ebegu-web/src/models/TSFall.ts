import TSAbstractEntity from './TSAbstractEntity';
import TSUser from './TSUser';

export default class TSFall extends TSAbstractEntity {

    private _fallNummer: number;
    private _verantwortlicher: TSUser;

    constructor(fallNummer?: number, verantwortlicher?: TSUser) {
        super();
        this._fallNummer = fallNummer;
        this._verantwortlicher = verantwortlicher;
    }


    get fallNummer(): number {
        return this._fallNummer;
    }

    set fallNummer(value: number) {
        this._fallNummer = value;
    }

    get verantwortlicher(): TSUser {
        return this._verantwortlicher;
    }

    set verantwortlicher(value: TSUser) {
        this._verantwortlicher = value;
    }
}
