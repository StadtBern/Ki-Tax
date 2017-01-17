import TSAbstractEntity from './TSAbstractEntity';
import TSUser from './TSUser';

export default class TSFall extends TSAbstractEntity {

    private _fallNummer: number;
    private _nextNumberKind: number;
    private _verantwortlicher: TSUser;
    private _besitzerUsername: string; //wir koennten hier auch das TSUser objekt verwenden aber wir brauchen aktuell nur die info ob einer existiert

    constructor(fallNummer?: number, verantwortlicher?: TSUser, nextNumberKind?: number) {
        super();
        this._fallNummer = fallNummer;
        this._verantwortlicher = verantwortlicher;
        this._nextNumberKind = nextNumberKind;
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

    get nextNumberKind(): number {
        return this._nextNumberKind;
    }

    set nextNumberKind(value: number) {
        this._nextNumberKind = value;
    }

    get besitzerUsername(): string {
        return this._besitzerUsername;
    }

    set besitzerUsername(value: string) {
        this._besitzerUsername = value;
    }
}
