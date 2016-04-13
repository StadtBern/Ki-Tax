import TSFall from '../../models/TSFall';
import TSGesuch from '../../models/TSGesuch';
import TSFamiliensituation from '../../models/TSFamiliensituation';
import {TSFamilienstatus} from '../../models/enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet} from '../../models/enums/TSGesuchstellerKardinalitaet';
import FallRS from './fallRS.rest';
import GesuchRS from './gesuchRS.rest';
import FamiliensituationRS from './familiensituationRS.rest';


export default class GesuchForm {
    fall:TSFall;
    gesuch:TSGesuch;
    familiensituation: TSFamiliensituation;
    fallRS: FallRS;
    gesuchRS: GesuchRS;
    familiensituationRS: FamiliensituationRS;

    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS'];
    /* @ngInject */
    constructor(familiensituationRS: FamiliensituationRS,
                fallRS: FallRS, gesuchRS: GesuchRS) {

        this.fallRS = fallRS;
        this.gesuchRS = gesuchRS;
        this.familiensituationRS = familiensituationRS;
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

    public updateFamiliensituation() {
        //testen ob aktuelles familiensituation schon gespeichert ist
        if (this.familiensituation.timestampErstellt) {
            return this.familiensituationRS.update(this.familiensituation).then((familienResponse: any) => {
                this.familiensituation = familienResponse.data;
            });
        } else {
            //todo team. Fall und Gesuch sollten in ihren eigenen Services gespeichert werden
            return this.fallRS.create(this.fall).then((fallResponse: any) => {
                this.fall = fallResponse.data;
                this.gesuch.fall = fallResponse.data;
                return this.gesuchRS.create(this.gesuch).then((gesuchResponse: any) => {
                    this.gesuch = gesuchResponse.data;
                    this.familiensituation.gesuch = gesuchResponse.data;
                    return this.familiensituationRS.create(this.familiensituation).then((familienResponse: any) => {
                        return this.familiensituation = familienResponse.data;

                    });
                });
            });
        }
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
