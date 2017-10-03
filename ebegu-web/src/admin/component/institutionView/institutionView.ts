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
import './institutionView.less';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import {TSMandant} from '../../../models/TSMandant';
import TSAdresse from '../../../models/TSAdresse';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import EbeguUtil from '../../../utils/EbeguUtil';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {OkHtmlDialogController} from '../../../gesuch/dialog/OkHtmlDialogController';
import IPromise = angular.IPromise;
import IFormController = angular.IFormController;
import ListResourceRS from '../../../core/service/listResourceRS.rest';
import TSLand from '../../../models/types/TSLand';
import AbstractAdminViewController from '../../abstractAdminView';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import IQService = angular.IQService;
let template = require('./institutionView.html');
let style = require('./institutionView.less');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');

export class InstitutionViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        institutionen: '<',
        traegerschaften: '<',
        mandant: '<'
    };
    template: string = template;
    controller: any = InstitutionViewController;
    controllerAs: string = 'vm';
}

export class InstitutionViewController extends AbstractAdminViewController {

    institutionRS: InstitutionRS;
    institutionStammdatenRS: InstitutionStammdatenRS;
    ebeguUtil: EbeguUtil;
    institutionen: TSInstitution[];
    traegerschaften: TSTraegerschaft[];
    mandant: TSMandant;
    instStammdatenList: TSInstitutionStammdaten[] = [];
    selectedInstitution: TSInstitution = null;
    isSelected: boolean = false;
    selectedInstitutionStammdaten: TSInstitutionStammdaten = null;
    isSelectedStammdaten: boolean = false;
    betreuungsangebotValues: Array<any>;
    selectedInstitutionStammdatenBetreuungsangebot: any = null;
    laenderList: TSLand[];
    errormessage: string = undefined;
    hasDifferentZahlungsadresse: boolean = false;


