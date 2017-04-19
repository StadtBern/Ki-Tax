export enum TSAntragTyp {
    GESUCH = <any> 'GESUCH', // TODO Hefr umbenennen!
    MUTATION = <any> 'MUTATION',
    ERNEUERUNGSGESUCH = <any> 'ERNEUERUNGSGESUCH'
}

export function getTSAntragTypValues(): Array<TSAntragTyp> {
    return [
        TSAntragTyp.GESUCH,
        TSAntragTyp.MUTATION,
        TSAntragTyp.ERNEUERUNGSGESUCH
    ];
}
