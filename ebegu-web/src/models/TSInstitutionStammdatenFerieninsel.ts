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

import TSAbstractEntity from './TSAbstractEntity';

export default class TSInstitutionStammdatenFerieninsel extends TSAbstractEntity {

    private _ausweichstandortSommerferien: String;
    private _ausweichstandortHerbstferien: String;
    private _ausweichstandortSportferien: String;
    private _ausweichstandortFruehlingsferien: String;

    constructor() {
        super();
    }

    get ausweichstandortSommerferien(): String {
        return this._ausweichstandortSommerferien;
    }

    set ausweichstandortSommerferien(value: String) {
        this._ausweichstandortSommerferien = value;
    }

    get ausweichstandortHerbstferien(): String {
        return this._ausweichstandortHerbstferien;
    }

    set ausweichstandortHerbstferien(value: String) {
        this._ausweichstandortHerbstferien = value;
    }

    get ausweichstandortSportferien(): String {
        return this._ausweichstandortSportferien;
    }

    set ausweichstandortSportferien(value: String) {
        this._ausweichstandortSportferien = value;
    }

    get ausweichstandortFruehlingsferien(): String {
        return this._ausweichstandortFruehlingsferien;
    }

    set ausweichstandortFruehlingsferien(value: String) {
        this._ausweichstandortFruehlingsferien = value;
    }
}
