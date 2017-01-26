import IComponentOptions = angular.IComponentOptions;
import TSMitteilung from '../../../models/TSMitteilung';
import MitteilungRS from '../../../core/service/mitteilungRS.rest';
import {TSMitteilungTeilnehmerTyp} from '../../../models/enums/TSMitteilungTeilnehmerTyp';
import {TSMitteilungStatus} from '../../../models/enums/TSMitteilungStatus';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import TSUser from '../../../models/TSUser';
import TSFall from '../../../models/TSFall';
import FallRS from '../../../gesuch/service/fallRS.rest';
import {IMitteilungenStateParams} from '../../mitteilungen.route';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import DateUtil from '../../../utils/DateUtil';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import IFormController = angular.IFormController;
import TSGesuch from '../../../models/TSGesuch';

let template = require('./mitteilungenView.html');
require('./mitteilungenView.less');

export class MitteilungenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = MitteilungenViewController;
    controllerAs = 'vm';
}

export class MitteilungenViewController {

    fall: TSFall;
    currentMitteilung: TSMitteilung;
    allMitteilungen: Array<TSMitteilung>;
    form: IFormController;
    TSRole: any;
    TSRoleUtil: any;


    static $inject: string[] = ['$stateParams', 'MitteilungRS', 'AuthServiceRS', 'FallRS', '$q'];
    /* @ngInject */
    constructor(private $stateParams: IMitteilungenStateParams, private mitteilungRS: MitteilungRS, private authServiceRS: AuthServiceRS,
                private fallRS: FallRS, private $q: IQService) {
        this.initViewModel();
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    private initViewModel(): void {
        if (this.$stateParams.fallId) {
            this.fallRS.findFall(this.$stateParams.fallId).then((response) => {
                this.fall = response;
                this.loadEntwurf();
                this.loadAllMitteilungen();
            });
        }
    }

    /**
     * Diese Methode laedt einen Entwurf wenn es einen existiert. Sonst gibt sie eine leeren
     * Mitteilung zurueck.
     */
    private loadEntwurf() {
        this.mitteilungRS.getEntwurfForCurrentRolle(this.fall.id).then((entwurf: TSMitteilung) => {
            if (entwurf) {
                this.currentMitteilung = entwurf;
            } else {
                this.initMitteilungForCurrentBenutzer();
            }
        });
    }

    private initMitteilungForCurrentBenutzer() {
        let currentUser: TSUser = this.authServiceRS.getPrincipal();

        //common attributes
        this.currentMitteilung = new TSMitteilung();
        this.currentMitteilung.fall = this.fall;
        this.currentMitteilung.mitteilungStatus = TSMitteilungStatus.ENTWURF;
        this.currentMitteilung.sender = currentUser;

        //role-dependent attributes
        if (this.authServiceRS.isRole(TSRole.GESUCHSTELLER)) { // Ein GS darf nur dem JA schreiben
            this.currentMitteilung.empfaenger = this.fall.verantwortlicher ? this.fall.verantwortlicher : undefined;
            this.currentMitteilung.empfaengerTyp = TSMitteilungTeilnehmerTyp.JUGENDAMT;
            this.currentMitteilung.senderTyp = TSMitteilungTeilnehmerTyp.GESUCHSTELLER;

        } else if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole())) { // Das JA darf nur dem GS schreiben
            this.currentMitteilung.empfaenger = this.fall.besitzer ? this.fall.besitzer : undefined;
            this.currentMitteilung.empfaengerTyp = TSMitteilungTeilnehmerTyp.GESUCHSTELLER;
            this.currentMitteilung.senderTyp = TSMitteilungTeilnehmerTyp.JUGENDAMT;

        } else if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionOnlyRoles())) { // Eine Institution darf nur dem JA schreiben
            this.currentMitteilung.empfaenger = this.fall.verantwortlicher ? this.fall.verantwortlicher : undefined;
            this.currentMitteilung.empfaengerTyp = TSMitteilungTeilnehmerTyp.JUGENDAMT;
            this.currentMitteilung.senderTyp = TSMitteilungTeilnehmerTyp.INSTITUTION;

        }
    }

    public getCurrentMitteilung(): TSMitteilung {
        return this.currentMitteilung;
    }

    /**
     * Wechselt den Status der aktuellen Mitteilung auf NEU und schickt diese zum Server
     */
    public sendMitteilung(): void {
        this.currentMitteilung.mitteilungStatus = TSMitteilungStatus.NEU;
        this.currentMitteilung.sentDatum = DateUtil.now();
        this.saveMitteilung(true).catch(() => {
            // All set data have to be set back to be able to save as Entwurf again
            this.currentMitteilung.mitteilungStatus = TSMitteilungStatus.ENTWURF;
            this.currentMitteilung.sentDatum = undefined;
            // forward promise
            let deferred = this.$q.defer();
            deferred.resolve(undefined);
            return deferred.promise;
        });
    }

    public saveEntwurf(): void {
        this.saveMitteilung(false);
    }

    /**
     * Speichert die aktuelle Mitteilung nur wenn das formular dirty ist oder wenn das Parameter forceSave true ist.
     * Wenn das Formular leer ist, wird der Entwurf geloescht (falls er bereits existiert)
     */
    private saveMitteilung(forceSave: boolean): IPromise<TSMitteilung> {
        if (((this.form.$dirty && !this.isMitteilungEmpty())) || forceSave === true) {
            return this.mitteilungRS.createMitteilung(this.getCurrentMitteilung()).then((response) => {
                this.loadEntwurf();
                this.loadAllMitteilungen();
                return this.currentMitteilung;
            }).finally(() => {
                this.form.$setPristine();
                this.form.$setUntouched();
            });

        } else if (this.isMitteilungEmpty() && !this.currentMitteilung.isNew() && this.currentMitteilung.id) {
            return this.mitteilungRS.removeEntwurf(this.getCurrentMitteilung()).then((response) => {
                this.initMitteilungForCurrentBenutzer();
                return this.currentMitteilung;
            });
        } else {
            return this.$q.when(this.currentMitteilung);
        }
    }

    private isMitteilungEmpty() {
        return (!this.currentMitteilung.message || this.currentMitteilung.message.length <= 0)
            && (!this.currentMitteilung.subject || this.currentMitteilung.subject.length <= 0);
    }

    private loadAllMitteilungen(): void {
        this.mitteilungRS.getMitteilungenForCurrentRolle(this.fall.id).then((response) => {
            this.allMitteilungen = response;
        });
    }

    /**
     * Gibt true zurueck wenn der aktuelle BenutzerTyp, der Sender der uebergenenen Mitteilung ist.
     */
    private isCurrentUserTypTheSenderTyp(mitteilung: TSMitteilung): boolean {
        return mitteilung && mitteilung.sender && this.authServiceRS.getPrincipal()
            && mitteilung.senderTyp === this.getMitteilungTeilnehmerTypForUserRole(this.authServiceRS.getPrincipal().role);
    }

    public isSenderTypInstitution(mitteilung: TSMitteilung): boolean {
        return mitteilung && mitteilung.sender && mitteilung.senderTyp === TSMitteilungTeilnehmerTyp.INSTITUTION;
    }

    public isSenderTypJugendamt(mitteilung: TSMitteilung): boolean {
        return mitteilung && mitteilung.sender && mitteilung.senderTyp === TSMitteilungTeilnehmerTyp.JUGENDAMT;
    }

    public isSenderTypGesuchsteller(mitteilung: TSMitteilung): boolean {
        return mitteilung && mitteilung.sender && mitteilung.senderTyp === TSMitteilungTeilnehmerTyp.GESUCHSTELLER;
    }

    private getMitteilungTeilnehmerTypForUserRole(role: TSRole): TSMitteilungTeilnehmerTyp {
        switch (role) {
            case TSRole.GESUCHSTELLER: {
                return TSMitteilungTeilnehmerTyp.GESUCHSTELLER;
            }
            case TSRole.SACHBEARBEITER_INSTITUTION:
            case TSRole.SACHBEARBEITER_TRAEGERSCHAFT: {
                return TSMitteilungTeilnehmerTyp.INSTITUTION;
            }
            case TSRole.SUPER_ADMIN:
            case TSRole.ADMIN:
            case TSRole.SACHBEARBEITER_JA: {
                return TSMitteilungTeilnehmerTyp.JUGENDAMT;
            }
            default:
                return null;
        }
    }

}
