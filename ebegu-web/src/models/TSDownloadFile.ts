import TSAbstractEntity from './TSAbstractEntity';

export default class TSDownloadFile extends TSAbstractEntity {

    private _accessToken: string;

    get accessToken(): string {
        return this._accessToken;
    }

    set accessToken(value: string) {
        this._accessToken = value;
    }
}


