import TSAbstractEntity from './TSAbstractEntity';
import TSVerfuegungZeitabschnitt from './TSVerfuegungZeitabschnitt';

export default class TSVerfuegung extends TSAbstractEntity {

    private _generatedBemerkungen: string;
    private _manuelleBemerkungen: string;
    private _zeitabschnitte: Array<TSVerfuegungZeitabschnitt>;


    constructor(generatedBemerkungen?: string, manuelleBemerkungen?: string, zeitabschnitte?: Array<TSVerfuegungZeitabschnitt>) {
        super();
        this._generatedBemerkungen = generatedBemerkungen;
        this._manuelleBemerkungen = manuelleBemerkungen;
        this._zeitabschnitte = zeitabschnitte;
    }

    get generatedBemerkungen(): string {
        return this._generatedBemerkungen;
    }

    set generatedBemerkungen(value: string) {
        this._generatedBemerkungen = value;
    }

    get manuelleBemerkungen(): string {
        return this._manuelleBemerkungen;
    }

    set manuelleBemerkungen(value: string) {
        this._manuelleBemerkungen = value;
    }

    get zeitabschnitte(): Array<TSVerfuegungZeitabschnitt> {
        return this._zeitabschnitte;
    }

    set zeitabschnitte(value: Array<TSVerfuegungZeitabschnitt>) {
        this._zeitabschnitte = value;
    }
}
