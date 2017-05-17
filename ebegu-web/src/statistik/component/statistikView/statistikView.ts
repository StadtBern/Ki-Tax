import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSStatistikParameter from '../../../models/TSStatistikParameter';
import {TSStatistikParameterType} from '../../../models/enums/TSStatistikParameterType';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {ReportRS} from '../../../core/service/reportRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import * as moment from 'moment';
import Moment = moment.Moment;
import DateUtil from '../../../utils/DateUtil';

let template = require('./statistikView.html');
require('./statistikView.less');

export class StatistikViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = StatistikViewController;
    controllerAs = 'vm';
}

export class StatistikViewController {
    private _statistikParameter: TSStatistikParameter;
    private _gesuchsperioden: Array<TSGesuchsperiode>;
    TSRole: any;
    TSRoleUtil: any;
    private DATETIME_PARAM_FORMAT: string = 'YYYY-MM-DD HH:mm:ss'; //TODO (team) wieso hier DateTime???
    private DATE_PARAM_FORMAT: string = 'YYYY-MM-DD';
    // Statistiken sind nur moeglich ab Beginn der fruehesten Periode bis Ende der letzten Periode
    private maxDate: Moment;
    private minDate: Moment;

    static $inject: string[] = ['$state', 'GesuchsperiodeRS', '$log', 'ReportRS', 'DownloadRS'];

    constructor(private $state: IStateService, private gesuchsperiodeRS: GesuchsperiodeRS, private $log: ILogService, private reportRS: ReportRS, private downloadRS: DownloadRS) {
    }

    $onInit() {
        this._statistikParameter = new TSStatistikParameter();
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this._gesuchsperioden = response;
            if (this._gesuchsperioden.length > 0) {
                this.maxDate = this._gesuchsperioden[0].gueltigkeit.gueltigBis;
                this.minDate = this._gesuchsperioden[this._gesuchsperioden.length - 1].gueltigkeit.gueltigAb;
            }
        });
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    public generateStatistik(form: IFormController, type?: TSStatistikParameterType): void {
        if (form.$valid) {
            let tmpType = (<any>TSStatistikParameterType)[type];
            tmpType ? this.$log.debug('Statistik Type: ' + tmpType) : this.$log.debug('default, Type not recognized');
            this.$log.debug('Validated Form: ' + form.$name);

            switch (tmpType) {
                case TSStatistikParameterType.GESUCH_STICHTAG: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getGesuchStichtagReportExcel(this._statistikParameter.stichtag.format(this.DATETIME_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((downloadFile: TSDownloadFile) => {

                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.GESUCH_ZEITRAUM: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getGesuchZeitraumReportExcel(this._statistikParameter.von.format(this.DATETIME_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATETIME_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((downloadFile: TSDownloadFile) => {

                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.KINDER: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getKinderReportExcel(
                        this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((downloadFile: TSDownloadFile) => {

                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.GESUCHSTELLER: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getGesuchstellerReportExcel(this._statistikParameter.stichtag.format(this.DATE_PARAM_FORMAT))
                        .then((downloadFile: TSDownloadFile) => {
                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.KANTON: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getKantonReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT))
                        .then((downloadFile: TSDownloadFile) => {

                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.MITARBEITERINNEN: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getMitarbeiterinnenReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT))
                        .then((downloadFile: TSDownloadFile) => {
                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG: {
                    let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getGesuchstellerKinderBetreuungReportExcel(
                        this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((downloadFile: TSDownloadFile) => {

                            this.$log.debug('accessToken: ' + downloadFile.accessToken);
                            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                        });
                    break;
                }
                case TSStatistikParameterType.ZAHLUNGEN_PERIODE:
                    if (this._statistikParameter.gesuchsperiode) {
                        let win: Window = this.downloadRS.prepareDownloadWindow();
                        this.reportRS.getZahlungPeriodeReportExcel(
                            this._statistikParameter.gesuchsperiode)
                            .then((downloadFile: TSDownloadFile) => {

                                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                            });
                    } else {
                        this.$log.warn('gesuchsperiode muss gewählt sein');
                    }
                    break;
                default:
                    this.$log.debug('default, Type not recognized');
                    break;
            }
        }
    }

    get statistikParameter(): TSStatistikParameter {
        return this._statistikParameter;
    }

    get gesuchsperioden(): Array<TSGesuchsperiode> {
        return this._gesuchsperioden;
    }
}
