export enum TSWizardStepStatus {
    MUTIERT = <any> 'MUTIERT',
    OK = <any> 'OK',
    NOK = <any> 'NOK',
    PLATZBESTAETIGUNG = <any> 'PLATZBESTAETIGUNG',
    WARTEN = <any> 'WARTEN',
    IN_BEARBEITUNG = <any> 'IN_BEARBEITUNG',
    UNBESUCHT = <any> 'UNBESUCHT',
}

export function getTSWizardStepStatusValues(): Array<TSWizardStepStatus> {
    return [
        TSWizardStepStatus.MUTIERT,
        TSWizardStepStatus.OK,
        TSWizardStepStatus.NOK,
        TSWizardStepStatus.PLATZBESTAETIGUNG,
        TSWizardStepStatus.WARTEN,
        TSWizardStepStatus.IN_BEARBEITUNG,
        TSWizardStepStatus.UNBESUCHT
    ];
}
