import TSAbstractEntity from './TSAbstractEntity';

export default class TSFile extends TSAbstractEntity {

    private _filename: string;

    private _filepfad: string;

    private _filesize: string;

    get filename(): string {
        return this._filename;
    }

    set filename(value: string) {
        this._filename = value;
    }

    get filepfad(): string {
        return this._filepfad;
    }

    set filepfad(value: string) {
        this._filepfad = value;
    }

    get filesize(): string {
        return this._filesize;
    }

    set filesize(value: string) {
        this._filesize = value;
    }
}


