import TSDokument from './TSDokument';
import {TSDokumentGrundTyp} from './enums/TSDokumentGrundTyp';
import TSAbstractEntity from './TSAbstractEntity';
import {TSDokumentTyp} from './enums/TSDokumentTyp';
import {TSDokumentGrundPersonType} from './enums/TSDokumentGrundPersonType';

export default class TSDokumentGrund extends TSAbstractEntity {

    private _dokumentGrundTyp: TSDokumentGrundTyp;

    private _fullName: string;

    private _tag: string;

    private _personType: TSDokumentGrundPersonType;

    private _personNumber: number;

    private _dokumente: Array<TSDokument>;

    private _dokumentTyp: TSDokumentTyp;

    private _needed: boolean;


    constructor(dokumentGrundTyp?: TSDokumentGrundTyp) {
        super();
        this._dokumentGrundTyp = dokumentGrundTyp;
    }

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

    get personType(): TSDokumentGrundPersonType {
        return this._personType;
    }

    set personType(value: TSDokumentGrundPersonType) {
        this._personType = value;
    }

    get personNumber(): number {
        return this._personNumber;
    }

    set personNumber(value: number) {
        this._personNumber = value;
    }
}


