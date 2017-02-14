/**
 * Hilfsdto welches verwendet werden kann um eine Betreuungsnummer in ihre subteile aufzuteilen
 */
export default class TSBetreuungsnummerParts {

    private _jahr: string;
    private _fallId: string;
    private _kindnummer: string;
    private _betreuungsnummer: string;

    constructor(jahr: string, fallId: string, kindnummer: string, betreuungsnummer: string) {
        this._jahr = jahr;
        this._fallId = fallId;
        this._kindnummer = kindnummer;
        this._betreuungsnummer = betreuungsnummer;
    }

    get jahr(): string {
        return this._jahr;
    }

    set jahr(value: string) {
        this._jahr = value;
    }

    get fallId(): string {
        return this._fallId;
    }

    set fallId(value: string) {
        this._fallId = value;
    }

    get kindnummer(): string {
        return this._kindnummer;
    }

    set kindnummer(value: string) {
        this._kindnummer = value;
    }

    get betreuungsnummer(): string {
        return this._betreuungsnummer;
    }

    set betreuungsnummer(value: string) {
        this._betreuungsnummer = value;
    }

}
