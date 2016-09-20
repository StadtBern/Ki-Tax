import TSAbstractEntity from './TSAbstractEntity';

export default class TSFile extends TSAbstractEntity {

    private _fileName: string;

    private _filePfad: string;

    private _fileSize: string;

    get fileName(): string {
        return this._fileName;
    }

    set fileName(value: string) {
        this._fileName = value;
    }

    get filePfad(): string {
        return this._filePfad;
    }

    set filePfad(value: string) {
        this._filePfad = value;
    }

    get fileSize(): string {
        return this._fileSize;
    }

    set fileSize(value: string) {
        this._fileSize = value;
    }
}


