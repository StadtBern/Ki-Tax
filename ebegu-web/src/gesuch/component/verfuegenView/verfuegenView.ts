import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import BerechnungsManager from '../../service/berechnungsManager';
import DateUtil from '../../../utils/DateUtil';
let template = require('./verfuegenView.html');
require('./verfuegenView.less');


export class VerfuegenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenViewController;
    controllerAs = 'vm';
}

export class VerfuegenViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil'];

    private nombres: string[] = ['hola', 'adios'];

    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil) {
        super(state, gesuchModelManager, berechnungsManager);
        this.initModel();
    }

    /**
     * Die finanzielle Situation und die Einkommensverschlechterungen muessen mithilfe des Berechnungsmanagers berechnet werden, um manche Daten zur Verfügung
     * zu haben. Das ist notwendig weil die finanzielle Situation nicht gespeichert wird. D.H. das erste Mal in einer Sitzung wenn ein Gesuch geoeffnet wird,
     * ist gar nichts berechnet. Wenn man dann die Verfügen direkt aufmacht, ist alles leer und wird nichts angezeigt, deswegen muss alles auch hier berechnet werden.
     * Um Probleme mit der Performance zu vermeiden, wird zuerst geprueft, ob die Berechnung schon vorher gemacht wurde, wenn ja dann wird sie einfach verwendet
     * ohne sie neu berechnen zu muessen. Dieses geht aber davon aus, dass die Berechnungen immer richtig kalkuliert wurden
     */
    private initModel(): void {
        console.log('finSit -> cargado');
        this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.gesuch); //.then(() => {});
        if (this.gesuchModelManager.gesuch.einkommensverschlechterungInfo && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1) {
            console.log('EV1 -> cargado');
            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, 1); //.then(() => {});
        }
        if (this.gesuchModelManager.gesuch.einkommensverschlechterungInfo && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2) {
            console.log('EV2 -> cargado');
            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, 2); //.then(() => {});
        }
    }


    public cancel(): void {
        this.state.go('gesuch.verfuegen');
    }

    public getVerfuegungZeitabschnitte(): Array<string> {
        //todo beim zeitabschnitte vom Server holen
        // this.verfuegenRS.getVerfuegen(betreuung).then((response) => {
        //     this.gesuchModelManager.setVerfuegenToWorkWith(response);
        // });

        return this.nombres;
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

    public getKindName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getKindToWorkWith().kindJA) {
            return this.gesuchModelManager.getKindToWorkWith().kindJA.getFullName();
        }
        return undefined;
    }

    public getInstitutionName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten) {
            return this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten.institution.name;
        }
        return undefined;
    }

    public getBetreuungNumber(): string {
        if (this.ebeguUtil && this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.ebeguUtil.calculateBetreuungsId(this.getGesuchsperiode(), this.getFall(), this.gesuchModelManager.getKindToWorkWith().kindNummer,
                this.gesuchModelManager.getBetreuungToWorkWith().betreuungNummer);
        }
        return undefined;
    }

    public getBetreuungsstatus(): TSBetreuungsstatus {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus;
        }
        return undefined;
    }

    public getAnfangsPeriode(): string {
        if (this.ebeguUtil) {
            return this.ebeguUtil.getFirstDayGesuchsperiodeAsString(this.gesuchModelManager.getGesuchsperiode());
        }
        return undefined;
    }

    public getFamiliengroesseFS(): number {
        if (this.berechnungsManager && this.berechnungsManager.finanzielleSituationResultate) {
            return this.berechnungsManager.finanzielleSituationResultate.familiengroesse;
        }
        return undefined;
    }

    public getFamiliengroesseEV1(): number {
        if (this.berechnungsManager && this.berechnungsManager.einkommensverschlechterungResultateBjP1) {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP1.familiengroesse;
        }
        return undefined;
    }

    public getFamiliengroesseEV2(): number {
        if (this.berechnungsManager && this.berechnungsManager.einkommensverschlechterungResultateBjP1) {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP2.familiengroesse;
        }
        return undefined;
    }

    public getAnfangsVerschlechterung1(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.gesuch && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.stichtagFuerBasisJahrPlus1, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public getAnfangsVerschlechterung2(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.gesuch && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.stichtagFuerBasisJahrPlus2, 'DD.MM.YYYY');
        }
        return undefined;
    }

}
