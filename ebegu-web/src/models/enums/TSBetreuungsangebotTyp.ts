export enum TSBetreuungsangebotTyp {
    KITA = <any> 'KITA',
    TAGESELTERN = <any> 'TAGESELTERN',
    TAGI = <any> 'TAGI',
    TAGESSCHULE = <any> 'TAGESSCHULE'
}

export function getTSBetreuungsangebotTypValues(): Array<TSBetreuungsangebotTyp> {
    return [
        TSBetreuungsangebotTyp.KITA,
        TSBetreuungsangebotTyp.TAGESELTERN,
        TSBetreuungsangebotTyp.TAGI,
        TSBetreuungsangebotTyp.TAGESSCHULE
    ];
}
