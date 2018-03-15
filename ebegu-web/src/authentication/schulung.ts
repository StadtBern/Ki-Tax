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
import TSUser from '../models/TSUser';
import {TSRole} from '../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
import {TSMandant} from '../models/TSMandant';
import TSInstitution from '../models/TSInstitution';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
import AuthenticationUtil from '../utils/AuthenticationUtil';
import {TestFaelleRS} from '../admin/service/testFaelleRS.rest';
import ITimeoutService = angular.ITimeoutService;

let template = require('./schulung.html');
require('./schulung.less');

export class SchulungComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = SchulungViewController;
    controllerAs = 'vm';
}

export class SchulungViewController {

    public usersList: Array<TSUser> = Array<TSUser>();
    private gesuchstellerList: string[];
    private institutionsuserList: Array<TSUser> = Array<TSUser>();
    private mandant: TSMandant;
    private institutionForelle: TSInstitution;
    private traegerschaftFisch: TSTraegerschaft;

    static $inject: string[] = ['$state', 'AuthServiceRS', '$timeout', 'TestFaelleRS'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS,
                private $timeout: ITimeoutService,
                private testFaelleRS: TestFaelleRS) {

        this.mandant = this.getMandant();
        this.traegerschaftFisch = this.getTraegerschaftFisch();
        this.institutionForelle = this.getInstitutionForelle();
        this.testFaelleRS.getSchulungBenutzer().then((response: any) => {
            this.gesuchstellerList = response;
            for (let i = 0; i < this.gesuchstellerList.length; i++) {
                let name = this.gesuchstellerList[i];
                let username = 'sch' + (((i + 1) < 10) ? '0' + (i + 1).toString() : (i + 1).toString());
                this.usersList.push(new TSUser('Sandra', name, username, 'password1', 'sandra.' + name.toLocaleLowerCase() + '@example.com', this.mandant, TSRole.GESUCHSTELLER));
            }

            this.institutionsuserList.push(new TSUser('Fritz', 'Fisch', 'sch20', 'password1', 'fritz.fisch@example.com',
                this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftFisch, undefined));
            this.institutionsuserList.push(new TSUser('Franz', 'Forelle', 'sch21', 'password1', 'franz.forelle@example.com',
                this.mandant, TSRole.SACHBEARBEITER_INSTITUTION, undefined, this.institutionForelle));
        });
    }

    /**
     * Der Mandant wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     * @returns {TSMandant}
     */
    private getMandant(): TSMandant {
        let mandant = new TSMandant();
        mandant.name = 'TestMandant';
        mandant.id = 'e3736eb8-6eef-40ef-9e52-96ab48d8f220';
        return mandant;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftFisch(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'Fisch';
        traegerschaft.mail = 'fisch@example.com';
        traegerschaft.id = '11111111-1111-1111-1111-111111111111';
        return traegerschaft;
    }

    /**
     * Die Institution wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getInstitutionForelle(): TSInstitution {
        let institution = new TSInstitution();
        institution.name = 'Forelle';
        institution.id = '22222222-1111-1111-1111-111111111111';
        institution.mail = 'forelle@example.com';
        institution.traegerschaft = this.traegerschaftFisch;
        institution.mandant = this.mandant;
        return institution;
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {
            AuthenticationUtil.navigateToStartPageForRole(user, this.$state);
        });
    }
}
