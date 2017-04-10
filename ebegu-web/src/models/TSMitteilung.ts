import TSAbstractEntity from './TSAbstractEntity';
import TSFall from './TSFall';
import TSUser from './TSUser';
import {TSMitteilungTeilnehmerTyp} from './enums/TSMitteilungTeilnehmerTyp';
import {TSMitteilungStatus} from './enums/TSMitteilungStatus';
import TSBetreuung from './TSBetreuung';
import * as moment from 'moment';

export default class TSMitteilung extends TSAbstractEntity {

    private _fall: TSFall;
    private _betreuung: TSBetreuung;
    private _senderTyp: TSMitteilungTeilnehmerTyp;
    private _empfaengerTyp: TSMitteilungTeilnehmerTyp;
    private _sender: TSUser;
    private _empfaenger: TSUser;
    private _subject: string;
    private _message: string;
    private _mitteilungStatus: TSMitteilungStatus;
    private _sentDatum: moment.Moment;


    constructor(fall?: TSFall, betreuung?: TSBetreuung, senderTyp?: TSMitteilungTeilnehmerTyp, empfaengerTyp?: TSMitteilungTeilnehmerTyp, sender?: TSUser,
                empfaenger?: TSUser, subject?: string, message?: string, mitteilungStatus?: TSMitteilungStatus,
                sentDatum?: moment.Moment) {
        super();
        this._fall = fall;
        this._betreuung = betreuung;
        this._senderTyp = senderTyp;
        this._empfaengerTyp = empfaengerTyp;
        this._sender = sender;
        this._empfaenger = empfaenger;
        this._subject = subject;
        this._message = message;
        this._mitteilungStatus = mitteilungStatus;
        this._sentDatum = sentDatum;
    }

    get fall(): TSFall {
        return this._fall;
    }

    set fall(value: TSFall) {
        this._fall = value;
    }

    get betreuung(): TSBetreuung {
        return this._betreuung;
    }

    set betreuung(value: TSBetreuung) {
        this._betreuung = value;
    }

    get senderTyp(): TSMitteilungTeilnehmerTyp {
        return this._senderTyp;
    }

    set senderTyp(value: TSMitteilungTeilnehmerTyp) {
        this._senderTyp = value;
    }

    get empfaengerTyp(): TSMitteilungTeilnehmerTyp {
        return this._empfaengerTyp;
    }

    set empfaengerTyp(value: TSMitteilungTeilnehmerTyp) {
        this._empfaengerTyp = value;
    }

    get sender(): TSUser {
        return this._sender;
    }

    set sender(value: TSUser) {
        this._sender = value;
    }

    get empfaenger(): TSUser {
        return this._empfaenger;
    }

    set empfaenger(value: TSUser) {
        this._empfaenger = value;
    }

    get subject(): string {
        return this._subject;
    }

    set subject(value: string) {
        this._subject = value;
    }

    get message(): string {
        return this._message;
    }

    set message(value: string) {
        this._message = value;
    }

    get mitteilungStatus(): TSMitteilungStatus {
        return this._mitteilungStatus;
    }

    set mitteilungStatus(value: TSMitteilungStatus) {
        this._mitteilungStatus = value;
    }

    get sentDatum(): moment.Moment {
        return this._sentDatum;
    }

    set sentDatum(value: moment.Moment) {
        this._sentDatum = value;
    }

    get verantwortlicher(): string {
        if (this._fall.verantwortlicher) {
            return this._fall.verantwortlicher.getFullName();
        }
        return '';
    }

    get senderAsString(): string {
        let senderAsString: string;
        if (this.sender.institution) {
            senderAsString = this.sender.institution.name + ', ';
        } else if (this.sender.traegerschaft) {
            senderAsString = this.sender.traegerschaft.name + ', ';
        }
        if (senderAsString) {
            return senderAsString + this.sender.getFullName();
        } else {
            return this.sender.getFullName();
        }
    }

    public isErledigt(): boolean {
        return this.mitteilungStatus === TSMitteilungStatus.ERLEDIGT;
    }
}
