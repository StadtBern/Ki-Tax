import {IComponentOptions, IPromise} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSMitteilung from '../../../models/TSMitteilung';
import {TSMitteilungStatus} from '../../../models/enums/TSMitteilungStatus';
import {TSMitteilungTeilnehmerTyp} from '../../../models/enums/TSMitteilungTeilnehmerTyp';
import {TSRole} from '../../../models/enums/TSRole';
import TSFall from '../../../models/TSFall';
import TSBetreuung from '../../../models/TSBetreuung';
import {IMitteilungenStateParams} from '../../../mitteilungen/mitteilungen.route';
import MitteilungRS from '../../service/mitteilungRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import BetreuungRS from '../../service/betreuungRS.rest';
import FallRS from '../../../gesuch/service/fallRS.rest';
import TSUser from '../../../models/TSUser';
import Moment = moment.Moment;
import IFormController = angular.IFormController;
import IQService = angular.IQService;
import IWindowService = angular.IWindowService;
import IRootScopeService = angular.IRootScopeService;
import {IStateService} from 'angular-ui-router';
import EbeguUtil from '../../../utils/EbeguUtil';
let template = require('./dv-mitteilung-list.html');
require('./dv-mitteilung-list.less');

export class DVMitteilungListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        fall: '<',
        betreuung: '<',
        form: '<',
    };

    template = template;
    controller = DVMitteilungListController;
    controllerAs = 'vm';
}

export class DVMitteilungListController {

    fall: TSFall;
    betreuung: TSBetreuung;
    form: IFormController;

    currentMitteilung: TSMitteilung;
    allMitteilungen: Array<TSMitteilung>;
    TSRole: any;
    TSRoleUtil: any;
    ebeguUtil: EbeguUtil;


    static $inject: any[] = ['$stateParams', 'MitteilungRS', 'AuthServiceRS',
        'FallRS', 'BetreuungRS', '$q', '$window', '$rootScope', '$state', 'EbeguUtil'];
    /* @ngInject */
    constructor(private $stateParams: IMitteilungenStateParams, private mitteilungRS: MitteilungRS, private authServiceRS: AuthServiceRS,
                private fallRS: FallRS, private betreuungRS: BetreuungRS, private $q: IQService, private $window: IWindowService, private $rootScope: IRootScopeService,
                private $state: IStateService, ebeguUtil: EbeguUtil) {

        this.initViewModel();
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
        this.ebeguUtil = ebeguUtil;
    }

    private initViewModel() {
        if (this.$stateParams.fallId) {
            this.fallRS.findFall(this.$stateParams.fallId).then((response) => {
                this.fall = response;
                if (this.$stateParams.betreuungId) {
                    this.betreuungRS.findBetreuung(this.$stateParams.betreuungId).then((response) => {
                        this.betreuung = response;
                        this.loadEntwurf();
                        this.loadAllMitteilungen();
                    });
                } else {
                    this.loadEntwurf();
                    this.setAllMitteilungenGelesen().then((response) => {
                        this.loadAllMitteilungen();
                        if (this.$rootScope) {
                            this.$rootScope.$emit('POSTEINGANG_MAY_CHANGED', null);
                        }
                    });
                }
            });
        }
    }

    public cancel(): void {
        this.form.$setPristine();
        this.$window.history.back();
    }

