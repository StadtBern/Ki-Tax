export default class TSFinanzielleSituationResultateDTO {

    private _geschaeftsgewinnDurchschnittGesuchsteller1: number;
    private _geschaeftsgewinnDurchschnittGesuchsteller2: number;
    private _einkommenBeiderGesuchsteller: number;
    private _nettovermoegenFuenfProzent: number;
    private _anrechenbaresEinkommen: number;
    private _abzuegeBeiderGesuchsteller: number;
    private _abzugAufgrundFamiliengroesse: number;
    private _totalAbzuege: number;
    private _massgebendesEinkommen: number;
    private _familiengroesse: number;


    constructor(
            geschaeftsgewinnDurchschnittGesuchsteller1?: number, geschaeftsgewinnDurchschnittGesuchsteller2?: number,
            einkommenBeiderGesuchsteller?: number, nettovermoegenFuenfProzent?: number, anrechenbaresEinkommen?: number,
            abzuegeBeiderGesuchsteller?: number, abzugAufgrundFamiliengroesse?: number, totalAbzuege?: number,
            massgebendesEinkommen?: number, familiengroesse?: number) {
        this._geschaeftsgewinnDurchschnittGesuchsteller1 = geschaeftsgewinnDurchschnittGesuchsteller1;
        this._geschaeftsgewinnDurchschnittGesuchsteller2 = geschaeftsgewinnDurchschnittGesuchsteller2;
        this._einkommenBeiderGesuchsteller = einkommenBeiderGesuchsteller;
        this._nettovermoegenFuenfProzent = nettovermoegenFuenfProzent;
        this._anrechenbaresEinkommen = anrechenbaresEinkommen;
        this._abzuegeBeiderGesuchsteller = abzuegeBeiderGesuchsteller;
        this._abzugAufgrundFamiliengroesse = abzugAufgrundFamiliengroesse;
        this._totalAbzuege = totalAbzuege;
        this._massgebendesEinkommen = massgebendesEinkommen;
        this._familiengroesse = familiengroesse;
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

    get abzugAufgrundFamiliengroesse(): number {
        return this._abzugAufgrundFamiliengroesse;
    }

    set abzugAufgrundFamiliengroesse(value: number) {
        this._abzugAufgrundFamiliengroesse = value;
    }

    get totalAbzuege(): number {
        return this._totalAbzuege;
    }

    set totalAbzuege(value: number) {
        this._totalAbzuege = value;
    }

    get massgebendesEinkommen(): number {
        return this._massgebendesEinkommen;
    }

    set massgebendesEinkommen(value: number) {
        this._massgebendesEinkommen = value;
    }

    get familiengroesse(): number {
        return this._familiengroesse;
    }

    set familiengroesse(value: number) {
        this._familiengroesse = value;
    }
}
