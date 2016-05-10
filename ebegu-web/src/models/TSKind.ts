import TSAbstractPersonEntity from './TSAbstractPersonEntity';
import {TSGeschlecht} from './enums/TSGeschlecht';
import {TSPensumFachstelle} from './TSPensumFachstelle';

export default class TSKind extends TSAbstractPersonEntity {

    private _wohnhaftImGleichenHaushalt: number;
    private _unterstuetzungspflicht: boolean;
    private _familienErgaenzendeBetreuung: boolean;
    private _mutterspracheDeutsch: boolean;
    private _pensumFachstelle: TSPensumFachstelle;
    private _bemerkungen: string;

    constructor(vorname?: string, nachname?: string, geburtsdatum?: moment.Moment, geschlecht?: TSGeschlecht,
                wohnhaftImGleichenHaushalt?: number, unterstuetzungspflicht?: boolean, familienErgaenzendeBetreuung?: boolean,
                mutterspracheDeutsch?: boolean, pensumFachstelle?: TSPensumFachstelle, bemerkungen?: string) {

        super(vorname, nachname, geburtsdatum, geschlecht);
        this._wohnhaftImGleichenHaushalt = wohnhaftImGleichenHaushalt;
        this._unterstuetzungspflicht = unterstuetzungspflicht;
        this._familienErgaenzendeBetreuung = familienErgaenzendeBetreuung;
        this._mutterspracheDeutsch = mutterspracheDeutsch;
        this._pensumFachstelle = pensumFachstelle;
        this._bemerkungen = bemerkungen;
    }


    get wohnhaftImGleichenHaushalt(): number {
        return this._wohnhaftImGleichenHaushalt;
    }

    set wohnhaftImGleichenHaushalt(value: number) {
        this._wohnhaftImGleichenHaushalt = value;
    }

    get unterstuetzungspflicht(): boolean {
        return this._unterstuetzungspflicht;
    }

    set unterstuetzungspflicht(value: boolean) {
        this._unterstuetzungspflicht = value;
    }

    get familienErgaenzendeBetreuung(): boolean {
        return this._familienErgaenzendeBetreuung;
    }

    set familienErgaenzendeBetreuung(value: boolean) {
        this._familienErgaenzendeBetreuung = value;
    }

    get mutterspracheDeutsch(): boolean {
        return this._mutterspracheDeutsch;
    }

    set mutterspracheDeutsch(value: boolean) {
        this._mutterspracheDeutsch = value;
    }

    get pensumFachstelle(): TSPensumFachstelle {
        return this._pensumFachstelle;
    }

    set pensumFachstelle(value: TSPensumFachstelle) {
        this._pensumFachstelle = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }
}
