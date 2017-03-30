import IComponentOptions = angular.IComponentOptions;
import TSFall from '../../../models/TSFall';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSAntragStatusHistory from '../../../models/TSAntragStatusHistory';
import {IAlleVerfuegungenStateParams} from '../../alleVerfuegungen.route';
import FallRS from '../../../gesuch/service/fallRS.rest';
import TSBetreuung from '../../../models/TSBetreuung';
import BetreuungRS from '../../../core/service/betreuungRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import IFormController = angular.IFormController;
import IStateService = angular.ui.IStateService;
import ILogService = angular.ILogService;

let template = require('./alleVerfuegungenView.html');
require('./alleVerfuegungenView.less');

export class AlleVerfuegungenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = AlleVerfuegungenViewController;
    controllerAs = 'vm';
}

export class AlleVerfuegungenViewController {

    fall: TSFall;
    alleVerfuegungen: Array<any> = new Array<any>();
    itemsByPage: number = 20;
    TSRoleUtil = TSRoleUtil;

    static $inject: string[] = ['$state', '$stateParams', 'AuthServiceRS', 'FallRS', 'BetreuungRS', 'GesuchModelManager', 'DownloadRS', '$log'];
    /* @ngInject */
    constructor(private $state: IStateService, private $stateParams: IAlleVerfuegungenStateParams,
                private authServiceRS: AuthServiceRS, private fallRS: FallRS,
                private betreuungRS: BetreuungRS, private gesuchModelManager: GesuchModelManager,
                private downloadRS: DownloadRS, private $log: ILogService) {
    }

    $onInit() {
        if (this.$stateParams.fallId) {
            this.fallRS.findFall(this.$stateParams.fallId).then((response) => {
                this.fall = response;
                if (this.fall === undefined) {
                    this.cancel();
                }

                    this.betreuungRS.findAllBetreuungenWithVerfuegungFromFall(this.fall.id).then((response) => {
                    response.forEach((item) => {
                        this.alleVerfuegungen.push(item);

                    });
                        this.alleVerfuegungen = response;
                });
            });
        } else {
            this.cancel();
        }
    }

    public getAlleVerfuegungen(): Array<TSAntragStatusHistory> {
        return this.alleVerfuegungen;
    }

    public openVerfuegung(betreuungNummer: string, kindNummer: number, gesuchId: string): void {
        if (betreuungNummer && kindNummer && gesuchId) {
            let kindIndex: number = this.gesuchModelManager.convertKindNumberToKindIndex(kindNummer);
            if (kindIndex >= 0) {
                this.gesuchModelManager.setKindIndex(kindIndex);
                this.$state.go('gesuch.verfuegenView', {
                    betreuungNumber: betreuungNummer,
                    kindNumber: kindNummer,
                    gesuchId: gesuchId
                });
            }
        }
    }

    public cancel(): void {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            this.$state.go('gesuchstellerDashboard');
        } else {
            this.$state.go('pendenzen');
        }
    }

    public showVerfuegungPdfLink(betreuung: TSBetreuung): boolean {
        return !(TSBetreuungsstatus.NICHT_EINGETRETEN === betreuung.betreuungsstatus);
    }

    public openVerfuegungPDF(betreuung: TSBetreuung): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenVerfuegungGeneratedDokument(this.gesuchModelManager.getGesuch().id,
            betreuung.id, false, '')
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            });
    }
    public openNichteintretenPDF(betreuung: TSBetreuung): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenNichteintretenGeneratedDokument(betreuung.id, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            });
    }
}
