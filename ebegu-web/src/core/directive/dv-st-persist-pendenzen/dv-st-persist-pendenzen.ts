import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from 'angular';
import TSUser from '../../../models/TSUser';
import UserRS from '../../service/userRS.rest';
import {InstitutionRS} from '../../service/institutionRS.rest';
import {DVsTPersistService} from '../../service/dVsTPersistService';
import {DVPendenzenListController} from '../../component/dv-pendenzen-list/dv-pendenzen-list';

/**
 * This directive allows a filter and sorting configuration to be saved after leaving the table.
 * The information will be stored in an angular-service, whi
 */
export default class DVSTPersistPendenzen implements IDirective {
    static $inject: string[] = ['UserRS', 'InstitutionRS', 'DVsTPersistService'];

    restrict = 'A';
    require = ['^stTable', '^dvPendenzenList'];
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(private userRS: UserRS, private institutionRS: InstitutionRS, private dVsTPersistService: DVsTPersistService) {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrlArray: any) => {
            let nameSpace: string = attrs.dvStPersistPendenzen;
            let stTableCtrl: any = ctrlArray[0];
            let pendenzenListController: DVPendenzenListController = ctrlArray[1];

            //save the table state every time it changes
            scope.$watch(function () {
                return stTableCtrl.tableState();
            }, function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    // sessionStorage.setItem(nameSpace, JSON.stringify(newValue));
                    dVsTPersistService.saveData(nameSpace, newValue);
                }
            }, true);

            // if (sessionStorage.getItem(nameSpace)) {
            // let savedState = JSON.parse(sessionStorage.getItem(nameSpace));

            //fetch the table state when the directive is loaded
            let savedState = dVsTPersistService.loadData(nameSpace);
            if (savedState) {
                if (savedState.search && savedState.search.predicateObject) { //update all objects of the model for the filters
                    pendenzenListController.selectedAntragTyp = savedState.search.predicateObject.antragTyp;
                    pendenzenListController.selectedGesuchsperiode = savedState.search.predicateObject.gesuchsperiodeString;
                    pendenzenListController.selectedAntragStatus = savedState.search.predicateObject.status;
                    pendenzenListController.selectedBetreuungsangebotTyp = savedState.search.predicateObject.angebote;
                    this.setInstitutionFromName(pendenzenListController, savedState.search.predicateObject.institutionen);
                    pendenzenListController.selectedFallNummer = savedState.search.predicateObject.fallNummer;
                    pendenzenListController.selectedFamilienName = savedState.search.predicateObject.familienName;
                    pendenzenListController.selectedKinder = savedState.search.predicateObject.kinder;
                    pendenzenListController.selectedEingangsdatum = savedState.search.predicateObject.eingangsdatum;
                    this.setUserFromName(pendenzenListController, savedState.search.predicateObject.verantwortlicher);
                }
                let tableState = stTableCtrl.tableState();

                angular.extend(tableState, savedState);
                stTableCtrl.pipe();

            }
        };
    }

    /**
     * Extracts the user out of her name. This method is needed because the filter saves the user using its name
     * while the dropdownlist is constructed using the object TSUser. So in order to be able to select the right user
     * with need the complete object and not only its Fullname.
     */
    private setUserFromName(pendenzenListController: DVPendenzenListController, verantwortlicherFullname: string): void {
        if (verantwortlicherFullname && pendenzenListController) {
            this.userRS.getBenutzerJAorAdmin().then((response: any) => {
                let userList: TSUser[] = angular.copy(response);
                if (userList) {
                    for (let i = 0; i < userList.length; i++) {
                        if (userList[i] && userList[i].getFullName() === verantwortlicherFullname) {
                            pendenzenListController.selectedVerantwortlicher = userList[i];
                            pendenzenListController.userChanged(pendenzenListController.selectedVerantwortlicher);
                            break;
                        }
                    }
                }
            });
        }
    }

    /**
     * Extracts the Institution from the institutionList of the controller using the name that had been saved in the
     * filter. This is needed because the filter saves the name and not the object.
     */
    private setInstitutionFromName(pendenzenListController: DVPendenzenListController, institution: string): void {
        if (institution && pendenzenListController) {
            this.institutionRS.getInstitutionenForCurrentBenutzer().then((institutionList: any) => {
                if (institutionList) {
                    for (let i = 0; i < institutionList.length; i++) {
                        if (institutionList[i].name === institution) {
                            pendenzenListController.selectedInstitution = institutionList[i];
                            break;
                        }
                    }
                }
            });
        }
    }

    static factory(): IDirectiveFactory {
        const directive = (userRS: any, institutionRS: any, dVsTPersistService: any) => new DVSTPersistPendenzen(userRS, institutionRS, dVsTPersistService);
        directive.$inject = ['UserRS', 'InstitutionRS', 'DVsTPersistService'];
        return directive;
    }
}

