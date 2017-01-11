export enum TSCacheTyp {
    EBEGU_CACHE = <any> 'ebeguCache',
    EBEGU_PARAMETER = <any> 'ebeguParameter',
}

export function getTSCacheTypValues(): Array<TSCacheTyp> {
    return [
        TSCacheTyp.EBEGU_CACHE,
        TSCacheTyp.EBEGU_PARAMETER
    ];
}
