import {IComponentOptions} from 'angular';
require('./dv-mobile-navigation-toggle.less');
let template = require('./dv-mobile-navigation-toggle.html');

export class DvMobileNavigationToggleComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvMobileNavigationToggleController;
    controllerAs = 'vm';
}

export class DvMobileNavigationToggleController {

    static $inject: any[] = ['$mdSidenav'];

    constructor(private $mdSidenav: ng.material.ISidenavService) {
    }

    public toggleSidenav(componentId: string): void {
        this.$mdSidenav(componentId).toggle();
    }

    public closeSidenav(componentId: string): void {
        this.$mdSidenav(componentId).close();
    }
}
