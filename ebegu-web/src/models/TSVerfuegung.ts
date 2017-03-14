import TSAbstractEntity from './TSAbstractEntity';
import TSVerfuegungZeitabschnitt from './TSVerfuegungZeitabschnitt';
import {TSVerfuegungZeitabschnittZahlungsstatus} from './enums/TSVerfuegungZeitabschnittZahlungsstatus';

export default class TSVerfuegung extends TSAbstractEntity {

    private _generatedBemerkungen: string;
    private _manuelleBemerkungen: string;
    private _zeitabschnitte: Array<TSVerfuegungZeitabschnitt>;
    private _kategorieNormal: boolean;
    private _kategorieMaxEinkommen: boolean;
    private _kategorieKeinPensum: boolean;
    private _kategorieZuschlagZumErwerbspensum: boolean;
    private _kategorieNichtEintreten: boolean;

    constructor(generatedBemerkungen?: string, manuelleBemerkungen?: string, zeitabschnitte?: Array<TSVerfuegungZeitabschnitt>,
                kategorieNormal?: boolean, kategorieMaxEinkommen?: boolean, kategorieKeinPensum?: boolean, kategorieZuschlagZumErwerbspensum?: boolean,
                kategorieNichtEintreten?: boolean) {
        super();
        this._generatedBemerkungen = generatedBemerkungen;
        this._manuelleBemerkungen = manuelleBemerkungen;
        this._zeitabschnitte = zeitabschnitte;
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

    /**
     * Checks whether all Zeitabschnitte have the same data as the previous (vorgaenger) Verfuegung.
     */
    public areSameVerfuegungsdaten(): boolean {
        for (let i = 0; i < this._zeitabschnitte.length; i++) {
            if (this._zeitabschnitte[i].sameVerfuegungsdaten !== true) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether all Zeitabschnitte that have been paid have the same data as the previous (vorgaenger) Verfuegung.
     */
    public isSameVerrechneteVerfuegungdaten(): boolean {
        for (let i = 0; i < this._zeitabschnitte.length; i++) {
            if (this._zeitabschnitte[i].sameVerfuegungsdaten !== true && this._zeitabschnitte[i].zahlungsstatus === TSVerfuegungZeitabschnittZahlungsstatus.VERRECHNET) {
                return false;
            }
        }
        return true;
    }
}
