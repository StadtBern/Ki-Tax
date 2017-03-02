import {IComponentOptions, IIntervalService} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStateService} from 'angular-ui-router';
import {TSHTTPEvent} from '../../events/TSHTTPEvent';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import IPromise = angular.IPromise;
import Moment = moment.Moment;
import IRootScopeService = angular.IRootScopeService;
let template = require('./dv-countdown.html');
let dialogTemplate = require('../../../gesuch/dialog/okDialogTemplate.html');

export class DvCountdownComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvCountdownController;
    controllerAs = 'vm';
}

export class DvCountdownController {

    TSRoleUtil: any;
    timer: moment.Duration;
    timerInterval: IPromise<any>;

    static $inject: any[] = ['AuthServiceRS', '$state', '$interval', '$rootScope', 'DvDialog'];

    constructor(private authServiceRS: AuthServiceRS, private $state: IStateService, private $interval: IIntervalService, private $rootScope: IRootScopeService, private DvDialog: DvDialog) {
        this.TSRoleUtil = TSRoleUtil;
        this.$rootScope.$on(TSHTTPEvent[TSHTTPEvent.REQUEST_FINISHED], () => {
            if (this.authServiceRS.isRole(TSRole.GESUCHSTELLER)) {
                if (this.timerInterval === undefined) {
                    this.startTimer();
                } else {
                    this.resetTimer();
                }
            } else {
                    this.cancelInterval();
            }
        });

    }

    public getTimeLeft(): string {
        if (this.timer) {
            if (this.timer.asMinutes() < 5) {
                return this.timer.minutes() + ' : ' + (this.timer.seconds() < 10 ? '0' + this.timer.seconds() : this.timer.seconds());
            }
        }
        return '';
    }

    public decrease() {
        this.timer.asMilliseconds() <= 0 ? this.stopTimer() : this.timer = moment.duration(this.timer.asSeconds() - 1, 'seconds');
    }
    //Fuer Testzwecke hier auf 5 setzen, ab dann erscheint der Countdown naemlich
    public resetTimer(): void {
        this.timer = moment.duration(10, 'minutes');
    }

    public stopTimer(): void {
        this.cancelInterval();
        this.DvDialog.showDialog(dialogTemplate, OkDialogController, {
            title: 'Bitte fahren Sie mit der Bearbeitung fort',
        });
    }

    public cancelInterval(): void {
        if (this.timerInterval !== undefined) {
            this.$interval.cancel(this.timerInterval);
            this.timerInterval = undefined;
            this.timer = undefined;
        }
    }

    public startTimer(): void {
        this.resetTimer();
        //Fuer Testzwecke hier auf 10 oder 100 setzen
        this.timerInterval = this.$interval(this.decrease.bind(this), 1000);
    }
}
