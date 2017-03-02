import {IComponentOptions, IIntervalService} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStateService} from 'angular-ui-router';
import IPromise = angular.IPromise;
import Moment = moment.Moment;
let template = require('./dv-countdown.html');

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
    timerPromise: IPromise<any>;

    static $inject: any[] = ['$state', '$interval'];

    constructor(private $state: IStateService, private $interval: IIntervalService) {
        this.TSRoleUtil = TSRoleUtil;
        this.startTimer();
    }
    public goBackHome(): void {
        this.$state.go('gesuchstellerDashboard');
    }
    public getTimeLeft(): string {
        if (this.timer.asMinutes() < 5) {
            return this.timer.minutes() + ' : ' +  (this.timer.seconds() < 10 ? '0' + this.timer.seconds() : this.timer.seconds());
        } else {
            return '';
        }
    }

    public decrease() {
        this.timer.asMilliseconds() <= 0 ? this.stopTimer() : this.timer = moment.duration(this.timer.asSeconds() - 1, 'seconds');
    }
    public stopTimer(): void {
        if (this.timerPromise !== undefined) {
            this.$interval.cancel(this.timerPromise);
            this.timerPromise = undefined;
        } else {
            this.startTimer();
        }
    }
    public startTimer(): void {
        this.timer = moment.duration(5, 'minutes');
        this.timerPromise = this.$interval(this.decrease.bind(this), 10);
    }

    public isCurrentPageGSDashboard(): boolean {
        return (this.$state.current && this.$state.current.name === 'gesuchstellerDashboard');
    }
}
