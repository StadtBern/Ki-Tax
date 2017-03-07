export enum TSZahlungsstatus {
    AUSGELOEST = <any> 'AUSGELOEST',
    BESTAETIGT = <any> 'BESTAETIGT'
}

export function getTSZahlungsstatusValues(): Array<TSZahlungsstatus> {
    return [
        TSZahlungsstatus.AUSGELOEST,
        TSZahlungsstatus.BESTAETIGT
    ];
}
