import TSGesuchsteller from './TSGesuchsteller';
import TSKindContainer from './TSKindContainer';
import TSAbstractAntragEntity from './TSAbstractAntragEntity';

export default class TSGesuch extends TSAbstractAntragEntity {

    private _gesuchsteller1: TSGesuchsteller;
    private _gesuchsteller2: TSGesuchsteller;
    private _kindContainer: Array<TSKindContainer>;
    private _einkommensverschlechterung: boolean;



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

    get kindContainer(): Array<TSKindContainer> {
        return this._kindContainer;
    }

    set kindContainer(value: Array<TSKindContainer>) {
        this._kindContainer = value;
    }

    get einkommensverschlechterung(): boolean {
        return this._einkommensverschlechterung;
    }

    set einkommensverschlechterung(value: boolean) {
        this._einkommensverschlechterung = value;
    }
}