    static $inject = ['InstitutionRS', 'EbeguUtil', 'InstitutionStammdatenRS', 'ErrorService', 'DvDialog', 'ListResourceRS', 'AuthServiceRS'];
    /* @ngInject */
    constructor(institutionRS: InstitutionRS, ebeguUtil: EbeguUtil, institutionStammdatenRS: InstitutionStammdatenRS,
                private errorService: ErrorService, private dvDialog: DvDialog, listResourceRS: ListResourceRS, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
        this.institutionRS = institutionRS;
        this.ebeguUtil = ebeguUtil;
        this.institutionStammdatenRS = institutionStammdatenRS;
        this.setBetreuungsangebotTypValues();
        listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });

    }

    getInstitutionenList(): TSInstitution[] {
        return this.institutionen;
    }

    getTreagerschaftList(): Array<TSTraegerschaft> {
        return this.traegerschaften;
    }

    setSelectedInstitution(institution: TSInstitution): void {
        this.selectedInstitution = institution;
        this.isSelected = true;
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
        this.institutionStammdatenRS.getAllInstitutionStammdatenByInstitution(this.selectedInstitution.id).then((loadedInstStammdaten) => {
            this.instStammdatenList = loadedInstStammdaten;
        });
        this.errormessage = undefined;
    }

    isCreateInstitutionsMode(): boolean {
        return this.selectedInstitution.isNew();

    }

    isCreateStammdatenMode(): boolean {
        return this.selectedInstitutionStammdaten.isNew();
    }

    getSelectedInstitution(): TSInstitution {
        return this.selectedInstitution;
    }

    isSelectedInstitution(): boolean {
        return this.isSelected;
    }

    removeInstitution(institution: any): void {
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'LOESCHEN_DIALOG_TITLE',
            parentController: undefined,
            elementID: undefined
        })
        .then(() => {   //User confirmed removal
            this.selectedInstitution = null;
            this.isSelected = false;
            this.institutionRS.removeInstitution(institution.id).then((response) => {
                let index = EbeguUtil.getIndexOfElementwithID(institution, this.institutionen);
                if (index > -1) {
                    this.institutionen.splice(index, 1);
                }
            });
        });

    }

    createInstitution(): void {
        this.selectedInstitution = new TSInstitution();
        this.selectedInstitution.mandant = this.mandant;
        this.isSelected = true;
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
        this.instStammdatenList = [];
    }

    saveInstitution(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            if (this.isCreateInstitutionsMode() === true) {
                this.institutionRS.createInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                    this.institutionen.push(institution);
                    this.resetInstitutionSelection();
                    if (!institution.synchronizedWithOpenIdm) {
                        this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                            title: 'INSTITUTION_CREATE_SYNCHRONIZE'
                        });
                    }
                });
            } else {
                this.institutionRS.updateInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                    let index = EbeguUtil.getIndexOfElementwithID(institution, this.institutionen);
                    if (index > -1) {
                        this.institutionen[index] = institution;
                        this.resetInstitutionSelection();
                        if (!institution.synchronizedWithOpenIdm) {
                            this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                                title: 'INSTITUTION_UPDATE_SYNCHRONIZE'
                            });
                        }
                    }
                });
            }
        }

    }

    private resetInstitutionSelection() {
        this.selectedInstitution = null;
        this.isSelected = false;
        this.errormessage = undefined;
    }

    getSelectedInstitutionStammdatenList(): TSInstitutionStammdaten[] {
        return this.instStammdatenList;
    }

    setSelectedInstitutionStammdaten(institutionStammdaten: any): void {
        this.selectedInstitutionStammdaten = institutionStammdaten;
        this.selectedInstitutionStammdatenBetreuungsangebot = this.getBetreuungsangebotFromInstitutionList(institutionStammdaten.betreuungsangebotTyp);
        this.isSelectedStammdaten = true;
        this.hasDifferentZahlungsadresse = !!this.selectedInstitutionStammdaten.adresseKontoinhaber;
    }

    getSelectedInstitutionStammdaten(): TSInstitutionStammdaten {
        return this.selectedInstitutionStammdaten;
    }

    isSelectedInstitutionStammdaten(): boolean {
        return this.isSelectedStammdaten;
    }

    createInstitutionStammdaten(): void {
        this.selectedInstitutionStammdaten = new TSInstitutionStammdaten();
        this.selectedInstitutionStammdaten.adresse = new TSAdresse();
        this.selectedInstitutionStammdaten.institution = this.selectedInstitution;
        this.isSelectedStammdaten = true;
    }

    differentZahlungsadresseClicked(): void {
        if (this.hasDifferentZahlungsadresse) {
            this.selectedInstitutionStammdaten.adresseKontoinhaber = new TSAdresse();
        } else {
            this.selectedInstitutionStammdaten.adresseKontoinhaber = undefined;
        }
    }

    saveInstitutionStammdaten(form: IFormController): void {
        if (form.$valid) {
            this.selectedInstitutionStammdaten.betreuungsangebotTyp = this.selectedInstitutionStammdatenBetreuungsangebot.key;
            this.errorService.clearAll();
            if (this.isCreateStammdatenMode()) {
                this.institutionStammdatenRS.createInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                    this.instStammdatenList.push(institutionStammdaten);
                    this.resetInstitutionStammdatenSelection();
                });
            } else {
                this.institutionStammdatenRS.updateInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                    let index = EbeguUtil.getIndexOfElementwithID(institutionStammdaten, this.instStammdatenList);
                    if (index > -1) {
                        this.instStammdatenList[index] = institutionStammdaten;
                        this.resetInstitutionStammdatenSelection();
                    }
                });
            }
        }
    }

    private resetInstitutionStammdatenSelection() {
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
    }

    removeInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): void {
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'LOESCHEN_DIALOG_TITLE',
            parentController: undefined,
            elementID: undefined
        })
        .then(() => {   //User confirmed removal
            this.institutionStammdatenRS.removeInstitutionStammdaten(institutionStammdaten.id).then((result) => {
                let index = EbeguUtil.getIndexOfElementwithID(institutionStammdaten, this.instStammdatenList);
                if (index > -1) {
                    this.instStammdatenList.splice(index, 1);
                }
                this.isSelectedStammdaten = false;
            }).catch((ex) => {
                this.errormessage = 'INSTITUTION_STAMMDATEN_DELETE_FAILED';
            });
        });

    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    getBetreuungsangebotFromInstitutionList(betreuungsangebotTyp: TSBetreuungsangebotTyp) {
        return $.grep(this.betreuungsangebotValues, (value: any) => {
            return value.key === betreuungsangebotTyp;
        })[0];
    }

    isKita(): boolean {
        if (this.selectedInstitutionStammdatenBetreuungsangebot && this.selectedInstitutionStammdatenBetreuungsangebot.key === TSBetreuungsangebotTyp.KITA) {
            return true;
        } else {
            return false;
        }
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

    private syncWithOpenIdm(): void {
        this.institutionRS.synchronizeInstitutions().then((respone) => {
            let returnString = respone.data.replace(/(?:\r\n|\r|\n)/g, '<br />');
            return this.dvDialog.showDialog(okHtmlDialogTempl, OkHtmlDialogController, {
                title: returnString
            }).then(() => {
                //do nothing
            });
        });
    }

}
