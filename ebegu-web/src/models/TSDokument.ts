import TSFile from './TSFile';
import * as moment from 'moment';

export default class TSDokument extends TSFile {

    private _timestampUpload: moment.Moment;

    constructor(timestampUpload?: moment.Moment) {
        super();
        this._timestampUpload = timestampUpload;
    }

    get timestampUpload(): moment.Moment {
        return this._timestampUpload;
    }

    set timestampUpload(value: moment.Moment) {
        this._timestampUpload = value;
    }
}


