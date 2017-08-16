import TSAbstractAntragDTO from '../TSAbstractAntragDTO';

export default class TSSearchResultEntry {

    private _additionalInformation: string;
    private _antragDTO: TSAbstractAntragDTO;
    private _entity: string;
    private _resultId: string;
    private _gesuchID: string;
    private _fallID: string;
    private _text: string;

    constructor() {
    }

    get additionalInformation(): string {
        return this._additionalInformation;
    }

    set additionalInformation(value: string) {
        this._additionalInformation = value;
    }

    get antragDTO(): TSAbstractAntragDTO {
        return this._antragDTO;
    }

    set antragDTO(value: TSAbstractAntragDTO) {
        this._antragDTO = value;
    }

    get entity(): string {
        return this._entity;
    }

    set entity(value: string) {
        this._entity = value;
    }

    get resultId(): string {
        return this._resultId;
    }

    set resultId(value: string) {
        this._resultId = value;
    }

    get gesuchID(): string {
        return this._gesuchID;
    }

    set gesuchID(value: string) {
        this._gesuchID = value;
    }

    public get fallID(): string {
        return this._fallID;
    }

    public set fallID(value: string) {
        this._fallID = value;
    }

    get text(): string {
        return this._text;
    }

    set text(value: string) {
        this._text = value;
    }
}
