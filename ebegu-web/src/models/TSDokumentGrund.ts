import TSDokument from './TSDokument';
import {TSDokumentGrundTyp} from './enums/TSDokumentGrundTyp';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSDokumentGrund extends TSAbstractEntity {

    private _dokumentGrundTyp: TSDokumentGrundTyp;

    private _fullname: string;

    private _tag: string;

    private _dokumente: Array<TSDokument>;

    get dokumentGrundTyp(): TSDokumentGrundTyp {
        return this._dokumentGrundTyp;
    }

    set dokumentGrundTyp(value: TSDokumentGrundTyp) {
        this._dokumentGrundTyp = value;
    }

    get fullname(): string {
        return this._fullname;
    }

    set fullname(value: string) {
        this._fullname = value;
    }

    get tag(): string {
        return this._tag;
    }

    set tag(value: string) {
        this._tag = value;
    }

    get dokumente(): Array<TSDokument> {
        return this._dokumente;
    }

    set dokumente(value: Array<TSDokument>) {
        this._dokumente = value;
    }
}


