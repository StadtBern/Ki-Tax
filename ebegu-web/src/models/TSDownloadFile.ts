import TSFile from './TSFile';

export default class TSDownloadFile extends TSFile {

    private _accessToken: string;

    get accessToken(): string {
        return this._accessToken;
    }

    set accessToken(value: string) {
        this._accessToken = value;
    }
}


