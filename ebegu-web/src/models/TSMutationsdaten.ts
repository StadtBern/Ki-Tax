import TSAbstractEntity from './TSAbstractEntity';

export default class TSMutationsdaten extends TSAbstractEntity {

    private _mutationFamiliensituation: boolean;
    private _mutationGesuchsteller: boolean;
    private _mutationUmzug: boolean;
    private _mutationKind: boolean;
    private _mutationBetreuung: boolean;
    private _mutationAbwesenheit: boolean;
    private _mutationErwerbspensum: boolean;
    private _mutationFinanzielleSituation: boolean;
    private _mutationEinkommensverschlechterung: boolean;

    constructor(mutationFamiliensituation?: boolean, mutationGesuchsteller?: boolean, mutationUmzug?: boolean,
                mutationKind?: boolean, mutationBetreuung?: boolean, mutationAbwesenheit?: boolean, mutationErwerbspensum?: boolean,
                mutationFinanzielleSituation?: boolean, mutationEinkommensverschlechterung?: boolean) {
        super();
        this._mutationFamiliensituation = mutationFamiliensituation;
        this._mutationGesuchsteller = mutationGesuchsteller;
        this._mutationUmzug = mutationUmzug;
        this._mutationKind = mutationKind;
        this._mutationBetreuung = mutationBetreuung;
        this._mutationAbwesenheit = mutationAbwesenheit;
        this._mutationErwerbspensum = mutationErwerbspensum;
        this._mutationFinanzielleSituation = mutationFinanzielleSituation;
        this._mutationEinkommensverschlechterung = mutationEinkommensverschlechterung;
    }

    get mutationFamiliensituation(): boolean {
        return this._mutationFamiliensituation;
    }

    set mutationFamiliensituation(value: boolean) {
        this._mutationFamiliensituation = value;
    }

    get mutationGesuchsteller(): boolean {
        return this._mutationGesuchsteller;
    }

    set mutationGesuchsteller(value: boolean) {
        this._mutationGesuchsteller = value;
    }

    get mutationUmzug(): boolean {
        return this._mutationUmzug;
    }

    set mutationUmzug(value: boolean) {
        this._mutationUmzug = value;
    }

    get mutationKind(): boolean {
        return this._mutationKind;
    }

    set mutationKind(value: boolean) {
        this._mutationKind = value;
    }

    get mutationBetreuung(): boolean {
        return this._mutationBetreuung;
    }

    set mutationBetreuung(value: boolean) {
        this._mutationBetreuung = value;
    }

    get mutationAbwesenheit(): boolean {
        return this._mutationAbwesenheit;
    }

    set mutationAbwesenheit(value: boolean) {
        this._mutationAbwesenheit = value;
    }

    get mutationErwerbspensum(): boolean {
        return this._mutationErwerbspensum;
    }

    set mutationErwerbspensum(value: boolean) {
        this._mutationErwerbspensum = value;
    }

    get mutationFinanzielleSituation(): boolean {
        return this._mutationFinanzielleSituation;
    }

    set mutationFinanzielleSituation(value: boolean) {
        this._mutationFinanzielleSituation = value;
    }

    get mutationEinkommensverschlechterung(): boolean {
        return this._mutationEinkommensverschlechterung;
    }

    set mutationEinkommensverschlechterung(value: boolean) {
        this._mutationEinkommensverschlechterung = value;
    }
}
