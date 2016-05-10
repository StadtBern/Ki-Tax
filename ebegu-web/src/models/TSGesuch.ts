import TSAbstractEntity from './TSAbstractEntity';
import TSFall from './TSFall';
import TSGesuchsteller from './TSGesuchsteller';

export default class TSGesuch extends TSAbstractEntity {
    private _fall: TSFall;
    private _gesuchsteller1: TSGesuchsteller;
    private _gesuchsteller2: TSGesuchsteller;


    public get fall(): TSFall {
        return this._fall;
    }

    public set fall(value: TSFall) {
        this._fall = value;
    }

    public get gesuchsteller1(): TSGesuchsteller {
        return this._gesuchsteller1;
    }

    public set gesuchsteller1(value: TSGesuchsteller) {
        this._gesuchsteller1 = value;
    }

    public get gesuchsteller2(): TSGesuchsteller {
        return this._gesuchsteller2;
    }

    public set gesuchsteller2(value: TSGesuchsteller) {
        this._gesuchsteller2 = value;
    }
}
