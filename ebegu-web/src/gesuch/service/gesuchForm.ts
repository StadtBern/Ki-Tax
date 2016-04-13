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
        this.familiensituation = new TSFamiliensituation();
    }

    /**
     * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde
     * @returns {boolean} False wenn "Alleinerziehend" oder "weniger als 5 Jahre" und dazu "alleine" ausgewaehlt wurde.
     */
    public isGesuchsteller2Required():boolean {
        return !(((this.familiensituation.familienstatus === TSFamilienstatus.ALLEINERZIEHEND) || (this.familiensituation.familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE))
            && (this.familiensituation.gesuchstellerKardinalitaet === TSGesuchstellerKardinalitaet.ALLEINE));

    }

    static instance () : GesuchForm {
        return new GesuchForm();
    }

}
