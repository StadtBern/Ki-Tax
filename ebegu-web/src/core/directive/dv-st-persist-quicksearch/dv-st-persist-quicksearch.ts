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

import {IAttributes, IAugmentedJQuery, IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope} from 'angular';
import TSUser from '../../../models/TSUser';
import UserRS from '../../service/userRS.rest';
import {InstitutionRS} from '../../service/institutionRS.rest';
import {DVsTPersistService} from '../../service/dVsTPersistService';
import {DVQuicksearchListController} from '../../../quicksearch/component/dv-quicksearch-list/dv-quicksearch-list';

/**
 * This directive allows a filter and sorting configuration to be saved after leaving the table.
 * The information will be stored in an angular-service, whi
 */
export default class DVSTPersistQuicksearch implements IDirective {
    static $inject: string[] = ['UserRS', 'InstitutionRS', 'DVsTPersistService'];

    restrict = 'A';
    require = ['^stTable', '^dvQuicksearchList'];
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(private userRS: UserRS, private institutionRS: InstitutionRS, private dVsTPersistService: DVsTPersistService) {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrlArray: any) => {
            let nameSpace: string = attrs.dvStPersistQuicksearch;
            let stTableCtrl: any = ctrlArray[0];
            let quicksearchListController: DVQuicksearchListController = ctrlArray[1];

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
                    quicksearchListController.selectedAntragTyp = savedState.search.predicateObject.antragTyp;
                    quicksearchListController.selectedGesuchsperiode = savedState.search.predicateObject.gesuchsperiodeString;
                    quicksearchListController.selectedAntragStatus = savedState.search.predicateObject.status;
                    quicksearchListController.selectedBetreuungsangebotTyp = savedState.search.predicateObject.angebote;
                    this.setInstitutionFromName(quicksearchListController, savedState.search.predicateObject.institutionen);
                    quicksearchListController.selectedFallNummer = savedState.search.predicateObject.fallNummer;
                    quicksearchListController.selectedFamilienName = savedState.search.predicateObject.familienName;
                    quicksearchListController.selectedKinder = savedState.search.predicateObject.kinder;
                    quicksearchListController.selectedEingangsdatum = savedState.search.predicateObject.eingangsdatum;
                    quicksearchListController.selectedDokumenteHochgeladen = savedState.search.predicateObject.dokumenteHochgeladen;
                    this.setVerantwortlicherFromName(quicksearchListController, savedState.search.predicateObject.verantwortlicher);
                    this.setVerantwortlicherSCHFromName(quicksearchListController, savedState.search.predicateObject.verantwortlicherSCH);
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
    private setVerantwortlicherFromName(quicksearchListController: DVQuicksearchListController, verantwortlicherFullname: string): void {
        if (verantwortlicherFullname && quicksearchListController) {
            this.userRS.getBenutzerJAorAdmin().then((response: any) => {
                let userList: TSUser[] = angular.copy(response);
                if (userList) {
                    for (let i = 0; i < userList.length; i++) {
                        if (userList[i] && userList[i].getFullName() === verantwortlicherFullname) {
                            quicksearchListController.selectedVerantwortlicher = userList[i];
                            quicksearchListController.userChanged(quicksearchListController.selectedVerantwortlicher);
                            break;
                        }
                    }
                }
            });
        }
    }

    /**
     * Extracts the user out of her name. This method is needed because the filter saves the user using its name
     * while the dropdownlist is constructed using the object TSUser. So in order to be able to select the right user
     * with need the complete object and not only its Fullname.
     */
    private setVerantwortlicherSCHFromName(quicksearchListController: DVQuicksearchListController, verantwortlicherSCHFullname: string): void {
        if (verantwortlicherSCHFullname && quicksearchListController) {
            this.userRS.getBenutzerSCHorAdminSCH().then((response: any) => {
                let userList: TSUser[] = angular.copy(response);
                if (userList) {
                    for (let i = 0; i < userList.length; i++) {
                        if (userList[i] && userList[i].getFullName() === verantwortlicherSCHFullname) {
                            quicksearchListController.selectedVerantwortlicherSCH = userList[i];
                            quicksearchListController.userChanged(quicksearchListController.selectedVerantwortlicherSCH);
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
    private setInstitutionFromName(quicksearchListController: DVQuicksearchListController, institution: string): void {
        if (institution && quicksearchListController) {
            this.institutionRS.getInstitutionenForCurrentBenutzer().then((institutionList: any) => {
                if (institutionList) {
                    for (let i = 0; i < institutionList.length; i++) {
                        if (institutionList[i].name === institution) {
                            quicksearchListController.selectedInstitution = institutionList[i];
                            break;
                        }
                    }
                }
            });
        }
    }

    static factory(): IDirectiveFactory {
        const directive = (userRS: any, institutionRS: any, dVsTPersistService: any) => new DVSTPersistQuicksearch(userRS, institutionRS, dVsTPersistService);
        directive.$inject = ['UserRS', 'InstitutionRS', 'DVsTPersistService'];
        return directive;
    }
}

