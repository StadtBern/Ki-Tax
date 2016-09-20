import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IComponentOptions} from 'angular';
import './parameterView.less';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import DateUtil from '../../../utils/DateUtil';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {EbeguVorlageRS} from '../../service/ebeguVorlageRS.rest';
import TSEbeguVorlage from '../../../models/TSEbeguVorlage';
import EbeguUtil from '../../../utils/EbeguUtil';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import TSTempDokument from '../../../models/TSTempDokument';
import IPromise = angular.IPromise;
import ITranslateService = angular.translate.ITranslateService;
import Moment = moment.Moment;
import ILogService = angular.ILogService;
let template = require('./parameterView.html');
let style = require('./parameterView.less');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');

export class ParameterViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = ParameterViewController;
    controllerAs: string = 'vm';
}

export class ParameterViewController {
    static $inject = ['EbeguParameterRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$translate', 'EbeguVorlageRS',
        'EbeguUtil', 'DvDialog', 'DownloadRS'];

    ebeguParameterRS: EbeguParameterRS;
    ebeguRestUtil: EbeguRestUtil;

    gesuchsperiodenList: Array<TSGesuchsperiode>;
    gesuchsperiode: TSGesuchsperiode;

    jahr: number;

    ebeguParameterListGesuchsperiode: TSEbeguParameter[];
    ebeguVorlageListGesuchsperiode: TSEbeguVorlage[];
    ebeguParameterListJahr: TSEbeguParameter[];


    /* @ngInject */
    constructor(ebeguParameterRS: EbeguParameterRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                ebeguRestUtil: EbeguRestUtil, private $translate: ITranslateService,
                private ebeguVorlageRS: EbeguVorlageRS, private ebeguUtil: EbeguUtil,
                private dvDialog: DvDialog, private downloadRS: DownloadRS) {
        this.ebeguParameterRS = ebeguParameterRS;
        this.ebeguRestUtil = ebeguRestUtil;
        this.readGesuchsperioden();
        this.jahr = DateUtil.currentYear();
        this.jahrChanged();
    }

    private readGesuchsperioden(): void {
        this.gesuchsperiodeRS.getAllNichtAbgeschlosseneGesuchsperioden().then((response: any) => {
            this.gesuchsperiodenList = angular.copy(response);
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

    createGesuchsperiode(): void {
        this.gesuchsperiode = new TSGesuchsperiode(false, new TSDateRange());
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
            this.readEbeguParameterByGesuchsperiode();
        });
    }

    private getIndexOfElementwithID(gesuchsperiodeToSearch: TSGesuchsperiode): number {
        var idToSearch = gesuchsperiodeToSearch.id;
        for (var i = 0; i < this.gesuchsperiodenList.length; i++) {
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

    jahrChanged(): void {
        this.readEbeguParameterByJahr();
    }

    saveParameterByGesuchsperiode(): void {
        for (var i = 0; i < this.ebeguParameterListGesuchsperiode.length; i++) {
            var param = this.ebeguParameterListGesuchsperiode[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
        this.gesuchsperiode = undefined;
    }

    saveParameterByJahr(): void {
        for (var i = 0; i < this.ebeguParameterListJahr.length; i++) {
            var param = this.ebeguParameterListJahr[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
    }

    hasVorlage(selectVorlage: TSEbeguVorlage): boolean {
        if (selectVorlage.vorlage) {
            return true;
        }
        return false;
    }

    uploadAnhaenge(files: any[], selectEbeguVorlage: TSEbeguVorlage) {


        console.log('Uploading files ');

        this.ebeguVorlageRS.uploadVorlage(files[0], selectEbeguVorlage, this.gesuchsperiode.id).then((response) => {
            this.addResponseToCurrentList(response);

        });
    }

    private addResponseToCurrentList(response: TSEbeguVorlage) {
        let returnedDG: TSEbeguVorlage = angular.copy(response);
        var index = this.getIndexOfElement(returnedDG, this.ebeguVorlageListGesuchsperiode);

        if (index > -1) {
            //this.$log.debug('add dokument to dokumentList');
            this.ebeguVorlageListGesuchsperiode[index] = returnedDG;
        }
        this.ebeguUtil.handleSmarttablesUpdateBug(this.ebeguVorlageListGesuchsperiode);
    }

    public getIndexOfElement(entityToSearch: TSEbeguVorlage, listToSearchIn: TSEbeguVorlage[]): number {
        var idToSearch = entityToSearch.name;
        for (var i = 0; i < listToSearchIn.length; i++) {
            if (listToSearchIn[i].name === idToSearch) {
                return i;
            }
        }
        return -1;
    }

    remove(ebeguVorlage: TSEbeguVorlage) {
        console.log('component -> remove dokument ' + ebeguVorlage.vorlage.fileName);
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'FILE_LOESCHEN'
        })
            .then(() => {   //User confirmed removal

                this.ebeguVorlageRS.deleteEbeguVorlage(ebeguVorlage.id).then((response) => {

                    var index = EbeguUtil.getIndexOfElementwithID(ebeguVorlage, this.ebeguVorlageListGesuchsperiode);
                    if (index > -1) {
                        console.log('remove Vorlage in EbeguVorlage');
                        ebeguVorlage.vorlage = null;
                        this.ebeguVorlageListGesuchsperiode[index] = ebeguVorlage;
                    }
                });
                this.ebeguUtil.handleSmarttablesUpdateBug(this.ebeguVorlageListGesuchsperiode);

            });
    }

    download(ebeguVorlage: TSEbeguVorlage, attachment: boolean) {
        console.log('download vorlage ' + ebeguVorlage.vorlage.fileName);

        this.downloadRS.getAccessTokenVorlage(ebeguVorlage.vorlage.id).then((response) => {
            let tempDokument: TSTempDokument = angular.copy(response);
            console.log('accessToken: ' + tempDokument.accessToken);

            this.downloadRS.startDownload(tempDokument.accessToken, ebeguVorlage.vorlage.fileName, attachment);
        });
    }


}
