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
import {ApplicationPropertyRS} from '../admin/service/applicationPropertyRS.rest';
import ITimeoutService = angular.ITimeoutService;

let template = require('./dummyAuthentication.html');
require('./dummyAuthentication.less');

export class DummyAuthenticationComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DummyAuthenticationListViewController;
    controllerAs = 'vm';
}

export class DummyAuthenticationListViewController {

    public usersList: Array<TSUser>;
    public superadmin: TSUser;
    private mandant: TSMandant;
    private institution: TSInstitution;
    private traegerschaftStadtBern: TSTraegerschaft;
    private traegerschaftLeoLea: TSTraegerschaft;
    private traegerschaftSGF: TSTraegerschaft;
    private traegerschaftFamex: TSTraegerschaft;
    private devMode: boolean;

    static $inject: string[] = ['$state', 'AuthServiceRS', '$timeout', 'ApplicationPropertyRS'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS,
                private $timeout: ITimeoutService, private applicationPropertyRS: ApplicationPropertyRS) {
        this.usersList = [];
        this.mandant = this.getMandant();
        this.traegerschaftStadtBern = this.getTraegerschaftStadtBern();
        this.traegerschaftLeoLea = this.getTraegerschaftLeoLea();
        this.traegerschaftSGF = this.getTraegerschaftSGF();
        this.traegerschaftFamex = this.getTraegerschaftFamex();
        this.institution = this.getInsitution();
        applicationPropertyRS.isDevMode().then((response) => {
            this.devMode = response;
        });
        this.usersList.push(new TSUser('Kurt', 'Blaser', 'blku', 'password5', 'markus.hofstetter@dvbern.ch',
            this.mandant, TSRole.ADMIN));
        this.usersList.push(new TSUser('Jörg', 'Becker', 'jobe', 'password1', 'joerg.becker@example.com',
            this.mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Jennifer', 'Müller', 'jemu', 'password2', 'franziska.herger@dvbern.ch',
            this.mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Sophie', 'Bergmann', 'beso', 'password3', 'sophie.bergmann@example.com',
            this.mandant, TSRole.SACHBEARBEITER_INSTITUTION, undefined, this.institution));
        this.usersList.push(new TSUser('Agnes', 'Krause', 'krad', 'password4', 'agnes.krause@example.com',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftStadtBern));
        this.usersList.push(new TSUser('Lea', 'Lehmann', 'lele', 'password7', 'lea.lehmann@gexample.com',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftLeoLea));
        this.usersList.push(new TSUser('Simon', 'Gfeller', 'gfsi', 'password8', 'simon.gfeller@example.com',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftSGF));
        this.usersList.push(new TSUser('Emma', 'Gerber', 'geem', 'password6', 'emma.gerber@example.com',
            this.mandant, TSRole.GESUCHSTELLER));
        this.usersList.push(new TSUser('Heinrich', 'Mueller', 'muhe', 'password6', 'heinrich.mueller@example.com',
            this.mandant, TSRole.GESUCHSTELLER));
        this.usersList.push(new TSUser('Michael', 'Berger', 'bemi', 'password6', 'michael.berger@example.com',
            this.mandant, TSRole.GESUCHSTELLER));
        this.usersList.push(new TSUser('Hans', 'Zimmermann', 'ziha', 'password6', 'hans.zimmermann@example.com',
            this.mandant, TSRole.GESUCHSTELLER));
        this.usersList.push(new TSUser('Rodolfo', 'Geldmacher', 'gero', 'password11', 'rodolfo.geldmacher@example.com',
            this.mandant, TSRole.STEUERAMT));
        this.usersList.push(new TSUser('Julien', 'Schuler', 'scju', 'password9', 'julien.schuler@example.com',
            this.mandant, TSRole.SCHULAMT));
        this.usersList.push(new TSUser('Adrian', 'Schuler', 'scad', 'password9', 'adrian.schuler@example.com',
            this.mandant, TSRole.ADMINISTRATOR_SCHULAMT));
        this.usersList.push(new TSUser('Julia', 'Jurist', 'juju', 'password9', 'julia.jurist@example.com',
            this.mandant, TSRole.JURIST));
        this.usersList.push(new TSUser('Reto', 'Revisor', 'rere', 'password9', 'reto.revisor@example.com',
            this.mandant, TSRole.REVISOR));
        this.superadmin = new TSUser('E-BEGU', 'Superuser', 'ebegu', 'password10', 'hallo@dvbern.ch',
            this.mandant, TSRole.SUPER_ADMIN);
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
     * Die Institution wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getInsitution(): TSInstitution {
        let institution = new TSInstitution();
        institution.name = 'Kita Brünnen';
        institution.id = '1b6f476f-e0f5-4380-9ef6-836d688853a3';
        institution.mail = 'kita.bruennen@bern.ch';
        institution.traegerschaft = this.traegerschaftStadtBern;
        institution.mandant = this.mandant;
        return institution;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftStadtBern(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'Kitas & Tagis Stadt Bern';
        traegerschaft.mail = 'kitasundtagis@bern.ch';
        traegerschaft.id = 'f9ddee82-81a1-4cda-b273-fb24e9299308';
        return traegerschaft;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftLeoLea(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'LeoLea';
        traegerschaft.mail = 'info@leolea.ch';
        traegerschaft.id = 'd667e2d0-3702-4933-8fb7-be7a39755232';
        return traegerschaft;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftSGF(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'SGF';
        traegerschaft.mail = 'info@sgfbern.ch';
        traegerschaft.id = 'bb5d4bd8-84c9-4cb6-8134-a97312dead67';
        return traegerschaft;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftFamex(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'FAMEX';
        traegerschaft.mail = 'info@famex';
        traegerschaft.id = '4a552145-5ccd-4bf8-b827-c77c930daaa8';
        return traegerschaft;
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {
            AuthenticationUtil.navigateToStartPageForRole(user, this.$state);

        });
    }
}
