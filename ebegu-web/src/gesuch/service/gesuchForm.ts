import TSFall from '../../models/TSFall';
import TSGesuch from '../../models/TSGesuch';
import TSFamiliensituation from '../../models/TSFamiliensituation';
import {TSFamilienstatus} from '../../models/enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet} from '../../models/enums/TSGesuchstellerKardinalitaet';


export default class GesuchForm {
    fall:TSFall;
    gesuch:TSGesuch;
    familiensituation: TSFamiliensituation;

    static $inject: string[] = [];
    /* @ngInject */
    constructor() {
        this.fall = new TSFall();
        this.gesuch = new TSGesuch();
        this.setFamilienSituation(new TSFamiliensituation());
    }

    /**
     * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde
     * @returns {boolean} False wenn "Alleinerziehend" oder "weniger als 5 Jahre" und dazu "alleine" ausgewaehlt wurde.
     */
    public isGesuchsteller2Required():boolean {
        if ((this.familiensituation !== null) && (this.familiensituation !== undefined)) {
            return !(((this.familiensituation.familienstatus === TSFamilienstatus.ALLEINERZIEHEND) || (this.familiensituation.familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE))
            && (this.familiensituation.gesuchstellerKardinalitaet === TSGesuchstellerKardinalitaet.ALLEINE));
        }
        return false;
    }

    /**
     +         * Die Familiensituation wird nur durch die gegebene Familiensituation ersetzt wenn die erste
     +         * null oder undefined ist.
     +         * @param familiensituation
     +         */
    public setFamilienSituation(familiensituation: TSFamiliensituation): void {
        if((this.familiensituation === undefined) || (this.familiensituation === null)) {
            this.familiensituation = familiensituation;
        }
    }

}
