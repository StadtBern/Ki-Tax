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
import IRootScopeService = angular.IRootScopeService;
import ITimeoutService = angular.ITimeoutService;
import {TSAuthEvent} from '../models/enums/TSAuthEvent';
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

    static $inject: string[] = ['$state', 'AuthServiceRS', '$rootScope', '$timeout', 'TestFaelleRS'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS,
                private $rootScope: IRootScopeService, private $timeout: ITimeoutService,
                private testFaelleRS: TestFaelleRS) {

        this.mandant = this.getMandant();
        this.traegerschaftFisch = this.getTraegerschaftFisch();
        this.institutionForelle = this.getInstitutionForelle();
        this.testFaelleRS.getSchulungBenutzer().then((response: any) => {
            this.gesuchstellerList = response;
            for (let i = 0; i < this.gesuchstellerList.length; i++) {
                let name = this.gesuchstellerList[i];
                let username = 'sch' + (((i + 1) < 10) ? '0' + (i + 1).toString() : (i + 1).toString());
                this.usersList.push(new TSUser('Sandra', name, username, 'password1', 'sandra.' + name.toLocaleLowerCase() + '@mailinator.com', this.mandant, TSRole.GESUCHSTELLER));
            }

            this.institutionsuserList.push(new TSUser('Fritz', 'Fisch', 'sch20', 'password1', 'fritz.fisch@mailinator.com',
                this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftFisch, undefined));
            this.institutionsuserList.push(new TSUser('Franz', 'Forelle', 'sch21', 'password1', 'franz.forelle@mailinator.com',
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
        traegerschaft.mail = 'fisch@mailinator.com';
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
        institution.mail = 'forelle@mailinator.com';
        institution.traegerschaft = this.traegerschaftFisch;
        institution.mandant = this.mandant;
        return institution;
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {
            AuthenticationUtil.navigateToStartPageForRole(user, this.$state);
            this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], 'logged in');
        });
    }
}
