import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
import {IAuthenticationStateParams} from './authentication.route';
import IWindowService = angular.IWindowService;
import IHttpParamSerializer = angular.IHttpParamSerializer;
import ITimeoutService = angular.ITimeoutService;
let template = require('./authentication.html');
require('./authentication.less');

export class AuthenticationComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = AuthenticationListViewController;
    controllerAs = 'vm';
}

export class AuthenticationListViewController {


    static $inject: string[] = ['$state', '$stateParams', '$window', '$httpParamSerializer', '$timeout', 'AuthServiceRS'];

    private redirectionUrl: string = '/ebegu/saml2/jsp/fedletSSOInit.jsp?';
    private relayString: string;
    private redirectionHref: string;
    private redirecting: boolean;
    private countdown: number = 5;

    constructor(private $state: IStateService, private $stateParams: IAuthenticationStateParams,
                private $window: IWindowService, private $httpParamSerializer: IHttpParamSerializer,
                private $timeout: ITimeoutService, private authService: AuthServiceRS) {
        //wir leiten hier mal direkt weiter, theoretisch koennte man auch eine auswahl praesentieren

        this.relayString = this.$stateParams.relayPath ? (this.$stateParams.relayPath + '?sendRedirectForValidationNow=true') : '';
        this.redirectionHref = this.createRedirectionURL();
        if (this.$stateParams.type !== undefined && this.$stateParams.type === 'logout') {
            this.redirectTolIAMForLogout();
        } else {
            this.redirectTolIAMForLogin();
        }
    }

    public redirectTolIAMForLogin(): void {
        this.redirecting = true;
        this.$timeout(this.doCountdown, 1000);
        this.$timeout(this.redirect, 5000);
    }

    public createRedirectionURL(): string {
        //todo team? der relayPath sollte besser keine absolute url sein sondern wir sollten uns hier
        // unter einr bestimmten nummer  merken was die url war (in persistence oder session oder cookie) und
        //dann nur diese nummer an das iam geben, so kann man als hacker garantiert den relayState nicht misbrauchen
        // alternativ koennten wir eine domain whitelist fuehren oder sowas
        //idpEntityID id koennte allenfalls als property hinterlegt werden?, der rst ist glaube ich konstant
        let queryParams = {
            'metaAlias': '/egov_bern/sp',
            'idpEntityID': 'https://elogin-test.bern.ch/am',
            'binding': 'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST',
            'RelayState': this.relayString
        };

        let urlToGoTo = this.redirectionUrl + this.$httpParamSerializer(queryParams);
        return urlToGoTo;
    }

    public redirect = () => {

        let urlToGoTo = this.createRedirectionURL();
        console.log('redirecting to login', urlToGoTo);

        this.$window.open(urlToGoTo, '_self');
    };

    private redirectTolIAMForLogout() {
        console.log('reached login page from logout request');

    }

    private doCountdown = () => {
        if (this.countdown > 0) {
            this.countdown--;
            this.$timeout(this.doCountdown, 1000);
        }

    }
}
