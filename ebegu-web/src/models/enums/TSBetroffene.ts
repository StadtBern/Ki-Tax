export enum TSBetroffene {
    GESUCHSTELLER_1 = <any> 'GESUCHSTELLER_1',
    GESUCHSTELLER_2 = <any> 'GESUCHSTELLER_2',
    BEIDE_GESUCHSTELLER = <any> 'BEIDE_GESUCHSTELLER'
}

export function getTSBetroffeneValues(): Array<TSBetroffene> {
    return [
        TSBetroffene.GESUCHSTELLER_1,
        TSBetroffene.GESUCHSTELLER_2,
        TSBetroffene.BEIDE_GESUCHSTELLER
    ];
}
