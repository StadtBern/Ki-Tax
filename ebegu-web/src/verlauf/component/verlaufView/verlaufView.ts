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

import IComponentOptions = angular.IComponentOptions;
import IFormController = angular.IFormController;
import IStateService = angular.ui.IStateService;
import TSFall from '../../../models/TSFall';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSGesuch from '../../../models/TSGesuch';
import {IVerlaufStateParams} from '../../verlauf.route';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import AntragStatusHistoryRS from '../../../core/service/antragStatusHistoryRS.rest';
import TSAntragStatusHistory from '../../../models/TSAntragStatusHistory';
import EbeguUtil from '../../../utils/EbeguUtil';

let template = require('./verlaufView.html');
require('./verlaufView.less');

export class VerlaufViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerlaufViewController;
    controllerAs = 'vm';
}

export class VerlaufViewController {

    form: IFormController;
    fall: TSFall;
    gesuch: TSGesuch;
    gesuche: {[gesuchId: string]: string} = {};
    itemsByPage: number = 20;
    TSRoleUtil = TSRoleUtil;
    verlauf: Array<TSAntragStatusHistory>;

    static $inject: string[] = ['$state', '$stateParams', 'AuthServiceRS', 'GesuchRS', 'AntragStatusHistoryRS', 'EbeguUtil'];
    /* @ngInject */
    constructor(private $state: IStateService, private $stateParams: IVerlaufStateParams,
                private authServiceRS: AuthServiceRS, private gesuchRS: GesuchRS,
                private antragStatusHistoryRS: AntragStatusHistoryRS, private ebeguUtil: EbeguUtil) {
    }

    $onInit() {
        if (this.$stateParams.gesuchId) {
            this.gesuchRS.findGesuch(this.$stateParams.gesuchId).then((response) => {
                this.gesuch = response;
                if (this.gesuch === undefined) {
                    this.cancel();
                }
                this.fall = this.gesuch.fall;
                this.antragStatusHistoryRS.loadAllAntragStatusHistoryByGesuchsperiode(this.gesuch.fall, this.gesuch.gesuchsperiode).then((response) => {
                    this.verlauf = response;
                });
                this.gesuchRS.getAllAntragDTOForFall(this.fall.id).then((response) => {
                    response.forEach((item) => {
                        this.gesuche[item.antragId] = this.ebeguUtil.getAntragTextDateAsString(item.antragTyp, item.eingangsdatum, item.laufnummer);
                    });
                });
            });
        } else {
            this.cancel();
        }
    }

    public getVerlaufList(): Array<TSAntragStatusHistory> {
        return this.verlauf;
    }

    public cancel(): void {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            this.$state.go('gesuchstellerDashboard');
        } else {
            this.$state.go('pendenzen');
        }
    }

    public getGesuch(gesuchid: string): TSGesuch {
        this.gesuchRS.findGesuch(gesuchid).then((response) => {
            return response;
        });
        return undefined;
    }
}
