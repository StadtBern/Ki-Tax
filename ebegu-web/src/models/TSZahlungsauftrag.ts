import {TSAbstractDateRangedEntity} from './TSAbstractDateRangedEntity';
import {TSDateRange} from './types/TSDateRange';
import TSZahlung from './TSZahlung';

export default class TSZahlungsauftrag extends TSAbstractDateRangedEntity {


    private _datumGeneriert: moment.Moment;
    private _datumFaellig: moment.Moment;
    private _ausgeloest: boolean;
    private _beschrieb: string;
    private _betragTotalAuftrag: number;
    private _zahlungen: Array<TSZahlung>;


    constructor(gueltigkeit?: TSDateRange, datumGeneriert?: moment.Moment, datumFaellig?: moment.Moment,
                ausgeloest?: boolean, beschrieb?: string, betragTotalAuftrag?: number, zahlungen?: Array<TSZahlung>) {
        super(gueltigkeit);
        this._datumGeneriert = datumGeneriert;
        this._datumFaellig = datumFaellig;
        this._ausgeloest = ausgeloest;
        this._beschrieb = beschrieb;
        this._betragTotalAuftrag = betragTotalAuftrag;
        this._zahlungen = zahlungen;
    }

    get datumGeneriert(): moment.Moment {
        return this._datumGeneriert;
    }

    set datumGeneriert(value: moment.Moment) {
        this._datumGeneriert = value;
    }

    get datumFaellig(): moment.Moment {
        return this._datumFaellig;
    }

    set datumFaellig(value: moment.Moment) {
        this._datumFaellig = value;
    }

    get ausgeloest(): boolean {
        return this._ausgeloest;
    }

    set ausgeloest(value: boolean) {
        this._ausgeloest = value;
    }

    get beschrieb(): string {
        return this._beschrieb;
    }

    set beschrieb(value: string) {
        this._beschrieb = value;
    }

    get betragTotalAuftrag(): number {
        return this._betragTotalAuftrag;
    }

    set betragTotalAuftrag(value: number) {
        this._betragTotalAuftrag = value;
    }

    get zahlungen(): Array<TSZahlung> {
        return this._zahlungen;
    }

    set zahlungen(value: Array<TSZahlung>) {
        this._zahlungen = value;
    }
}


