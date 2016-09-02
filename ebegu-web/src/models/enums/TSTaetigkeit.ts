export enum TSTaetigkeit {
    ANGESTELLT = <any> 'ANGESTELLT',
    SELBSTAENDIG = <any> 'SELBSTAENDIG',
    AUSBILDUNG = <any> 'AUSBILDUNG',
    RAV = <any> 'RAV',
    GESUNDHEITLICHE_EINSCHRAENKUNGEN = <any> 'GESUNDHEITLICHE_EINSCHRAENKUNGEN'
}

export function getTSTaetigkeit(): Array<TSTaetigkeit> {
    return [
        TSTaetigkeit.ANGESTELLT,
        TSTaetigkeit.SELBSTAENDIG,
        TSTaetigkeit.AUSBILDUNG,
        TSTaetigkeit.RAV,
        TSTaetigkeit.GESUNDHEITLICHE_EINSCHRAENKUNGEN
    ];
}
