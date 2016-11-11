import {TSBetreuungsangebotTyp} from './enums/TSBetreuungsangebotTyp';
import {TSAntragTyp} from './enums/TSAntragTyp';
import {TSAntragStatus} from './enums/TSAntragStatus';

export default class TSAntragDTO {

    private _antragId: string;
    private _fallNummer: number;
    private _familienName: string;
    private _antragTyp: TSAntragTyp;
    private _eingangsdatum: moment.Moment;
    private _aenderungsdatum: moment.Moment;
    private _verantwortlicher: string;
    private _angebote: Array<TSBetreuungsangebotTyp>;
    private _institutionen: Array<string>;
    private _status: TSAntragStatus;
    private _gesuchsperiodeGueltigAb: moment.Moment;
    private _gesuchsperiodeGueltigBis: moment.Moment;
    private _verfuegt: boolean;
    private _laufnummer : number;

    constructor(antragId?: string, fallNummer?: number, familienName?: string, antragTyp?: TSAntragTyp,
                eingangsdatum?: moment.Moment, aenderungsdatum?: moment.Moment, angebote?: Array<TSBetreuungsangebotTyp>, institutionen?: Array<string>,
                verantwortlicher?: string, status?: TSAntragStatus, gesuchsperiodeGueltigAb?: moment.Moment, gesuchsperiodeGueltigBis?: moment.Moment, verfuegt?: boolean, laufnummer?: number) {

        this._antragId = antragId;
        this._fallNummer = fallNummer;
        this._familienName = familienName;
        this._antragTyp = antragTyp;
        this._eingangsdatum = eingangsdatum;
        this._aenderungsdatum = aenderungsdatum;
        this._angebote = angebote;
        this._institutionen = institutionen;
        this._verantwortlicher = verantwortlicher;
        this._status = status;
        this._gesuchsperiodeGueltigAb = gesuchsperiodeGueltigAb;
        this._gesuchsperiodeGueltigBis = gesuchsperiodeGueltigBis;
        this._verfuegt = verfuegt;
        this._laufnummer = laufnummer;
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

    get gesuchsperiodeGueltigAb(): moment.Moment {
        return this._gesuchsperiodeGueltigAb;
    }

    set gesuchsperiodeGueltigAb(value: moment.Moment) {
        this._gesuchsperiodeGueltigAb = value;
    }

    get gesuchsperiodeGueltigBis(): moment.Moment {
        return this._gesuchsperiodeGueltigBis;
    }

    set gesuchsperiodeGueltigBis(value: moment.Moment) {
        this._gesuchsperiodeGueltigBis = value;
    }

    get verfuegt(): boolean {
        return this._verfuegt;
    }

    set verfuegt(value: boolean) {
        this._verfuegt = value;
    }

    get laufnummer(): number {
        return this._laufnummer;
    }

    set laufnummer(value: number) {
        this._laufnummer = value;
    }


    get gesuchsperiodeString() {
        if (this._gesuchsperiodeGueltigAb && this._gesuchsperiodeGueltigBis) {
            return this._gesuchsperiodeGueltigAb.year() + '/'
                + this._gesuchsperiodeGueltigBis.year();
        }
        return undefined;
    }
}
