import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
import EbeguUtil from '../../../utils/EbeguUtil';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
let template = require('./verfuegenListView.html');
require('./verfuegenListView.less');


export class VerfuegenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenListViewController;
    controllerAs = 'vm';
}

export class VerfuegenListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', 'WizardStepManager'];
    private kinderWithBetreuungList: Array<TSKindContainer>;


    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager) {
        super(state, gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    /**
     * Die finanzielle Situation und die Einkommensverschlechterungen muessen mithilfe des Berechnungsmanagers berechnet werden, um manche Daten zur Verfügung
     * zu haben. Das ist notwendig weil die finanzielle Situation nicht gespeichert wird. D.H. das erste Mal in einer Sitzung wenn ein Gesuch geoeffnet wird,
     * ist gar nichts berechnet. Wenn man dann die Verfügen direkt aufmacht, ist alles leer und wird nichts angezeigt, deswegen muss alles auch hier berechnet werden.
     * Um Probleme mit der Performance zu vermeiden, wird zuerst geprueft, ob die Berechnung schon vorher gemacht wurde, wenn ja dann wird sie einfach verwendet
     * ohne sie neu berechnen zu muessen. Dieses geht aber davon aus, dass die Berechnungen immer richtig kalkuliert wurden.
     *
     * Die Verfuegungen werden IMMER geladen, wenn diese View geladen wird. Dieses ist etwas ineffizient. Allerdings muss es eigentlich so funktionieren, weil
     * die Daten sich haben aendern koennen. Es ist ein aehnlicher Fall wie mit der finanziellen Situation. Sollte es Probleme mit der Performance geben, muessen
     * wir ueberlegen, ob wir es irgendwie anders berechnen koennen um den Server zu entlasten.
     */
    private initViewModel(): void {
        this.kinderWithBetreuungList = this.gesuchModelManager.getKinderWithBetreuungList();
        this.wizardStepManager.updateWizardStepStatus(TSWizardStepName.VERFUEGEN, TSWizardStepStatus.WARTEN);

        //Berechnung aller finanziellen Daten
        if (!this.berechnungsManager.finanzielleSituationResultate) {
            this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch()); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo
            && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
            && !this.berechnungsManager.einkommensverschlechterungResultateBjP1) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 1); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo
            && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
            && !this.berechnungsManager.einkommensverschlechterungResultateBjP2) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 2); //.then(() => {});
        }
        //todo wenn man aus der verfuegung zurueck kommt muss man hier nicht neu berechnen
        this.gesuchModelManager.calculateVerfuegungen();
    }

    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        return this.kinderWithBetreuungList;
    }

    public openVerfuegung(kind: TSKindContainer, betreuung: TSBetreuung): void {
        let kindNumber: number = this.gesuchModelManager.findKind(kind);
        if (kindNumber > 0) {
            this.gesuchModelManager.setKindNumber(kindNumber);
            let betreuungNumber: number = this.gesuchModelManager.findBetreuung(betreuung);
            if (betreuungNumber > 0) {
                this.gesuchModelManager.setBetreuungNumber(betreuungNumber);
                this.state.go('gesuch.verfuegenView');
            }
        }
    }

    public getFall() {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().fall;
        }
        return undefined;
    }

    public getGesuchsperiode() {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getGesuchsperiode();
        }
        return undefined;
    }

}
