import {IComponentOptions} from 'angular';
import UserRS from '../../../core/service/userRS.rest';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSUser from '../../../models/TSUser';
import EbeguUtil from '../../../utils/EbeguUtil';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../service/gesuchRS.rest';
import BerechnungsManager from '../../service/berechnungsManager';
import {IStateService} from 'angular-ui-router';
import Moment = moment.Moment;
let template = require('./gesuchToolbar.html');
require('./gesuchToolbar.less');

export class GesuchToolbarComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = GesuchToolbarController;
    controllerAs = 'vm';
}

export class GesuchToolbarController {

    userList: Array<TSUser>;
    gesuchsperiodeList: Array<TSGesuchsperiode>;

    static $inject = ['UserRS', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', 'GesuchsperiodeRS', 'GesuchRS',
        'BerechnungsManager', '$state'];

    constructor(private userRS: UserRS, private gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil,
                private CONSTANTS: any, private gesuchsperiodeRS: GesuchsperiodeRS, private gesuchRS: GesuchRS,
                private berechnungsManager: BerechnungsManager, private $state: IStateService) {
        this.updateUserList();
        this.updateGesuchsperiodenList();
    }

    public getVerantwortlicherFullName(): string {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().fall && this.gesuchModelManager.getGesuch().fall.verantwortlicher) {
            return this.gesuchModelManager.getGesuch().fall.verantwortlicher.getFullName();
        }
        return '';
    }

    public updateUserList(): void {
        this.userRS.getAllUsers().then((response) => {
            this.userList = angular.copy(response);
        });
    }

    public updateGesuchsperiodenList(): void {
        let gesuch = this.gesuchModelManager.getGesuch();
        if (gesuch && gesuch.id) {
            this.gesuchsperiodeRS.getAllGesuchsperiodenForFall(gesuch.fall.id).then((response) => {
                this.gesuchsperiodeList = angular.copy(response);
            });
        }
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
    }

    /**
     *
     * @param user
     * @returns {boolean} true if the given user is already the verantwortlicher of the current fall
     */
    public isCurrentVerantwortlicher(user: TSUser): boolean {
        return (user && this.gesuchModelManager.getFallVerantwortlicher() && this.gesuchModelManager.getFallVerantwortlicher().username === user.username);
    }

    public getGesuchName(): string {
        let gesuch = this.gesuchModelManager.getGesuch();
        if (gesuch && gesuch.gesuchsteller1) {
            return this.ebeguUtil.addZerosToNumber(gesuch.fall.fallNummer, this.CONSTANTS.FALLNUMMER_LENGTH) +
                ' ' + gesuch.gesuchsteller1.nachname;
        } else {
            return '--';
        }
    }

    public getCurrentGesuchsperiode(): string {
        let gesuch = this.gesuchModelManager.getGesuch();
        if (gesuch && gesuch.gesuchsperiode) {
            return this.getGesuchsperiodeAsString(gesuch.gesuchsperiode);
        } else {
            return '--';
        }
    }

    public getGesuchsperiodeAsString(tsGesuchsperiode: TSGesuchsperiode) {
        return this.ebeguUtil.getGesuchsperiodeAsString(tsGesuchsperiode);
    }

    public setGesuchsperiode(tsGesuchsperiode: TSGesuchsperiode) {
        this.gesuchRS.findGesuchByFallAndPeriode(this.gesuchModelManager.getGesuch().fall.id, tsGesuchsperiode.id)
            .then((response) => {
                if (response) {
                    this.openGesuch(response);
                }
            });
    }

    private openGesuch(gesuch: TSGesuch): void {
        if (gesuch) {
            this.berechnungsManager.clear();
            this.gesuchModelManager.setGesuch(gesuch);
            this.$state.go('gesuch.fallcreation');
        }
    }
}
