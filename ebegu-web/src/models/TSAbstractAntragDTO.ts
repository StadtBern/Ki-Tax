import EbeguUtil from '../utils/EbeguUtil';

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

    public getQuicksearchString(): string {
        let text = '';
        if (this.fallNummer) {
            text = EbeguUtil.addZerosToNumber(this.fallNummer, 6);
        }
        if (this.familienName) {
            text = text + ' ' + this.familienName;
        }
        return text;
    }

}
