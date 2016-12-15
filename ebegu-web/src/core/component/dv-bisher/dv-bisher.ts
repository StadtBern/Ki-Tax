import {IComponentOptions} from 'angular';
import * as moment from 'moment';
import DateUtil from '../../../utils/DateUtil';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
import ILogService = angular.ILogService;
let template =  require('./dv-bisher.html');
require('./dv-bisher.less');

/**
 * Verwendung:
 * - gs: Wert, den der Gesuchsteller eingegeben hat
 * - ja: Wert, den das Jugendamt korrigiert
 *
 * Attribute fuer Eingaben-Blocks (zusammengehoerende Felder):
 * - specificBisherText: Falls einfach der GS-Wert als Bisher angezeigt werden soll. Z.B. Checkbox "Fachstelle": Wir
 *      wollen als Bisher-Text nicht "Ja", sondern "Fachstelle X, mit 50%, vom 01.01.2015 bis 31.12.2015"
 * - blockExisted: Gibt an, ob der Block ueberhaupt vom GS ausgefuellt wurde. Falls nein, muss *jede* Eingabe des JA
 *      als Korrektur angezeigt werden
 * - showBisherIfNone: Zeigt, ob der Bisherwert (bzw. "Keine Eingabe") auch angezeigt werden soll, wenn es keinen Bisher-Wert
 *      gibt. Normalerweise wollen wir das. Ausnahme sind Blocks, wo wir das "Keine Eingabe" *pro Block* anzeigen wollen
 *      und nicht unter jedem Feld.
 */
export class DvBisherComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gs: '<',
        ja: '<',
        specificBisherText: '<',
        blockExisted: '<',
        showBisherIfNone: '<',
    };
    template = template;
    controller = DvBisher;
    controllerAs = 'vm';
}

export class DvBisher {

    static $inject = ['GesuchModelManager', '$translate', '$log'];

    gs: any;
    ja: any;
    showBisherIfNone: boolean;  // sollen die korrekturen des jugendamts angezeigt werden wenn im GS container kein wert ist
    specificBisherText: string;
    blockExisted: boolean;


    /* @ngInject */
    constructor(private gesuchModelManager: GesuchModelManager, private $translate: ITranslateService, private $log: ILogService) {
        if (this.showBisherIfNone === undefined) {//wenn nicht von aussen gesetzt auf true
            this.showBisherIfNone = true;
        }
    }

    public getBisher() : string {
        if (this.specificBisherText) {
            // War es eine Loeschung, oder ein Hinzufuegen?
            if (this.hasBisher()) {
                return this.specificBisherText;
            } else {
                return this.$translate.instant('LABEL_KEINE_ANGABE');
            }
        } else if (this.gs instanceof moment) {
            return  DateUtil.momentToLocalDateFormat(this.gs, 'DD.MM.YYYY');
        } else if (this.gs === true) {
            return this.$translate.instant('LABEL_JA');
        } else if (this.gs === false) {
            return this.$translate.instant('LABEL_NEIN');
        } else if (!this.hasBisher()) {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        } else {
            return this.$translate.instant(this.gs);
        }
    }

    public hasBisher() : boolean {
        return !this.isEmpty(this.gs);
    }

    public showBisher() : boolean {
        return ((this.showBisherIfNone || this.blockExisted === true) || this.hasBisher()) && this.gesuchModelManager.isKorrekturModusJugendamt();
    }

    public equals(gs: any, ja: any) : boolean {
        if (gs instanceof moment) {
            return this.equals(DateUtil.momentToLocalDateFormat(gs, 'DD.MM.YYYY'), DateUtil.momentToLocalDateFormat(ja, 'DD.MM.YYYY'));
        }
        return gs === ja || (this.isEmpty(gs) && this.isEmpty(ja));//either they are equal or both are a form of empty
    }

    private isEmpty(val: any): boolean {
        return val === undefined || val === null || val === '';
    }
}
