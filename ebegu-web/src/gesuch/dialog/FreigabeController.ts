import GesuchRS from '../service/gesuchRS.rest';
import TSGesuch from '../../models/TSGesuch';
import TSUser from '../../models/TSUser';
import UserRS from '../../core/service/userRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import IPromise = angular.IPromise;
import IDialogService = angular.material.IDialogService;

/**
 * Controller fuer das Freigabe Popup
 */
export class FreigabeController {

    static $inject: string[] = ['docID', '$mdDialog', 'GesuchRS', 'UserRS', 'AuthServiceRS', 'EbeguUtil', 'CONSTANTS'];

    private gesuch: TSGesuch;
    private selectedUser: string;
    private userList: Array<TSUser>;
    private fallNummer: string;
    private familie: string;
    private errorMessage: string;

    constructor(private docID: string, private $mdDialog: IDialogService, private gesuchRS: GesuchRS, private userRS: UserRS, private authService: AuthServiceRS, private ebeguUtil: EbeguUtil, CONSTANTS: any) {

        gesuchRS.findGesuch(this.docID).then((response: TSGesuch) => {
            if (response) {
                this.gesuch = response;
                this.fallNummer = ebeguUtil.addZerosToNumber(response.fall.fallNummer, CONSTANTS.FALLNUMMER_LENGTH);
                this.familie = this.familieText(response);
                this.selectedUser = authService.getPrincipal().username;
            } else {
                this.errorMessage = "Gesuch nicht gefunden!";
            }
        });

        this.updateUserList();

    }

    private familieText(gesuch: TSGesuch): string {
        let familie: string;
        if (gesuch.gesuchsteller1){
            familie = gesuch.gesuchsteller1.extractFullName();
        }
        if (gesuch.gesuchsteller2){
            familie += ", " + gesuch.gesuchsteller2.extractFullName();
        }
        return familie;
    }

    private updateUserList() {
        this.userRS.getBenutzerJAorAdmin().then((response: any) => {
            this.userList = angular.copy(response);
        });
    }

    public isSchulamt(): boolean {
        return this.gesuch ? this.gesuch.areThereOnlySchulamtAngebote() : false;
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
