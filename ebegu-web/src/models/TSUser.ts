import {TSRole} from './enums/TSRole';

export default class TSUser {

    //todo team Hier muessen wir schauen ob alle diese Felder benoetigt werden, dafuer brauchen wir aber zuerst die Daten von IAM
    private _userId: string;
    private _nachname: string;
    private _vorname: string;
    private _username: string;
    private _password: string;
    private _email: string;
    private _roles: Array<TSRole>;

    constructor(userId?: string, vorname?: string, nachname?: string, username?: string,
                password?: string, email?: string, roles?: Array<TSRole>) {
        this._userId = userId;
        this._vorname = vorname;
        this._nachname = nachname;
        this._username = username;
        this._password = password;
        this._email = email;
        this._roles = roles;
    }


    get userId(): string {
        return this._userId;
    }

    set userId(value: string) {
        this._userId = value;
    }

    get nachname(): string {
        return this._nachname;
    }

    set nachname(value: string) {
        this._nachname = value;
    }

    get vorname(): string {
        return this._vorname;
    }

    set vorname(value: string) {
        this._vorname = value;
    }

    get username(): string {
        return this._username;
    }

    set username(value: string) {
        this._username = value;
    }

    get password(): string {
        return this._password;
    }

    set password(value: string) {
        this._password = value;
    }

    get email(): string {
        return this._email;
    }

    set email(value: string) {
        this._email = value;
    }

    get roles(): Array<TSRole> {
        return this._roles;
    }

    set roles(value: Array<TSRole>) {
        this._roles = value;
    }

    public getFullName(): string {
        return (this.vorname ? this.vorname :  '')  + ' ' + (this.nachname ?  this.nachname : '');
    }
}
