export enum TSEingangsart {
    ONLINE = <any> 'ONLINE',
    PAPIER = <any> 'PAPIER'
}

export function getTSEingangsartValues(): Array<TSEingangsart> {
    return [
        TSEingangsart.ONLINE,
        TSEingangsart.PAPIER
    ];
}
