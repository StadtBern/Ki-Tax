import TSAbstractEntity from './TSAbstractEntity';
import TSVerfuegungZeitabschnitt from './TSVerfuegungZeitabschnitt';

export default class TSVerfuegung extends TSAbstractEntity {

    private _generatedBemerkungen: string;
    private _manuelleBemerkungen: string;
    private _zeitabschnitte: Array<TSVerfuegungZeitabschnitt>;
    private _sameVerfuegungsdaten: boolean;
    private _kategorieNormal: boolean;
    private _kategorieMaxEinkommen: boolean;
    private _kategorieKeinPensum: boolean;
    private _kategorieZuschlagZumErwerbspensum: boolean;
    private _kategorieNichtEintreten: boolean;

    constructor(generatedBemerkungen?: string, manuelleBemerkungen?: string, zeitabschnitte?: Array<TSVerfuegungZeitabschnitt>, sameVerfuegungsdaten?: boolean,
                kategorieNormal?: boolean, kategorieMaxEinkommen?: boolean, kategorieKeinPensum?: boolean, kategorieZuschlagZumErwerbspensum?: boolean,
                kategorieNichtEintreten?: boolean) {
        super();
        this._generatedBemerkungen = generatedBemerkungen;
        this._manuelleBemerkungen = manuelleBemerkungen;
        this._zeitabschnitte = zeitabschnitte;
        this._sameVerfuegungsdaten = sameVerfuegungsdaten;
        this._kategorieNormal = kategorieNormal;
        this._kategorieMaxEinkommen = kategorieMaxEinkommen;
        this._kategorieKeinPensum = kategorieKeinPensum;
        this._kategorieZuschlagZumErwerbspensum = kategorieZuschlagZumErwerbspensum;
        this._kategorieNichtEintreten = kategorieNichtEintreten;
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

    get sameVerfuegungsdaten(): boolean {
        return this._sameVerfuegungsdaten;
    }

    set sameVerfuegungsdaten(value: boolean) {
        this._sameVerfuegungsdaten = value;
    }

    get kategorieNormal(): boolean {
        return this._kategorieNormal;
    }

    set kategorieNormal(value: boolean) {
        this._kategorieNormal = value;
    }

    get kategorieMaxEinkommen(): boolean {
        return this._kategorieMaxEinkommen;
    }

    set kategorieMaxEinkommen(value: boolean) {
        this._kategorieMaxEinkommen = value;
    }

    get kategorieKeinPensum(): boolean {
        return this._kategorieKeinPensum;
    }

    set kategorieKeinPensum(value: boolean) {
        this._kategorieKeinPensum = value;
    }

    get kategorieZuschlagZumErwerbspensum(): boolean {
        return this._kategorieZuschlagZumErwerbspensum;
    }

    set kategorieZuschlagZumErwerbspensum(value: boolean) {
        this._kategorieZuschlagZumErwerbspensum = value;
    }

    get kategorieNichtEintreten(): boolean {
        return this._kategorieNichtEintreten;
    }

    set kategorieNichtEintreten(value: boolean) {
        this._kategorieNichtEintreten = value;
    }
}
