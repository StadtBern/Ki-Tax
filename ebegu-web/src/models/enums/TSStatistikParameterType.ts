export enum TSStatistikParameterType {
    GESUCH_STICHTAG = <any>'GESUCH_STICHTAG',
    GESUCH_ZEITRAUM = <any>'GESUCH_ZEITRAUM',
    KINDER = <any>'KINDER',
    GESUCHSTELLER = <any>'GESUCHSTELLER',
    KANTON = <any>'KANTON',
    GESUCHSTELLER_KINDER_BETREUUNG = <any>'GESUCHSTELLER_KINDER_BETREUUNG',
    ZAHLUNGEN_PERIODE = <any>'ZAHLUNGEN_PERIODE'
}

export function getTSStatistikParameterKeyValues(): Array<TSStatistikParameterType> {
    return [
        TSStatistikParameterType.GESUCH_STICHTAG,
        TSStatistikParameterType.GESUCH_ZEITRAUM,
        TSStatistikParameterType.KINDER,
        TSStatistikParameterType.GESUCHSTELLER,
        TSStatistikParameterType.KANTON,
        TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG,
        TSStatistikParameterType.ZAHLUNGEN_PERIODE,
    ];
}
