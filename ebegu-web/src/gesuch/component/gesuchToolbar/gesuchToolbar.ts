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
import {TSGesuchEvent} from '../../../models/enums/TSGesuchEvent';
import GesuchModelManager from '../../service/gesuchModelManager';
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
    };

    template = templateX;
    controller = GesuchToolbarController;
    controllerAs = 'vmx';
}

export class GesuchToolbarGesuchstellerComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gesuchid: '@'
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

    onVerantwortlicherChange: (attr: any) => void;

    gesuchsperiodeList: { [key: string]: Array<TSAntragDTO> } = {};
    antragTypList: { [key: string]: TSAntragDTO } = {};
    mutierenPossibleForCurrentAntrag: boolean = false;

    static $inject = ['UserRS', 'EbeguUtil', 'CONSTANTS', 'GesuchRS',
        '$state', '$stateParams', '$scope', 'GesuchModelManager'];

    constructor(private userRS: UserRS, private ebeguUtil: EbeguUtil,
                private CONSTANTS: any, private gesuchRS: GesuchRS,
                private $state: IStateService, private $stateParams: IGesuchStateParams, private $scope: IScope,
                private gesuchModelManager: GesuchModelManager) {
        this.updateUserList();
        this.updateAntragDTOList();

        //add watchers
        this.addWatchers($scope);

    }


    private addWatchers($scope: angular.IScope) {
        // needed because of test is not able to inject $scope!
        if ($scope) {
            $scope.$watch(() => {
                return this.gesuchid;
            }, (newValue, oldValue) => {
                if (newValue !== oldValue) {
                    if (this.gesuchid) {
                        this.updateAntragDTOList();
                    } else {
                        this.antragTypList = {};
                        this.gesuchsperiodeList = {};
                    }
                }
            });
            $scope.$on(TSGesuchEvent[TSGesuchEvent.STATUS_VERFUEGT], () => {
                this.updateAntragDTOList();
            });
        }
    }

    public showGesuchPeriodeNavigationMenu(): boolean {
        return !angular.equals(this.gesuchsperiodeList, {});
    }

    public showAntragTypListNavigationMenu(): boolean {
        return !angular.equals(this.antragTypList, {});
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
        this.userRS.getAllUsers().then((response) => {
            this.userList = angular.copy(response);
        });
    }

    public updateAntragDTOList(): void {
        if (this.getGesuch() && this.getGesuch().id) {
            this.gesuchRS.getAllAntragDTOForFall(this.getGesuch().fall.id).then((response) => {
                this.antragList = angular.copy(response);
                this.updateGesuchperiodeList();
                this.updateAntragTypList();
                this.antragMutierenPossible();
            });
        } else {
            this.gesuchsperiodeList = {};
            this.antragTypList = {};
            this.antragMutierenPossible();
        }
    }

    private updateGesuchperiodeList() {

        for (var i = 0; i < this.antragList.length; i++) {
            let gs = this.antragList[i].gesuchsperiodeString;

            if (!this.gesuchsperiodeList[gs]) {
                this.gesuchsperiodeList[gs] = [];
            }
            this.gesuchsperiodeList[gs].push(this.antragList[i]);
        }
    }

    private updateAntragTypList() {
        for (var i = 0; i < this.antragList.length; i++) {
            let antrag: TSAntragDTO = this.antragList[i];
            if (this.getGesuch().gesuchsperiode.gueltigkeit.gueltigAb.isSame(antrag.gesuchsperiodeGueltigAb)) {
                let txt = this.ebeguUtil.getAntragTextDateAsString(antrag.antragTyp, antrag.eingangsdatum);

                this.antragTypList[txt] = antrag;
            }
        }
    }

    getKeys(map: { [key: string]: Array<TSAntragDTO> }): Array<String> {
        var keys: Array<String> = [];
        for (var key in map) {
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

    //TODO: Muss mit IAM noch angepasst werden. Fall und Name soll vom Login stammen nicht vom Gesuch, da auf DashbordSeite die Fallnummer und Name des GS angezeigt werden soll
    public getGesuchName(): string {
        if (this.getGesuch()) {
            var text = this.ebeguUtil.addZerosToNumber(this.getGesuch().fall.fallNummer, this.CONSTANTS.FALLNUMMER_LENGTH);
            if (this.getGesuch().gesuchsteller1 && this.getGesuch().gesuchsteller1.nachname) {
                text = text + ' ' + this.getGesuch().gesuchsteller1.nachname;
            }
            return text;
        } else {
            return '';
        }
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
            return this.ebeguUtil.getAntragTextDateAsString(this.getGesuch().typ, this.getGesuch().eingangsdatum);
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
        for (var i = 0; i < arrayTSAntragDTO.length; i++) {
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
        if (this.antragList) {
            let gesuchInBearbeitungVorhanden = false;
            for (var i = 0; i < this.antragList.length; i++) {
                let antragItem: TSAntragDTO = this.antragList[i];
                // Wir muessen nur die Antraege der aktuell ausgewaehlten Gesuchsperiode beachten
                if (antragItem.gesuchsperiodeString === this.getCurrentGesuchsperiode()) {
                    // Falls das Gesuch nicht verfuegt ist, darf nicht mutiert werden
                    if (antragItem.verfuegt === false) {
                        gesuchInBearbeitungVorhanden = true;
                    }
                }
            }
            this.mutierenPossibleForCurrentAntrag = !gesuchInBearbeitungVorhanden;
        } else {
            this.mutierenPossibleForCurrentAntrag = false;
        }
    }

    public antragMutieren(): void {
        this.mutierenPossibleForCurrentAntrag = false;
        this.$state.go('gesuch.mutation', {gesuchId: this.gesuchid});
        //TODO (hefr) hier muesste dann noch der blaue balken angepasst werden! NACH der mutation!
    }

    //TODO (team) den (noch ungespeicherten) Mutationsantrag zur Liste im blauen Balken hinzufuegen
    private addAntragToList(antrag: TSGesuch): void {
        let antragDTO = new TSAntragDTO();
        antragDTO.antragTyp = TSAntragTyp.MUTATION;
        let txt = this.ebeguUtil.getAntragTextDateAsString(antragDTO.antragTyp, antrag.eingangsdatum);
        this.antragTypList[txt] = antragDTO;
    }
}
