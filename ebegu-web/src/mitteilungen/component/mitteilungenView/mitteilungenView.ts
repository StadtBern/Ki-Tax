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


    static $inject: string[] = ['$stateParams', 'MitteilungRS', 'AuthServiceRS', 'FallRS'];
    /* @ngInject */
    constructor(private $stateParams: IMitteilungenStateParams, private mitteilungRS: MitteilungRS, private authServiceRS: AuthServiceRS,
                private fallRS: FallRS) {
        this.initViewModel();
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
        // if (entwurf.exists()) {
        //     this.currentMitteilung = entwurf;
        // } else {
            this.initMitteilungForCurrentBenutzer();
        // }
    }

    private initMitteilungForCurrentBenutzer() {
        let currentUser: TSUser = this.authServiceRS.getPrincipal();

        //common attributes
        this.currentMitteilung = new TSMitteilung();
        this.currentMitteilung.empfaenger = this.fall.verantwortlicher ? this.fall.verantwortlicher : undefined;
        this.currentMitteilung.fall = this.fall;
        this.currentMitteilung.mitteilungStatus = TSMitteilungStatus.NEU;
        this.currentMitteilung.sender = currentUser;

        //role-depending attributes
        if (this.authServiceRS.isRole(TSRole.GESUCHSTELLER)) {
            this.currentMitteilung.empfaengerTyp = TSMitteilungTeilnehmerTyp.JUGENDAMT;
            this.currentMitteilung.senderTyp = TSMitteilungTeilnehmerTyp.GESUCHSTELLER;

        } else if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole())) {
            this.currentMitteilung.empfaengerTyp = TSMitteilungTeilnehmerTyp.GESUCHSTELLER;
            this.currentMitteilung.senderTyp = TSMitteilungTeilnehmerTyp.JUGENDAMT;
        }
    }

    public getCurrentMitteilung(): TSMitteilung {
        return this.currentMitteilung;
    }

    public sendMitteilung(): void {
        this.mitteilungRS.createMitteilung(this.getCurrentMitteilung()).then((response) => {
            this.currentMitteilung = response;
        });
    }

    private loadAllMitteilungen(): void {
        this.mitteilungRS.getMitteilungenForCurrentRolle(this.fall.id).then((response) => {
            this.allMitteilungen = response;
        });
    }

    public getIcon(mitteilung: TSMitteilung): string {
        if (mitteilung) {
            if (!this.isCurrentUserTheSender(mitteilung)) { // is a response
                return 'fa fa-lg fa-share';
            }
            else if (TSMitteilungStatus.NEU === mitteilung.mitteilungStatus) {
                return 'fa fa-lg fa-folder-open';
            } else if (TSMitteilungStatus.ERLEDIGT === mitteilung.mitteilungStatus) {
                return 'fa fa-lg fa-check';
            } else if (TSMitteilungStatus.GELESEN === mitteilung.mitteilungStatus) {
                return 'fa fa-lg fa-eye';
            }
        }
        return 'fa fa-lg fa-folder-open'; // by default neu
    }

    /**
     * Gibt true zurueck wenn der aktuelle Benutzer, der Sender der uebergenenen Mitteilung ist
     */
    private isCurrentUserTheSender(mitteilung: TSMitteilung): boolean {
        return mitteilung && mitteilung.sender && this.authServiceRS.getPrincipal()
            && mitteilung.sender.username === this.authServiceRS.getPrincipal().username;
    }
}

