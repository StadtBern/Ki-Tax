import TSApplicationProperty from '../../../models/TSApplicationProperty';
import ApplicationPropertyRS from '../../service/applicationPropertyRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IHttpPromiseCallbackArg} from 'angular';
import * as template from './adminView.html';
import './adminView.less';

export class AdminViewComponentConfig implements angular.IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        applicationProperties: '<'
    };
    template: string = template;
    controller: any = AdminViewController;
    controllerAs: string = 'vm';
}

export class AdminViewController {
    static $inject = ['ApplicationPropertyRS', 'MAX_LENGTH'];

    length: number;
    applicationProperty: TSApplicationProperty;
    applicationPropertyRS: ApplicationPropertyRS;
    applicationProperties: TSApplicationProperty[];
    ebeguRestUtil: EbeguRestUtil;

    /* @ngInject */
    constructor(applicationPropertyRS: ApplicationPropertyRS, MAX_LENGTH: number, ebeguRestUtil: EbeguRestUtil) {
        this.length = MAX_LENGTH;
        this.applicationProperty = null;
        this.applicationProperties = null;
        this.applicationPropertyRS = applicationPropertyRS;
        this.ebeguRestUtil = ebeguRestUtil;
        //this.fetchList();
    }

    //fetchList() {
    //    return this.applicationPropertyRS.getAllApplicationProperties();
    //}

    submit(): void {
        //testen ob aktuelles property schon gespeichert ist
        if (this.applicationProperty.timestampErstellt) {
            this.applicationPropertyRS.update(this.applicationProperty.name, this.applicationProperty.value)
                .then((response) => {
                    var index = this.getIndexOfElementwithID(response.data);
                    var items: TSApplicationProperty[] = this.ebeguRestUtil.parseApplicationProperties(response.data);
                    if (items != null && items.length > 0) {
                        this.applicationProperties[index] = items[0];
                    }
                });

        } else {
            this.applicationPropertyRS.create(this.applicationProperty.name, this.applicationProperty.value)
                .then((response) => {
                    var items: TSApplicationProperty[] = this.ebeguRestUtil.parseApplicationProperties(response.data);
                    if (items != null && items.length > 0) {
                        //todo pruefen ob das item schon existiert hat wie oben
                        this.applicationProperties.push(items[0]);
                    }
                });
        }
        this.resetForm();
        //todo team fehlerhandling
    }

    removeRow(row: any): void { // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        this.applicationPropertyRS.remove(row.name).then((reponse: IHttpPromiseCallbackArg<any>) => {
            var index = this.applicationProperties.indexOf(row);
            if (index !== -1) {
                this.applicationProperties.splice(index, 1);
                this.resetForm();
            }
        });
    }

    createItem(): void {
        this.applicationProperty = new TSApplicationProperty('', '');
    }

    editRow(row: any): void { // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        this.applicationProperty = row;
    }

    resetForm(): void {
        this.applicationProperty = null;
    }

    private getIndexOfElementwithID(prop: TSApplicationProperty) {
        var idToSearch = prop.id;
        for (var i = 0; i < this.applicationProperties.length; i++) {
            if (this.applicationProperties[i].id === idToSearch) {
                return i;
            }
        }
        return -1;

    }
}
