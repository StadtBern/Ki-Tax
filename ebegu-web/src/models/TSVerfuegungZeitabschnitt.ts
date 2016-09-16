import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';

export default class TSVerfuegungZeitabschnitt extends TSAbstractDateRangedEntity {

    private _erwerbspensumGS1: number;
    private _erwerbspensumGS2: number;
    private _betreuungspensum: number;
    private _fachstellenpensum: number;
    private _anspruchspensumRest: number;
    private _anspruchberechtigtesPensum: number;
    private _bgPensum: number;
    private _betreuungsstunden: number;
    private _vollkosten: number;
    private _elternbeitrag: number;
    private _abzugFamGroesse: number;
    private _massgebendesEinkommenVorAbzugFamgr: number;
    private _bemerkungen: string;
    private _status: string;


    constructor(erwerbspensumGS1?: number, erwerbspensumGS2?: number, betreuungspensum?: number, fachstellenpensum?: number,
                anspruchspensumRest?: number, anspruchberechtigtesPensum?: number, bgPensum?: number, betreuungsstunden?: number, vollkosten?: number,
                elternbeitrag?: number, abzugFamGroesse?: number, massgebendesEinkommen?: number, bemerkungen?: string, status?: string, gueltigkeit?: TSDateRange) {
        super(gueltigkeit);
        this._erwerbspensumGS1 = erwerbspensumGS1;
        this._erwerbspensumGS2 = erwerbspensumGS2;
        this._betreuungspensum = betreuungspensum;
        this._fachstellenpensum = fachstellenpensum;
        this._anspruchspensumRest = anspruchspensumRest;
        this._anspruchberechtigtesPensum = anspruchberechtigtesPensum;
        this._bgPensum = bgPensum;
        this._betreuungsstunden = betreuungsstunden;
        this._vollkosten = vollkosten;
        this._elternbeitrag = elternbeitrag;
        this._abzugFamGroesse = abzugFamGroesse;
        this._massgebendesEinkommenVorAbzugFamgr = massgebendesEinkommen;
        this._bemerkungen = bemerkungen;
        this._status = status;
    }

    get erwerbspensumGS1(): number {
        return this._erwerbspensumGS1;
    }

    set erwerbspensumGS1(value: number) {
        this._erwerbspensumGS1 = value;
    }

    get erwerbspensumGS2(): number {
        return this._erwerbspensumGS2;
    }

    set erwerbspensumGS2(value: number) {
        this._erwerbspensumGS2 = value;
    }

    get betreuungspensum(): number {
        return this._betreuungspensum;
    }

    set betreuungspensum(value: number) {
        this._betreuungspensum = value;
    }

    get fachstellenpensum(): number {
        return this._fachstellenpensum;
    }

    set fachstellenpensum(value: number) {
        this._fachstellenpensum = value;
    }

    get anspruchspensumRest(): number {
        return this._anspruchspensumRest;
    }

    set anspruchspensumRest(value: number) {
        this._anspruchspensumRest = value;
    }

    get anspruchberechtigtesPensum(): number {
        return this._anspruchberechtigtesPensum;
    }

    set anspruchberechtigtesPensum(value: number) {
        this._anspruchberechtigtesPensum = value;
    }

    get bgPensum(): number {
        return this._bgPensum;
    }

    set bgPensum(value: number) {
        this._bgPensum = value;
    }

    get betreuungsstunden(): number {
        return this._betreuungsstunden;
    }

    set betreuungsstunden(value: number) {
        this._betreuungsstunden = value;
    }

    get vollkosten(): number {
        return this._vollkosten;
    }

    set vollkosten(value: number) {
        this._vollkosten = value;
    }

    get elternbeitrag(): number {
        return this._elternbeitrag;
    }

    set elternbeitrag(value: number) {
        this._elternbeitrag = value;
    }

    get abzugFamGroesse(): number {
        return this._abzugFamGroesse;
    }

    set abzugFamGroesse(value: number) {
        this._abzugFamGroesse = value;
    }

    get massgebendesEinkommenVorAbzugFamgr(): number {
        return this._massgebendesEinkommenVorAbzugFamgr;
    }

    set massgebendesEinkommenVorAbzugFamgr(value: number) {
        this._massgebendesEinkommenVorAbzugFamgr = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }

    get status(): string {
        return this._status;
    }

    set status(value: string) {
        this._status = value;
    }
}
