import TSApplicationProperty from '../../../models/TSApplicationProperty';
import {ApplicationPropertyRS} from '../../service/applicationPropertyRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IHttpPromiseCallbackArg, IComponentOptions} from 'angular';
import {ReindexRS} from '../../service/reindexRS.rest';
require('./adminView.less');
let template = require('./adminView.html');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let linkDialogTempl = require('../../../gesuch/dialog/linkDialogTemplate.html');

export class AdminViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        applicationProperties: '<'
    };
    template: string = template;
    controller: any = AdminViewController;
    controllerAs: string = 'vm';
}

export class AdminViewController {
    static $inject = ['ApplicationPropertyRS', 'MAX_LENGTH', 'EbeguRestUtil', 'ReindexRS'];

    length: number;
    applicationProperty: TSApplicationProperty;
    applicationPropertyRS: ApplicationPropertyRS;
    applicationProperties: TSApplicationProperty[];
    ebeguRestUtil: EbeguRestUtil;


    /* @ngInject */
    constructor(applicationPropertyRS: ApplicationPropertyRS, MAX_LENGTH: number, ebeguRestUtil: EbeguRestUtil,
                private reindexRS: ReindexRS) {
        this.length = MAX_LENGTH;
        this.applicationProperty = undefined;
        this.applicationPropertyRS = applicationPropertyRS;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    submit(): void {
        //testen ob aktuelles property schon gespeichert ist
        if (this.applicationProperty.isNew()) {
            this.applicationPropertyRS.update(this.applicationProperty.name, this.applicationProperty.value)
                .then((response: any) => {
                    let index = this.getIndexOfElementwithID(response.data);
                    let items: TSApplicationProperty[] = this.ebeguRestUtil.parseApplicationProperties(response.data);
                    if (items != null && items.length > 0) {
                        this.applicationProperties[index] = items[0];
                    }
                });

        } else {
            this.applicationPropertyRS.create(this.applicationProperty.name, this.applicationProperty.value)
                .then((response: any) => {
                    let items: TSApplicationProperty[] = this.ebeguRestUtil.parseApplicationProperties(response.data);
                    if (items != null && items.length > 0) {
                        this.applicationProperties = this.applicationProperties.concat(items[0]);
                    }
                });
        }
        this.resetForm();
    }

    removeRow(row: TSApplicationProperty): void {
        this.applicationPropertyRS.remove(row.name).then((reponse: IHttpPromiseCallbackArg<any>) => {
            let index = this.applicationProperties.indexOf(row);
            if (index !== -1) {
                this.applicationProperties.splice(index, 1);
                this.resetForm();
            }
        });
    }

    createItem(): void {
        this.applicationProperty = new TSApplicationProperty('', '');
    }

    editRow(row: TSApplicationProperty): void {
        this.applicationProperty = row;
    }

    resetForm(): void {
        this.applicationProperty = undefined;
    }

    private getIndexOfElementwithID(prop: TSApplicationProperty) {
        let idToSearch = prop.id;
        for (let i = 0; i < this.applicationProperties.length; i++) {
            if (this.applicationProperties[i].id === idToSearch) {
                return i;
            }
        }
        return -1;

    }

    public startReindex() {
        return this.reindexRS.reindex();
    }
}
