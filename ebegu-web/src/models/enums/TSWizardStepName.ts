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
        TSWizardStepName.VERFUEGEN
    ];
}
