import {IDirective, IDirectiveFactory} from 'angular';
import Moment = moment.Moment;
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import IDocumentService = angular.IDocumentService;
import {DvDialog} from "./directive/dv-dialog/dv-dialog";
import {FreigabeController} from "../../gesuch/dialog/FreigabeController";
import AuthServiceRS from "../../authentication/service/AuthServiceRS.rest";
import {TSRoleUtil} from "../../utils/TSRoleUtil";
let FREIGEBEN_DIALOG_TEMPLATE = require('../../gesuch/dialog/freigabe.html');

export class DVBarcodeListener implements IDirective {
    restrict = 'A';
    controller = DVBarcodeController;
    controllerAs = 'vm';

    static factory(): IDirectiveFactory {
        const directive = () => new DVBarcodeListener();
        directive.$inject = [];
        return directive;
    }
}

export class DVBarcodeController {

    static $inject: string[] = ['$document', '$timeout', 'DvDialog', 'AuthServiceRS'];

    private barcodeReading: boolean = false;
    private barcodeBuffer: string[] = [];
    private barcodeReadtimeout: any = null;

    /* @ngInject */
    constructor($document: IDocumentService, $timeout: ITimeoutService, dVDialog: DvDialog, authService: AuthServiceRS) {

        //TODO: (medu) how to add authenticated role dependent keypress handler?  Is this correct?
        if (authService.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole())) {

            $document.bind("keypress", (e) => {

                if (this.barcodeReading) {
                    if (e.key !== '§') {
                        this.barcodeBuffer.push(e.key);
                        console.log('Current buffer: ' + this.barcodeBuffer.join(""));
                    }
                }

                if (e.key === '§') {
                    if (this.barcodeReading) {
                        console.log("End Barcode read");

                        let barcodeRead: string = this.barcodeBuffer.join("");
                        console.log('Barcode read:' + barcodeRead);
                        barcodeRead = barcodeRead.replace("§", "");

                        let barcodeParts: string[] = barcodeRead.split("|");

                        let barcodeDocType: string = barcodeParts[0];
                        let barcodeDocFunction: string = barcodeParts[1];
                        let barcodeDocID: string = barcodeParts[2];

                        console.log("Barcode Doc Type: " + barcodeDocType);
                        console.log("Barcode Doc Function: " + barcodeDocFunction);
                        console.log("Barcode Doc ID: " + barcodeDocID);

                        this.barcodeBuffer = [];
                        $timeout.cancel(this.barcodeReadtimeout);

                        dVDialog.showDialogFullscreen(FREIGEBEN_DIALOG_TEMPLATE, FreigabeController, {
                            docID: barcodeDocID
                        }).then(() => {
                            //TODO: (medu) update view, for example when gesuch is visible in pending table
                        });

                    }
                    else {
                        console.log("Begin Barcode read");

                        this.barcodeReadtimeout = $timeout(() => {
                            this.barcodeReading = false;
                            console.log("End Barcode read");
                            console.log('Clearing buffer: ' + this.barcodeBuffer.join(""));
                            this.barcodeBuffer = [];
                        }, 1000);
                    }

                    this.barcodeReading = !this.barcodeReading;

                }
            });
        }
    }
}
