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
import ErrorService from '../../../core/errors/service/ErrorService';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import ListResourceRS from '../../../core/service/listResourceRS.rest';
import {ModulTagesschuleRS} from '../../../core/service/modulTagesschuleRS.rest';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import {TSDayOfWeek} from '../../../models/enums/TSDayOfWeek';
import {getTSModulTagesschuleNameValues, TSModulTagesschuleName} from '../../../models/enums/TSModulTagesschuleName';
import TSAdresse from '../../../models/TSAdresse';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSInstitutionStammdatenTagesschule from '../../../models/TSInstitutionStammdatenTagesschule';
import TSModulTagesschule from '../../../models/TSModulTagesschule';
import TSLand from '../../../models/types/TSLand';
import EbeguUtil from '../../../utils/EbeguUtil';
import AbstractAdminViewController from '../../abstractAdminView';
import {IInstitutionStammdatenStateParams} from '../../admin.route';
import IStateService = angular.ui.IStateService;
import IFormController = angular.IFormController;

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
    modulTageschuleMap: { [key: string]: TSModulTagesschule; } = {};

    static $inject = ['InstitutionRS', 'ModulTagesschuleRS', 'EbeguUtil', 'InstitutionStammdatenRS', 'ErrorService', '$state', 'ListResourceRS', 'AuthServiceRS', '$stateParams'];

    constructor(private institutionRS: InstitutionRS, private modulTagesschuleRS: ModulTagesschuleRS, private ebeguUtil: EbeguUtil,
                private institutionStammdatenRS: InstitutionStammdatenRS,
                private errorService: ErrorService, private $state: IStateService, private listResourceRS: ListResourceRS, authServiceRS: AuthServiceRS,
                private $stateParams: IInstitutionStammdatenStateParams) {
        super(authServiceRS);
    }

    $onInit() {
        this.setBetreuungsangebotTypValues();
        this.listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });
        if (!this.$stateParams.institutionStammdatenId) {
            this.institutionRS.findInstitution(this.$stateParams.institutionId).then((institution) => {
                this.selectedInstitution = institution;
                this.createInstitutionStammdaten();
                this.loadModuleTagesschule();
            });
        } else {
            this.institutionStammdatenRS.findInstitutionStammdaten(this.$stateParams.institutionStammdatenId).then((institutionStammdaten) => {
                this.setSelectedInstitutionStammdaten(institutionStammdaten);
                this.loadModuleTagesschule();
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
            this.replaceTagesschulmoduleOnInstitutionStammdatenTagesschule();
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
        return this.selectedInstitutionStammdatenBetreuungsangebot
            && this.selectedInstitutionStammdatenBetreuungsangebot.key === TSBetreuungsangebotTyp.KITA;
    }

    isTagesschule(): boolean {
        return this.selectedInstitutionStammdatenBetreuungsangebot
            && this.selectedInstitutionStammdatenBetreuungsangebot.key === TSBetreuungsangebotTyp.TAGESSCHULE;
    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    public getModulTagesschuleNamen(): TSModulTagesschuleName[] {
        return getTSModulTagesschuleNameValues();
    }

    public getModulTagesschule(modulname: TSModulTagesschuleName): TSModulTagesschule {
        let modul: TSModulTagesschule = this.modulTageschuleMap[modulname];
        if (!modul) {
            modul = new TSModulTagesschule();
            modul.wochentag = TSDayOfWeek.MONDAY;
            modul.modulTagesschuleName = modulname;
            this.modulTageschuleMap[modulname] = modul;
        }
        return modul;
    }

    private loadModuleTagesschule(): void {
        this.modulTageschuleMap = {};
        if (this.selectedInstitutionStammdaten && this.selectedInstitutionStammdaten.id) {
            if (this.selectedInstitutionStammdaten.institutionStammdatenTagesschule && this.selectedInstitutionStammdaten.institutionStammdatenTagesschule.moduleTagesschule) {
                this.fillModulTagesschuleMap(this.selectedInstitutionStammdaten.institutionStammdatenTagesschule.moduleTagesschule);
            }
        } else {
            this.fillModulTagesschuleMap([]);
        }
    }

    private fillModulTagesschuleMap(modulListFromServer: TSModulTagesschule[]) {
        getTSModulTagesschuleNameValues().forEach((modulname: TSModulTagesschuleName) => {
            let foundmodul = modulListFromServer.filter(modul => (modul.modulTagesschuleName === modulname))[0];
            if (foundmodul) {
                this.modulTageschuleMap[modulname] = foundmodul;
            } else {
                this.modulTageschuleMap[modulname] = this.getModulTagesschule(modulname);
            }
        });
    }

    private replaceTagesschulmoduleOnInstitutionStammdatenTagesschule(): void {
        let definiedModulTagesschule = [];
        for (let modulname in this.modulTageschuleMap) {
            let tempModul: TSModulTagesschule = this.modulTageschuleMap[modulname];
            if (tempModul.zeitVon && tempModul.zeitBis) {
                definiedModulTagesschule.push(tempModul);
            }
        }
        if (definiedModulTagesschule.length > 0) {
            if (!this.selectedInstitutionStammdaten.institutionStammdatenTagesschule) {
                this.selectedInstitutionStammdaten.institutionStammdatenTagesschule = new TSInstitutionStammdatenTagesschule();
            }
            this.selectedInstitutionStammdaten.institutionStammdatenTagesschule.moduleTagesschule = definiedModulTagesschule;
        }
    }
}