    /**
     * Diese Methode laedt einen Entwurf wenn es einen existiert. Sonst gibt sie eine leeren
     * Mitteilung zurueck.
     */
    private loadEntwurf() {
        // Wenn der Fall keinen Besitzer hat, darf auch keine Nachricht geschrieben werden
        // Ausser wir sind Institutionsbenutzer
        let isInstitutionsUser: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionOnlyRoles());
        if (this.fall.besitzer || isInstitutionsUser) {
            if (this.betreuung) {
                this.mitteilungRS.getEntwurfForCurrentRolleForBetreuung(this.betreuung.id).then((entwurf: TSMitteilung) => {
                    if (entwurf) {
                        this.currentMitteilung = entwurf;
                    } else {
                        this.initMitteilungForCurrentBenutzer();
                    }
                });
            } else {
                this.mitteilungRS.getEntwurfForCurrentRolleForFall(this.fall.id).then((entwurf: TSMitteilung) => {
                    if (entwurf) {
                        this.currentMitteilung = entwurf;
                    } else {
                        this.initMitteilungForCurrentBenutzer();
                    }
                });
            }
        }
    }

    private initMitteilungForCurrentBenutzer() {
        let currentUser: TSUser = this.authServiceRS.getPrincipal();

        //common attributes
        this.currentMitteilung = new TSMitteilung();
        this.currentMitteilung.fall = this.fall;
        if (this.betreuung) {
            this.currentMitteilung.betreuung = this.betreuung;
        }
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
     * Speichert die aktuelle Mitteilung als gesendet.
     */
    public sendMitteilung(): IPromise<TSMitteilung> {
        if (!this.isMitteilungEmpty()) {
            return this.mitteilungRS.sendMitteilung(this.getCurrentMitteilung()).then((response) => {
                this.loadEntwurf();
                this.loadAllMitteilungen();
                return this.currentMitteilung;
            }).finally(() => {
                this.form.$setPristine();
                this.form.$setUntouched();
            });
        } else {
            return this.$q.when(this.currentMitteilung);
        }
    }

    /**
     * Speichert die aktuelle Mitteilung nur wenn das formular dirty ist.
     * Wenn das Formular leer ist, wird der Entwurf geloescht (falls er bereits existiert)
     */
    public saveEntwurf(): IPromise<TSMitteilung> {
        if (((this.form.$dirty && !this.isMitteilungEmpty()))) {
            return this.mitteilungRS.saveEntwurf(this.getCurrentMitteilung()).then((response) => {
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
        if (this.betreuung) {
            this.mitteilungRS.getMitteilungenForCurrentRolleForBetreuung(this.betreuung.id).then((response) => {
                this.allMitteilungen = response;
            });
        } else {
            this.mitteilungRS.getMitteilungenForCurrentRolleForFall(this.fall.id).then((response) => {
                this.allMitteilungen = response;
            });
        }
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

    private setAllMitteilungenGelesen(): IPromise<Array<TSMitteilung>> {
        return this.mitteilungRS.setAllNewMitteilungenOfFallGelesen(this.fall.id);
    }

    /**
     * Aendert den Status der gegebenen Mitteilung auf ERLEDIGT wenn es GELESEN war oder
     * auf GELESEN wenn es ERLEDIGT war
     */
    public setErledigt(mitteilung: TSMitteilung): void {
        if (mitteilung && mitteilung.mitteilungStatus === TSMitteilungStatus.GELESEN) {
            mitteilung.mitteilungStatus = TSMitteilungStatus.ERLEDIGT;
            this.mitteilungRS.setMitteilungErledigt(mitteilung.id);

        } else if (mitteilung && mitteilung.mitteilungStatus === TSMitteilungStatus.ERLEDIGT) {
            mitteilung.mitteilungStatus = TSMitteilungStatus.GELESEN;
            this.mitteilungRS.setMitteilungGelesen(mitteilung.id);
        }
    }

    public isStatusErledigtGelesen(mitteilung: TSMitteilung): boolean {
        return mitteilung && (mitteilung.mitteilungStatus === TSMitteilungStatus.ERLEDIGT || mitteilung.mitteilungStatus === TSMitteilungStatus.GELESEN);
    }

    public getBgNummer(): string {
        let bgNummer: string = '';
        if (this.betreuung) {
            bgNummer = this.ebeguUtil.calculateBetreuungsId(this.betreuung.gesuchsperiode, this.fall, this.betreuung.kindNummer, this.betreuung.betreuungNummer);
        }
        return bgNummer;
    }

    public betreuungAsString(mitteilung: TSMitteilung): string {
        let betreuungAsString: string;
        if (mitteilung.betreuung) {
            let bgNummer: string = this.ebeguUtil.calculateBetreuungsId(mitteilung.betreuung.gesuchsperiode, mitteilung.fall,
                mitteilung.betreuung.kindNummer, mitteilung.betreuung.betreuungNummer);
            betreuungAsString = mitteilung.betreuung.kindFullname + ', ' + bgNummer;
        }
        return betreuungAsString;
    }

    public gotoBetreuung(mitteilung: TSMitteilung): void {
        this.$state.go('gesuch.betreuung', {
            betreuungNumber: mitteilung.betreuung.betreuungNummer,
            kindNumber: mitteilung.betreuung.kindNummer,
            gesuchId: mitteilung.betreuung.gesuchId
        });
    }
}
