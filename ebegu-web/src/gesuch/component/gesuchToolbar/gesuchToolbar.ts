import {IComponentOptions, ILogService} from 'angular';
import UserRS from '../../../core/service/userRS.rest';
import TSUser from '../../../models/TSUser';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../service/gesuchRS.rest';
import {IStateService} from 'angular-ui-router';
import TSAntragDTO from '../../../models/TSAntragDTO';
import {IGesuchStateParams} from '../../gesuch.route';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import GesuchModelManager from '../../service/gesuchModelManager';
import {isAnyStatusOfVerfuegt} from '../../../models/enums/TSAntragStatus';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import * as moment from 'moment';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import {TSMitteilungEvent} from '../../../models/enums/TSMitteilungEvent';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
import IScope = angular.IScope;
import {TSRole} from '../../../models/enums/TSRole';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSGesuchsperiodeStatus} from '../../../models/enums/TSGesuchsperiodeStatus';
import FallRS from '../../service/fallRS.rest';
import TSFall from '../../../models/TSFall';
let templateX = require('./gesuchToolbar.html');
let templateGS = require('./gesuchToolbarGesuchsteller.html');
require('./gesuchToolbar.less');

export class GesuchToolbarComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gesuchid: '@',
        fallid: '@',
        isDashboardScreen: '@',
        hideActionButtons: '@',
        forceLoadingFromFall: '@'
    };

    template = templateX;
    controller = GesuchToolbarController;
    controllerAs = 'vmx';
}

export class GesuchToolbarGesuchstellerComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gesuchid: '@',
        fallid: '@',
        isDashboardScreen: '@',
        hideActionButtons: '@',
        forceLoadingFromFall: '@'
    };
    template = templateGS;
    controller = GesuchToolbarController;
    // Darf, wie es scheint nicht 'vm' heissen, sonst werden im gesuchToolBarGesuchsteller.html keine Funktionen gefunden. Bug?!
    controllerAs = 'vmgs';
}

export class GesuchToolbarController {

    userList: Array<TSUser>;
    antragList: Array<TSAntragDTO>;
    gesuchid: string;
    fallid: string;
    isDashboardScreen: boolean;
    hideActionButtons: boolean;
    TSRoleUtil: any;
    forceLoadingFromFall: boolean;
    fall: TSFall;

    gesuchsperiodeList: {[key: string]: Array<TSAntragDTO>} = {};
    gesuchNavigationList: {[key: string]: Array<string>} = {};   //mapped z.B. '2006 / 2007' auf ein array mit den Namen der Antraege
    antragTypList: {[key: string]: TSAntragDTO} = {};
    mutierenPossibleForCurrentAntrag: boolean = false;
    erneuernPossibleForCurrentAntrag: boolean = false;
    neuesteGesuchsperiode: TSGesuchsperiode;

    static $inject = ['UserRS', 'EbeguUtil', 'CONSTANTS', 'GesuchRS',
        '$state', '$stateParams', '$scope', 'GesuchModelManager', 'AuthServiceRS',
        '$mdSidenav', '$log', 'GesuchsperiodeRS', 'FallRS'];

    constructor(private userRS: UserRS, private ebeguUtil: EbeguUtil,
                private CONSTANTS: any, private gesuchRS: GesuchRS,
                private $state: IStateService, private $stateParams: IGesuchStateParams, private $scope: IScope,
                private gesuchModelManager: GesuchModelManager,
                private authServiceRS: AuthServiceRS,
                private $mdSidenav: ng.material.ISidenavService,
                private $log: ILogService,
                private gesuchsperiodeRS: GesuchsperiodeRS,
                private fallRS: FallRS) {

    }

