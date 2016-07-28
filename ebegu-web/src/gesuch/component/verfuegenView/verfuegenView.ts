import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import BerechnungsManager from '../../service/berechnungsManager';
import DateUtil from '../../../utils/DateUtil';
import VerfuegungRS from '../../../core/service/verfuegungRS.rest';
import TSGesuch from '../../../models/TSGesuch';
import TSVerfuegung from '../../../models/TSVerfuegung';
import TSKindContainer from '../../../models/TSKindContainer';
import TSVerfuegungZeitabschnitt from '../../../models/TSVerfuegungZeitabschnitt';
let template = require('./verfuegenView.html');
require('./verfuegenView.less');


export class VerfuegenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenViewController;
    controllerAs = 'vm';
}

export class VerfuegenViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', 'VerfuegungRS'];

    private verfuegungen: TSVerfuegung[] = [];

    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, private verfuegungRS: VerfuegungRS) {
        super(state, gesuchModelManager, berechnungsManager);
        this.initViewModel();
    }

    public initViewModel(): void {
        this.verfuegungRS.calculateVerfuegung(this.gesuchModelManager.gesuch.id).then((response: TSKindContainer[]) => {
            this.verfuegungen.push(response[0].betreuungen[0].verfuegung);
        });
    }

    public cancel(): void {
        this.state.go('gesuch.verfuegen');
    }

    public getVerfuegenToWorkWith(): TSVerfuegung {
        if (this.verfuegungen.length > 0) {
            return this.verfuegungen[0];
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
