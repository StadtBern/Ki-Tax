export enum TSRole {
    SUPER_ADMIN = <any> 'SUPER_ADMIN',
    ADMIN = <any> 'ADMIN',
    SACHBEARBEITER_JA= <any> 'SACHBEARBEITER_JA',
    SACHBEARBEITER_INSTITUTION= <any> 'SACHBEARBEITER_INSTITUTION',
    SACHBEARBEITER_TRAEGERSCHAFT= <any> 'SACHBEARBEITER_TRAEGERSCHAFT',
    GESUCHSTELLER= <any> 'GESUCHSTELLER',
    JURIST= <any> 'JURIST',
    REVISOR= <any> 'REVISOR',
    STEUERAMT= <any> 'STEUERAMT',
    SCHULAMT= <any> 'SCHULAMT'
}

export function getTSRoleValues(): Array<TSRole> {
    return [
        TSRole.SUPER_ADMIN,
        TSRole.ADMIN,
        TSRole.SACHBEARBEITER_JA,
        TSRole.SACHBEARBEITER_INSTITUTION,
        TSRole.SACHBEARBEITER_TRAEGERSCHAFT,
        TSRole.GESUCHSTELLER,
        TSRole.JURIST,
        TSRole.REVISOR,
        TSRole.STEUERAMT,
        TSRole.SCHULAMT,
    ];
}

export function rolePrefix(): string {
    return 'TSRole_';
}


