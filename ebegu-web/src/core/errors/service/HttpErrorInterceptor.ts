/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import ErrorService from './ErrorService';
import {TSErrorType} from '../../../models/enums/TSErrorType';
import {TSErrorLevel} from '../../../models/enums/TSErrorLevel';
import TSExceptionReport from '../../../models/TSExceptionReport';
import IQService = angular.IQService;
import IRootScopeService = angular.IRootScopeService;
import IHttpInterceptor = angular.IHttpInterceptor;
import ILogService = angular.ILogService;

export default class HttpErrorInterceptor implements IHttpInterceptor {

    static $inject = ['$rootScope', '$q', 'ErrorService', '$log'];

    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService, private errorService: ErrorService,
                private $log: ILogService) {
    }


    public responseError = (response: any) => {
        if (response.status === 403) {
            this.errorService.addMesageAsError('ERROR_UNAUTHORIZED');
            return this.$q.reject(response);
        }
        //here we handle all errorcodes except 401 and 403, 401 is handeld in HttpAuthInterceptor
        if (response.status !== 401) {
            //here we could analyze the http status of the response. But instead we check if the  response has the format
            // of a known response such as errortypes such as violationReport or ExceptionReport and transform it
            //as such. If the response matches know expected format we create an unexpected error.
            let errors: Array<TSExceptionReport> = this.handleErrorResponse(response);
            this.errorService.handleErrors(errors);
            return this.$q.reject(errors);
        }
        return this.$q.reject(response);
    }

    /**
     * Tries to determine what kind of response data the error-response retunred and  handles the data object
     * of the response accordingly.
     *
     * The expected types are ViolationReport objects from JAXRS if there was a beanValidation error
     * or EbeguExceptionReports in case there was some other application exception
     *
     * @param response
     */
    private handleErrorResponse(response: any) {
        let errors: Array<TSExceptionReport>;
        // Alle daten loggen um das Debuggen zu vereinfachen
        // noinspection IfStatementWithTooManyBranchesJS
        if (this.isDataViolationResponse(response.data)) {
            errors = this.convertViolationReport(response.data);

        } else if (this.isDataEbeguExceptionReport(response.data)) {
            errors = this.convertEbeguExceptionReport(response.data);
        } else if (this.isFileUploadException(response.data)) {
            errors = [];
            errors.push(new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, 'ERROR_FILE_TOO_LARGE', response.data));
        } else {
            this.$log.error('ErrorStatus: "' + response.status + '" StatusText: "' + response.statusText + '"');
            this.$log.error('ResponseData:' + JSON.stringify(response.data));
            //the error objects is neither a ViolationReport nor a ExceptionReport. Create a generic error msg
            errors = [];
            errors.push(new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, 'ERROR_UNEXPECTED', response.data));
        }
        return errors;
    }

    private convertViolationReport(data: any): Array<TSExceptionReport> {
        let aggregatedExceptionReports: Array<TSExceptionReport> = [];
        return aggregatedExceptionReports.concat(this.convertToExceptionReport(data.parameterViolations))
            .concat(this.convertToExceptionReport(data.classViolations))
            .concat(this.convertToExceptionReport(data.fieldViolations))
            .concat(this.convertToExceptionReport(data.propertyViolations))
            .concat(this.convertToExceptionReport(data.returnValueViolations));

    }

    private convertToExceptionReport(violations: any): Array<TSExceptionReport> {
        let exceptionReports: Array<TSExceptionReport> = [];
        if (violations) {
            for (let violationEntry of violations) {
                let constraintType: string = violationEntry.constraintType;
                let message: string = violationEntry.message;
                let path: string = violationEntry.path;
                let value: string = violationEntry.value;
                let report: TSExceptionReport = TSExceptionReport.createFromViolation(constraintType, message, path, value);
                exceptionReports.push(report);
            }
        }
        return exceptionReports;

    }

    private convertEbeguExceptionReport(data: any) {
        let exceptionReport: TSExceptionReport = TSExceptionReport.createFromExceptionReport(data);
        let exceptionReports: Array<TSExceptionReport> = [];
        exceptionReports.push(exceptionReport);
        return exceptionReports;

    }

    /**
     *
     * checks if response data json-object has the keys required to be a violationReport (from jaxRS)
     * @param data object whose keys are checked
     * @returns {boolean} true if fields of violationReport are present
     */
    private isDataViolationResponse(data: any): boolean {
        //hier pruefen wir ob wir die Felder von org.jboss.resteasy.api.validation.ViolationReport.ViolationReport() bekommen
        if (data !== null && data !== undefined) {
            let hasParamViol: boolean = data.hasOwnProperty('parameterViolations');
            let hasClassViol: boolean = data.hasOwnProperty('classViolations');
            let hasfieldViol: boolean = data.hasOwnProperty('fieldViolations');
            let hasPropViol: boolean = data.hasOwnProperty('propertyViolations');
            let hasRetViol: boolean = data.hasOwnProperty('returnValueViolations');
            return hasParamViol && hasClassViol && hasfieldViol && hasPropViol && hasRetViol;
        }
        return false;

    }

    private isDataEbeguExceptionReport(data: any): boolean {
        if (data !== null && data !== undefined) {
            let hassErrorCodeEnum: boolean = data.hasOwnProperty('errorCodeEnum');
            let hasExceptionName: boolean = data.hasOwnProperty('exceptionName');
            let hasMethodName: boolean = data.hasOwnProperty('methodName');
            let hasStackTrace: boolean = data.hasOwnProperty('stackTrace');
            let hasTranslatedMessage: boolean = data.hasOwnProperty('translatedMessage');
            let hasCustomMessage: boolean = data.hasOwnProperty('customMessage');
            let hasArgumentList: boolean = data.hasOwnProperty('argumentList');
            return hassErrorCodeEnum && hasExceptionName && hasMethodName && hasStackTrace
                && hasTranslatedMessage && hasCustomMessage && hasArgumentList;
        }
        return false;

    }

    private isFileUploadException(response: String) {
        if (!response) {
            return false;
        }

        return response.indexOf('java.io.IOException: UT000020: Connection terminated as request was larger than ') > -1;
    }
}
