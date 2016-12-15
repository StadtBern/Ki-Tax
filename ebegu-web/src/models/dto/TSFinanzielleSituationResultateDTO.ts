export default class TSFinanzielleSituationResultateDTO {

    private _geschaeftsgewinnDurchschnittGesuchsteller1: number;
    private _geschaeftsgewinnDurchschnittGesuchsteller2: number;
    private _einkommenBeiderGesuchsteller: number;
    private _nettovermoegenFuenfProzent: number;
    private _anrechenbaresEinkommen: number;
    private _abzuegeBeiderGesuchsteller: number;
    private _massgebendesEinkVorAbzFamGr: number;

    constructor(
            geschaeftsgewinnDurchschnittGesuchsteller1?: number, geschaeftsgewinnDurchschnittGesuchsteller2?: number,
            einkommenBeiderGesuchsteller?: number, nettovermoegenFuenfProzent?: number, anrechenbaresEinkommen?: number,
            abzuegeBeiderGesuchsteller?: number, massgebendesEinkVorAbzFamGr?: number) {
        this._geschaeftsgewinnDurchschnittGesuchsteller1 = geschaeftsgewinnDurchschnittGesuchsteller1;
        this._geschaeftsgewinnDurchschnittGesuchsteller2 = geschaeftsgewinnDurchschnittGesuchsteller2;
        this._einkommenBeiderGesuchsteller = einkommenBeiderGesuchsteller;
        this._nettovermoegenFuenfProzent = nettovermoegenFuenfProzent;
        this._anrechenbaresEinkommen = anrechenbaresEinkommen;
        this._abzuegeBeiderGesuchsteller = abzuegeBeiderGesuchsteller;
        this._massgebendesEinkVorAbzFamGr = massgebendesEinkVorAbzFamGr;
    }


    get geschaeftsgewinnDurchschnittGesuchsteller1(): number {
        return this._geschaeftsgewinnDurchschnittGesuchsteller1;
    }

    set geschaeftsgewinnDurchschnittGesuchsteller1(value: number) {
        this._geschaeftsgewinnDurchschnittGesuchsteller1 = value;
    }

    get geschaeftsgewinnDurchschnittGesuchsteller2(): number {
        return this._geschaeftsgewinnDurchschnittGesuchsteller2;
    }

    set geschaeftsgewinnDurchschnittGesuchsteller2(value: number) {
        this._geschaeftsgewinnDurchschnittGesuchsteller2 = value;
    }

    get einkommenBeiderGesuchsteller(): number {
        return this._einkommenBeiderGesuchsteller;
    }

    set einkommenBeiderGesuchsteller(value: number) {
        this._einkommenBeiderGesuchsteller = value;
    }

    get nettovermoegenFuenfProzent(): number {
        return this._nettovermoegenFuenfProzent;
    }

    set nettovermoegenFuenfProzent(value: number) {
        this._nettovermoegenFuenfProzent = value;
    }

    get anrechenbaresEinkommen(): number {
        return this._anrechenbaresEinkommen;
    }

    set anrechenbaresEinkommen(value: number) {
        this._anrechenbaresEinkommen = value;
    }

    get abzuegeBeiderGesuchsteller(): number {
        return this._abzuegeBeiderGesuchsteller;
    }

    set abzuegeBeiderGesuchsteller(value: number) {
        this._abzuegeBeiderGesuchsteller = value;
    }

    get massgebendesEinkVorAbzFamGr(): number {
        return this._massgebendesEinkVorAbzFamGr;
    }

    set massgebendesEinkVorAbzFamGr(value: number) {
        this._massgebendesEinkVorAbzFamGr = value;
    }


}
