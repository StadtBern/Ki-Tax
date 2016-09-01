export enum TSWizardStepName {
    GESUCH_ERSTELLEN = <any> 'GESUCH_ERSTELLEN',
    FAMILIENSITUATION = <any> 'FAMILIENSITUATION',
    GESUCHSTELLER = <any> 'GESUCHSTELLER',
    KINDER = <any> 'KINDER',
    BETREUUNG = <any> 'BETREUUNG',
    ERWERBSPENSUM = <any> 'ERWERBSPENSUM',
    FINANZIELLE_SITUATION = <any> 'FINANZIELLE_SITUATION',
    EINKOMMENSVERSCHLECHTERUNG = <any> 'EINKOMMENSVERSCHLECHTERUNG',
    DOKUMENTE = <any> 'DOKUMENTE',
    VERFUEGEN = <any> 'VERFUEGEN'
}

export function getTSWizardStepNameValues(): Array<TSWizardStepName> {
    return [
        TSWizardStepName.GESUCH_ERSTELLEN,
        TSWizardStepName.FAMILIENSITUATION,
        TSWizardStepName.GESUCHSTELLER,
        TSWizardStepName.KINDER,
        TSWizardStepName.BETREUUNG,
        TSWizardStepName.ERWERBSPENSUM,
        TSWizardStepName.FINANZIELLE_SITUATION,
        TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG,
        TSWizardStepName.DOKUMENTE,
        TSWizardStepName.VERFUEGEN
    ];
}
