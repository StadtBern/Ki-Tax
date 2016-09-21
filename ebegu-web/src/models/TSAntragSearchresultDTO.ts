import TSAntragDTO from './TSAntragDTO';

export default class TSAntragSearchresultDTO {

    private _antragDTOs: Array<TSAntragDTO>;
    private _totalResultSize: number;


    constructor(antragDTOs?: Array<TSAntragDTO>, totalResultSize?: number) {
        this._antragDTOs = antragDTOs;
        this._totalResultSize = totalResultSize;
    }


    get antragDTOs(): Array<TSAntragDTO> {
        return this._antragDTOs;
    }

    set antragDTOs(value: Array<TSAntragDTO>) {
        this._antragDTOs = value;
    }

    get totalResultSize(): number {
        return this._totalResultSize;
    }

    set totalResultSize(value: number) {
        this._totalResultSize = value;
    }
}
