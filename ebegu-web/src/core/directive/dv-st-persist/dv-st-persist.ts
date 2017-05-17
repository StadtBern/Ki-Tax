import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from 'angular';
import {DVAntragListController} from '../../component/dv-antrag-list/dv-antrag-list';
import TSUser from '../../../models/TSUser';
import UserRS from '../../service/userRS.rest';

/**
 * This directive allows saves a filter and sorting configuration to be saved after leaving the table
 */
export default class DVSTPersist implements IDirective {
    static $inject: string[] = ['UserRS'];

    restrict = 'A';
    require = ['^stTable', '^dvAntragList'];
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(private userRS: UserRS) {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrlArray: any) => {
            let nameSpace = attrs.dvStPersist;
            let stTableCtrl: any = ctrlArray[0];
            let antragListController: DVAntragListController = ctrlArray[1];

            //save the table state every time it changes
            scope.$watch(function () {
                return stTableCtrl.tableState();
            }, function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    sessionStorage.setItem(nameSpace, JSON.stringify(newValue));
                    // service.save();
                }
            }, true);

            //fetch the table state when the directive is loaded
            if (sessionStorage.getItem(nameSpace)) {
                let savedState = JSON.parse(sessionStorage.getItem(nameSpace));
                // let savedState = service.load();
                if (savedState.search && savedState.search.predicateObject) { //update all objects of the model for the filters
                    antragListController.selectedAntragTyp = savedState.search.predicateObject.antragTyp;
                    antragListController.selectedGesuchsperiode = savedState.search.predicateObject.gesuchsperiodeString;
                    antragListController.selectedAntragStatus = savedState.search.predicateObject.status;
                    antragListController.selectedBetreuungsangebotTyp = savedState.search.predicateObject.angebote;
                    antragListController.selectedInstitution = savedState.search.predicateObject.institutionen;
                    antragListController.selectedFallNummer = savedState.search.predicateObject.fallNummer;
                    antragListController.selectedFamilienName = savedState.search.predicateObject.familienName;
                    antragListController.selectedKinder = savedState.search.predicateObject.kinder;
                    antragListController.selectedAenderungsdatum = savedState.search.predicateObject.aenderungsdatum;
                    antragListController.selectedEingangsdatum = savedState.search.predicateObject.eingangsdatum;
                    this.getUserFromName(antragListController, savedState.search.predicateObject.verantwortlicher);
                }
                let tableState = stTableCtrl.tableState();

                // angular.extend(tableState, savedState);
                // stTableCtrl.tableState().search = savedState.search;
                tableState.search = savedState.search;
                stTableCtrl.pipe();

            }
        };
    }

    /**
     * Extracts the user out of her name. This method is needed because the filter saves the user using its name
     * while the dropdownlist is constructed using the object TSUser. So in order to be able to select the right user
     * with need the complete object and not only its Fullname.
     */
    private getUserFromName(antragListController: DVAntragListController, verantwortlicherFullname: string): void {
        if (verantwortlicherFullname) {
            this.userRS.getBenutzerJAorAdmin().then((response: any) => {
                let userList: TSUser[] = angular.copy(response);
                if (userList) {
                    for (let i = 0; i < userList.length; i++) {
                        if (userList[i] && userList[i].getFullName() === verantwortlicherFullname) {
                            antragListController.selectedVerantwortlicher = userList[i];
                            break;
                        }
                    }
                }
            });
        }
    }

    static factory(): IDirectiveFactory {
        const directive = (userRS: any) => new DVSTPersist(userRS);
        directive.$inject = ['UserRS'];
        return directive;
    }
}

