export enum TSTaetigkeit {
    ANGESTELLT = <any> 'Angestellt',
    SELBSTAENDIG = <any> 'SELBSTAENDIG',
    AUSBILDUNG = <any> 'AUSBILDUNG',
    RAV = <any> 'RAV'
}

export function getTSTaetigkeit(): Array<TSTaetigkeit> {
    return [
        TSTaetigkeit.ANGESTELLT,
        TSTaetigkeit.SELBSTAENDIG,
        TSTaetigkeit.AUSBILDUNG,
        TSTaetigkeit.RAV,
    ];
}
