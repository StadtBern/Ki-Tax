import TSAbstractEntity from './TSAbstractEntity';

export class TSMandant extends TSAbstractEntity {

    private _name: string;

    constructor(name?: string) {
        this._name = name;
    }


    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }
}
