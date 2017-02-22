import {IHttpParamSerializer, ILogService} from 'angular';

export class ReportRS {
    serviceURL: string;
    httpParamSerializer: IHttpParamSerializer;
    log: ILogService;

    static $inject = ['$httpParamSerializer', 'REST_API', '$log', '$window'];
    /* @ngInject */
    constructor($httpParamSerializer: IHttpParamSerializer, REST_API: string, $log: ILogService, private $window: ng.IWindowService) {
        this.serviceURL = REST_API + 'reporting';
        this.httpParamSerializer = $httpParamSerializer;
        this.log = $log;
    }

    public getGesuchStichtagReportExcel(dateTimeStichtag: string, gesuchPeriodeID: string): void {

        let reportParams: string = this.httpParamSerializer({
            dateTimeStichtag: dateTimeStichtag,
            gesuchPeriodeID: gesuchPeriodeID
        });

        this.startDownload(this.serviceURL + '/excel/gesuchStichtag?' + reportParams, false);
    }

    public getGesuchZeitraumReportExcel(dateTimeFrom: string, dateTimeTo: string, gesuchPeriodeID: string): void {

        let reportParams: string = this.httpParamSerializer({
            dateTimeFrom: dateTimeFrom,
            dateTimeTo: dateTimeTo,
            gesuchPeriodeID: gesuchPeriodeID
        });

        this.startDownload(this.serviceURL + '/excel/gesuchZeitraum?' + reportParams, false);
    }

    private startDownload(href: string, attachment: boolean): boolean {

        if (attachment) {
            // add MatrixParam for to download file instead of inline
            this.download(href);
        } else {
            let win =this.$window.open(href, '_blank');
            if (!win) {
                let warn: string = 'Popup-Blocker scheint eingeschaltet zu sein. ' +
                    'Dadurch kann das Dokument im Browser nicht angezeigt werden und wird heruntergeladen. '+
                    'Bitte erlauben Sie der Seite Pop-Ups öffnen zu dürfen, um das Dokument im Browser anzuzeigen.';
                this.log.error(warn);
                this.download(href);
                this.$window.alert(warn);
                return false;
            } else {
                win.focus();
            }
        }

        //This would be the way to open file in new window (for now it's better to open in new tab)
        //this.$window.open(href, name, 'toolbar=0,location=0,menubar=0');
        return true;

    }

    private download(href: string) {
        href = href + ';attachment=true;';
        this.$window.location.href = href;
        return href;
    }

    public getServiceName(): string {
        return 'ReportRS';
    }

}
