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

import {IComponentOptions, IFormController} from 'angular';
import './ferieninselView.less';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSDateRange} from '../../../models/types/TSDateRange';
import AbstractAdminViewController from '../../abstractAdminView';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {FerieninselStammdatenRS} from '../../service/ferieninselStammdatenRS.rest';
import TSFerieninselStammdaten from '../../../models/TSFerieninselStammdaten';
import {getTSFeriennameValues, TSFerienname} from '../../../models/enums/TSFerienname';
import TSFerieninselZeitraum from '../../../models/TSFerieninselZeitraum';
import ITimeoutService = angular.ITimeoutService;
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';

let template = require('./ferieninselView.html');
let style = require('./ferieninselView.less');

export class FerieninselViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = FerieninselViewController;
    controllerAs: string = 'vm';
}

export class FerieninselViewController extends AbstractAdminViewController {
    static $inject = ['GesuchsperiodeRS', 'FerieninselStammdatenRS', '$timeout', 'AuthServiceRS'];

    form: IFormController;

    gesuchsperiodenList: Array<TSGesuchsperiode> = [];
    gesuchsperiode: TSGesuchsperiode;

    ferieninselStammdatenMap: { [key: string]: TSFerieninselStammdaten; } = {};

    TSRoleUtil: TSRoleUtil;


    /* @ngInject */
    constructor(private gesuchsperiodeRS: GesuchsperiodeRS,
                private ferieninselStammdatenRS: FerieninselStammdatenRS,
                private $timeout: ITimeoutService, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
        this.TSRoleUtil = TSRoleUtil;
        this.$timeout(() => {
            this.readGesuchsperioden();
        });
    }

    private readGesuchsperioden(): void {
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: Array<TSGesuchsperiode>) => {
            this.gesuchsperiodenList =  response;
        });
    }

    public gesuchsperiodeClicked(gesuchsperiode: any) {
        if (gesuchsperiode.isSelected) {
            this.gesuchsperiode = gesuchsperiode;
            this.readFerieninselStammdatenByGesuchsperiode();
        } else {
            this.gesuchsperiode = undefined;
        }
    }

    private readFerieninselStammdatenByGesuchsperiode(): void {
        this.ferieninselStammdatenMap = {};
        this.ferieninselStammdatenRS.findFerieninselStammdatenByGesuchsperiode(this.gesuchsperiode.id).then((response: TSFerieninselStammdaten[]) => {
            let ferieninselStammdatenList: TSFerieninselStammdaten[] =  response;
            for (let obj of ferieninselStammdatenList) {
                this.ferieninselStammdatenMap[obj.ferienname] = obj;
            }
            this.resetErrors();
        });
    }

    public getFeriennamen(): TSFerienname[] {
        return getTSFeriennameValues();
    }

    public getFerieninselStammdaten(ferienname: TSFerienname): TSFerieninselStammdaten {
        let stammdaten: TSFerieninselStammdaten = this.ferieninselStammdatenMap[ferienname];
        if (!stammdaten) {
            stammdaten = new TSFerieninselStammdaten();
            stammdaten.ferienname = ferienname;
            stammdaten.gesuchsperiode = this.gesuchsperiode;
            stammdaten.zeitraum = new TSFerieninselZeitraum();
            stammdaten.zeitraum.gueltigkeit = new TSDateRange();
            this.ferieninselStammdatenMap[ferienname] = stammdaten;
        }
        return stammdaten;
    }

    public saveFerieninselStammdaten(ferieninselStammdaten: TSFerieninselStammdaten): void {
        if (this.form.$valid && this.isFerieninselStammdatenValid(ferieninselStammdaten)) {
            this.ferieninselStammdatenRS.saveFerieninselStammdaten(ferieninselStammdaten).then((response: TSFerieninselStammdaten) => {
                this.ferieninselStammdatenMap[response.ferienname] = response;
            });
        }
    }

    public addFerieninselZeitraum(ferieninselStammdaten: TSFerieninselStammdaten): void {
        if (!ferieninselStammdaten.zeitraumList) {
            ferieninselStammdaten.zeitraumList = [];
        }
        ferieninselStammdaten.zeitraumList.push(new TSFerieninselZeitraum());
    }

    public removeFerieninselZeitraum(ferieninselStammdaten: TSFerieninselStammdaten, ferieninselZeitraum: TSFerieninselZeitraum): void {
        let index: number = ferieninselStammdaten.zeitraumList.indexOf(ferieninselZeitraum, 0);
        ferieninselStammdaten.zeitraumList.splice(index, 1);
    }

    public isFerieninselStammdatenValid(ferieninselStammdaten: TSFerieninselStammdaten): boolean {
        let fiValid: boolean = !(EbeguUtil.isNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            || EbeguUtil.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigAb)
            || EbeguUtil.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigBis));

        return fiValid;
    }

    public isSaveButtonDisabled(ferieninselStammdaten: TSFerieninselStammdaten): boolean {
        // Disabled, solange noch keines der Felder ausgefuellt ist
        return EbeguUtil.isNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            && EbeguUtil.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigAb)
            && EbeguUtil.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigBis);
    }

    public isAnmeldeschlussRequired(ferieninselStammdaten: TSFerieninselStammdaten): boolean {
        // Wenn mindestens ein Zeitraum erfasst ist
        return EbeguUtil.isNotNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigAb)
            || EbeguUtil.isNotNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigBis);
    }

    public isDatumAbRequired(ferieninselZeitraum: TSFerieninselZeitraum, ferieninselStammdaten: TSFerieninselStammdaten) {
        // Wenn entweder der Anmeldeschluss erfasst ist, oder das Datum bis
        return EbeguUtil.isNotNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            || (EbeguUtil.isNotNullOrUndefined(ferieninselZeitraum.gueltigkeit)
                && EbeguUtil.isNotNullOrUndefined(ferieninselZeitraum.gueltigkeit.gueltigBis));
    }

    public isDatumBisRequired(ferieninselZeitraum: TSFerieninselZeitraum, ferieninselStammdaten: TSFerieninselStammdaten) {
        // Wenn entweder der Anmeldeschluss erfasst ist, oder das Datum ab
       return EbeguUtil.isNotNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            || (EbeguUtil.isNotNullOrUndefined(ferieninselZeitraum.gueltigkeit)
               && EbeguUtil.isNotNullOrUndefined(ferieninselZeitraum.gueltigkeit.gueltigAb));
    }

    public isReadonly(): boolean {
        return !this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorRoles());
    }

    /**
     * Alle errors werden zurueckgesetzt. Dies ist notwendig, weil beim Wechseln zwischen Gesuchsperiode, das Form nicht neugemacht wird.
     * Deswegen werden alle alten Daten bzw. Errors beibehalten und deshalb falsche Failures gegeben. Ausserdem wird das Form als Pristine gesetzt
     * damit keine Reste aus den alten Daten uebernommen werden.
     */
    private resetErrors() {
        this.form.$setPristine();
        this.form.$setUntouched();

        // iterate over all from properties
        angular.forEach(this.form, (ctrl, name) => {
            // ignore angular fields and functions
            if (name.indexOf('$') !== 0) {
                // iterate over all $errors for each field
                angular.forEach(ctrl.$error, (value, name) => {
                    // reset validity
                    ctrl.$setValidity(name, null);
                });
            }
        });
    }
}
