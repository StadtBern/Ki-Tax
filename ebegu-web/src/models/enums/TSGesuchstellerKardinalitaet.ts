/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.API {

    export enum TSGesuchstellerKardinalitaet {
        ALLEINE = <any>"ALLEINE",
        ZU_ZWEIT = <any>"ZU_ZWEIT"
    }

    export function getTSGesuchstellerKardinalitaetValues(): Array<TSGesuchstellerKardinalitaet> {
        return [
            TSGesuchstellerKardinalitaet.ALLEINE,
            TSGesuchstellerKardinalitaet.ZU_ZWEIT
        ];
    }

}
