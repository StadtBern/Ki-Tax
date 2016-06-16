import TSGesuchsperiode from './TSGesuchsperiode';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSAntragTyp} from './enums/TSAntragTyp';

export default class TSPendenzJA {

    private _antragId: string;
    private _fallNummer: number;
    private _familienName: string;
    private _antragTyp: TSAntragTyp;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    // private _pendenzStatus: TSPendenzStatus;
    // private _bearbeiter: TSUser;
    private _angebote: Array<TSBetreuungsangebotTyp>;
    private _institutionen: Array<string>;

    constructor(antragId?: string, fallNummer?: number, familienName?: string, antragTyp?: TSAntragTyp, gesuchsperiode?: TSGesuchsperiode, eingangsdatum?: moment.Moment,
                angebote?: Array<TSBetreuungsangebotTyp>, institutionen?: Array<string>) {

        this._antragId = antragId;
        this._fallNummer = fallNummer;
        this._familienName = familienName;
        this._antragTyp = antragTyp;
        this._gesuchsperiode = gesuchsperiode;
        this._eingangsdatum = eingangsdatum;
        this._angebote = angebote;
        this._institutionen = institutionen;
    }


    get antragId(): string {
        return this._antragId;
    }

    set antragId(value: string) {
        this._antragId = value;
    }

    get fallNummer(): number {
        return this._fallNummer;
    }

    set fallNummer(value: number) {
        this._fallNummer = value;
    }

    get familienName(): string {
        return this._familienName;
    }

    set familienName(value: string) {
        this._familienName = value;
    }

    get antragTyp(): TSAntragTyp {
        return this._antragTyp;
    }

    set antragTyp(value: TSAntragTyp) {
        this._antragTyp = value;
    }

    get gesuchsperiode(): TSGesuchsperiode {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: TSGesuchsperiode) {
        this._gesuchsperiode = value;
    }

    get eingangsdatum(): moment.Moment {
        return this._eingangsdatum;
    }

    set eingangsdatum(value: moment.Moment) {
        this._eingangsdatum = value;
    }

    get angebote(): Array<TSBetreuungsangebotTyp> {
        return this._angebote;
    }

    set angebote(value: Array<TSBetreuungsangebotTyp>) {
        this._angebote = value;
    }

    get institutionen(): Array<string> {
        return this._institutionen;
    }

    set institutionen(value: Array<string>) {
        this._institutionen = value;
    }
}
