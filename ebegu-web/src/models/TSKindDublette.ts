export default class TSKindDublette {

    private _gesuchId: string;
    private _fallNummer: number;
    private _kindNummer: number;

    constructor(gesuchId?: string, fallNummer?: number, kindNummer?: number) {
        this._gesuchId = gesuchId;
        this._fallNummer = fallNummer;
        this._kindNummer = kindNummer;
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

    get kindNummer(): number {
        return this._kindNummer;
    }

    set kindNummer(value: number) {
        this._kindNummer = value;
    }
}
