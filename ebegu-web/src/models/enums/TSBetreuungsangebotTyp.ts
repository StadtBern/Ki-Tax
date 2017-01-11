export enum TSBetreuungsangebotTyp {
    KITA = <any> 'KITA',
    TAGESELTERN_KLEINKIND = <any> 'TAGESELTERN_KLEINKIND',
    TAGESELTERN_SCHULKIND = <any> 'TAGESELTERN_SCHULKIND',
    TAGI = <any> 'TAGI',
    TAGESSCHULE = <any> 'TAGESSCHULE'
}

export function getTSBetreuungsangebotTypValues(): Array<TSBetreuungsangebotTyp> {
    return [
        TSBetreuungsangebotTyp.KITA,
        TSBetreuungsangebotTyp.TAGI,
        TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND,
        TSBetreuungsangebotTyp.TAGESELTERN_SCHULKIND,
        TSBetreuungsangebotTyp.TAGESSCHULE
    ];
}

export function isSchulamt(status: TSBetreuungsangebotTyp): boolean {
    return status === TSBetreuungsangebotTyp.TAGESSCHULE;
}

export function isJugendamt(status: TSBetreuungsangebotTyp): boolean {
    return !isSchulamt(status);
}
