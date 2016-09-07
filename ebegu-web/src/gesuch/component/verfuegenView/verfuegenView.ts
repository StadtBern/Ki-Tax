import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import BerechnungsManager from '../../service/berechnungsManager';
import DateUtil from '../../../utils/DateUtil';
import TSVerfuegung from '../../../models/TSVerfuegung';
import TSVerfuegungZeitabschnitt from '../../../models/TSVerfuegungZeitabschnitt';
import IFormController = angular.IFormController;
import WizardStepManager from '../../service/wizardStepManager';
import {TSRole} from '../../../models/enums/TSRole';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
let template = require('./verfuegenView.html');
require('./verfuegenView.less');


export class VerfuegenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenViewController;
    controllerAs = 'vm';
}

export class VerfuegenViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', '$scope', 'WizardStepManager',
        'AuthServiceRS'];

    private verfuegungen: TSVerfuegung[] = [];

    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, private $scope: any, wizardStepManager: WizardStepManager,
                private authServiceRS: AuthServiceRS) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);

        $scope.$on('$stateChangeStart', (navEvent: any, toState: any, toParams: any, fromState: any, fromParams: any) => {
            console.log('resetting state due to navigation change, ');
            if (navEvent.defaultPrevented !== undefined && navEvent.defaultPrevented === false) {
                //Wenn die Maske verlassen wird, werden automatisch die Eintraege entfernt, die noch nicht in der DB gespeichert wurden
                this.reset();
            }
        });
    }

    cancel(form: IFormController): void {
        this.reset();
        form.$setPristine();
    }

    reset() {
        this.gesuchModelManager.restoreBackupOfPreviousGesuch();
    }

    save(form: IFormController) {
        if (form.$valid) {
            //TODO (team) Hier muessen dann noch die Bemerkungen gespeichert werden!
            //this.errorService.clearAll();
            //TODO (team) achtung, beim Speichern (bzw. nach dem Speichern) muss  this.backupCurrentGesuch(); aufgerufen werden auf dem gesuchModelManager
            // this.gesuchModelManager.updateKind().then((kindResponse: any) => {
            //TODO (team) das muss in der Direktive dv-navigation gemacht werden. Erst nach der Implementierung von saveVerfuegung.
            // Beim Speichern der Vefuegung gibt es einen Fehler, der mit der Implementierung dieser Methode nicht mehr erscheinen sollte.
                this.$state.go('gesuch.verfuegen');
            // });
        }
    }

    public getVerfuegenToWorkWith(): TSVerfuegung {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getVerfuegenToWorkWith();
        }
        return undefined;
    }

    public getVerfuegungZeitabschnitte(): Array<TSVerfuegungZeitabschnitt> {
        if (this.getVerfuegenToWorkWith()) {
            return this.getVerfuegenToWorkWith().zeitabschnitte;
        }
        return undefined;
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
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.stichtagFuerBasisJahrPlus1, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public getAnfangsVerschlechterung2(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.stichtagFuerBasisJahrPlus2, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public showNextButton(): boolean {
        return !this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION)
        && !this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT);
    }

}
