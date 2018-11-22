/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {IComponentOptions, IIntervalService} from 'angular';
import {IStateService} from 'angular-ui-router';
import * as moment from 'moment';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import BatchJobRS from '../../../core/service/batchRS.rest';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {ReportAsyncRS} from '../../../core/service/reportAsyncRS.rest';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {TSRole} from '../../../models/enums/TSRole';
import {TSStatistikParameterType} from '../../../models/enums/TSStatistikParameterType';
import TSBatchJobInformation from '../../../models/TSBatchJobInformation';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import TSStatistikParameter from '../../../models/TSStatistikParameter';
import TSWorkJob from '../../../models/TSWorkJob';
import DateUtil from '../../../utils/DateUtil';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import IFormController = angular.IFormController;
import ILogService = angular.ILogService;
import ITranslateService = angular.translate.ITranslateService;
import Moment = moment.Moment;

let template = require('./statistikView.html');
require('./statistikView.less');

let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');

export class StatistikViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = StatistikViewController;
    controllerAs = 'vm';
}

export class StatistikViewController {

    private polling: angular.IPromise<any>;
    private _statistikParameter: TSStatistikParameter;
    private _gesuchsperioden: Array<TSGesuchsperiode>;
    TSRole: any;
    TSRoleUtil: any;
    private DATE_PARAM_FORMAT: string = 'YYYY-MM-DD';
    // Statistiken sind nur moeglich ab Beginn der fruehesten Periode bis Ende der letzten Periode
    private maxDate: Moment;
    private minDate: Moment;
    private userjobs: Array<TSWorkJob>;
    private allJobs: Array<TSBatchJobInformation>;
    private flagShowErrorNoGesuchSelected: boolean = false;

    static $inject: string[] = ['$state', 'GesuchsperiodeRS', '$log', 'ReportAsyncRS', 'DownloadRS', 'BatchJobRS',
        'ErrorService', '$translate', '$interval', 'DvDialog'];

    constructor(private $state: IStateService, private gesuchsperiodeRS: GesuchsperiodeRS, private $log: ILogService,
        private reportAsyncRS: ReportAsyncRS, private downloadRS: DownloadRS, private bachJobRS: BatchJobRS, private errorService: ErrorService,
        private $translate: ITranslateService, private $interval: IIntervalService, private dvDialog: DvDialog) {
    }

