import {IComponentOptions, ILogService} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../service/gesuchRS.rest';
import IFormController = angular.IFormController;
let template = require('./kommentarView.html');
require('./kommentarView.less');

export class KommentarViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KommentarViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer den Kommentare
 */
export class KommentarViewController {


    static $inject: string[] = ['$log', 'GesuchModelManager', 'GesuchRS'];
    /* @ngInject */
    constructor(private $log: ILogService, private gesuchModelManager: GesuchModelManager, private gesuchRS: GesuchRS) {

    }

    getGesuch(): TSGesuch {
        return this.gesuchModelManager.gesuch;
    }

    saveBemerkung(): void {

        if (this.getGesuch().id) {
            console.log('saveBemerkungen');
            // Bemerkungen auf dem Gesuch werden nur gespeichert, wenn das gesuch schon persisted ist!
            this.gesuchRS.updateBemerkung(this.getGesuch().id, this.getGesuch().bemerkungen);
        } else {
            console.log('gesuch noch nicht persisted');
        }
    }

}
