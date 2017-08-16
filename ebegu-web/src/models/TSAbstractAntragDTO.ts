export default class TSAbstractAntragDTO {

    private _fallNummer: number;
    private _familienName: string;

    constructor(fallNummer?: number, familienName?: string) {
        this._fallNummer = fallNummer;
        this._familienName = familienName;
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

}
