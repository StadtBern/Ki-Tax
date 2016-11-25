import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
import {IAuthenticationStateParams} from './authentication.route';
import IWindowService = angular.IWindowService;
import IHttpParamSerializer = angular.IHttpParamSerializer;
import ITimeoutService = angular.ITimeoutService;
import ILocationService = angular.ILocationService;
let template = require('./authentication.html');
require('./authentication.less');

export class AuthenticationComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = AuthenticationListViewController;
    controllerAs = 'vm';
}

export class AuthenticationListViewController {


    static $inject: string[] = ['$state', '$stateParams', '$window', '$httpParamSerializer', '$timeout', 'AuthServiceRS'
        , '$location'];

    private redirectionUrl: string = '/ebegu/saml2/jsp/fedletSSOInit.jsp';
    private relayString: string;
    private redirectionHref: string;

    private logoutHref: string;
    private redirecting: boolean;
    private countdown: number = 0;


    constructor(private $state: IStateService, private $stateParams: IAuthenticationStateParams,
                private $window: IWindowService, private $httpParamSerializer: IHttpParamSerializer,
                private $timeout: ITimeoutService, private authService: AuthServiceRS, private $location: ILocationService) {
        //wir leiten hier mal direkt weiter, theoretisch koennte man auch eine auswahl praesentieren
        this.relayString = angular.copy(this.$stateParams.relayPath ? (this.$stateParams.relayPath) : '');
        this.authService.initSSOLogin(this.relayString).then((response) => {
            this.redirectionUrl = response;
            this.redirectionHref = response;
            if (this.$stateParams.type !== undefined && this.$stateParams.type === 'logout') {
                this.doLogout();
            } else {
                this.redirecting = true;
                if (this.countdown > 0) {this.$timeout(this.doCountdown, 1000);}
                this.$timeout(this.redirect, this.countdown * 1000);
            }
        });

        if (this.authService.getPrincipal()) {  // wenn logged in
            this.authService.initSingleLogout(this.getBaseURL())
                .then((responseLogut) => {
                    this.logoutHref = responseLogut;
                });
        }
    }

    public getBaseURL(): string {
        //let port = (this.$location.port() === 80 || this.$location.port() === 443) ? '' : ':' + this.$location.port();
        let absURL = this.$location.absUrl();
        let index = absURL.indexOf(this.$location.url());
        let result = absURL;
        if (index !== -1) {
            result = absURL.substr(0, index);
            let hashindex = result.indexOf('#');
            if (hashindex !== -1) {
                result = absURL.substr(0, hashindex);
            }

        }
        return result;
    }

    public singlelogout() {
        this.authService.logoutRequest().then(() => {
            if (this.logoutHref !== '' || this.logoutHref === undefined) {
                this.$window.open(this.logoutHref, '_self');
            } else {
                this.$state.go('start');  // wenn wir nicht in iam ausloggen gehen wir auf start
            }
        });
    }

    public isLoggedId(): boolean {
        console.log('logged in principal', this.authService.getPrincipal());
        return this.authService.getPrincipal() ? true : false;
    }


    public redirect = () => {
        let urlToGoTo = this.redirectionHref;
        console.log('redirecting to login', urlToGoTo);

        this.$window.open(urlToGoTo, '_self');
    };

    /**
     * triggered einen logout, fuer iam user sowohl in iam als auch in ebegu,
     * bei lokalen benutzern wird auch nur bei uns ausgeloggt
     */
    private doLogout() {
        if (this.authService.getPrincipal()) {  // wenn logged in
            this.authService.initSingleLogout(this.getBaseURL()).then((responseLogut) => {
                this.logoutHref = responseLogut;
                this.singlelogout();
            });
        }


    }

    private doCountdown = () => {
        if (this.countdown > 0) {
            this.countdown--;
            this.$timeout(this.doCountdown, 1000);
        }

    }
}
