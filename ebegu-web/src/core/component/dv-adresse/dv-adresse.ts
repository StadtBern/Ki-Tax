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

import AdresseRS from '../../service/adresseRS.rest';
import TSLand from '../../../models/types/TSLand';
import ListResourceRS from '../../service/listResourceRS.rest';
import {IComponentOptions, IFormController} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import TSAdresseContainer from '../../../models/TSAdresseContainer';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {isAtLeastFreigegeben} from '../../../models/enums/TSAntragStatus';
import ITranslateService = angular.translate.ITranslateService;

require('./dv-adresse.less');

export class AdresseComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        adresse: '<',
        prefix: '@',
        organisation: '<',
        showNichtInGemeinde: '<',
        showIfBisherNone: '<'
    };
    template = require('./dv-adresse.html');
    controller = DvAdresseController;
    controllerAs = 'vm';
    require: any = {parentForm: '?^form'};
}


export class DvAdresseController {
    static $inject = ['AdresseRS', 'ListResourceRS', 'GesuchModelManager', '$translate', 'AuthServiceRS'];

    adresse: TSAdresseContainer;
    prefix: string;
    adresseRS: AdresseRS;
    $translate: ITranslateService;
    parentForm: IFormController;
    laenderList: TSLand[];
    organisation: boolean;
    TSRoleUtil = TSRoleUtil;
    showNichtInGemeinde: boolean;
    gesuchModelManager: GesuchModelManager;
    bisherLand: string;

    /* @ngInject */
    constructor(adresseRS: AdresseRS, listResourceRS: ListResourceRS, gesuchModelManager: GesuchModelManager,
                $translate: ITranslateService, private authServiceRS: AuthServiceRS) {
        this.TSRoleUtil = TSRoleUtil;
        this.adresseRS = adresseRS;
        this.gesuchModelManager = gesuchModelManager;
        this.$translate = $translate;
        this.bisherLand = this.getBisherLand();
        listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });
    }

    submit() {
        this.adresseRS.create(this.adresse)
            .then((response: any) => {
                if (response.status === 201) {
                    this.resetForm();
                }
            });
    }

    resetForm() {
        this.adresse = undefined;
    }


    public isGesuchReadonly(): boolean {
        return this.gesuchModelManager.isGesuchReadonly();
    }

    public showDatumVon(): boolean {
        return this.adresse.showDatumVon;
    }

    public getModel(): TSAdresseContainer {
        return this.adresse;
    }

    private getBisherLand(): string {
        if (this.getModel() &&  this.getModel().adresseGS && this.getModel().adresseGS.land) {
            return this.$translate.instant('Land_' + this.getModel().adresseGS.land);
        }
        return '';
    }

    public enableNichtInGemeinde(): boolean {
        return !this.isGesuchReadonly()
            && isAtLeastFreigegeben(this.gesuchModelManager.getGesuch().status)
            && this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole());
    }
}

