/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.API {

    export enum TSGesuchKardinalitaet {
        ALLEINE = <any>"ALLEINE",
        ZU_ZWEIT = <any>"ZU_ZWEIT"
    }

    export function getTSGesuchKardinalitaetValues(): Array<TSGesuchKardinalitaet> {
        return [
            TSGesuchKardinalitaet.ALLEINE,
            TSGesuchKardinalitaet.ZU_ZWEIT
        ];
    }

}
