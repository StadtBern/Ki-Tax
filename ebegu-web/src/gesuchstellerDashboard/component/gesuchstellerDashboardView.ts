import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSAntragDTO from '../../models/TSAntragDTO';
import PendenzRS from '../../pendenzen/service/PendenzRS.rest';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../core/service/gesuchsperiodeRS.rest';
import TSFall from '../../models/TSFall';
import {TSEingangsart} from '../../models/enums/TSEingangsart';
import FallRS from '../../gesuch/service/fallRS.rest';
import {TSAntragStatus, IN_BEARBEITUNG_BASE_NAME, isAnyStatusOfVerfuegt} from '../../models/enums/TSAntragStatus';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import EbeguUtil from '../../utils/EbeguUtil';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import ITranslateService = angular.translate.ITranslateService;
let template = require('./gesuchstellerDashboardView.html');
require('./gesuchstellerDashboardView.less');

export class GesuchstellerDashboardListViewConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = GesuchstellerDashboardListViewController;
    controllerAs = 'vm';
}

export class GesuchstellerDashboardListViewController {

    private antragList: Array<TSAntragDTO> = [];
    private _activeGesuchsperiodenList: Array<TSGesuchsperiode>;
    private fallId: string;
    totalResultCount: string = '-';


    static $inject: string[] = ['$state', '$log', 'CONSTANTS', 'AuthServiceRS', 'PendenzRS', 'EbeguUtil', 'GesuchsperiodeRS', 'FallRS', '$translate'];

    constructor(private $state: IStateService, private $log: ILogService, private CONSTANTS: any,
                private authServiceRS: AuthServiceRS, private pendenzRS: PendenzRS, private ebeguUtil: EbeguUtil, private gesuchsperiodeRS: GesuchsperiodeRS,
                private fallRS: FallRS, private $translate: ITranslateService) {
        this.initViewModel();
    }

    private initViewModel() {
        this.updateAntragList();
        this.updateActiveGesuchsperiodenList();
    }

    private updateAntragList() {
        this.fallRS.findFallByCurrentBenutzerAsBesitzer().then((existingFall: TSFall) => {
            if (existingFall) {
                this.fallId = existingFall.id;
                this.pendenzRS.getAntraegeGesuchstellerList().then((response: any) => {
                    this.antragList = angular.copy(response);
                });
            }
        });
    }

    private updateActiveGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: any) => {
            this._activeGesuchsperiodenList = angular.copy(response);
        });
    }

    public getActiveGesuchsperiodenList(): Array<TSGesuchsperiode> {
        return this._activeGesuchsperiodenList;
    }

    public goToMitteilungenOeffen() {
        this.$log.warn('Not yet impl');
    }

    public getAntragList(): Array<TSAntragDTO> {
        return this.antragList;
    }

    public getNumberMitteilungen(): number {
        return 12;
    }

    public openAntrag(periode: TSGesuchsperiode): void {
        let antrag: TSAntragDTO = this.getAntragForGesuchsperiode(periode);
        if (antrag) {
            if (TSAntragStatus.IN_BEARBEITUNG_GS === antrag.status) {
                // Noch nicht freigegeben
                this.$state.go('gesuch.fallcreation', {createNew: false, gesuchId: antrag.antragId});
            } else if (!isAnyStatusOfVerfuegt(antrag.status) || antrag.beschwerdeHaengig) {
                // Alles ausser verfuegt und InBearbeitung
                this.$state.go('gesuch.dokumente', {createNew: false, gesuchId: antrag.antragId});
            } else {
                // Im Else-Fall ist das Gesuch nicht mehr ueber den Button verfuegbar
                // Es kann nur noch eine Mutation gemacht werden
                this.$state.go('gesuch.mutation', {
                    createMutation: true,
                    eingangsart: TSEingangsart.ONLINE,
                    gesuchId: antrag.antragId,
                    gesuchsperiodeId: periode.id,
                    fallId: this.fallId
                });
            }
        } else {
            // Noch kein Antrag vorhanden
            this.$state.go('gesuch.fallcreation', {
                createNew: true,
                eingangsart: TSEingangsart.ONLINE,
                gesuchId: null,
                gesuchsperiodeId: periode.id,
                fallId: this.fallId
            });
        }
    }

    public getButtonText(periode: TSGesuchsperiode): string {
        let antrag: TSAntragDTO = this.getAntragForGesuchsperiode(periode);
        if (antrag) {
            if (TSAntragStatus.IN_BEARBEITUNG_GS === antrag.status) {
                // Noch nicht freigegeben -> Text BEARBEITEN
                return this.$translate.instant('GS_BEARBEITEN');
            } else if (!isAnyStatusOfVerfuegt(antrag.status) || antrag.beschwerdeHaengig) {
                // Alles ausser verfuegt und InBearbeitung -> Text DOKUMENTE HOCHLADEN
                return this.$translate.instant('GS_DOKUMENTE_HOCHLADEN');
            } else {
                // Im Else-Fall ist das Gesuch nicht mehr ueber den Button verfuegbar
                // Es kann nur noch eine Mutation gemacht werden -> Text MUTIEREN
                return this.$translate.instant('GS_MUTIEREN');
            }
        } else {
            // Noch kein Antrag vorhanden -> Text GESUCH BEANTRAGEN
            // this.$state.go('gesuch.fallcreation', {createNew: true, gesuchId: null});
            return this.$translate.instant('GS_BEANTRAGEN');
        }
    }

    public editAntrag(antrag: TSAntragDTO): void {
        if (antrag) {
            if (isAnyStatusOfVerfuegt(antrag.status)) {
                this.$state.go('gesuch.verfuegen', {createNew: false, gesuchId: antrag.antragId});
            } else {
                this.$state.go('gesuch.fallcreation', {createNew: false, gesuchId: antrag.antragId});
            }
        }
    }

    private getAntragForGesuchsperiode(periode: TSGesuchsperiode): TSAntragDTO {
        // Die Antraege sind nach Laufnummer sortiert, d.h. der erste einer Periode ist immer der aktuellste
        if (this.antragList) {
            for (let antrag of this.antragList) {
                if (antrag.gesuchsperiodeGueltigAb.year() === periode.gueltigkeit.gueltigAb.year()) {
                    return antrag;
                }
            }
        }
        return undefined;
    }

    /**
     * Status muss speziell uebersetzt werden damit Gesuchsteller nur "In Bearbeitung" sieht und nicht in "Bearbeitung Gesuchsteller"
     */
    public translate(status: TSAntragStatus) {
        let isUserGesuchsteller: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles());
        if (status === TSAntragStatus.IN_BEARBEITUNG_GS && isUserGesuchsteller) {
            return this.ebeguUtil.translateString(IN_BEARBEITUNG_BASE_NAME);
        }
        if ((status === TSAntragStatus.NUR_SCHULAMT || status === TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN)
            && isUserGesuchsteller) {
            return this.ebeguUtil.translateString('ABGESCHLOSSEN');
        }
        return this.ebeguUtil.translateString(TSAntragStatus[status]);
    }
}
