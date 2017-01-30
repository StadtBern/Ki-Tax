import TSAntragDTO from '../TSAntragDTO';
export default class TSSearchResultEntry {

    private _additionalInformation: string;
    private _antragDTO: TSAntragDTO;
    private _entity: string;
    private _resultId: string;
    private _gesuchID: string;
    private _text: string;

    constructor() {
    }

    get additionalInformation(): string {
        return this._additionalInformation;
    }

    set additionalInformation(value: string) {
        this._additionalInformation = value;
    }

    get antragDTO(): TSAntragDTO {
        return this._antragDTO;
    }

    set antragDTO(value: TSAntragDTO) {
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

    get text(): string {
        return this._text;
    }

    set text(value: string) {
        this._text = value;
    }
}
