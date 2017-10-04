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

import TSAbstractEntity from './TSAbstractEntity';
import {TSWizardStepName} from './enums/TSWizardStepName';
import {TSWizardStepStatus} from './enums/TSWizardStepStatus';

export default class TSWizardStep extends TSAbstractEntity {

    private _gesuchId: string;
    private _wizardStepName: TSWizardStepName;
    private _wizardStepStatus: TSWizardStepStatus;
    private _bemerkungen: string;
    private _verfuegbar: boolean;

    constructor(gesuchId?: string, wizardStepName?: TSWizardStepName, wizardStepStatus?: TSWizardStepStatus, bemerkungen?: string,
                verfuegbar?: boolean) {
        super();
        this._gesuchId = gesuchId;
        this._wizardStepName = wizardStepName;
        this._wizardStepStatus = wizardStepStatus;
        this._bemerkungen = bemerkungen;
        this._verfuegbar = verfuegbar;
    }

    get gesuchId(): string {
        return this._gesuchId;
    }

    set gesuchId(value: string) {
        this._gesuchId = value;
    }

    get wizardStepName(): TSWizardStepName {
        return this._wizardStepName;
    }

    set wizardStepName(value: TSWizardStepName) {
        this._wizardStepName = value;
    }

    get wizardStepStatus(): TSWizardStepStatus {
        return this._wizardStepStatus;
    }

    set wizardStepStatus(value: TSWizardStepStatus) {
        this._wizardStepStatus = value;
    }

    get bemerkungen(): string {
        return this._bemerkungen;
    }

    set bemerkungen(value: string) {
        this._bemerkungen = value;
    }

    get verfuegbar(): boolean {
        return this._verfuegbar;
    }

    set verfuegbar(value: boolean) {
        this._verfuegbar = value;
    }
}
