import IComponentOptions = angular.IComponentOptions;
let template = require('./pendenzenSteueramtListView.html');

export class PendenzenSteueramtListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenSteueramtListViewController;
    controllerAs = 'vm';
}

export class PendenzenSteueramtListViewController {

    itemsByPage: number = 20;
    numberOfPages: number = 1;


    static $inject: string[] = [];

    constructor() {
    }

    $onInit() {
        this.initViewModel();
    }

    private initViewModel() {
    }
}
