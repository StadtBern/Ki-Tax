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

export enum TSWizardStepName {
    GESUCH_ERSTELLEN = <any> 'GESUCH_ERSTELLEN',
    FAMILIENSITUATION = <any> 'FAMILIENSITUATION',
    GESUCHSTELLER = <any> 'GESUCHSTELLER',
    UMZUG = <any> 'UMZUG',
    KINDER = <any> 'KINDER',
    BETREUUNG = <any> 'BETREUUNG',
    ABWESENHEIT = <any> 'ABWESENHEIT',
    ERWERBSPENSUM = <any> 'ERWERBSPENSUM',
    FINANZIELLE_SITUATION = <any> 'FINANZIELLE_SITUATION',
    EINKOMMENSVERSCHLECHTERUNG = <any> 'EINKOMMENSVERSCHLECHTERUNG',
    DOKUMENTE = <any> 'DOKUMENTE',
    FREIGABE = <any> 'FREIGABE',
    VERFUEGEN = <any> 'VERFUEGEN'
}

/**
 * It is crucial that this function returns all elements in the order they will have in the navigation menu.
 * the order of this function will be used to navigate through all steps, so if this order is not correct the
 * navigation won't work as expected.
 * @returns {TSWizardStepName[]}
 */
export function getTSWizardStepNameValues(): Array<TSWizardStepName> {
    return [
        TSWizardStepName.GESUCH_ERSTELLEN,
        TSWizardStepName.FAMILIENSITUATION,
        TSWizardStepName.GESUCHSTELLER,
        TSWizardStepName.UMZUG,
        TSWizardStepName.KINDER,
        TSWizardStepName.BETREUUNG,
        TSWizardStepName.ABWESENHEIT,
        TSWizardStepName.ERWERBSPENSUM,
        TSWizardStepName.FINANZIELLE_SITUATION,
        TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG,
        TSWizardStepName.DOKUMENTE,
        TSWizardStepName.FREIGABE,
        TSWizardStepName.VERFUEGEN
    ];
}
