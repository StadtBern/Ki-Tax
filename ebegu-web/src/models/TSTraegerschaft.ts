import TSAbstractEntity from './TSAbstractEntity';

export class TSTraegerschaft extends TSAbstractEntity {

    private _name: string;
    private _active: boolean;
    private _mail: string;

    // just to communicate with client
    private _synchronizedWithOpenIdm: boolean = false;

    constructor(name?: string, active?: boolean, mail?: string) {
        super();
        this._name = name;
        this._active = active;
        this._mail = mail;
    }


    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }

    get active(): boolean {
        return this._active;
    }

    set active(value: boolean) {
        this._active = value;
    }

    get synchronizedWithOpenIdm(): boolean {
        return this._synchronizedWithOpenIdm;
    }

    set synchronizedWithOpenIdm(value: boolean) {
        this._synchronizedWithOpenIdm = value;
    }

    get mail(): string {
        return this._mail;
    }

    set mail(value: string) {
        this._mail = value;
    }
}
