export enum TSAntragTyp {
    GESUCH = <any> 'GESUCH',
    MUTATION = <any> 'MUTATION'
}

export function getTSAntragTypValues(): Array<TSAntragTyp> {
    return [
        TSAntragTyp.GESUCH,
        TSAntragTyp.MUTATION
    ];
}
