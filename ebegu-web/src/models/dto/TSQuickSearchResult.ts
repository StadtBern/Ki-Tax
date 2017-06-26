import TSSearchResultEntry from './TSSearchResultEntry';

export default class TSQuickSearchResult {


    private _resultEntities: Array<TSSearchResultEntry>;
    private _totalResultSize: number;


    constructor(antragDTOs?: Array<TSSearchResultEntry>, totalResultSize?: number) {
        this._resultEntities = antragDTOs;
        this._totalResultSize = totalResultSize;
    }


    get resultEntities(): Array<TSSearchResultEntry> {
        return this._resultEntities;
    }

    set resultEntities(value: Array<TSSearchResultEntry>) {
        this._resultEntities = value;
    }

    get totalResultSize(): number {
        return this._totalResultSize;
    }

    set totalResultSize(value: number) {
        this._totalResultSize = value;
    }
}
