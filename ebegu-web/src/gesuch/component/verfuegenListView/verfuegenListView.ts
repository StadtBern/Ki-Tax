import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
import EbeguUtil from '../../../utils/EbeguUtil';
import BerechnungsManager from '../../service/berechnungsManager';
import VerfuegungRS from '../../../core/service/verfuegungRS.rest';
let template = require('./verfuegenListView.html');
require('./verfuegenListView.less');


export class VerfuegenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenListViewController;
    controllerAs = 'vm';
}

export class VerfuegenListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil'];
    private kinderWithBetreuungList: Array<TSKindContainer>;


    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil) {
        super(state, gesuchModelManager, berechnungsManager);
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

        //Berechnung aller finanziellen Daten
        this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.gesuch); //.then(() => {});
        if (this.gesuchModelManager.gesuch && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo
            && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, 1); //.then(() => {});
        }
        if (this.gesuchModelManager.gesuch && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo
            && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, 2); //.then(() => {});
        }

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
        if (this.gesuchModelManager && this.gesuchModelManager.gesuch) {
            return this.gesuchModelManager.gesuch.fall;
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
