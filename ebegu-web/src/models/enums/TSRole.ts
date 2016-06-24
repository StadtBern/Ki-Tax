export enum TSRole {
    ADMIN = <any> 'ADMIN',
    SACHBEARBEITER_JA= <any> 'SACHBEARBEITER_JA',
    SACHBEARBEITER_INSTITUTION= <any> 'SACHBEARBEITER_INSTITUTION',
    GESUCHSTELLER= <any> 'GESUCHSTELLER',
    JURIST= <any> 'JURIST',
    REVISOR= <any> 'REVISOR',
    STEUERAMT= <any> 'STEUERAMT'
}

export function getTSRoleValues(): Array<TSRole> {
    return [
        TSRole.ADMIN,
        TSRole.SACHBEARBEITER_JA,
        TSRole.SACHBEARBEITER_INSTITUTION,
        TSRole.GESUCHSTELLER,
        TSRole.JURIST,
        TSRole.REVISOR,
        TSRole.STEUERAMT,
    ];
}

export function rolePrefix(): string {
    return 'TSRole_';
}


