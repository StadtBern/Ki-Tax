import {TSDokumentTyp} from './enums/TSDokumentTyp';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSDokument extends TSAbstractEntity {

    private _dokumentName: string;

    private _dokumentPfad: string;

    private _dokumentTyp: TSDokumentTyp;

    private _dokumentSize: string;

    get dokumentName(): string {
        return this._dokumentName;
    }

    set dokumentName(value: string) {
        this._dokumentName = value;
    }

    get dokumentTyp(): TSDokumentTyp {
        return this._dokumentTyp;
    }

    set dokumentTyp(value: TSDokumentTyp) {
        this._dokumentTyp = value;
    }

    get dokumentPfad(): string {
        return this._dokumentPfad;
    }

    set dokumentPfad(value: string) {
        this._dokumentPfad = value;
    }

    get dokumentSize(): string {
        return this._dokumentSize;
    }

    set dokumentSize(value: string) {
        this._dokumentSize = value;
    }
}