    $onInit() {
        this._statistikParameter = new TSStatistikParameter();
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this._gesuchsperioden = response;
            if (this._gesuchsperioden.length > 0) {
                this.maxDate = this._gesuchsperioden[0].gueltigkeit.gueltigBis;
                this.minDate = DateUtil.localDateToMoment('2017-01-01');
            }
        });
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;

        this.refreshUserJobs();
        this.initBatchJobPolling();
    }

    $onDestroy() {
        if (this.polling) {
            this.$interval.cancel(this.polling);
            this.$log.debug('canceld job polling');
        }
    }

    private initBatchJobPolling() {
        //check all 8 seconds for the state
        this.polling = this.$interval(() => this.refreshUserJobs(), 12000);

    }

    private refreshUserJobs() {
        this.bachJobRS.getBatchJobsOfUser().then((response: TSWorkJob[]) => {
            this.userjobs = response;
        });
    }

    public generateStatistik(form: IFormController, type?: TSStatistikParameterType): void {
        if (this.isMassenversandValid(type) && form.$valid) {
            let tmpType = (<any>TSStatistikParameterType)[type];
            tmpType ? this.$log.debug('Statistik Type: ' + tmpType) : this.$log.debug('default, Type not recognized');
            this.$log.debug('Validated Form: ' + form.$name);

            switch (tmpType) {
                case TSStatistikParameterType.GESUCH_STICHTAG:
                    this.reportAsyncRS.getGesuchStichtagReportExcel(this._statistikParameter.stichtag.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.GESUCH_ZEITRAUM:
                    this.reportAsyncRS.getGesuchZeitraumReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.KINDER:
                    this.reportAsyncRS.getKinderReportExcel(
                        this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.GESUCHSTELLER:
                    this.reportAsyncRS.getGesuchstellerReportExcel(this._statistikParameter.stichtag.format(this.DATE_PARAM_FORMAT))
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.KANTON:
                    this.reportAsyncRS.getKantonReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT))
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.MITARBEITERINNEN:
                    this.reportAsyncRS.getMitarbeiterinnenReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT))
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.BENUTZER:
                    this.reportAsyncRS.getBenutzerReportExcel()
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG:
                    this.reportAsyncRS.getGesuchstellerKinderBetreuungReportExcel(
                        this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                        this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                        .then((batchExecutionId: string) => {
                            this.informReportGenerationStarted(batchExecutionId);
                        })
                        .catch(() => {
                            this.$log.error('An error occurred downloading the document, closing download window.');
                        });
                    break;

                case TSStatistikParameterType.ZAHLUNGEN_PERIODE:
                    if (this._statistikParameter.gesuchsperiode) {
                        this.reportAsyncRS.getZahlungPeriodeReportExcel(
                            this._statistikParameter.gesuchsperiode)
                            .then((batchExecutionId: string) => {
                                this.$log.debug('executionID: ' + batchExecutionId);
                                let startmsg = this.$translate.instant('STARTED_GENERATION');
                                this.errorService.addMesageAsInfo(startmsg);
                            })
                            .catch(() => {
                                this.$log.error('An error occurred downloading the document, closing download window.');
                            });
                    } else {
                        this.$log.warn('gesuchsperiode muss gewählt sein');
                    }
                    break;
                case TSStatistikParameterType.MASSENVERSAND:
                    if (this.statistikParameter.text) {
                        this.dvDialog.showRemoveDialog(removeDialogTemplate, undefined, RemoveDialogController, {
                            title: this.$translate.instant('MASSENVERSAND_ERSTELLEN_CONFIRM_TITLE'),
                            deleteText: this.$translate.instant('MASSENVERSAND_ERSTELLEN_CONFIRM_INFO'),
                            parentController: undefined,
                            elementID: undefined
                        }).then(() => {   //User confirmed removal
                            this.createMassenversand();
                        });
                    } else {
                        this.createMassenversand();
                    }
                    break;
                default:
                    this.$log.debug('default, Type not recognized');
                    break;
            }
        }
    }

    private createMassenversand(): void {
        this.$log.info('Erstelle Massenversand');
        this.reportAsyncRS.getMassenversandReportExcel(
            this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
            this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
            this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null,
            this._statistikParameter.bgGesuche,
            this._statistikParameter.mischGesuche,
            this._statistikParameter.tsGesuche,
            this._statistikParameter.ohneFolgegesuche,
            this._statistikParameter.text)
            .then((batchExecutionId: string) => {
                this.informReportGenerationStarted(batchExecutionId);
            })
            .catch(() => {
                this.$log.error('An error occurred downloading the document, closing download window.');
            });
    }

    private informReportGenerationStarted(batchExecutionId: string) {
        this.$log.debug('executionID: ' + batchExecutionId);
        let startmsg = this.$translate.instant('STARTED_GENERATION');
        this.errorService.addMesageAsInfo(startmsg);
        this.refreshUserJobs();
    }

    get statistikParameter(): TSStatistikParameter {
        return this._statistikParameter;
    }

    get gesuchsperioden(): Array<TSGesuchsperiode> {
        return this._gesuchsperioden;
    }

    public rowClicked(row: TSWorkJob) {
        if (EbeguUtil.isNotNullOrUndefined(row) && EbeguUtil.isNotNullOrUndefined(row.execution)) {
            if (EbeguUtil.isNotNullOrUndefined(row.execution.batchStatus) && row.execution.batchStatus === 'COMPLETED') {
                let win: Window = this.downloadRS.prepareDownloadWindow();
                this.$log.debug('accessToken: ' + row.resultData);
                this.downloadRS.startDownload(row.resultData, 'report.xlsx', false, win);
            } else {
                this.$log.info('batch-job is not yet finnished');
            }
        }
    }

    /**
     * helper methode die es dem Admin erlaubt alle jobs zu sehen
     */
    public showAllJobs() {
        this.bachJobRS.getAllJobs().then((result: TSWorkJob[]) => {
            let res: TSBatchJobInformation[] = [];
            res = res.concat(result.map((value) => {
                return value.execution || undefined;
            }));
            this.allJobs = res;
        });
    }

    private isMassenversandValid(type?: TSStatistikParameterType): boolean {
        if (type === TSStatistikParameterType.MASSENVERSAND) {
            // simulate a click in the checkboxes of Verantwortlichkeit
            this.gesuchTypeClicked();

            return !this.flagShowErrorNoGesuchSelected;
        }
        // for any other kind of statistik we return always true
        return true;
    }

    public gesuchTypeClicked(): void {
        this.flagShowErrorNoGesuchSelected =
            !this.statistikParameter.bgGesuche
            && !this.statistikParameter.mischGesuche
            && !this.statistikParameter.tsGesuche;
    }
}
