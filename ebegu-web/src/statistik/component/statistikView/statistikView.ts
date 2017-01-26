import {IComponentOptions} from 'angular';
import EbeguUtil from '../../../utils/EbeguUtil';
import {IStateService} from 'angular-ui-router';

let template = require('./statistikView.html');
require('./statistikView.less');

export class StatistikViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = StatistikViewController;
    controllerAs = 'vm';
}

export class StatistikViewController {

    static $inject: string[] = ['EbeguUtil', '$state'];
    accordion: string = '';

    constructor(private ebeguUtil: EbeguUtil, private $state: IStateService) {
        this.initViewModel();
    }

    private initViewModel() {
    }
   /* public toggleAccordion( i: string ): void {
        if (this.isAccordionOpen(i)) {
            this.accordion.splice(this.accordion.indexOf(i), 1);
        } else {
            this.accordion = [];
            this.accordion.push(i);
        }
    }
    public isAccordionOpen( i: string ): boolean {
        return this.accordion.indexOf(i) > -1;
    }*/
     public toggleAccordion( i: string ): void {
     if (this.isAccordionOpen(i)) {
     this.accordion = '';
     } else {
     this.accordion = i;
     }
     }
     public isAccordionOpen( i: string ): boolean {
     return this.accordion === i;
     }
}
