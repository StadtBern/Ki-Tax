export enum TSGesuchsperiodeStatus {
    ENTWURF = <any> 'ENTWURF',
    AKTIV = <any> 'AKTIV',
    INAKTIV = <any> 'INAKTIV',
    GESCHLOSSEN = <any> 'GESCHLOSSEN'
}

export function getTSGesuchsperiodeStatusValues(): Array<TSGesuchsperiodeStatus> {
    return [
        TSGesuchsperiodeStatus.ENTWURF,
        TSGesuchsperiodeStatus.AKTIV,
        TSGesuchsperiodeStatus.INAKTIV,
        TSGesuchsperiodeStatus.GESCHLOSSEN
    ];
}
