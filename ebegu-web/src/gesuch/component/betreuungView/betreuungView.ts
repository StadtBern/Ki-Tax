import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
import {TSBetreuungsangebotTyp, getTSBetreuungsangebotTypValues} from '../../../models/enums/TSBetreuungsangebotTyp';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {TSInstitutionStammdaten} from '../../../models/TSInstitutionStammdaten';
let template = require('./betreuungView.html');

export class BetreuungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungViewController;
    controllerAs = 'vm';
}



export class BetreuungViewController extends AbstractGesuchViewController {
    betreuungsangebot: any;
    betreuungsangebotValues: Array<any>;

    static $inject = ['$state', 'GesuchModelManager', 'EbeguRestUtil'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguRestUtil: EbeguRestUtil) {
        super(state, gesuchModelManager);
        this.setBetreuungsangebotTypValues();
        this.betreuungsangebot = undefined;
    }

    public getKindModel(): TSKindContainer {
        return this.gesuchModelManager.getKindToWorkWith();
    }

    submit(): void {
    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguRestUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    cancel() {
        this.removeBetreuungFromKind();
        this.state.go('gesuch.betreuungen');
    }

    private removeBetreuungFromKind(): void {
        if (!this.gesuchModelManager.getBetreuungToWorkWith().timestampErstellt) {
            //wenn das Kind noch nicht erstellt wurde, löschen wir das Kind vom Array
            this.gesuchModelManager.removeBetreuungFromKind();
        }
    }

    public getInstitutionenSDList(): Array<TSInstitutionStammdaten> {
        let result: Array<TSInstitutionStammdaten> = [];
        if (this.betreuungsangebot) {
            this.gesuchModelManager.institutionenList.forEach((instStamm: TSInstitutionStammdaten) => {
                if (instStamm.betreuungsangebotTyp === this.betreuungsangebot.key) {
                    result.push(instStamm);
                }
            });
        }
        return result;
    }

}
