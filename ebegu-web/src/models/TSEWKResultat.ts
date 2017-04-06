import TSAbstractEntity from './TSAbstractEntity';
import TSEWKPerson from './TSEWKPerson';
/**
 * DTO für Resultate aus dem EWK
 */
export default class TSEWKResultat extends TSAbstractEntity {

    private _maxResultate: number;
    private _anzahlResultate: number;
    private _personen: Array<TSEWKPerson>;


    constructor(maxResultate?: number, anzahlResultate?: number, personen?: Array<TSEWKPerson>) {
        super();
        this._maxResultate = maxResultate;
        this._anzahlResultate = anzahlResultate;
        this._personen = personen;
    }

    get maxResultate(): number {
        return this._maxResultate;
    }

    set maxResultate(value: number) {
        this._maxResultate = value;
    }

    get anzahlResultate(): number {
        return this._anzahlResultate;
    }

    set anzahlResultate(value: number) {
        this._anzahlResultate = value;
    }

    get personen(): Array<TSEWKPerson> {
        return this._personen;
    }

    set personen(value: Array<TSEWKPerson>) {
        this._personen = value;
    }

    public isTooManyResults(): boolean {
        return this.anzahlResultate > this.maxResultate;
    }
}
