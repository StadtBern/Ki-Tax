import TSGesuch from '../../models/TSGesuch';
import {IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import FinanzielleSituationRS from './finanzielleSituationRS.rest';
import TSFinanzielleSituationResultateDTO from '../../models/dto/TSFinanzielleSituationResultateDTO';
import EinkommensverschlechterungContainerRS from './einkommensverschlechterungContainerRS.rest';


export default class BerechnungsManager {
    finanzielleSituationResultate: TSFinanzielleSituationResultateDTO;
    einkommensverschlechterungResultateBjP1: TSFinanzielleSituationResultateDTO;
    einkommensverschlechterungResultateBjP2: TSFinanzielleSituationResultateDTO;

    static $inject = ['FinanzielleSituationRS', 'EbeguRestUtil', 'EinkommensverschlechterungContainerRS'];
    /* @ngInject */
    constructor(private finanzielleSituationRS: FinanzielleSituationRS, private ebeguRestUtil: EbeguRestUtil,
                private einkommensverschlechterungContainerRS: EinkommensverschlechterungContainerRS) {
        this.finanzielleSituationResultate = new TSFinanzielleSituationResultateDTO();
        this.einkommensverschlechterungResultateBjP1 = new TSFinanzielleSituationResultateDTO();
        this.einkommensverschlechterungResultateBjP2 = new TSFinanzielleSituationResultateDTO();
    }

    public calculateFinanzielleSituation(gesuch: TSGesuch): IPromise<TSFinanzielleSituationResultateDTO> {
        return this.finanzielleSituationRS.calculateFinanzielleSituation(
            gesuch)
            .then((finSitContRespo: TSFinanzielleSituationResultateDTO) => {
                this.finanzielleSituationResultate = finSitContRespo;
                return finSitContRespo;
            });
    }

    public calculateEinkommensverschlechterung(gesuch: TSGesuch, basisJahrPlus: number): IPromise<TSFinanzielleSituationResultateDTO> {
        return this.einkommensverschlechterungContainerRS.calculateEinkommensverschlechterung(
            gesuch, basisJahrPlus)
            .then((finSitContRespo: TSFinanzielleSituationResultateDTO) => {
                if (basisJahrPlus === 2) {
                    this.einkommensverschlechterungResultateBjP2 = finSitContRespo;
                } else {
                    this.einkommensverschlechterungResultateBjP1 = finSitContRespo;
                }
                return finSitContRespo;
            });
    }

    getEinkommensverschlechterungResultate(basisJahrPlus: number): TSFinanzielleSituationResultateDTO {
        if (basisJahrPlus === 2) {
            return this.einkommensverschlechterungResultateBjP2;
        }
        return this.einkommensverschlechterungResultateBjP1;
    }
}
