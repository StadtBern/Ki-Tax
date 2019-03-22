/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import * as moment from 'moment';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSStatistikParameter extends TSAbstractEntity {
    private _gesuchsperiode: string;
    private _stichtag: moment.Moment;
    private _von: moment.Moment;
    private _bis: moment.Moment;
    private _text: string;
    private _bgGesuche: boolean;
    private _mischGesuche: boolean;
    private _tsGesuche: boolean;
    private _ohneFolgegesuche: boolean;

    constructor(gesuchsperiode?: string, stichtag?: moment.Moment,
                von?: moment.Moment, bis?: moment.Moment) {
        super();
        this._gesuchsperiode = gesuchsperiode;
        this._stichtag = stichtag;
        this._von = von;
        this._bis = bis;
    }

    get gesuchsperiode(): string {
        return this._gesuchsperiode;
    }

    set gesuchsperiode(value: string) {
        this._gesuchsperiode = value;
    }

    get stichtag(): moment.Moment {
        return this._stichtag;
    }

    set stichtag(value: moment.Moment) {
        this._stichtag = value;
    }

    get von(): moment.Moment {
        return this._von;
    }

    set von(value: moment.Moment) {
        this._von = value;
    }

    get bis(): moment.Moment {
        return this._bis;
    }

    set bis(value: moment.Moment) {
        this._bis = value;
    }

    public get text(): string {
        return this._text;
    }

    public set text(value: string) {
        this._text = value;
    }

    public get bgGesuche(): boolean {
        return this._bgGesuche;
    }

    public set bgGesuche(value: boolean) {
        this._bgGesuche = value;
    }

    public get mischGesuche(): boolean {
        return this._mischGesuche;
    }

    public set mischGesuche(value: boolean) {
        this._mischGesuche = value;
    }

    public get tsGesuche(): boolean {
        return this._tsGesuche;
    }

    public set tsGesuche(value: boolean) {
        this._tsGesuche = value;
    }

    public get ohneFolgegesuche(): boolean {
        return this._ohneFolgegesuche;
    }

    public set ohneFolgegesuche(value: boolean) {
        this._ohneFolgegesuche = value;
    }
}
