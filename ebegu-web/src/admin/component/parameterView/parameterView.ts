import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IComponentOptions, ILogService} from 'angular';
import './parameterView.less';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {EbeguVorlageRS} from '../../service/ebeguVorlageRS.rest';
import EbeguUtil from '../../../utils/EbeguUtil';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import GlobalCacheService from '../../../gesuch/service/globalCacheService';
import {TSCacheTyp} from '../../../models/enums/TSCacheTyp';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import AbstractAdminViewController from '../../abstractAdminView';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {getTSGesuchsperiodeStatusValues, TSGesuchsperiodeStatus} from '../../../models/enums/TSGesuchsperiodeStatus';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import ITranslateService = angular.translate.ITranslateService;
import ITimeoutService = angular.ITimeoutService;
let template = require('./parameterView.html');
let style = require('./parameterView.less');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');

export class ParameterViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = ParameterViewController;
    controllerAs: string = 'vm';
}

export class ParameterViewController extends AbstractAdminViewController {
    static $inject = ['EbeguParameterRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$translate', 'EbeguVorlageRS',
        'EbeguUtil', 'DvDialog', '$log', 'GlobalCacheService', 'GesuchModelManager', '$timeout',
        '$window', 'AuthServiceRS'];

    ebeguParameterRS: EbeguParameterRS;
    ebeguRestUtil: EbeguRestUtil;

    gesuchsperiodenList: Array<TSGesuchsperiode> = [];
    gesuchsperiode: TSGesuchsperiode;

    jahr: number;
    ebeguJahresabhParameter: TSEbeguParameter[] = []; // enthält alle Jahresabhängigen Params für alle Jahre

    ebeguParameterListGesuchsperiode: TSEbeguParameter[];
    ebeguParameterListJahr: TSEbeguParameter[]; // enthält alle Params für nur 1 Jahr

    statusChanged: boolean = false;

    /* @ngInject */
    constructor(ebeguParameterRS: EbeguParameterRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                ebeguRestUtil: EbeguRestUtil, private $translate: ITranslateService,
                private ebeguVorlageRS: EbeguVorlageRS, private ebeguUtil: EbeguUtil,
                private dvDialog: DvDialog, private $log: ILogService,
                private globalCacheService: GlobalCacheService, private gesuchModelManager: GesuchModelManager,
                private $timeout: ITimeoutService, private $window: ng.IWindowService, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
        this.ebeguParameterRS = ebeguParameterRS;
        this.ebeguRestUtil = ebeguRestUtil;
        $timeout(() => {
            this.readGesuchsperioden();
            this.updateJahresabhParamList();
        });
    }

