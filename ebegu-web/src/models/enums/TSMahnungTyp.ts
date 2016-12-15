export enum TSMahnungTyp {
    ERSTE_MAHNUNG = <any>'ERSTE_MAHNUNG',
    ZWEITE_MAHNUNG = <any>'ZWEITE_MAHNUNG',
}

export function getTSMahnungTypValues(): Array<TSMahnungTyp> {
    return [
        TSMahnungTyp.ERSTE_MAHNUNG,
        TSMahnungTyp.ZWEITE_MAHNUNG,
    ];
}
