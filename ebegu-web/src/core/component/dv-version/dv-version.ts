/*
 * Copyright (c) 2017 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 *
 */
import {IComponentOptions, ILogService, IQService} from 'angular';
import {TSVersionCheckEvent} from '../../events/TSVersionCheckEvent';
import DateUtil from '../../../utils/DateUtil';
import HttpVersionInterceptor from '../../service/version/HttpVersionInterceptor';
import IRootScopeService = angular.IRootScopeService;
import IWindowService = angular.IWindowService;

let template = require('./dv-version.html');
require('./dv-version.scss');

export class DVVersionComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DVVersionController;
    controllerAs = 'vm';
}

export class DVVersionController {

    TSRoleUtil: any;

    private backendVersion: string;
    private frontendVersion: string;
    private buildTime: string;
    private showSingleVersion: boolean = true;
    private currentYear: number;

    static $inject = ['$rootScope', 'HttpVersionInterceptor', '$q', '$window', '$log'];

    constructor(private $rootScope: IRootScopeService, private httpVersionInterceptor: HttpVersionInterceptor, private $q: IQService,
        private $window: IWindowService, private $log: ILogService) {

    }

    $onInit() {

        this.backendVersion = this.httpVersionInterceptor.getBackendVersion();
        this.frontendVersion = this.httpVersionInterceptor.frontendVersion();
        this.buildTime = this.httpVersionInterceptor.getBuildTime();
        this.currentYear = DateUtil.currentYear();

        this.$rootScope.$on(TSVersionCheckEvent[TSVersionCheckEvent.VERSION_MISMATCH], () => {
            this.backendVersion = this.httpVersionInterceptor.getBackendVersion();
            this.updateDisplayVersion();
            let msg = 'Der Client (' + this.frontendVersion + ') hat eine andere Version als der Server('
                + this.backendVersion + '). Bitte laden sie die Seite komplett neu (F5)';
            this.$window.alert(msg);

        });

    }

    private updateDisplayVersion() {
        this.showSingleVersion = this.frontendVersion === this.backendVersion || this.backendVersion === null;
    }

}
