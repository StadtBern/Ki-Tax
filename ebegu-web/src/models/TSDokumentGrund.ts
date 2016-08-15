import TSDokument from './TSDokument';
import {TSDokumentGrundTyp} from './enums/TSDokumentGrundTyp';
import TSAbstractEntity from './TSAbstractEntity';
import {TSDokumentTyp} from './enums/TSDokumentTyp';

export default class TSDokumentGrund extends TSAbstractEntity {

    constructor(dokumentGrundTyp?: TSDokumentGrundTyp) {
        super();
        this._dokumentGrundTyp = dokumentGrundTyp;
    }

    private _dokumentGrundTyp: TSDokumentGrundTyp;

    private _fullName: string;

    private _tag: string;

    private _dokumente: Array<TSDokument>;

    private _dokumentTyp: TSDokumentTyp;

    private _needed: boolean;

    get dokumentGrundTyp(): TSDokumentGrundTyp {
        return this._dokumentGrundTyp;
    }

    set dokumentGrundTyp(value: TSDokumentGrundTyp) {
        this._dokumentGrundTyp = value;
    }

    get fullName(): string {
        return this._fullName;
    }

    set fullName(value: string) {
        this._fullName = value;
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

    get dokumentTyp(): TSDokumentTyp {
        return this._dokumentTyp;
    }

    set dokumentTyp(value: TSDokumentTyp) {
        this._dokumentTyp = value;
    }
    
    get needed(): boolean {
        return this._needed;
    }

    set needed(value: boolean) {
        this._needed = value;
    }
}


