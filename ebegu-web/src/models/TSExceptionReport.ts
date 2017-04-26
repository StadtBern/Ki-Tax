import {TSErrorType} from './enums/TSErrorType';
import {TSErrorLevel} from './enums/TSErrorLevel';
import {TSErrorAction} from './enums/TSErrorAction';

export default class TSExceptionReport {

    private _type: TSErrorType;
    private _severity: TSErrorLevel;
    private _msgKey: string;

    //fields for ExceptionReport entity
    private _exceptionName: string;
    private _methodName: string;
    private _translatedMessage: string;
    private _customMessage: string;
    private _errorCodeEnum: string;
    private _stackTrace: string;
    private _argumentList: any;

    // fields for ViolationReports
    private _path: string;

    private _action: TSErrorAction = undefined;


    /**
     *
     * @param type Type of the Error
     * @param severity Severity of the Error
     * @param msgKey This is the message key of the error. can also be the message itself
     * @param args
     */
    constructor(type: TSErrorType, severity: TSErrorLevel, msgKey: string, args: any) {
        this._type = type || null;
        this._severity = severity || null;
        this._msgKey = msgKey || null;
        this._argumentList = args || [];
        this._action = undefined;
    }


    isConstantValue(constant: any, value: any) {
        let keys = Object.keys(constant);
        for (let i = 0; i < keys.length; i++) {
            if (value === constant[keys[i]]) {
                return true;
            }
        }

        return false;
    }

    isValid() {
        let validType = this.isConstantValue(TSErrorType, this.type);
        let validSeverity = this.isConstantValue(TSErrorLevel, this.severity);
        let validMsgKey = typeof this.msgKey === 'string' && this.msgKey.length > 0;

        return validType && validSeverity && validMsgKey;
    };

    isInternal() {
        return this.type === TSErrorType.INTERNAL;
    };


    get type(): TSErrorType {
        return this._type;
    }

    set type(value: TSErrorType) {
        this._type = value;
    }

    get severity(): TSErrorLevel {
        return this._severity;
    }

    set severity(value: TSErrorLevel) {
        this._severity = value;
    }

    get msgKey(): string {
        return this._msgKey;
    }

    set msgKey(value: string) {
        this._msgKey = value;
    }

    get exceptionName(): string {
        return this._exceptionName;
    }

    set exceptionName(value: string) {
        this._exceptionName = value;
    }

    get methodName(): string {
        return this._methodName;
    }

    set methodName(value: string) {
        this._methodName = value;
    }

    get translatedMessage(): string {
        return this._translatedMessage;
    }

    set translatedMessage(value: string) {
        this._translatedMessage = value;
    }

    get customMessage(): string {
        return this._customMessage;
    }

    set customMessage(value: string) {
        this._customMessage = value;
    }

    get errorCodeEnum(): string {
        return this._errorCodeEnum;
    }

    set errorCodeEnum(value: string) {
        this._errorCodeEnum = value;
    }

    get stackTrace(): string {
        return this._stackTrace;
    }

    set stackTrace(value: string) {
        this._stackTrace = value;
    }

    get argumentList(): any {
        return this._argumentList;
    }

    set argumentList(value: any) {
        this._argumentList = value;
    }


    get path(): string {
        return this._path;
    }

    set path(value: string) {
        this._path = value;
    }

    get action(): TSErrorAction {
        return this._action;
    }

    set action(value: TSErrorAction) {
        this._action = value;
    }

    public static createFromViolation(constraintType: string, message: string, path: string, value: string): TSExceptionReport {
        let report: TSExceptionReport = new TSExceptionReport(TSErrorType.VALIDATION, TSErrorLevel.SEVERE, message, value);
        report.path = path;
        //hint: here we could also pass along the path to the Exception Report
        return report;
    }

    public static createClientSideError(severity: TSErrorLevel, msgKey: string, args: any): TSExceptionReport {
        let report: TSExceptionReport = new TSExceptionReport(TSErrorType.CLIENT_SIDE, severity, msgKey, args);
        return report;
    }

    /**
     * takes a data Object that matches the fields of a EbeguExceptionReport and transforms them to a TSExceptionReport.
     * @param data
     * @returns {TSExceptionReport}
     */
    public static createFromExceptionReport(data: any) {
        let msgToDisp =  data.translatedMessage || data.customMessage || 'ERROR_UNEXPECTED_EBEGU_RUNTIME';
        let exceptionReport: TSExceptionReport = new TSExceptionReport(TSErrorType.BADREQUEST, TSErrorLevel.SEVERE, msgToDisp, data.argumentList);
        exceptionReport.errorCodeEnum = data.errorCodeEnum;
        exceptionReport.exceptionName = data.exceptionName;
        exceptionReport.methodName = data.methodName;
        exceptionReport.stackTrace = data.stackTrace;
        exceptionReport.translatedMessage = msgToDisp;
        exceptionReport.customMessage = data.customMessage;
        exceptionReport.argumentList = data.argumentList;
        return exceptionReport;

    }
}
