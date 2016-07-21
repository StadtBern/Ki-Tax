import TSDokumentGrund from '../TSDokumentGrund';
export default class TSDokumenteDTO {

    private _dokumentGruende: Array<TSDokumentGrund>;

    get dokumentGruende(): Array<TSDokumentGrund> {
        return this._dokumentGruende;
    }

    set dokumentGruende(value: Array<TSDokumentGrund>) {
        this._dokumentGruende = value;
    }
}
