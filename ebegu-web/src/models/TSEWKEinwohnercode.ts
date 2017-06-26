import TSAbstractEntity from './TSAbstractEntity';
import * as moment from 'moment';
/**
 * DTO für einen Einwohnercode aus dem EWK
 */
export default class TSEWKEinwohnercode extends TSAbstractEntity {


    private _code: string;
    private _codeTxt: string;
    private _gueltigVon: moment.Moment;
    private _gueltigBis: moment.Moment;


    constructor(code?: string, codeTxt?: string, gueltigVon?: moment.Moment, gueltigBis?: moment.Moment) {
        super();
        this._code = code;
        this._codeTxt = codeTxt;
        this._gueltigVon = gueltigVon;
        this._gueltigBis = gueltigBis;
    }

    get code(): string {
        return this._code;
    }

    set code(value: string) {
        this._code = value;
    }

    get codeTxt(): string {
        return this._codeTxt;
    }

    set codeTxt(value: string) {
        this._codeTxt = value;
    }

    get gueltigVon(): moment.Moment {
        return this._gueltigVon;
    }

    set gueltigVon(value: moment.Moment) {
        this._gueltigVon = value;
    }

    get gueltigBis(): moment.Moment {
        return this._gueltigBis;
    }

    set gueltigBis(value: moment.Moment) {
        this._gueltigBis = value;
    }
}
