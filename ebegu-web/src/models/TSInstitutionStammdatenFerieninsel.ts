/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import EbeguUtil from '../utils/EbeguUtil';
import {TSFerienname} from './enums/TSFerienname';
import TSAbstractEntity from './TSAbstractEntity';

export default class TSInstitutionStammdatenFerieninsel extends TSAbstractEntity {

    private _ausweichstandortSommerferien: string;
    private _ausweichstandortHerbstferien: string;
    private _ausweichstandortSportferien: string;
    private _ausweichstandortFruehlingsferien: string;

    constructor() {
        super();
    }

    get ausweichstandortSommerferien(): string {
        return this._ausweichstandortSommerferien;
    }

    set ausweichstandortSommerferien(value: string) {
        this._ausweichstandortSommerferien = value;
    }

    get ausweichstandortHerbstferien(): string {
        return this._ausweichstandortHerbstferien;
    }

    set ausweichstandortHerbstferien(value: string) {
        this._ausweichstandortHerbstferien = value;
    }

    get ausweichstandortSportferien(): string {
        return this._ausweichstandortSportferien;
    }

    set ausweichstandortSportferien(value: string) {
        this._ausweichstandortSportferien = value;
    }

    get ausweichstandortFruehlingsferien(): string {
        return this._ausweichstandortFruehlingsferien;
    }

    set ausweichstandortFruehlingsferien(value: string) {
        this._ausweichstandortFruehlingsferien = value;
    }

    public isDefined(ferienname: TSFerienname): boolean {
        switch (ferienname) {
            case TSFerienname.FRUEHLINGSFERIEN:
                return !EbeguUtil.isEmptyStringNullOrUndefined(this.ausweichstandortFruehlingsferien);
            case TSFerienname.HERBSTFERIEN:
                return !EbeguUtil.isEmptyStringNullOrUndefined(this.ausweichstandortHerbstferien);
            case TSFerienname.SOMMERFERIEN:
                return !EbeguUtil.isEmptyStringNullOrUndefined(this.ausweichstandortSommerferien);
            case TSFerienname.SPORTFERIEN:
                return !EbeguUtil.isEmptyStringNullOrUndefined(this.ausweichstandortSportferien);
            default:
                return false;
        }
    }

    public getAusweichstandortFromFerienname(ferienname: TSFerienname): string {
        switch (ferienname) {
            case TSFerienname.FRUEHLINGSFERIEN:
                return this.ausweichstandortFruehlingsferien;
            case TSFerienname.HERBSTFERIEN:
                return this.ausweichstandortHerbstferien;
            case TSFerienname.SOMMERFERIEN:
                return this.ausweichstandortSommerferien;
            case TSFerienname.SPORTFERIEN:
                return this.ausweichstandortSportferien;
            default:
                return '';
        }
    }
}
