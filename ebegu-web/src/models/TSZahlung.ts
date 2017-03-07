import TSAbstractEntity from './TSAbstractEntity';
import {TSZahlungsstatus} from './enums/TSZahlungsstatus';

export default class TSZahlung extends TSAbstractEntity {

    private _institutionsName: string;

    private _status: TSZahlungsstatus;

    private _betragTotalZahlung: number;

    constructor(institutionsName?: string, status?: TSZahlungsstatus, betragTotalZahlung?: number) {
        super();
        this._institutionsName = institutionsName;
        this._status = status;
        this._betragTotalZahlung = betragTotalZahlung;
    }

    get institutionsName(): string {
        return this._institutionsName;
    }

    set institutionsName(value: string) {
        this._institutionsName = value;
    }

    get status(): TSZahlungsstatus {
        return this._status;
    }

    set status(value: TSZahlungsstatus) {
        this._status = value;
    }

    get betragTotalZahlung(): number {
        return this._betragTotalZahlung;
    }

    set betragTotalZahlung(value: number) {
        this._betragTotalZahlung = value;
    }
}


