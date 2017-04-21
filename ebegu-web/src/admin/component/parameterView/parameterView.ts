import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IComponentOptions, ILogService} from 'angular';
import './parameterView.less';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {EbeguVorlageRS} from '../../service/ebeguVorlageRS.rest';
import TSEbeguVorlage from '../../../models/TSEbeguVorlage';
import EbeguUtil from '../../../utils/EbeguUtil';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import GlobalCacheService from '../../../gesuch/service/globalCacheService';
import {TSCacheTyp} from '../../../models/enums/TSCacheTyp';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import ITranslateService = angular.translate.ITranslateService;
import ITimeoutService = angular.ITimeoutService;
import AbstractAdminViewController from '../../abstractAdminView';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {getTSGesuchsperiodeStatusValues, TSGesuchsperiodeStatus} from '../../../models/enums/TSGesuchsperiodeStatus';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
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
        'EbeguUtil', 'DvDialog', 'DownloadRS', '$log', 'GlobalCacheService', 'GesuchModelManager', '$timeout',
        '$window', 'AuthServiceRS'];

    ebeguParameterRS: EbeguParameterRS;
    ebeguRestUtil: EbeguRestUtil;

    gesuchsperiodenList: Array<TSGesuchsperiode> = [];
    gesuchsperiode: TSGesuchsperiode;

    jahr: number;
    ebeguJahresabhParameter: TSEbeguParameter[] = []; // enthält alle Jahresabhängigen Params für alle Jahre

    ebeguParameterListGesuchsperiode: TSEbeguParameter[];
    ebeguVorlageListGesuchsperiode: TSEbeguVorlage[];
    ebeguParameterListJahr: TSEbeguParameter[]; // enthält alle Params für nur 1 Jahr


    /* @ngInject */
    constructor(ebeguParameterRS: EbeguParameterRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                ebeguRestUtil: EbeguRestUtil, private $translate: ITranslateService,
                private ebeguVorlageRS: EbeguVorlageRS, private ebeguUtil: EbeguUtil,
                private dvDialog: DvDialog, private downloadRS: DownloadRS, private $log: ILogService,
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
        this.ebeguVorlageRS.getEbeguVorlagenByGesuchsperiode(this.gesuchsperiode.id).then((response: TSEbeguVorlage[]) => {
            this.ebeguVorlageListGesuchsperiode = response;
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
            let prevGesPer: TSGesuchsperiode = this.gesuchsperiodenList[this.gesuchsperiodenList.length - 1];
            this.gesuchsperiode.gueltigkeit.gueltigAb = prevGesPer.gueltigkeit.gueltigAb.clone().add('years', 1);
            this.gesuchsperiode.gueltigkeit.gueltigBis = prevGesPer.gueltigkeit.gueltigBis.clone().add('years', 1);
        }
    }

    saveGesuchsperiode(): void {
        this.gesuchsperiodeRS.updateGesuchsperiode(this.gesuchsperiode).then((response: TSGesuchsperiode) => {
            this.gesuchsperiode = response;

            let index: number = this.getIndexOfElementwithID(response);
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
            this.gesuchModelManager.updateActiveGesuchsperiodenList(); //reset gesuchperioden is manager
        });
    }

    private getIndexOfElementwithID(gesuchsperiodeToSearch: TSGesuchsperiode): number {
        let idToSearch = gesuchsperiodeToSearch.id;
        for (let i = 0; i < this.gesuchsperiodenList.length; i++) {
            if (this.gesuchsperiodenList[i].id === idToSearch) {
                return i;
            }
        }
        return -1;

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
        this.gesuchModelManager.updateActiveGesuchsperiodenList();
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

    hasVorlage(selectVorlage: TSEbeguVorlage): boolean {
        if (selectVorlage.vorlage) {
            return true;
        }
        return false;
    }

    uploadAnhaenge(files: any[], selectEbeguVorlage: TSEbeguVorlage) {
        this.$log.debug('Uploading files ');

        this.ebeguVorlageRS.uploadVorlage(files[0], selectEbeguVorlage, this.gesuchsperiode.id).then((response) => {
            this.addResponseToCurrentList(response);

        });
    }

    private addResponseToCurrentList(response: TSEbeguVorlage) {
        let returnedDG: TSEbeguVorlage = angular.copy(response);
        let index = this.getIndexOfElement(returnedDG, this.ebeguVorlageListGesuchsperiode);

        if (index > -1) {
            //this.$log.debug('add dokument to dokumentList');
            this.ebeguVorlageListGesuchsperiode[index] = returnedDG;
        }
        this.ebeguUtil.handleSmarttablesUpdateBug(this.ebeguVorlageListGesuchsperiode);
    }

    public getIndexOfElement(entityToSearch: TSEbeguVorlage, listToSearchIn: TSEbeguVorlage[]): number {
        let idToSearch = entityToSearch.name;
        for (let i = 0; i < listToSearchIn.length; i++) {
            if (listToSearchIn[i].name === idToSearch) {
                return i;
            }
        }
        return -1;
    }

    remove(ebeguVorlage: TSEbeguVorlage) {
        this.$log.debug('component -> remove dokument ' + ebeguVorlage.vorlage.filename);
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'FILE_LOESCHEN'
        })
            .then(() => {   //User confirmed removal

                this.ebeguVorlageRS.deleteEbeguVorlage(ebeguVorlage.id).then((response) => {

                    let index = EbeguUtil.getIndexOfElementwithID(ebeguVorlage, this.ebeguVorlageListGesuchsperiode);
                    if (index > -1) {
                        this.$log.debug('remove Vorlage in EbeguVorlage');
                        ebeguVorlage.vorlage = null;
                        this.ebeguVorlageListGesuchsperiode[index] = ebeguVorlage;
                    }
                });
                this.ebeguUtil.handleSmarttablesUpdateBug(this.ebeguVorlageListGesuchsperiode);

            });
    }

    download(ebeguVorlage: TSEbeguVorlage, attachment: boolean) {
        this.$log.debug('download vorlage ' + ebeguVorlage.vorlage.filename);
        let win: Window = this.downloadRS.prepareDownloadWindow();

        this.downloadRS.getAccessTokenVorlage(ebeguVorlage.vorlage.id).then((downloadFile: TSDownloadFile) => {
            this.$log.debug('accessToken: ' + downloadFile.accessToken);
            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, attachment, win);
        });
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
