export default class TSKindDublette {

    private _gesuchId: string;
    private _fallNummer: number;
    private _kindNummerOriginal: number;
    private _kindNummerDublette: number;

    constructor(gesuchId?: string, fallNummer?: number, kindNummerOriginal?: number, kindNummerDublette?: number) {
        this._gesuchId = gesuchId;
        this._fallNummer = fallNummer;
        this._kindNummerOriginal = kindNummerOriginal;
        this._kindNummerDublette = kindNummerDublette;
    }

    get gesuchId(): string {
        return this._gesuchId;
    }

    set gesuchId(value: string) {
        this._gesuchId = value;
    }

    get fallNummer(): number {
        return this._fallNummer;
    }

    set fallNummer(value: number) {
        this._fallNummer = value;
    }

    get kindNummerOriginal(): number {
        return this._kindNummerOriginal;
    }

    set kindNummerOriginal(value: number) {
        this._kindNummerOriginal = value;
    }

    get kindNummerDublette(): number {
        return this._kindNummerDublette;
    }

    set kindNummerDublette(value: number) {
        this._kindNummerDublette = value;
    }
}
