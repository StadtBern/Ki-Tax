import {IComponentOptions} from 'angular';
import TSUser from '../models/TSUser';
import {TSRole} from '../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
import {TSMandant} from '../models/TSMandant';
import TSInstitution from '../models/TSInstitution';
import {TSTraegerschaft} from '../models/TSTraegerschaft';
let template = require('./dummyAuthentication.html');
require('./dummyAuthentication.less');

export class AuthenticationComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = AuthenticationListViewController;
    controllerAs = 'vm';
}

export class AuthenticationListViewController {

    public usersList: Array<TSUser>;
    private mandant: TSMandant;
    private institution: TSInstitution;
    private traegerschaft: TSTraegerschaft;

    static $inject: string[] = ['$state', 'AuthServiceRS'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS) {
        this.usersList = [];
        this.mandant = this.getMandant();
        this.traegerschaft = this.getTraegerschaft();
        this.institution = this.getInsitution();

        this.usersList.push(new TSUser('Jörg', 'Becker', 'jobe', 'password1', 'joerg.becker@bern.ch', this.mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Jennifer', 'Müller', 'jemu', 'password2', 'jenniver.mueller@bern.ch', this.mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Sophie', 'Bergmann', 'beso', 'password3', 'sophie.bergmann@gugus.ch',
            this.mandant, TSRole.SACHBEARBEITER_INSTITUTION, undefined, this.institution));
        this.usersList.push(new TSUser('Agnes', 'Krause', 'krad', 'password4', 'agnes.krause@gugus.ch',
            this.mandant, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, this.traegerschaft));
        this.usersList.push(new TSUser('Kurt', 'Blaser', 'blku', 'password5', 'kurt.blaser@bern.ch', this.mandant, TSRole.ADMIN));
        this.usersList.push(new TSUser('Emma', 'Gerber', 'geem', 'password6', 'emma.gerber@myemail.ch', this.mandant, TSRole.GESUCHSTELLER));
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
        institution.traegerschaft = this.traegerschaft;
        institution.mandant = this.mandant;
        return institution;
    }

    /**
     * Die Traegerschaft wird direkt gegeben. Diese Daten und die Daten der DB muessen uebereinstimmen
     */
    private getTraegerschaft(): TSTraegerschaft {
        let traegerschaft = new TSTraegerschaft();
        traegerschaft.name = 'Stadt Bern';
        traegerschaft.id = '11111111-1111-1111-1111-111111111113';
        return traegerschaft;
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {
            if (user.getRoleKey() === 'TSRole_SACHBEARBEITER_JA' || user.getRoleKey() === 'TSRole_ADMIN') {
                this.$state.go('pendenzen');
            } else  if (user.getRoleKey() === 'TSRole_SACHBEARBEITER_INSTITUTION' || user.getRoleKey() === 'TSRole_SACHBEARBEITER_TRAEGERSCHAFT') {
                this.$state.go('pendenzenInstitution');
            }
        });
    }
}
