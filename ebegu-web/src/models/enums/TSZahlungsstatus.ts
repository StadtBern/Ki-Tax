export enum TSZahlungsstatus {
    ENTWURF = <any> 'ENTWURF',
    AUSGELOEST = <any> 'AUSGELOEST',
    BESTAETIGT = <any> 'BESTAETIGT'
}

export function getTSZahlungsstatusValues(): Array<TSZahlungsstatus> {
    return [
        TSZahlungsstatus.ENTWURF,
        TSZahlungsstatus.AUSGELOEST,
        TSZahlungsstatus.BESTAETIGT
    ];
}
