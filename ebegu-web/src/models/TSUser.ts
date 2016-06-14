import {TSRole} from './enums/TSRole';

export default class TSUser {

    private _nachname: string;
    private _vorname: string;
    private _email: string;
    private _role: TSRole;

    constructor(nachname?: string, vorname?: string, email?: string, role?: TSRole) {
        this._nachname = nachname;
        this._vorname = vorname;
        this._email = email;
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

    get email(): string {
        return this._email;
    }

    set email(value: string) {
        this._email = value;
    }

    get role(): TSRole {
        return this._role;
    }

    set role(value: TSRole) {
        this._role = value;
    }
}
