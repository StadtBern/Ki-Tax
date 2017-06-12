import IComponentOptions = angular.IComponentOptions;
import {TSMessageEvent} from '../../../../models/enums/TSErrorEvent';
import ErrorService from '../../service/ErrorService';
import TSExceptionReport from '../../../../models/TSExceptionReport';
import {TSErrorLevel} from '../../../../models/enums/TSErrorLevel';
import IScope = angular.IScope;
import {TSRoleUtil} from '../../../../utils/TSRoleUtil';
import {TSErrorAction} from '../../../../models/enums/TSErrorAction';
import {DvDialog} from '../../../directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../../../gesuch/dialog/RemoveDialogController';
import GesuchRS from '../../../../gesuch/service/gesuchRS.rest';
let templ = require('./dvb-error-messages-panel.html');
let style = require('./dvb-error-messages-panel.less');
let removeDialogTemplate = require('../../../../gesuch/dialog/removeDialogTemplate.html');

export class DvErrorMessagesPanelComponentConfig implements IComponentOptions {

    scope = {};
    template = templ;
    controller = DvErrorMessagesPanelComponent;
    controllerAs = 'vm';
}

/**
 * component that can display error messages
 */
export class DvErrorMessagesPanelComponent {

    errors: Array<TSExceptionReport> = [];
    TSRoleUtil: any;


    static $inject: string[] = ['$scope', 'ErrorService', 'DvDialog', 'GesuchRS'];
    /* @ngInject */
    constructor(private $scope: IScope, private errorService: ErrorService, private dvDialog: DvDialog,
                private gesuchRS: GesuchRS) {
    }

    $onInit() {
        this.TSRoleUtil = TSRoleUtil;
        this.$scope.$on(TSMessageEvent[TSMessageEvent.ERROR_UPDATE], this.displayMessages);
        this.$scope.$on(TSMessageEvent[TSMessageEvent.INFO_UPDATE], this.displayMessages);
        this.$scope.$on(TSMessageEvent[TSMessageEvent.CLEAR], () => {
            this.errors = [];
        });
    }

    displayMessages = (event: any, errors: Array<TSExceptionReport>) => {
        this.errors = errors;
        this.show();
    }

    private executeAction(error: TSExceptionReport): void {
        if (error.action) {
            if (error.action === TSErrorAction.REMOVE_ONLINE_MUTATION && error.argumentList.length > 0) {
                this.removeOnlineMutation(error.objectId, error.argumentList[0]);

            } else if (error.action === TSErrorAction.REMOVE_ONLINE_ERNEUERUNGSGESUCH && error.argumentList.length > 0) {
                this.removeOnlineErneuerungsgesuch(error.objectId, error.argumentList[0]);
            }
        }
        this.clear();
    }

    private removeOnlineMutation(objectId: string, gesuchsperiodeId: string): void {
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            title: 'REMOVE_ONLINE_MUTATION_CONFIRMATION',
            deleteText: 'REMOVE_ONLINE_MUTATION_BESCHREIBUNG'
        }).then(() => {   //User confirmed removal
            this.gesuchRS.removeOnlineMutation(objectId, gesuchsperiodeId).then((response) => {});
        });
    }

    private removeOnlineErneuerungsgesuch(objectId: string, gesuchsperiodeId: string): void {
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            title: 'REMOVE_ONLINE_ERNEUERUNGSGESUCH_CONFIRMATION',
            deleteText: 'REMOVE_ONLINE_ERNEUERUNGSGESUCH_BESCHREIBUNG'
        }).then(() => {   //User confirmed removal
            this.gesuchRS.removeOnlineFolgegesuch(objectId, gesuchsperiodeId).then((response) => {});
        });
    }

    private isActionDefined(error: TSExceptionReport): boolean {
        return error.action !== undefined && error.action !== null;
    }

    show() {
        // this.element.show();
        angular.element('dvb-error-messages-panel').show();     //besser als $element injection fuer tests
    }

    clear() {
        this.errorService.clearAll();
    }

    messageStyle(): string {
        for (let error of this.errors) {
            if (error.severity !== TSErrorLevel.INFO) {
                return '';
            }
        }
        return 'info';
    }

}


