import TSGesuch from '../../models/TSGesuch';
import {IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import FinanzielleSituationRS from './finanzielleSituationRS.rest';
import TSFinanzielleSituationResultateDTO from '../../models/dto/TSFinanzielleSituationResultateDTO';


export default class BerechnungsManager {
    finanzielleSituationResultate: TSFinanzielleSituationResultateDTO;

    static $inject = ['FinanzielleSituationRS', 'EbeguRestUtil'];
    /* @ngInject */
    constructor(private finanzielleSituationRS: FinanzielleSituationRS, private ebeguRestUtil: EbeguRestUtil) {
        this.finanzielleSituationResultate = new TSFinanzielleSituationResultateDTO();
    }

    public calculateFinanzielleSituation(gesuch: TSGesuch): IPromise<TSFinanzielleSituationResultateDTO> {
        return this.finanzielleSituationRS.calculateFinanzielleSituation(
            gesuch)
            .then((finSitContRespo: TSFinanzielleSituationResultateDTO) => {
                this.finanzielleSituationResultate = finSitContRespo;
                return finSitContRespo;
            });
    }
}
