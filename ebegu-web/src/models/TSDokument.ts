import TSFile from './TSFile';
import * as moment from 'moment';
import TSUser from './TSUser';

export default class TSDokument extends TSFile {

    private _timestampUpload: moment.Moment;
    private _userUploaded: TSUser;

    constructor(timestampUpload?: moment.Moment, userUploaded?: TSUser) {
        super();
        this._timestampUpload = timestampUpload;
        this._userUploaded = userUploaded;
    }

    get timestampUpload(): moment.Moment {
        return this._timestampUpload;
    }

    set timestampUpload(value: moment.Moment) {
        this._timestampUpload = value;
    }

    get userUploaded(): TSUser {
        return this._userUploaded;
    }

    set userUploaded(value: TSUser) {
        this._userUploaded = value;
    }
}


