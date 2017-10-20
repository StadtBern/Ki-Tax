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

    ferieninselStammdatenList: TSFerieninselStammdaten[] = [];
    ferieninselStammdatenMap: { [key: string]: TSFerieninselStammdaten; } = {};


    /* @ngInject */
    constructor(private gesuchsperiodeRS: GesuchsperiodeRS,
                private ferieninselStammdatenRS: FerieninselStammdatenRS,
                private $timeout: ITimeoutService, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
        $timeout(() => {
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
            this.ferieninselStammdatenList = response;
            for (let obj of this.ferieninselStammdatenList) {
                this.ferieninselStammdatenMap[obj.ferienname] = obj;
            }
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
            this.ferieninselStammdatenRS.saveFerieninselStammdaten(ferieninselStammdaten);
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
        let fiValid: boolean = !(FerieninselViewController.isNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            || FerieninselViewController.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigAb)
            || FerieninselViewController.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigBis));

        return fiValid;
    }

    public isSaveButtonDisabled(ferieninselStammdaten: TSFerieninselStammdaten): boolean {
        // Disabled, solange noch keines der Felder ausgefuellt ist
        return FerieninselViewController.isNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            && FerieninselViewController.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigAb)
            && FerieninselViewController.isNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigBis);
    }

    public isAnmeldeschlussRequired(ferieninselStammdaten: TSFerieninselStammdaten): boolean {
        // Wenn mindestens ein Zeitraum erfasst ist
        return FerieninselViewController.notNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigAb)
            || FerieninselViewController.notNullOrUndefined(ferieninselStammdaten.zeitraum.gueltigkeit.gueltigBis);
    }

    public isDatumAbRequired(ferieninselZeitraum: TSFerieninselZeitraum, ferieninselStammdaten: TSFerieninselStammdaten) {
        // Wenn entweder der Anmeldeschluss erfasst ist, oder das Datum bis
        return FerieninselViewController.notNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            || (FerieninselViewController.notNullOrUndefined(ferieninselZeitraum.gueltigkeit)
                && FerieninselViewController.notNullOrUndefined(ferieninselZeitraum.gueltigkeit.gueltigBis));
    }

    public isDatumBisRequired(ferieninselZeitraum: TSFerieninselZeitraum, ferieninselStammdaten: TSFerieninselStammdaten) {
        // Wenn entweder der Anmeldeschluss erfasst ist, oder das Datum ab
       return FerieninselViewController.notNullOrUndefined(ferieninselStammdaten.anmeldeschluss)
            || (FerieninselViewController.notNullOrUndefined(ferieninselZeitraum.gueltigkeit)
               && FerieninselViewController.notNullOrUndefined(ferieninselZeitraum.gueltigkeit.gueltigAb));
    }

    private static isNullOrUndefined(data: any): boolean {
        return data === null || data === undefined;
    }

    private static notNullOrUndefined(data: any): boolean {
        return !FerieninselViewController.isNullOrUndefined(data);
    }
}
