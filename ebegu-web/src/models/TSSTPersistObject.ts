export class TSSTPersistObject {

    private _namespace: string;
    private _data: string;

    constructor(namespace?: string, data?: string) {
        this._namespace = namespace;
        this._data = data;
    }

    get namespace(): string {
        return this._namespace;
    }

    set namespace(value: string) {
        this._namespace = value;
    }

    get data(): string {
        return this._data;
    }

    set data(value: string) {
        this._data = value;
    }
}
