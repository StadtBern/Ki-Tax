export enum TSAntragTyp {
    ERSTGESUCH = <any> 'ERSTGESUCH',
    MUTATION = <any> 'MUTATION',
    ERNEUERUNGSGESUCH = <any> 'ERNEUERUNGSGESUCH'
}

export function getTSAntragTypValues(): Array<TSAntragTyp> {
    return [
        TSAntragTyp.ERSTGESUCH,
        TSAntragTyp.MUTATION,
        TSAntragTyp.ERNEUERUNGSGESUCH
    ];
}
