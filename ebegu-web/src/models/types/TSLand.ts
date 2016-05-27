export default class TSLand {
    private _name: string;
    private _code: string;

    constructor(code: string, name: string) {
        this._name = name;
        this._code = code;
    }

    public set name(name: string) {
        this._name = name;
    }

    public set code(code: string) {
        this._code = code;
    }

    public get name(): string {
        return this._name;
    }

    public get code(): string {
        return this._code;
    }
}
