import IRootScopeService = angular.IRootScopeService;
import {TSMessageEvent} from '../../../models/enums/TSErrorEvent';
import TSExceptionReport from '../../../models/TSExceptionReport';
import {TSErrorLevel} from '../../../models/enums/TSErrorLevel';
import {TSErrorType} from '../../../models/enums/TSErrorType';


export default class ErrorService {

    errors: Array<TSExceptionReport> = [];


    static $inject = ['$rootScope'];
    /* @ngInject */
    constructor(private $rootScope: IRootScopeService) {
    }


    /**
     * @returns {Array|DvbError}
     */
    getErrors(): Array<TSExceptionReport> {
        return angular.copy(this.errors);
    }

    /**
     * Clears all stored errors
     */
    clearAll() {
        this.errors = [];
        this.$rootScope.$broadcast(TSMessageEvent[TSMessageEvent.CLEAR]);
    }

    /** clear specific error
     * @param {string} msgKey
     */
    clearError(msgKey: string) {
        if (typeof msgKey !== 'string') {
            return;
        }

        let cleared = this.errors.filter(function (e: TSExceptionReport) {
            return e.msgKey !== msgKey;
        });

        if (cleared.length !== this.errors.length) {
            this.errors = cleared;
            this.$rootScope.$broadcast(TSMessageEvent[TSMessageEvent.ERROR_UPDATE], this.errors);
        }
    }

    /**
     * This can be used to add a client-siede global error
     * @param {string} msgKey translation key
     * @param {Object} [args] message parameters
     */
    addValidationError(msgKey: string, args?: any) {
        let err: TSExceptionReport = TSExceptionReport.createClientSideError(TSErrorLevel.SEVERE, msgKey, args);
        this.addDvbError(err);
    }

    containsError(dvbError: TSExceptionReport) {
        return this.errors.filter(function (e: TSExceptionReport) {
                return e.msgKey === dvbError.msgKey;
            }).length > 0;
    }

    addDvbError(dvbError: TSExceptionReport) {
        if (dvbError && dvbError.isValid()) {
            if(!this.containsError(dvbError)) {
                this.errors.push(dvbError);
                let udateEvent: TSMessageEvent = (dvbError.severity === TSErrorLevel.INFO ) ? TSMessageEvent.INFO_UPDATE : TSMessageEvent.ERROR_UPDATE;
                this.$rootScope.$broadcast(TSMessageEvent[udateEvent], this.errors);
            }
        } else{
            console.log('could not display received TSExceptionReport '+  dvbError);
        }
    }

    addMesageAsError(msg: string) {
        let error: TSExceptionReport = new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, msg, null);
        this.addDvbError(error);

    }

    addMesageAsInfo(msg: string) {
        let error: TSExceptionReport = new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.INFO, msg, null);
        this.addDvbError(error);
    }

    /**
     * @param {boolean} isValid when FALSE a new validationError is added. Otherwise the validationError is cleared
     * @param {string} msgKey
     * @param {Object} [args]
     */
    handleValidationError(isValid: boolean, msgKey: string, args?: any) {
        if (!!isValid) {
            this.clearError(msgKey);
        } else {
            this.addValidationError(msgKey, args);
        }
    }

    /**
     * @param {DvbError} dvbError adds a DvbError to the errors
     */
    handleError(dvbError: TSExceptionReport) {
        this.addDvbError(dvbError);
    }

    /**
     * @param {DvbError} dvbErrors adds all Errors to the errors service
     */
    handleErrors(dvbErrors: Array<TSExceptionReport>) {
        if (dvbErrors) {
            for (let err of dvbErrors) {
                this.addDvbError(err);
            }
        }

    }
}
