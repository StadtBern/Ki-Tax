import TSAbstractEntity from './TSAbstractEntity';

export default class TSEinkommensverschlechterungInfo extends TSAbstractEntity {

    private _einkommensverschlechterung: boolean = false;

    private _ekvFuerBasisJahrPlus1: boolean;
    private _ekvFuerBasisJahrPlus2: boolean;

    private _grundFuerBasisJahrPlus1: string;
    private _grundFuerBasisJahrPlus2: string;

    private _stichtagFuerBasisJahrPlus1: moment.Moment;
    private _stichtagFuerBasisJahrPlus2: moment.Moment;

    private _gemeinsameSteuererklaerung_BjP1: boolean;
    private _gemeinsameSteuererklaerung_BjP2: boolean;

    get einkommensverschlechterung(): boolean {
        return this._einkommensverschlechterung;
    }

    set einkommensverschlechterung(value: boolean) {
        this._einkommensverschlechterung = value;
    }

    get ekvFuerBasisJahrPlus1(): boolean {
        return this._ekvFuerBasisJahrPlus1;
    }

    set ekvFuerBasisJahrPlus1(value: boolean) {
        this._ekvFuerBasisJahrPlus1 = value;
    }

    get ekvFuerBasisJahrPlus2(): boolean {
        return this._ekvFuerBasisJahrPlus2;
    }

    set ekvFuerBasisJahrPlus2(value: boolean) {
        this._ekvFuerBasisJahrPlus2 = value;
    }

    get grundFuerBasisJahrPlus1(): string {
        return this._grundFuerBasisJahrPlus1;
    }

    set grundFuerBasisJahrPlus1(value: string) {
        this._grundFuerBasisJahrPlus1 = value;
    }

    get grundFuerBasisJahrPlus2(): string {
        return this._grundFuerBasisJahrPlus2;
    }

    set grundFuerBasisJahrPlus2(value: string) {
        this._grundFuerBasisJahrPlus2 = value;
    }

    get stichtagFuerBasisJahrPlus1(): moment.Moment {
        return this._stichtagFuerBasisJahrPlus1;
    }

    set stichtagFuerBasisJahrPlus1(value: moment.Moment) {
        this._stichtagFuerBasisJahrPlus1 = value;
    }

    get stichtagFuerBasisJahrPlus2(): moment.Moment {
        return this._stichtagFuerBasisJahrPlus2;
    }

    set stichtagFuerBasisJahrPlus2(value: moment.Moment) {
        this._stichtagFuerBasisJahrPlus2 = value;
    }

    get gemeinsameSteuererklaerung_BjP1(): boolean {
        return this._gemeinsameSteuererklaerung_BjP1;
    }

    set gemeinsameSteuererklaerung_BjP1(value: boolean) {
        this._gemeinsameSteuererklaerung_BjP1 = value;
    }

    get gemeinsameSteuererklaerung_BjP2(): boolean {
        return this._gemeinsameSteuererklaerung_BjP2;
    }

    set gemeinsameSteuererklaerung_BjP2(value: boolean) {
        this._gemeinsameSteuererklaerung_BjP2 = value;
    }
}
