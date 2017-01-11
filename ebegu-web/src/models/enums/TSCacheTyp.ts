export enum TSCacheTyp {
    EBEGU_DOCUMENT = <any> 'ebeguDocument',
    EBEGU_PARAMETER = <any> 'ebeguParameter',
}

export function getTSCacheTypValues(): Array<TSCacheTyp> {
    return [
        TSCacheTyp.EBEGU_DOCUMENT,
        TSCacheTyp.EBEGU_PARAMETER
    ];
}
