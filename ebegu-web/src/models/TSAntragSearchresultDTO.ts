import TSPendenzJA from './TSPendenzJA';

export default class TSAntragSearchresultDTO {

    private _antragDTOs: Array<TSPendenzJA>;
    private _totalResultSize: number;


    constructor(antragDTOs?: Array<TSPendenzJA>, totalResultSize?: number) {
        this._antragDTOs = antragDTOs;
        this._totalResultSize = totalResultSize;
    }


    get antragDTOs(): Array<TSPendenzJA> {
        return this._antragDTOs;
    }

    set antragDTOs(value: Array<TSPendenzJA>) {
        this._antragDTOs = value;
    }

    get totalResultSize(): number {
        return this._totalResultSize;
    }

    set totalResultSize(value: number) {
        this._totalResultSize = value;
    }
}
