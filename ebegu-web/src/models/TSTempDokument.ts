import TSAbstractEntity from './TSAbstractEntity';

export default class TSTempDokument extends TSAbstractEntity {

    private _accessToken: string;

    get accessToken(): string {
        return this._accessToken;
    }

    set accessToken(value: string) {
        this._accessToken = value;
    }
}


