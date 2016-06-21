import {IComponentOptions} from 'angular';
import UserRS from '../../../core/service/userRS.rest';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSUser from '../../../models/TSUser';
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

    static $inject = ['UserRS', 'GesuchModelManager'];
    /* @ngInject */
    constructor(private userRS: UserRS, private gesuchModelManager: GesuchModelManager) {
        this.updateUserList();
    }

    public getVerantwortlicherFullName(): string {
        if (this.gesuchModelManager.gesuch && this.gesuchModelManager.gesuch.fall && this.gesuchModelManager.gesuch.fall.verantwortlicher) {
            return this.gesuchModelManager.gesuch.fall.verantwortlicher.getFullName();
        }
        return '';
    }

    public updateUserList(): void {
        this.userRS.getAllUsers().then((response) => {
            this.userList = angular.copy(response);
        });
    }

    /**
     * Sets the given user as the verantworlicher fuer den aktuellen Fall
     * @param verantwortlicher
     */
    public setVerantwortlicher(verantwortlicher: TSUser): void {
        if (verantwortlicher) {
            this.gesuchModelManager.setUserAsFallVerantwortlicher(verantwortlicher);
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
}
