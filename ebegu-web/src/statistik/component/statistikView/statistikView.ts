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

import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSStatistikParameter from '../../../models/TSStatistikParameter';
import {TSStatistikParameterType} from '../../../models/enums/TSStatistikParameterType';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import * as moment from 'moment';
import DateUtil from '../../../utils/DateUtil';
import {ReportAsyncRS} from '../../../core/service/reportAsyncRS.rest';
import ErrorService from '../../../core/errors/service/ErrorService';
import BatchJobRS from '../../../core/service/batchRS.rest';
import TSWorkJob from '../../../models/TSWorkJob';
import IFormController = angular.IFormController;
import ILogService = angular.ILogService;
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;

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
    private DATE_PARAM_FORMAT: string = 'YYYY-MM-DD';
    // Statistiken sind nur moeglich ab Beginn der fruehesten Periode bis Ende der letzten Periode
    private maxDate: Moment;
    private minDate: Moment;

    static $inject: string[] = ['$state', 'GesuchsperiodeRS', '$log', 'ReportAsyncRS', 'DownloadRS', 'BatchJobRS', 'ErrorService', '$translate'];

    constructor(private $state: IStateService, private gesuchsperiodeRS: GesuchsperiodeRS, private $log: ILogService,
        private reportRS: ReportAsyncRS, private downloadRS: DownloadRS, private bachJobRS: BatchJobRS, private errorService: ErrorService,
        private $translate: ITranslateService) {
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
        this.bachJobRS.getBatchJobsOfUser().then((response: TSWorkJob[]) => {
            console.log('found batchjobs', response);
        });
    }

    public generateStatistik(form: IFormController, type?: TSStatistikParameterType): void {
        if (form.$valid) {
            let tmpType = (<any>TSStatistikParameterType)[type];
            tmpType ? this.$log.debug('Statistik Type: ' + tmpType) : this.$log.debug('default, Type not recognized');
            this.$log.debug('Validated Form: ' + form.$name);

            switch (tmpType) {
            case TSStatistikParameterType.GESUCH_STICHTAG: {
                this.reportRS.getGesuchStichtagReportExcel(this._statistikParameter.stichtag.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex: any) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.GESUCH_ZEITRAUM: {
                this.reportRS.getGesuchZeitraumReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.KINDER: {
                this.reportRS.getKinderReportExcel(
                    this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.GESUCHSTELLER: {
                this.reportRS.getGesuchstellerReportExcel(this._statistikParameter.stichtag.format(this.DATE_PARAM_FORMAT))
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.KANTON: {
                this.reportRS.getKantonReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT))
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.MITARBEITERINNEN: {
//                    let win: Window = this.downloadRS.prepareDownloadWindow();
                this.reportRS.getMitarbeiterinnenReportExcel(this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT))
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG: {
//                    let win: Window = this.downloadRS.prepareDownloadWindow();
                this.reportRS.getGesuchstellerKinderBetreuungReportExcel(
                    this._statistikParameter.von.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.bis.format(this.DATE_PARAM_FORMAT),
                    this._statistikParameter.gesuchsperiode ? this._statistikParameter.gesuchsperiode.toString() : null)
                .then((batchExecutionId: string) => {
                    this.$log.debug('executionID: ' + batchExecutionId);
                    let startmsg = this.$translate.instant('STARTED_GENERATION');
                    this.errorService.addMesageAsInfo(startmsg);
                })
                .catch((ex) => {
                    this.$log.error('An error occurred downloading the document, closing download window.');
                });
                break;
            }
            case TSStatistikParameterType.ZAHLUNGEN_PERIODE:
                if (this._statistikParameter.gesuchsperiode) {
//                        let win: Window = this.downloadRS.prepareDownloadWindow();
                    this.reportRS.getZahlungPeriodeReportExcel(
                        this._statistikParameter.gesuchsperiode)
                    .then((batchExecutionId: string) => {
                        this.$log.debug('executionID: ' + batchExecutionId);
                        let startmsg = this.$translate.instant('STARTED_GENERATION');
                        this.errorService.addMesageAsInfo(startmsg);
                    })
                    .catch((ex) => {
                        this.$log.error('An error occurred downloading the document, closing download window.');
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
