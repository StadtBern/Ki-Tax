import TSApplicationProperty from '../../../models/TSApplicationProperty';
import {ApplicationPropertyRS} from '../../service/applicationPropertyRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IHttpPromiseCallbackArg, IComponentOptions} from 'angular';
import {ReindexRS} from '../../service/reindexRS.rest';
import AbstractAdminViewController from '../../abstractAdminView';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import IScope = angular.IScope;
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

export class AdminViewController extends AbstractAdminViewController {
    static $inject = ['ApplicationPropertyRS', 'MAX_LENGTH', 'EbeguRestUtil', 'ReindexRS', 'AuthServiceRS'];

    length: number;
    applicationProperty: TSApplicationProperty;
    applicationPropertyRS: ApplicationPropertyRS;
    applicationProperties: TSApplicationProperty[];
    ebeguRestUtil: EbeguRestUtil;


    /* @ngInject */
    constructor(applicationPropertyRS: ApplicationPropertyRS, MAX_LENGTH: number, ebeguRestUtil: EbeguRestUtil,
                private reindexRS: ReindexRS, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
        this.length = MAX_LENGTH;
        this.applicationProperty = undefined;
        this.applicationPropertyRS = applicationPropertyRS;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    submit(): void {
        //testen ob aktuelles property schon gespeichert ist
        if (this.applicationProperty.isNew()) {
            this.applicationPropertyRS.update(this.applicationProperty.name, this.applicationProperty.value);

        } else {
            this.applicationPropertyRS.create(this.applicationProperty.name, this.applicationProperty.value);
        }
        this.applicationProperty = undefined;
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
        this.applicationPropertyRS.getAllApplicationProperties().then(response => {
            this.applicationProperties = response;
        });
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
