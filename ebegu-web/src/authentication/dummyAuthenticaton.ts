import {IComponentOptions} from 'angular';
import TSUser from '../models/TSUser';
import {TSRole} from '../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
import {TSMandant} from '../models/TSMandant';
import TSInstitution from '../models/TSInstitution';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
import {TSAuthEvent} from '../models/enums/TSAuthEvent';
import AuthenticationUtil from '../utils/AuthenticationUtil';
import IRootScopeService = angular.IRootScopeService;
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
    private mandant: TSMandant;
    private institution: TSInstitution;
    private traegerschaftStadtBern: TSTraegerschaft;
    private traegerschaftLeoLea: TSTraegerschaft;
    private traegerschaftSGF: TSTraegerschaft;

    static $inject: string[] = ['$state', 'AuthServiceRS', '$rootScope', '$timeout'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS,
                private $rootScope: IRootScopeService, private $timeout: ITimeoutService) {
        this.usersList = [];
        this.mandant = this.getMandant();
        this.traegerschaftStadtBern = this.getTraegerschaftStadtBern();
        this.traegerschaftLeoLea = this.getTraegerschaftLeoLea();
        this.traegerschaftSGF = this.getTraegerschaftSGF();
        this.institution = this.getInsitution();

        this.usersList.push(new TSUser('Jörg', 'Becker', 'jobe', 'password1', 'joerg.becker@bern.ch', this.mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Jennifer', 'Müller', 'jemu', 'password2', 'jenniver.mueller@bern.ch', this.mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Sophie', 'Bergmann', 'beso', 'password3', 'sophie.bergmann@gugus.ch',
            this.mandant, TSRole.SACHBEARBEITER_INSTITUTION, undefined, this.institution));
        this.usersList.push(new TSUser('Agnes', 'Krause', 'krad', 'password4', 'agnes.krause@gugus.ch',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftStadtBern));
        this.usersList.push(new TSUser('Lea', 'Lehmann', 'lele', 'password7', 'lea.lehmann@gugus.ch',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftLeoLea));
        this.usersList.push(new TSUser('Simon', 'Gfeller', 'gfsi', 'password8', 'simon.gfeller@gugus.ch',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaftSGF));
        this.usersList.push(new TSUser('Kurt', 'Blaser', 'blku', 'password5', 'kurt.blaser@bern.ch', this.mandant, TSRole.ADMIN));
        this.usersList.push(new TSUser('Emma', 'Gerber', 'geem', 'password6', 'emma.gerber@myemail.ch', this.mandant, TSRole.GESUCHSTELLER));

        this.usersList.push(new TSUser('Julien', 'Schuler', 'scju', 'password9', 'julien.schuler@myemail.ch', this.mandant, TSRole.SCHULAMT));
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
        institution.id = '11111111-1111-1111-1111-111111111107';
        institution.traegerschaft = this.traegerschaftStadtBern;
        institution.mandant = this.mandant;
        return institution;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftStadtBern(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'Stadt Bern';
        traegerschaft.id = '11111111-1111-1111-1111-111111111113';
        return traegerschaft;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftLeoLea(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'LeoLea';
        traegerschaft.id = '11111111-1111-1111-1111-111111111114';
        return traegerschaft;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaftSGF(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'Verein SGF';
        traegerschaft.id = '11111111-1111-1111-1111-111111111117';
        return traegerschaft;
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {

            AuthenticationUtil.navigateToStartPageForRole(user, this.$state);

            this.$timeout(()  => {
                this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.CHANGE_USER]);
            }, 1000);

        });
    }
}
