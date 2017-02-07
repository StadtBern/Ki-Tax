import {IComponentOptions} from 'angular';
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
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
import IScope = angular.IScope;
let templateX = require('./gesuchToolbar.html');
let templateGS = require('./gesuchToolbarGesuchsteller.html');
require('./gesuchToolbar.less');

export class GesuchToolbarComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gesuchid: '@',
        onVerantwortlicherChange: '&',
        fallid: '@',
        isDashboardScreen: '@',
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
    TSRoleUtil: any;

    onVerantwortlicherChange: (attr: any) => void;

    gesuchsperiodeList: {[key: string]: Array<TSAntragDTO>} = {};
    gesuchNavigationList: {[key: string]: Array<string>} = {};   //mapped z.B. '2006 / 2007' auf ein array mit den Namen der Antraege
    antragTypList: {[key: string]: TSAntragDTO} = {};
    mutierenPossibleForCurrentAntrag: boolean = false;

    static $inject = ['UserRS', 'EbeguUtil', 'CONSTANTS', 'GesuchRS',
        '$state', '$stateParams', '$scope', 'GesuchModelManager', 'AuthServiceRS',
        '$mdSidenav'];

    constructor(private userRS: UserRS, private ebeguUtil: EbeguUtil,
                private CONSTANTS: any, private gesuchRS: GesuchRS,
                private $state: IStateService, private $stateParams: IGesuchStateParams, private $scope: IScope,
                private gesuchModelManager: GesuchModelManager,
                private authServiceRS: AuthServiceRS,
                private $mdSidenav: ng.material.ISidenavService) {
        this.updateUserList();
        this.updateAntragDTOList();

        //add watchers
        this.addWatchers($scope);
        this.TSRoleUtil = TSRoleUtil;

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
                    }
                }
            });
            //watcher fuer status change
            if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
                $scope.$watch(() => {
                    return this.gesuchModelManager.getGesuch().status;
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
                    }
                }
            });
        }
    }

    public showGesuchPeriodeNavigationMenu(): boolean {
        return !this.isDashboardScreen && !angular.equals(this.gesuchsperiodeList, {});
    }

    /**
     * Die Liste wird nicht angezeigt wenn sie leer ist oder wenn der Benutzer sich auf dem Dashboard befindet
     */
    public showAntragTypListNavigationMenu(): boolean {
        return !this.isDashboardScreen && !angular.equals(this.antragTypList, {});
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
        if (this.getGesuch() && this.getGesuch().id) {
            this.gesuchRS.getAllAntragDTOForFall(this.getGesuch().fall.id).then((response) => {
                this.antragList = angular.copy(response);
                this.updateGesuchperiodeList();
                this.updateGesuchNavigationList();
                this.updateAntragTypList();
                this.antragMutierenPossible();
            });
        } else if (this.fallid) {
            this.gesuchRS.getAllAntragDTOForFall(this.fallid).then((response) => {
                this.antragList = angular.copy(response);
                if (response && response.length > 0) {
                    this.gesuchRS.findGesuch(this.getNewest(this.antragList).antragId).then((response) => {
                        this.gesuchModelManager.setGesuch(angular.copy(response));
                        this.updateGesuchperiodeList();
                        this.updateGesuchNavigationList();
                        this.updateAntragTypList();
                        this.antragMutierenPossible();
                    });
                }
            });
        } else {
            this.gesuchsperiodeList = {};
            this.gesuchNavigationList = {};
            this.antragTypList = {};
            this.antragMutierenPossible();
        }
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
        this.onVerantwortlicherChange({user: verantwortlicher});
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

    public getGesuchName(): string {
        return this.gesuchModelManager.getGesuchName();
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

    private goToOpenGesuch(gesuchId: string): void {
        if (gesuchId) {
            this.$state.go('gesuch.fallcreation', {createNew: false, gesuchId: gesuchId});
        }
    }


    public setAntragTypDatum(antragTypDatumKey: string) {
        let selectedAntragTypGesuch = this.antragTypList[antragTypDatumKey];
        this.goToOpenGesuch(selectedAntragTypGesuch.antragId);
    }

    public antragMutierenPossible(): void {
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
            createMutation: true, gesuchId: this.gesuchid,
            fallId: this.getGesuch().fall.id, eingangsart: eingangsart,
            gesuchsperiodeId: this.getGesuch().gesuchsperiode.id
        });
    }

    private addAntragToList(antrag: TSGesuch): void {
        let antragDTO = new TSAntragDTO();
        antragDTO.antragTyp = TSAntragTyp.MUTATION;
        let txt = this.ebeguUtil.getAntragTextDateAsString(antragDTO.antragTyp, antrag.eingangsdatum, antrag.laufnummer);
        this.antragTypList[txt] = antragDTO;
    }

    private hasBesitzer(): boolean {
        if (this.getGesuch() && this.getGesuch().fall && this.getGesuch().fall) {
            return this.getGesuch().fall.besitzer !== undefined && this.getGesuch().fall.besitzer !== null;
        }
        return false;
    }

    private getBesitzer(): string {
        if (this.getGesuch() && this.getGesuch().fall && this.getGesuch().fall) {
            return this.getGesuch().fall.besitzer !== undefined && this.getGesuch().fall.besitzer.getFullName();
        }
        return '';
    }

    public openMitteilungen(): void {
        this.$state.go('mitteilungen', {
            fallId: this.fallid
        });
    }
}
