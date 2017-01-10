export enum TSKinderabzug {
    KEIN_ABZUG = <any> 'KEIN_ABZUG',
    HALBER_ABZUG = <any> 'HALBER_ABZUG',
    GANZER_ABZUG = <any> 'GANZER_ABZUG'
}

export function getTSKinderabzugValues(): Array<TSKinderabzug> {
    return [
        TSKinderabzug.KEIN_ABZUG,
        TSKinderabzug.HALBER_ABZUG,
        TSKinderabzug.GANZER_ABZUG
    ];
}
