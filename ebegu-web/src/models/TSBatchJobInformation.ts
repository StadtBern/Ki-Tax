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

/**
 * DTO für eine Person aus dem EWK
 */
export default class TSBatchJobInformation {

    private _batchStatus: string;
    private _createTime: moment.Moment;
    private _endTime: moment.Moment;
    private _executionId: number;
    private _exitStatus: string;
    private _jobName: string;
    private _lastUpdatedTime: moment.Moment;
    private _startTime: moment.Moment;

    public get batchStatus(): string {
        return this._batchStatus;
    }

    public set batchStatus(value: string) {
        this._batchStatus = value;
    }

    public get createTime(): moment.Moment {
        return this._createTime;
    }

    public set createTime(value: moment.Moment) {
        this._createTime = value;
    }

    public get endTime(): moment.Moment {
        return this._endTime;
    }

    public set endTime(value: moment.Moment) {
        this._endTime = value;
    }

    public get executionId(): number {
        return this._executionId;
    }

    public set executionId(value: number) {
        this._executionId = value;
    }

    public get exitStatus(): string {
        return this._exitStatus;
    }

    public set exitStatus(value: string) {
        this._exitStatus = value;
    }

    public get jobName(): string {
        return this._jobName;
    }

    public set jobName(value: string) {
        this._jobName = value;
    }

    public get lastUpdatedTime(): moment.Moment {
        return this._lastUpdatedTime;
    }

    public set lastUpdatedTime(value: moment.Moment) {
        this._lastUpdatedTime = value;
    }

    public get startTime(): moment.Moment {
        return this._startTime;
    }

    public set startTime(value: moment.Moment) {
        this._startTime = value;
    }
}
