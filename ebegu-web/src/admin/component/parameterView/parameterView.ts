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

import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import {IComponentOptions, IFormController, ILogService} from 'angular';
import './parameterView.less';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import AbstractAdminViewController from '../../abstractAdminView';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import ITranslateService = angular.translate.ITranslateService;
import ITimeoutService = angular.ITimeoutService;
import IStateService = angular.ui.IStateService;

let template = require('./parameterView.html');
let style = require('./parameterView.less');

export class ParameterViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = ParameterViewController;
    controllerAs: string = 'vm';
}

export class ParameterViewController extends AbstractAdminViewController {
    static $inject = ['EbeguParameterRS', 'GesuchsperiodeRS', '$translate',
        '$log', '$state', '$timeout', 'AuthServiceRS'];

    form: IFormController;

    ebeguParameterRS: EbeguParameterRS;

    gesuchsperiodenList: Array<TSGesuchsperiode> = [];

    jahr: number;
    ebeguJahresabhParameter: TSEbeguParameter[] = []; // enthält alle Jahresabhängigen Params für alle Jahre

    ebeguParameterListJahr: TSEbeguParameter[]; // enthält alle Params für nur 1 Jahr


    /* @ngInject */
    constructor(ebeguParameterRS: EbeguParameterRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private $translate: ITranslateService, private $log: ILogService, private $state: IStateService,
                private $timeout: ITimeoutService, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
        this.ebeguParameterRS = ebeguParameterRS;
        $timeout(() => {
            this.readGesuchsperioden();
            this.updateJahresabhParamList();
        });
    }

    private readGesuchsperioden(): void {
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: Array<TSGesuchsperiode>) => {
            this.gesuchsperiodenList =  response; //angular.copy(response);
        });
    }

    private readEbeguParameterByJahr(): void {
        this.ebeguParameterRS.getEbeguParameterByJahr(this.jahr).then((response: TSEbeguParameter[]) => {
            this.ebeguParameterListJahr = response;
        });
    }

    gesuchsperiodeClicked(gesuchsperiode: any) {
        if (gesuchsperiode.isSelected) {
            this.$state.go('gesuchsperiode', {
                gesuchsperiodeId: gesuchsperiode.id
            });
        }
    }

    jahresabhParamSelected(parameter: TSEbeguParameter) {
        this.jahr = parameter.gueltigkeit.gueltigAb.get('year');
        this.jahrChanged();
    }

    createGesuchsperiode(): void {
        this.$state.go('gesuchsperiode', {
            gesuchsperiodeId: undefined
        });
    }

    getStatusTagesschulenFreischaltung(gp: TSGesuchsperiode): string {
        if (gp.hasTagesschulenAnmeldung()) {
            if (gp.isTagesschulenAnmeldungKonfiguriert()) {
                return this.$translate.instant('FREISCHALTUNG_TAGESSCHULE_KONFIGURIERT');
            } else {
                return this.$translate.instant('FREISCHALTUNG_TAGESSCHULE_NOT_YET');
            }
        } else {
            return this.$translate.instant('FREISCHALTUNG_TAGESSCHULE_NONE');
        }
    }

    cancelJahresabhaengig(): void {
        this.jahr = undefined;
    }

    jahrChanged(): void {
        this.readEbeguParameterByJahr();
    }

    public saveParameterByJahr(): void {
        if (this.ebeguParameterListJahr.length === 1) {
            let param = this.ebeguParameterListJahr[0];
            this.ebeguParameterRS.saveEbeguParameter(param).then((response) => {
                this.updateJahresabhParamList();
            });
        } else {
            this.$log.error('Aktuell kann diese oberflaeche nur einene einzelnen Jahresabg. Param speichern.');
        }
    }

    private updateJahresabhParamList() {
        this.ebeguParameterRS.getJahresabhParameter().then((response: Array<TSEbeguParameter>) => {
            this.ebeguJahresabhParameter = response;
        });
    }

    public jahresParamsEditable(): boolean {
        // Wenn die Periode, die in dem Jahr *endet* noch ENTWURF ist
        for (let gp of this.gesuchsperiodenList) {
            if (gp.gueltigkeit.gueltigBis.year() === this.jahr) {
                return this.periodenParamsEditableForPeriode(gp);
            }
        }
        return true;
    }
}
