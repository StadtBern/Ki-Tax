export enum TSZuschlagsgrund {
    UNREGELMAESSIGE_ARBEITSZEITEN = <any> 'UNREGELMAESSIGE_ARBEITSZEITEN',
    UEBERLAPPENDE_ARBEITSZEITEN = <any> 'UEBERLAPPENDE_ARBEITSZEITEN',
    FIXE_ARBEITSZEITEN = <any> 'FIXE_ARBEITSZEITEN',
    LANGER_ARBWEITSWEG = <any> 'LANGER_ARBWEITSWEG',
    ANDERE = <any> 'ANDERE'

}

export function getTSZuschlagsgrunde(): Array<TSZuschlagsgrund> {
    return [
        TSZuschlagsgrund.UNREGELMAESSIGE_ARBEITSZEITEN,
        TSZuschlagsgrund.UEBERLAPPENDE_ARBEITSZEITEN,
        TSZuschlagsgrund.FIXE_ARBEITSZEITEN,
        TSZuschlagsgrund.LANGER_ARBWEITSWEG,
        TSZuschlagsgrund.ANDERE,

    ];
}
/**
 * Gesuchsteller duerfen nicht alle Gruende auswaehlen
 * @returns {TSZuschlagsgrund[]}
 */
export function getTSZuschlagsgruendeForGS() {
    return [
        TSZuschlagsgrund.UNREGELMAESSIGE_ARBEITSZEITEN,
        TSZuschlagsgrund.UEBERLAPPENDE_ARBEITSZEITEN,
        TSZuschlagsgrund.FIXE_ARBEITSZEITEN,
        TSZuschlagsgrund.LANGER_ARBWEITSWEG,
    ];
}
