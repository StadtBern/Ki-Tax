export enum TSZahlungsauftragsstatus {
    ENTWURF = <any> 'ENTWURF',
    AUSGELOEST = <any> 'AUSGELOEST',
    BESTAETIGT = <any> 'BESTAETIGT'
}


export function getTSZahlungsauftragsstatusValues(): Array<TSZahlungsauftragsstatus> {
    return [
        TSZahlungsauftragsstatus.ENTWURF,
        TSZahlungsauftragsstatus.AUSGELOEST,
        TSZahlungsauftragsstatus.BESTAETIGT
    ];
}
