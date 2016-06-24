import {TSRole, rolePrefix} from './enums/TSRole';
import {TSMandant} from './TSMandant';

export default class TSUser {

    //todo team Hier muessen wir schauen ob alle diese Felder benoetigt werden, dafuer brauchen wir aber zuerst die Daten von IAM
    private _nachname: string;
    private _vorname: string;
    private _username: string;
    private _password: string;
    private _email: string;
    private _mandant: TSMandant;
    private _role: TSRole;

    constructor(vorname?: string, nachname?: string, username?: string,
                password?: string, email?: string, mandant?: TSMandant, role?: TSRole) {
        this._vorname = vorname;
        this._nachname = nachname;
        this._username = username;
        this._password = password;
        this._email = email;
        this._mandant = mandant;
        this._role = role;
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

    get mandant(): TSMandant {
        return this._mandant;
    }

    set mandant(value: TSMandant) {
        this._mandant = value;
    }

    get role(): TSRole {
        return this._role;
    }

    set role(value: TSRole) {
        this._role = value;
    }

    public getFullName(): string {
        return (this.vorname ? this.vorname : '') + ' ' + (this.nachname ? this.nachname : '');
    }

    getRoleKey(): string {
        return rolePrefix() + this.role;
    }
}
