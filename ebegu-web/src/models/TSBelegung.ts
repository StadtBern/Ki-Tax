import TSAbstractEntity from './TSAbstractEntity';
import TSModul from './TSModul';

export default class TSBelegung extends TSAbstractEntity {

    private _module: TSModul[];

    constructor(module?: TSModul[]) {
        super();
        this._module = module;
    }

    public get module(): TSModul[] {
        return this._module;
    }

    public set module(value: TSModul[]) {
        this._module = value;
    }
}
