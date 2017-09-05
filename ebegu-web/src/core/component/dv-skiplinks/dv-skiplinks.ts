import {IComponentOptions} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStateService} from 'angular-ui-router';
import {ShowTooltipController} from '../../../gesuch/dialog/ShowTooltipController';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';

let template = require('./dv-skiplinks.html');
let showKontaktTemplate = require('../../../gesuch/dialog/showKontaktTemplate.html');

export class DvSkiplinksComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvSkiplinksController;
    controllerAs = 'vm';
}

export class DvSkiplinksController {

    TSRoleUtil: any;

    static $inject: any[] = ['$state', 'DvDialog'];

    constructor(private $state: IStateService, private DvDialog: DvDialog) {
        this.TSRoleUtil = TSRoleUtil;
    }

    public goBackHome(): void {
        this.$state.go('gesuchstellerDashboard');
    }

    public isCurrentPageGSDashboard(): boolean {
        return (this.$state.current && this.$state.current.name === 'gesuchstellerDashboard');
    }

    public isCurrentPageGesuch(): boolean {
        return (this.$state.current && this.$state.current.name !== 'gesuchstellerDashboard' && this.$state.current.name !== 'alleVerfuegungen' && this.$state.current.name !== 'mitteilungen');
    }

    public focusLink(a: string): void {
        angular.element(a).focus();
    }

    public focusToolbar(): void {
        angular.element('.gesuch-toolbar-gesuchsteller.desktop button').first().focus();
    }

    public focusSidenav(): void {
        angular.element('.sidenav.gesuchMenu button').first().focus();
    }

    public showKontakt(): void {
        this.DvDialog.showDialog(showKontaktTemplate, ShowTooltipController, {
            title: '',
            text: '<span>Jugendamt</span><br>'
            + '<span>Effingerstrasse 21</span><br>'
            + '<span>3008 Bern</span><br>'
            + '<a href="tel:0313215115"><span>031 321 51 15</span></a><br>'
            + '<a href="mailto:kinderbetreuung@bern.ch"><span>kinderbetreuung@bern.ch</span></a>'
        });
    }
}
