import TSAbstractEntity from './TSAbstractEntity';

export class TSFachstelle extends TSAbstractEntity {

    private _name: string;
    private _beschreibung: string;
    private _behinderungsbestaetigung: boolean;

    constructor(name?: string, beschreibung?: string, behinderungsbestaetigung?: boolean) {
        super();
        this._name = name;
        this._beschreibung = beschreibung;
        this._behinderungsbestaetigung = behinderungsbestaetigung;
    }


    get name(): string {
        return this._name;
    }

    set name(value: string) {
        this._name = value;
    }

    get beschreibung(): string {
        return this._beschreibung;
    }

    set beschreibung(value: string) {
        this._beschreibung = value;
    }

    get behinderungsbestaetigung(): boolean {
        return this._behinderungsbestaetigung;
    }

    set behinderungsbestaetigung(value: boolean) {
        this._behinderungsbestaetigung = value;
    }
}
