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
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import {TSMandant} from '../../../models/TSMandant';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import {TSDateRange} from '../../../models/types/TSDateRange';
import EbeguUtil from '../../../utils/EbeguUtil';
import AbstractAdminViewController from '../../abstractAdminView';
import {IInstitutionStateParams} from '../../admin.route';
import IStateService = angular.ui.IStateService;
import IFormController = angular.IFormController;

let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let template = require('./institutionView.html');
require('./institutionView.less');
import $ = require('jquery');

export class InstitutionViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        traegerschaften: '<',
        mandant: '<'
    };
    template: string = template;
    controller: any = InstitutionViewController;
    controllerAs: string = 'vm';
}

export class InstitutionViewController extends AbstractAdminViewController {

    form: IFormController;

    traegerschaften: TSTraegerschaft[];
    mandant: TSMandant;
    instStammdatenList: TSInstitutionStammdaten[] = [];
    selectedInstitution: TSInstitution = undefined;
    selectedInstitutionStammdaten: TSInstitutionStammdaten = undefined;
    betreuungsangebotValues: Array<any>;
    errormessage: string = undefined;

    static $inject = ['InstitutionRS', 'InstitutionStammdatenRS', 'ErrorService', 'DvDialog', 'EbeguUtil', 'AuthServiceRS', '$stateParams', '$state'];

    constructor(private institutionRS: InstitutionRS, private institutionStammdatenRS: InstitutionStammdatenRS,
                private errorService: ErrorService, private dvDialog: DvDialog, private ebeguUtil: EbeguUtil,
                authServiceRS: AuthServiceRS, private $stateParams: IInstitutionStateParams,
                private $state: IStateService) {
        super(authServiceRS);
    }

    $onInit() {
        this.setBetreuungsangebotTypValues();
        if (!this.$stateParams.institutionId) {
            this.createInstitution();
        } else {
            this.institutionRS.findInstitution(this.$stateParams.institutionId).then((found: TSInstitution) => {
                this.setSelectedInstitution(found);
            });
        }
    }

    getTreagerschaftList(): Array<TSTraegerschaft> {
        return this.traegerschaften;
    }

    createInstitution(): void {
        this.selectedInstitution = new TSInstitution();
        this.selectedInstitution.mandant = this.mandant;
        this.selectedInstitutionStammdaten = undefined;
    }

    setSelectedInstitution(institution: TSInstitution): void {
        this.selectedInstitution = institution;
        this.selectedInstitutionStammdaten = undefined;
        if (!this.isCreateInstitutionsMode()) {
            this.institutionStammdatenRS.getAllInstitutionStammdatenByInstitution(this.selectedInstitution.id).then((loadedInstStammdaten) => {
                this.instStammdatenList = loadedInstStammdaten;
            });
        }
        this.errormessage = undefined;
    }

    isCreateInstitutionsMode(): boolean {
        return this.selectedInstitution && this.selectedInstitution.isNew();
    }

    getSelectedInstitution(): TSInstitution {
        return this.selectedInstitution;
    }

    saveInstitution(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            if (this.isCreateInstitutionsMode()) {
                this.institutionRS.createInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                    if (!institution.synchronizedWithOpenIdm) {
                        this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                            title: 'INSTITUTION_CREATE_SYNCHRONIZE'
                        });
                    }
                    this.setSelectedInstitution(institution);
                });
            } else {
                this.institutionRS.updateInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                    if (!institution.synchronizedWithOpenIdm) {
                        this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                            title: 'INSTITUTION_UPDATE_SYNCHRONIZE'
                        });
                    }
                });
            }
        }
    }

    private goBack() {
        this.$state.go('institutionen');
    }

    getInstitutionStammdatenList(): TSInstitutionStammdaten[] {
        return this.instStammdatenList;
    }

    removeInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): void {
        this.dvDialog.showRemoveDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'LOESCHEN_DIALOG_TITLE',
            parentController: undefined,
            elementID: undefined,
            form: this.form
        }).then(() => {   //User confirmed removal
            this.institutionStammdatenRS.removeInstitutionStammdaten(institutionStammdaten.id).then((result) => {
                let index = EbeguUtil.getIndexOfElementwithID(institutionStammdaten, this.instStammdatenList);
                if (index > -1) {
                    this.instStammdatenList.splice(index, 1);
                }
            }).catch((ex) => {
                this.errormessage = 'INSTITUTION_STAMMDATEN_DELETE_FAILED';
            });
        });
    }

    getDateString(dateRange: TSDateRange, format: string): string {
        if (dateRange.gueltigAb) {
            if (!dateRange.gueltigBis) {
                return dateRange.gueltigAb.format(format);
            } else {
                return dateRange.gueltigAb.format(format) + ' - ' + dateRange.gueltigBis.format(format);
            }
        }
        return '';
    }

    getBetreuungsangebotFromInstitutionList(betreuungsangebotTyp: TSBetreuungsangebotTyp) {
        return $.grep(this.betreuungsangebotValues, (value: any) => {
            return value.key === betreuungsangebotTyp;
        })[0];
    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    editInstitutionStammdaten(institutionstammdaten: TSInstitutionStammdaten) {
        this.$state.go('institutionstammdaten', {
            institutionId: this.selectedInstitution.id,
            institutionStammdatenId: institutionstammdaten.id
        });
    }

    createInstitutionStammdaten(): void {
        this.$state.go('institutionstammdaten', {
            institutionId: this.selectedInstitution.id,
            institutionStammdatenId: undefined
        });
    }
}
