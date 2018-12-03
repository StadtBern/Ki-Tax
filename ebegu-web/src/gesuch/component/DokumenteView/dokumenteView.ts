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

import {IComponentOptions, ILogService} from 'angular';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import TSDokumenteDTO from '../../../models/dto/TSDokumenteDTO';
import {TSCacheTyp} from '../../../models/enums/TSCacheTyp';
import {TSDokumentGrundTyp} from '../../../models/enums/TSDokumentGrundTyp';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSDokument from '../../../models/TSDokument';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStammdatenStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import DokumenteRS from '../../service/dokumenteRS.rest';
import GesuchModelManager from '../../service/gesuchModelManager';
import GlobalCacheService from '../../service/globalCacheService';
import WizardStepManager from '../../service/wizardStepManager';
import AbstractGesuchViewController from '../abstractGesuchView';
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;

let template = require('./dokumenteView.html');
require('./dokumenteView.less');

export class DokumenteViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DokumenteViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer den Dokumenten Upload
 */
export class DokumenteViewController extends AbstractGesuchViewController<any> {
    parsedNum: number;
    dokumenteEkv: TSDokumentGrund[] = [];
    dokumenteFinSit: TSDokumentGrund[] = [];
    dokumenteFamSit: TSDokumentGrund[] = [];
    dokumenteErwp: TSDokumentGrund[] = [];
    dokumenteKinder: TSDokumentGrund[] = [];
    dokumenteSonst: TSDokumentGrund[] = [];
    dokumentePapiergesuch: TSDokumentGrund[] = [];
    dokumenteFreigabequittung: TSDokumentGrund[] = [];

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager',
        'DokumenteRS', '$log', 'WizardStepManager', 'EbeguUtil', 'GlobalCacheService', '$scope',
        '$timeout', 'AuthServiceRS'];

    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private dokumenteRS: DokumenteRS, private $log: ILogService, wizardStepManager: WizardStepManager,
                private ebeguUtil: EbeguUtil, private globalCacheService: GlobalCacheService, $scope: IScope,
                $timeout: ITimeoutService, private authServiceRS: AuthServiceRS) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.DOKUMENTE, $timeout);
        this.parsedNum = parseInt($stateParams.gesuchstellerNumber, 10);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.calculate();
    }

    calculate() {
        if (this.gesuchModelManager.getGesuch()) {
            this.berechnungsManager
                .getDokumente(this.gesuchModelManager.getGesuch())
                .then((promiseValue: TSDokumenteDTO) => {
                    this.searchDokumente(promiseValue, this.dokumenteEkv, TSDokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
                    this.searchDokumente(promiseValue, this.dokumenteFinSit, TSDokumentGrundTyp.FINANZIELLESITUATION);
                    this.searchDokumente(promiseValue, this.dokumenteFamSit, TSDokumentGrundTyp.FAMILIENSITUATION);
                    this.searchDokumente(promiseValue, this.dokumenteErwp, TSDokumentGrundTyp.ERWERBSPENSUM);
                    this.searchDokumente(promiseValue, this.dokumenteKinder, TSDokumentGrundTyp.KINDER);
                    this.searchDokumente(promiseValue, this.dokumenteSonst, TSDokumentGrundTyp.SONSTIGE_NACHWEISE);
                    this.searchDokumente(promiseValue, this.dokumentePapiergesuch, TSDokumentGrundTyp.PAPIERGESUCH);
                    this.searchDokumente(promiseValue, this.dokumenteFreigabequittung, TSDokumentGrundTyp.FREIGABEQUITTUNG);
                });
        } else {
            this.$log.debug('No gesuch für dokumente');
        }
    }

    private searchDokumente(alleDokumente: TSDokumenteDTO, dokumenteForType: TSDokumentGrund[], dokumentGrundTyp: TSDokumentGrundTyp) {

        let dokumentGruende: Array<TSDokumentGrund> = alleDokumente.dokumentGruende;
        for (let i = 0; i < dokumentGruende.length; i++) {
            let tsDokument: TSDokumentGrund = dokumentGruende[i];
            if (tsDokument.dokumentGrundTyp === dokumentGrundTyp) {
                dokumenteForType.push(tsDokument);
            }
        }
        dokumenteForType.sort((n1: TSDokumentGrund, n2: TSDokumentGrund) => {
            let result: number = 0;

            if (n1 && n2) {
                if (n1.fullName && n2.fullName) {
                    result = n1.fullName.localeCompare(n2.fullName);
                }
                if (result === 0) {
                    if (n1.tag && n2.tag) {
                        result = n1.tag.localeCompare(n2.tag);
                    }
                }
                if (result === 0) {
                    if (n1.dokumentTyp && n2.dokumentTyp) {
                        result = n1.dokumentTyp.toString().localeCompare(n2.dokumentTyp.toString());
                    }
                }
            }
            return result;
        });
    }

    addUploadedDokuments(dokumentGrund: any, dokumente: TSDokumentGrund[]): void {
        this.$log.debug('addUploadedDokuments called');
        let index = EbeguUtil.getIndexOfElementwithID(dokumentGrund, dokumente);

        if (index > -1) {
            this.$log.debug('add dokument to dokumentList');
            dokumente[index] = dokumentGrund;

            // Clear cached Papiergesuch on add...
            if (dokumentGrund.dokumentGrundTyp === TSDokumentGrundTyp.PAPIERGESUCH) {
                this.globalCacheService.getCache(TSCacheTyp.EBEGU_DOCUMENT).removeAll();
            }
        }
        this.ebeguUtil.handleSmarttablesUpdateBug(dokumente);
    }

    removeDokument(dokumentGrund: TSDokumentGrund, dokument: TSDokument, dokumente: TSDokumentGrund[]) {

        let index = EbeguUtil.getIndexOfElementwithID(dokument, dokumentGrund.dokumente);

        if (index > -1) {
            this.$log.debug('add dokument to dokumentList');
            dokumentGrund.dokumente.splice(index, 1);
        }

        this.dokumenteRS.updateDokumentGrund(dokumentGrund).then((response) => {

            let returnedDG: TSDokumentGrund = angular.copy(response);

            if (returnedDG) {
                // replace existing object in table with returned if returned not null
                let index = EbeguUtil.getIndexOfElementwithID(returnedDG, dokumente);
                if (index > -1) {
                    this.$log.debug('update dokumentGrund in dokumentList');
                    dokumente[index] = dokumentGrund;

                    // Clear cached Papiergesuch on remove...
                    if (dokumentGrund.dokumentGrundTyp === TSDokumentGrundTyp.PAPIERGESUCH) {
                        this.globalCacheService.getCache(TSCacheTyp.EBEGU_DOCUMENT).removeAll();
                    }
                }
            } else {
                // delete object in table with sended if returned is null
                let index = EbeguUtil.getIndexOfElementwithID(dokumentGrund, dokumente);
                if (index > -1) {
                    this.$log.debug('remove dokumentGrund in dokumentList');
                    dokumente.splice(index, 1);
                }
            }
            this.wizardStepManager.findStepsFromGesuch(this.gesuchModelManager.getGesuch().id);
        });

        this.ebeguUtil.handleSmarttablesUpdateBug(dokumente);
    }

    public showDokumenteGeprueftButton(): boolean {
        return this.gesuchModelManager.getGesuch().dokumenteHochgeladen;
    }

    public setDokumenteGeprueft(): void {
        this.gesuchModelManager.getGesuch().dokumenteHochgeladen = false;
        this.gesuchModelManager.updateGesuch();
    }

    public isSteueramt(): boolean {
        return this.authServiceRS.isOneOfRoles(TSRoleUtil.getSteueramtOnlyRoles());
    }
}
