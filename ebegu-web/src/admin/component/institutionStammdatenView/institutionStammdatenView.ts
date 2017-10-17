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

import {IComponentOptions, IHttpResponse} from 'angular';
import * as moment from 'moment';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import ErrorService from '../../../core/errors/service/ErrorService';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import ListResourceRS from '../../../core/service/listResourceRS.rest';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSAdresse from '../../../models/TSAdresse';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSLand from '../../../models/types/TSLand';
import EbeguUtil from '../../../utils/EbeguUtil';
import AbstractAdminViewController from '../../abstractAdminView';
import {IInstitutionStammdatenStateParams} from '../../admin.route';
import IStateService = angular.ui.IStateService;
import IFormController = angular.IFormController;
import ITranslateService = angular.translate.ITranslateService;
import Moment = moment.Moment;

let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');
let template = require('./institutionStammdatenView.html');
require('./institutionStammdatenView.less');

export class InstitutionStammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = InstitutionStammdatenViewController;
    controllerAs = 'vm';
}

export class InstitutionStammdatenViewController extends AbstractAdminViewController {

    form: IFormController;

    selectedInstitution: TSInstitution;
    selectedInstitutionStammdaten: TSInstitutionStammdaten;
    betreuungsangebotValues: Array<any>;
    selectedInstitutionStammdatenBetreuungsangebot: any;
    laenderList: TSLand[];
    errormessage: string = undefined;
    hasDifferentZahlungsadresse: boolean = false;
    isNew: boolean;

    static $inject = ['InstitutionRS', 'EbeguUtil', 'InstitutionStammdatenRS', 'ErrorService', '$state', 'ListResourceRS', 'AuthServiceRS', '$stateParams'];

    constructor(private institutionRS: InstitutionRS, private ebeguUtil: EbeguUtil, private institutionStammdatenRS: InstitutionStammdatenRS,
                private errorService: ErrorService, private $state: IStateService,
                private listResourceRS: ListResourceRS, authServiceRS: AuthServiceRS, private $stateParams: IInstitutionStammdatenStateParams) {
        super(authServiceRS);
    }

    $onInit() {
        this.setBetreuungsangebotTypValues();
        this.listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });
        if (this.$stateParams.institutionStammdatenId === '') {
            this.institutionRS.findInstitution(this.$stateParams.institutionId).then((institution) => {
                this.selectedInstitution = institution;
                this.createInstitutionStammdaten();
                this.isNew = true;
            });
        } else {
            this.institutionStammdatenRS.findInstitutionStammdaten(this.$stateParams.institutionStammdatenId).then((institutionStammdaten) => {
                this.setSelectedInstitutionStammdaten(institutionStammdaten);
                this.isNew = false;
            });
        }
    }

    isCreateStammdatenMode(): boolean {
        return this.selectedInstitutionStammdaten && this.selectedInstitutionStammdaten.isNew();
    }

    setSelectedInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): void {
        this.selectedInstitutionStammdaten = institutionStammdaten;
        this.selectedInstitution = institutionStammdaten.institution;
        this.selectedInstitutionStammdatenBetreuungsangebot = this.getBetreuungsangebotFromInstitutionList(institutionStammdaten.betreuungsangebotTyp);
        this.hasDifferentZahlungsadresse = !!this.selectedInstitutionStammdaten.adresseKontoinhaber;

    }

    getSelectedInstitutionStammdaten(): TSInstitutionStammdaten {
        return this.selectedInstitutionStammdaten;
    }

    createInstitutionStammdaten(): void {
        this.selectedInstitutionStammdaten = new TSInstitutionStammdaten();
        this.selectedInstitutionStammdaten.adresse = new TSAdresse();
        this.selectedInstitutionStammdaten.institution = this.selectedInstitution;
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
                    this.goBack();
                });
            } else {
                this.institutionStammdatenRS.updateInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                    this.goBack();
                });
            }
        }
    }

    private goBack() {
        this.$state.go('institution', {
            institutionId: this.selectedInstitution.id
        });
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

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

}