    private readGesuchsperioden(): void {
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: Array<TSGesuchsperiode>) => {
            this.gesuchsperiodenList =  response; //angular.copy(response);
        });
    }

    private readEbeguParameterByGesuchsperiode(): void {
        this.ebeguParameterRS.getEbeguParameterByGesuchsperiode(this.gesuchsperiode.id).then((response: TSEbeguParameter[]) => {
            this.ebeguParameterListGesuchsperiode = response;
        });
    }

    private readEbeguParameterByJahr(): void {
        this.ebeguParameterRS.getEbeguParameterByJahr(this.jahr).then((response: TSEbeguParameter[]) => {
            this.ebeguParameterListJahr = response;
        });
    }

    gesuchsperiodeClicked(gesuchsperiode: any) {
        if (gesuchsperiode.isSelected) {
            this.gesuchsperiode = gesuchsperiode;
            this.readEbeguParameterByGesuchsperiode();
        } else {
            this.cancelGesuchsperiode();
        }
    }

    jahresabhParamSelected(parameter: TSEbeguParameter) {
        this.jahr = parameter.gueltigkeit.gueltigAb.get('year');
        this.jahrChanged();
    }

    createGesuchsperiode(): void {
        this.gesuchsperiode = new TSGesuchsperiode(TSGesuchsperiodeStatus.ENTWURF, new TSDateRange());
        if (this.gesuchsperiodenList) {
            let prevGesPer: TSGesuchsperiode = this.gesuchsperiodenList[0];
            this.gesuchsperiode.gueltigkeit.gueltigAb = prevGesPer.gueltigkeit.gueltigAb.clone().add('years', 1);
            this.gesuchsperiode.gueltigkeit.gueltigBis = prevGesPer.gueltigkeit.gueltigBis.clone().add('years', 1);
        }
    }

    saveGesuchsperiode(): void {
        // Den Dialog nur aufrufen, wenn der Status geändert wurde (oder die GP neu ist)
        if (this.gesuchsperiode.isNew() || this.statusChanged === true) {
            let dialogText = this.getGesuchsperiodeSaveDialogText();
            this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'GESUCHSPERIODE_DIALOG_TITLE',
                deleteText: dialogText
            }).then(() => {
                this.doSave();
            });
        } else {
            this.doSave();
        }
    }

    private doSave(): void {
        this.gesuchsperiodeRS.updateGesuchsperiode(this.gesuchsperiode).then((response: TSGesuchsperiode) => {
            this.gesuchsperiode = response;

            let index: number = EbeguUtil.getIndexOfElementwithID(response, this.gesuchsperiodenList);
            if (index !== -1) {
                this.gesuchsperiodenList[index] = response;
            } else {
                this.gesuchsperiodenList.push(response);
            }
            this.globalCacheService.getCache(TSCacheTyp.EBEGU_PARAMETER).removeAll();
            // Die E-BEGU-Parameter für die neue Periode lesen bzw. erstellen, wenn noch nicht vorhanden
            this.readEbeguParameterByGesuchsperiode();
            // Dasselbe fuer die jahresabhaengigen fuer die beiden Halbjahre der Periode
            this.ebeguParameterRS.getEbeguParameterByJahr(this.gesuchsperiode.gueltigkeit.gueltigAb.year()).then((response: TSEbeguParameter[]) => {
                this.ebeguParameterRS.getEbeguParameterByJahr(this.gesuchsperiode.gueltigkeit.gueltigBis.year()).then((response: TSEbeguParameter[]) => {
                    this.updateJahresabhParamList();
                });
            });
            this.gesuchsperiodeRS.updateActiveGesuchsperiodenList(); //reset gesuchperioden in manager
            this.gesuchsperiodeRS.updateNichtAbgeschlosseneGesuchsperiodenList();
            this.statusChanged = false;
        });
    }

    setStatusChanged(): void {
        this.statusChanged = true;
    }

    private getGesuchsperiodeSaveDialogText(): string {
        if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.ENTWURF) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_ENTWURF';
        } else if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.AKTIV) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_AKTIV';
        } else if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.INAKTIV) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_INAKTIV';
        } else if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.GESCHLOSSEN) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_GESCHLOSSEN';
        } else {
            this.$log.warn('Achtung, Status unbekannt: ', this.gesuchsperiode.status);
            return null;
        }
    }

    cancelGesuchsperiode(): void {
        this.gesuchsperiode = undefined;
        this.ebeguParameterListGesuchsperiode = undefined;
    }

    cancelJahresabhaengig(): void {
        this.jahr = undefined;
    }

    jahrChanged(): void {
        this.readEbeguParameterByJahr();
    }

    saveParameterByGesuchsperiode(): void {
        for (let i = 0; i < this.ebeguParameterListGesuchsperiode.length; i++) {
            let param = this.ebeguParameterListGesuchsperiode[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
        this.globalCacheService.getCache(TSCacheTyp.EBEGU_PARAMETER).removeAll();
        this.gesuchsperiodeRS.updateActiveGesuchsperiodenList();
        this.gesuchsperiodeRS.updateNichtAbgeschlosseneGesuchsperiodenList();
        this.gesuchsperiode = undefined;
    }

    saveParameterByJahr(): void {
        if (this.ebeguParameterListJahr.length !== 1) {
            this.$log.error('Aktuell kann diese oberflaeche nur einene einzelnen Jahresabg. Param speichern.');
        } else {
            let param = this.ebeguParameterListJahr[0];
            this.ebeguParameterRS.saveEbeguParameter(param).then((response) => {
                this.updateJahresabhParamList();
            });
        }
    }

    private updateJahresabhParamList() {
        this.ebeguParameterRS.getJahresabhParameter().then((response: Array<TSEbeguParameter>) => {
            this.ebeguJahresabhParameter = response;
        });
    }

    getTSGesuchsperiodeStatusValues(): Array<TSGesuchsperiodeStatus> {
        return getTSGesuchsperiodeStatusValues();
    }

    private periodenaParamsEditableForPeriode(gesuchsperiode: TSGesuchsperiode): boolean {
        if (gesuchsperiode && gesuchsperiode.status) {
            // Fuer SuperAdmin immer auch editierbar, wenn AKTIV oder INAKTIV, sonst nur ENTWURF
            if (TSGesuchsperiodeStatus.GESCHLOSSEN === gesuchsperiode.status) {
                return false;
            } else if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getSuperAdminRoles())) {
                return true;
            } else {
                return TSGesuchsperiodeStatus.ENTWURF === gesuchsperiode.status;
            }
        }
        return false;
    }

    public periodenaParamsEditable(): boolean {
        return this.periodenaParamsEditableForPeriode(this.gesuchsperiode);
    }

    public jahresParamsEditable(): boolean {
        // Wenn die Periode, die in dem Jahr *endet* noch ENTWURF ist
        for (let gp of this.gesuchsperiodenList) {
            if (gp.gueltigkeit.gueltigBis.year() === this.jahr) {
                return this.periodenaParamsEditableForPeriode(gp);
            }
        }
        return true;
    }
}
