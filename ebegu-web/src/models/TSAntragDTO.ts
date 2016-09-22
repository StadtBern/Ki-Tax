import TSGesuchsperiode from './TSGesuchsperiode';
import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSAntragTyp} from './enums/TSAntragTyp';
import {TSAntragStatus} from './enums/TSAntragStatus';

export default class TSAntragDTO {

    private _antragId: string;
    private _fallNummer: number;
    private _familienName: string;
    private _antragTyp: TSAntragTyp;
    private _gesuchsperiode: TSGesuchsperiode;
    private _eingangsdatum: moment.Moment;
    private _aenderungsdatum: moment.Moment;
    private _verantwortlicher: string;
    private _angebote: Array<TSBetreuungsangebotTyp>;
    private _institutionen: Array<string>;
    private _status: TSAntragStatus;

    constructor(antragId?: string, fallNummer?: number, familienName?: string, antragTyp?: TSAntragTyp, gesuchsperiode?: TSGesuchsperiode,
                eingangsdatum?: moment.Moment, aenderungsdatum?: moment.Moment, angebote?: Array<TSBetreuungsangebotTyp>, institutionen?: Array<string>,
                verantwortlicher?: string, status?: TSAntragStatus) {

        this._antragId = antragId;
        this._fallNummer = fallNummer;
        this._familienName = familienName;
        this._antragTyp = antragTyp;
        this._gesuchsperiode = gesuchsperiode;
        this._eingangsdatum = eingangsdatum;
        this._aenderungsdatum = aenderungsdatum;
        this._angebote = angebote;
        this._institutionen = institutionen;
        this._verantwortlicher = verantwortlicher;
        this._status = status;
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


    get aenderungsdatum(): moment.Moment {
        return this._aenderungsdatum;
    }

    set aenderungsdatum(value: moment.Moment) {
        this._aenderungsdatum = value;
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

    get verantwortlicher(): string {
        return this._verantwortlicher;
    }

    set verantwortlicher(value: string) {
        this._verantwortlicher = value;
    }

    get status(): TSAntragStatus {
        return this._status;
    }

    set status(value: TSAntragStatus) {
        this._status = value;
    }
}
