import GesuchRS from '../service/gesuchRS.rest';
import TSUser from '../../models/TSUser';
import UserRS from '../../core/service/userRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import TSAntragDTO from '../../models/TSAntragDTO';
import IPromise = angular.IPromise;
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

/**
 * Controller fuer das Freigabe Popup
 */
export class FreigabeController {

    static $inject: string[] = ['docID', '$mdDialog', 'GesuchRS', 'UserRS', 'AuthServiceRS',
        'EbeguUtil', 'CONSTANTS', '$translate'];

    private gesuch: TSAntragDTO;
    private selectedUser: string;
    private userList: Array<TSUser>;
    private fallNummer: string;
    private familie: string;
    private errorMessage: string;

    constructor(private docID: string, private $mdDialog: IDialogService, private gesuchRS: GesuchRS,
                private userRS: UserRS, private authService: AuthServiceRS, private ebeguUtil: EbeguUtil,
                CONSTANTS: any, private $translate: ITranslateService) {

        gesuchRS.findGesuchForFreigabe(this.docID).then((response: TSAntragDTO) => {
            this.errorMessage = undefined; // just for safety replace old value
            if (response) {
                if (response.canBeFreigegeben()) {
                    this.gesuch = response;
                    this.fallNummer = ebeguUtil.addZerosToNumber(response.fallNummer, CONSTANTS.FALLNUMMER_LENGTH);
                    this.familie = response.familienName;
                    this.selectedUser = authService.getPrincipal().username;
                } else {
                    this.errorMessage = this.$translate.instant('FREIGABE_GESUCH_ALREADY_FREIGEGEBEN');
                }
            } else {
                this.errorMessage = this.$translate.instant('FREIGABE_GESUCH_NOT_FOUND');
            }
        }).catch(() => {
            this.cancel(); // close popup
        });

        this.updateUserList();

    }


    private updateUserList() {
        this.userRS.getBenutzerJAorAdmin().then((response: any) => {
            this.userList = angular.copy(response);
        });
    }

    public isSchulamt(): boolean {
        return this.gesuch ? this.gesuch.hasOnlySchulamtAngebote() : false;
    }

    public hasError(): boolean {
        return this.errorMessage != null;
    }

    public hide(): IPromise<any> {
        return this.gesuchRS.antragFreigeben(this.docID, this.selectedUser)
            .then(() => {
                return this.$mdDialog.hide();
            });
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }

}
