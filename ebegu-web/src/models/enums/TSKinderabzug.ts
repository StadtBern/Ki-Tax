export enum TSKinderabzug {
    KEIN_ABZUG = <any> 'KEIN_ABZUG',
    HALBER_ABZUG = <any> 'HALBER_ABZUG',
    GANZER_ABZUG = <any> 'GANZER_ABZUG',
    KEINE_STEUERERKLAERUNG = <any> 'KEINE_STEUERERKLAERUNG'
}

export function getTSKinderabzugValues(): Array<TSKinderabzug> {
    return [
        TSKinderabzug.KEIN_ABZUG,
        TSKinderabzug.HALBER_ABZUG,
        TSKinderabzug.GANZER_ABZUG,
        TSKinderabzug.KEINE_STEUERERKLAERUNG
    ];
}
