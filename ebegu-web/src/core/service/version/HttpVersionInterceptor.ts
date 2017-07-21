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
import {IHttpInterceptor, ILogService, IQService, IRootScopeService} from 'angular';
import {TSVersionCheckEvent} from '../../events/TSVersionCheckEvent';

/**
 * this interceptor boradcasts a  VERSION_MATCH or VERSION_MISMATCH event whenever a rest service responds
 */
export default class HttpVersionInterceptor implements IHttpInterceptor {

    private backendVersion: string;

    static $inject = ['$rootScope', '$q', 'CONSTANTS', '$log'];

    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService, private CONSTANTS: any, private $log: ILogService) {
    }

    //interceptor methode
    public response = (response: any) => {
        if (response.headers && response.config && response.config.url.indexOf(this.CONSTANTS.REST_API) === 0 && !response.config.cache) {
            this.updateBackendVersion(response.headers('x-ebegu-version'));
        }

        return response;
    }

    /**
     * @param {*} newVersion
     */
    private updateBackendVersion(newVersion: string) {
        if (newVersion !== this.backendVersion) {
            this.backendVersion = newVersion;
            if (!this.hasVersionCompatibility(this.frontendVersion(), this.backendVersion)) {
                this.$log.warn('Versions of Frontend and Backend do not match');
                this.$rootScope.$broadcast(TSVersionCheckEvent[TSVersionCheckEvent.VERSION_MISMATCH]);
            } else {
                //could throw match event here but currently there is no action we want to perform when it matches
            }
        }
    }

    private hasVersionCompatibility(frontendVersion: string, backendVersion: string): boolean {
        // Wir erwarten, dass die Versionsnummern im Frontend und Backend immer synchronisiert werden
        return frontendVersion === backendVersion;
    }

    /**
     * @return {string}
     */
    public frontendVersion(): string {
        return VERSION;
    }

    public getBackendVersion(): string {
        return this.backendVersion;
    }

    public getBuildTime(): string {
        return BUILDTSTAMP;
    }

}