    $onInit() {
        this.updateUserList();
        this.updateAntragDTOList();
        //add watchers
        this.addWatchers(this.$scope);
        this.TSRoleUtil = TSRoleUtil;
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: TSGesuchsperiode[]) => {
            // Die neueste ist zuoberst
            this.neuesteGesuchsperiode = response[0];
        });
    }

    public toggleSidenav(componentId: string): void {
        this.$mdSidenav(componentId).toggle();
    }

    public closeSidenav(componentId: string): void {
        this.$mdSidenav(componentId).close();
    }

    public logout(): void {
        this.$state.go('login', {type: 'logout'});
    }

    private addWatchers($scope: angular.IScope) {
        // needed because of test is not able to inject $scope!
        if ($scope) {
            //watcher fuer gesuch id change
            $scope.$watch(() => {
                return this.gesuchid;
            }, (newValue, oldValue) => {
                if (newValue !== oldValue) {
                    if (this.gesuchid) {
                        this.updateAntragDTOList();
                    } else {
                        this.antragTypList = {};
                        this.gesuchNavigationList = {};
                        this.gesuchsperiodeList = {};
                        this.antragList = [];
                        this.antragMutierenPossible(); //neu berechnen ob mutieren moeglich ist
                        this.antragErneuernPossible();
                    }
                }
            });
            //watcher fuer status change
            if (this.gesuchModelManager && this.getGesuch()) {
                $scope.$watch(() => {
                    return this.getGesuch().status;
                }, (newValue, oldValue) => {
                    if ((newValue !== oldValue) && (isAnyStatusOfVerfuegt(newValue))) {
                        this.updateAntragDTOList();
                    }
                });
            }
            //watcher fuer fall id change
            $scope.$watch(() => {
                return this.fallid;
            }, (newValue, oldValue) => {
                if (newValue !== oldValue) {
                    if (this.fallid) {
                        this.updateAntragDTOList();
                    } else {
                        this.antragTypList = {};
                        this.gesuchNavigationList = {};
                        this.gesuchsperiodeList = {};
                        this.antragList = [];
                        this.antragMutierenPossible(); //neu berechnen ob mutieren moeglich ist
                        this.antragErneuernPossible();
                    }
                }
            });
            // Wenn eine Mutationsmitteilung uebernommen wird und deshalb eine neue Mutation erstellt wird, muss
            // die toolbar aktualisisert werden, damit diese Mutation auf der Liste erscheint
            $scope.$on(TSMitteilungEvent[TSMitteilungEvent.MUTATIONSMITTEILUNG_NEUE_MUTATION], () => {
                this.updateAntragDTOList();
            });
        }
    }

    public showGesuchPeriodeNavigationMenu(): boolean {
        return !this.isDashboardScreen && !angular.equals(this.gesuchsperiodeList, {})
            && !this.authServiceRS.isRole(TSRole.STEUERAMT);
    }

    /**
     * Die Liste wird nicht angezeigt wenn sie leer ist oder wenn der Benutzer sich auf dem Dashboard befindet
     */
    public showAntragTypListNavigationMenu(): boolean {
        return !this.isDashboardScreen && !angular.equals(this.antragTypList, {})
            && !this.authServiceRS.isRole(TSRole.STEUERAMT);
    }

    public showKontaktMenu(): boolean {
        if (this.getGesuch() && this.getGesuch().gesuchsteller1) {
            return true;
        }
        return false;
    }

    public getVerantwortlicherFullName(): string {
        if (this.getGesuch() && this.getGesuch().fall && this.getGesuch().fall.verantwortlicher) {
            return this.getGesuch().fall.verantwortlicher.getFullName();
        }
        return '';
    }

    public updateUserList(): void {
        this.userRS.getBenutzerJAorAdmin().then((response) => {
            this.userList = angular.copy(response);
        });
    }

    public updateAntragDTOList(): void {
        this.updateFall();
        if (!this.forceLoadingFromFall && this.getGesuch() && this.getGesuch().id) {
            this.gesuchRS.getAllAntragDTOForFall(this.getGesuch().fall.id).then((response) => {
                this.antragList = angular.copy(response);
                this.updateGesuchperiodeList();
                this.updateGesuchNavigationList();
                this.updateAntragTypList();
                this.antragMutierenPossible();
                this.antragErneuernPossible();
            });
        } else if (this.fallid) {
            this.gesuchRS.getAllAntragDTOForFall(this.fallid).then((response) => {
                this.antragList = angular.copy(response);
                if (response && response.length > 0) {
                    let newest = this.getNewest(this.antragList);
                    this.gesuchRS.findGesuch(newest.antragId).then((response) => {
                        if (!response) {
                            this.$log.warn('Could not find gesuch for id ' + newest.antragId);
                        }
                        this.gesuchModelManager.setGesuch(angular.copy(response));
                        this.updateGesuchperiodeList();
                        this.updateGesuchNavigationList();
                        this.updateAntragTypList();
                        this.antragMutierenPossible();
                        this.antragErneuernPossible();
                    });
                } else {
                    // Wenn das Gesuch noch neu ist, sind wir noch ungespeichert auf der FallCreation-Seite
                    // In diesem Fall durfen wir das Gesuch nicht zuruecksetzen
                    if (!this.gesuchModelManager.getGesuch().isNew()) {
                        // in this case there is no Gesuch for this fall, so we remove all content
                        this.gesuchModelManager.setGesuch(new TSGesuch());
                        this.resetNavigationParameters();
                    }
                }
            });
        } else {
            this.resetNavigationParameters();
        }
        this.forceLoadingFromFall = false; // reset it because it's not needed any more
    }

    private resetNavigationParameters() {
        this.gesuchsperiodeList = {};
        this.gesuchNavigationList = {};
        this.antragTypList = {};
        this.antragMutierenPossible();
        this.antragErneuernPossible();
    }

    private updateGesuchperiodeList() {
        this.gesuchsperiodeList = {};
        for (let i = 0; i < this.antragList.length; i++) {
            let gs = this.antragList[i].gesuchsperiodeString;

            if (!this.gesuchsperiodeList[gs]) {
                this.gesuchsperiodeList[gs] = [];
            }
            this.gesuchsperiodeList[gs].push(this.antragList[i]);
        }
    }

    private updateGesuchNavigationList() {
        this.gesuchNavigationList = {};  // clear
        for (let i = 0; i < this.antragList.length; i++) {
            let gs = this.antragList[i].gesuchsperiodeString;
            let antrag: TSAntragDTO = this.antragList[i];

            if (!this.gesuchNavigationList[gs]) {
                this.gesuchNavigationList[gs] = [];
            }
            this.gesuchNavigationList[gs].push(this.ebeguUtil.getAntragTextDateAsString(antrag.antragTyp, antrag.eingangsdatum, antrag.laufnummer));
        }
    }

    private updateAntragTypList() {
        this.antragTypList = {};  //clear
        for (let i = 0; i < this.antragList.length; i++) {
            let antrag: TSAntragDTO = this.antragList[i];
            if (this.getGesuch().gesuchsperiode.gueltigkeit.gueltigAb.isSame(antrag.gesuchsperiodeGueltigAb)) {
                let txt = this.ebeguUtil.getAntragTextDateAsString(antrag.antragTyp, antrag.eingangsdatum, antrag.laufnummer);

                this.antragTypList[txt] = antrag;
            }

        }
    }

    getKeys(map: {[key: string]: Array<TSAntragDTO>}): Array<String> {
        let keys: Array<String> = [];
        for (let key in map) {
            if (map.hasOwnProperty(key)) {
                keys.push(key);
            }
        }
        return keys;
    }

    /**
     * Sets the given user as the verantworlicher fuer den aktuellen Fall
     * @param verantwortlicher
     */
    public setVerantwortlicher(verantwortlicher: TSUser): void {
        if (verantwortlicher) {
            this.gesuchModelManager.setUserAsFallVerantwortlicher(verantwortlicher);
            this.gesuchModelManager.updateFall();
        }
        this.setUserAsFallVerantwortlicherLocal(verantwortlicher);
    }

    /**
     * Change local gesuch to change the current view
     * @param user
     */
    public setUserAsFallVerantwortlicherLocal(user: TSUser) {
        if (user && this.getGesuch() && this.getGesuch().fall) {
            this.getGesuch().fall.verantwortlicher = user;
        }
    }

    /**
     *
     * @param user
     * @returns {boolean} true if the given user is already the verantwortlicher of the current fall
     */
    public isCurrentVerantwortlicher(user: TSUser): boolean {
        return (user && this.getFallVerantwortlicher() && this.getFallVerantwortlicher().username === user.username);
    }

    public getFallVerantwortlicher(): TSUser {
        if (this.getGesuch() && this.getGesuch().fall) {
            return this.getGesuch().fall.verantwortlicher;
        }
        return undefined;
    }

    /**
     * Tries to get the "gesuchName" out of the gesuch contained in the gesuchModelManager. If this doesn't
     * succeed it gets the "gesuchName" out of the fall
     */
    public getGesuchName(): string {
        let gesuchName = this.gesuchModelManager.getGesuchName();
        if (!gesuchName || gesuchName.length <= 0) {
            gesuchName = this.ebeguUtil.getGesuchNameFromFall(this.fall);
        }
        return gesuchName;
    }

    public getGesuch(): TSGesuch {
        return this.gesuchModelManager.getGesuch();
    }

    public getCurrentGesuchsperiode(): string {
        if (this.getGesuch() && this.getGesuch().gesuchsperiode) {
            return this.getGesuchsperiodeAsString(this.getGesuch().gesuchsperiode);
        } else {
            return '';
        }
    }

    public getAntragTyp(): string {
        if (this.getGesuch()) {
            return this.ebeguUtil.getAntragTextDateAsString(this.getGesuch().typ, this.getGesuch().eingangsdatum, this.getGesuch().laufnummer);
        } else {
            return '';
        }
    }

    public getAntragDatum(): Moment {
        if (this.getGesuch() && this.getGesuch().eingangsdatum) {
            return this.getGesuch().eingangsdatum;
        } else {
            return moment();
        }
    }

    public getGesuchsperiodeAsString(tsGesuchsperiode: TSGesuchsperiode) {
        return tsGesuchsperiode.gesuchsperiodeString;
    }

    public setGesuchsperiode(gesuchsperiodeKey: string) {
        let selectedGesuche = this.gesuchsperiodeList[gesuchsperiodeKey];
        let selectedGesuch: TSAntragDTO = this.getNewest(selectedGesuche);

        this.goToOpenGesuch(selectedGesuch.antragId);
    }

    private getNewest(arrayTSAntragDTO: Array<TSAntragDTO>): TSAntragDTO {
        let newest: TSAntragDTO = arrayTSAntragDTO[0];
        for (let i = 0; i < arrayTSAntragDTO.length; i++) {
            // Wenn eines noch gar kein Eingangsdatum hat ist es sicher das neueste
            if (!arrayTSAntragDTO[i].eingangsdatum) {
                return arrayTSAntragDTO[i];
            }
            if (arrayTSAntragDTO[i].eingangsdatum.isAfter(newest.eingangsdatum)) {
                newest = arrayTSAntragDTO[i];
            }
        }
        return newest;
    }

    /**
     * Institutionen werden zum Screen Betreuungen geleitet, waehrend alle anderen Benutzer zu fallCreation gehen
     */
    private goToOpenGesuch(gesuchId: string): void {
        if (gesuchId) {
            if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getTraegerschaftInstitutionOnlyRoles())) {
                this.$state.go('gesuch.betreuungen', {gesuchId: gesuchId});
            } else if (this.authServiceRS.isRole(TSRole.STEUERAMT)) {
                this.$state.go('gesuch.familiensituation', {gesuchId: gesuchId});
            } else {
                this.$state.go('gesuch.fallcreation', {
                    createNew: false, gesuchId: gesuchId});
            }
        }
    }


    public setAntragTypDatum(antragTypDatumKey: string) {
        let selectedAntragTypGesuch = this.antragTypList[antragTypDatumKey];
        this.goToOpenGesuch(selectedAntragTypGesuch.antragId);
    }

    public showButtonMutieren(): boolean {
       if (this.hideActionButtons) {
           return false;
       }
       if (this.getGesuch()) {
           if (this.getGesuch().isNew()) {
               return false;
           }
           // Wenn die Gesuchsperiode geschlossen ist, kann sowieso keine Mutation mehr gemacht werden
           if (this.getGesuch().gesuchsperiode && this.getGesuch().gesuchsperiode.status === TSGesuchsperiodeStatus.GESCHLOSSEN) {
               return false;
           }
       }
       return this.mutierenPossibleForCurrentAntrag;
    }

    private antragMutierenPossible(): void {
        if (this.antragList && this.antragList.length !== 0) {
            let mutierenGesperrt = false;
            for (let i = 0; i < this.antragList.length; i++) {
                let antragItem: TSAntragDTO = this.antragList[i];
                // Wir muessen nur die Antraege der aktuell ausgewaehlten Gesuchsperiode beachten
                if (antragItem.gesuchsperiodeString === this.getCurrentGesuchsperiode()) {
                    // Falls wir ein Gesuch finden das nicht verfuegt ist oder eine Beschwerde hängig ist, darf nicht mutiert werden
                    if (antragItem.verfuegt === false || antragItem.beschwerdeHaengig === true) {
                        mutierenGesperrt = true;
                        break;
                    }
                }
            }
            this.mutierenPossibleForCurrentAntrag = !mutierenGesperrt;
        } else {
            this.mutierenPossibleForCurrentAntrag = false;
        }
    }

    public antragMutieren(): void {
        this.mutierenPossibleForCurrentAntrag = false;
        let eingangsart: TSEingangsart;
        if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles())) {
            eingangsart = TSEingangsart.ONLINE;
        } else {
            eingangsart = TSEingangsart.PAPIER;
        }
        this.$state.go('gesuch.mutation', {
            createMutation: true,
            gesuchId: this.getGesuchIdFuerMutationOrErneuerung(),
            fallId: this.getGesuch().fall.id,
            eingangsart: eingangsart,
            gesuchsperiodeId: this.getGesuch().gesuchsperiode.id
        });
    }

    private antragErneuernPossible(): void {
        if (this.antragList && this.antragList.length !== 0) {
            let erneuernGesperrt = false;
            for (let i = 0; i < this.antragList.length; i++) {
                let antragItem: TSAntragDTO = this.antragList[i];
                // Wir muessen nur die Antraege der aktuell ausgewaehlten Gesuchsperiode beachten
                if (antragItem.gesuchsperiodeString === this.getGesuchsperiodeAsString(this.neuesteGesuchsperiode)) {
                    // Es gibt schon (mindestens 1) Gesuch für die neueste Periode
                    erneuernGesperrt = true;
                    break;
                }
            }
            this.erneuernPossibleForCurrentAntrag = !erneuernGesperrt;
        } else {
            this.erneuernPossibleForCurrentAntrag = false;
        }
    }

    public antragErneuern(): void {
        this.erneuernPossibleForCurrentAntrag = false;
        let eingangsart: TSEingangsart;
        if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles())) {
            eingangsart = TSEingangsart.ONLINE;
        } else {
            eingangsart = TSEingangsart.PAPIER;
        }
        this.$state.go('gesuch.erneuerung', {
            createErneuerung: true,
            gesuchId: this.getGesuchIdFuerMutationOrErneuerung(),
            eingangsart: eingangsart,
            gesuchsperiodeId: this.neuesteGesuchsperiode.id,
            fallId: this.fallid
        });
    }

    private getGesuchIdFuerMutationOrErneuerung(): string {
        // GesuchId ermitteln fuer Mutation ermitteln: Falls wir auf der Verlauf-View sind, nehmen wir einfach
        // irgendeines der Liste (es wird auf dem Server sichergestellt, dass die Mutation ab dem neuesten Gesuch
        // der Periode gemacht wird), wichtig ist nur, dass es sich um die richtige Gesuchsperiode handelt.
        let gesuchId;
        if (this.gesuchid) {
            return this.gesuchid;
        } else {
            if (this.getGesuch()) {
                return this.getGesuch().id;
            }
        }
        return undefined;
    }

    private addAntragToList(antrag: TSGesuch): void {
        let antragDTO = new TSAntragDTO();
        antragDTO.antragTyp = TSAntragTyp.MUTATION;
        let txt = this.ebeguUtil.getAntragTextDateAsString(antragDTO.antragTyp, antrag.eingangsdatum, antrag.laufnummer);
        this.antragTypList[txt] = antragDTO;
    }

    private hasBesitzer(): boolean {
        return this.fall && this.fall.besitzer !== null && this.fall.besitzer !== undefined;
    }

    private getBesitzer(): string {
        if (this.fall) {
            return this.fall.besitzer.getFullName();
        }
        return '';
    }

    public openMitteilungen(): void {
        this.$state.go('mitteilungen', {
            fallId: this.fallid
        });
    }

    public showVerlauf(): boolean {
        return this.getGesuch() !== null && this.getGesuch() !== undefined && !this.getGesuch().isNew();
    }

    public openVerlauf(): void {
        this.$state.go('verlauf', {
            gesuchId: this.getGesuch().id
        });
    }
    public openAlleVerfuegungen(): void {
        this.$state.go('alleVerfuegungen', {
            fallId: this.fallid
        });
    }

    private updateFall(): void {
        if (this.fallid) {
            this.fallRS.findFall(this.fallid).then((response: TSFall) => {
                if (response) {
                    this.fall = response;
                }
            });
        }
    }
}
