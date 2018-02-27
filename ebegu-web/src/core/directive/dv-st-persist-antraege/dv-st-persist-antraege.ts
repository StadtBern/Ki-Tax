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
import {DVAntragListController} from '../../component/dv-antrag-list/dv-antrag-list';
import TSUser from '../../../models/TSUser';
import UserRS from '../../service/userRS.rest';
import {InstitutionRS} from '../../service/institutionRS.rest';
import {DVsTPersistService} from '../../service/dVsTPersistService';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../../models/enums/TSRole';

/**
 * This directive allows a filter and sorting configuration to be saved after leaving the table.
 * The information will be stored in an angular-service, whi
 */
export default class DVSTPersistAntraege implements IDirective {
    static $inject: string[] = ['UserRS', 'InstitutionRS', 'AuthServiceRS', 'DVsTPersistService'];

    restrict = 'A';
    require = ['^stTable', '^dvAntragList'];
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(private userRS: UserRS, private institutionRS: InstitutionRS, private authServiceRS: AuthServiceRS,
                private dVsTPersistService: DVsTPersistService) {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrlArray: any) => {
            let nameSpace: string = attrs.dvStPersistAntraege;
            let stTableCtrl: any = ctrlArray[0];
            let antragListController: DVAntragListController = ctrlArray[1];


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
            savedState = this.setCurrentUserAsVerantwortlicher(antragListController, savedState);
            if (savedState) {
                if (savedState.search && savedState.search.predicateObject) { //update all objects of the model for the filters
                    antragListController.selectedAntragTyp = savedState.search.predicateObject.antragTyp;
                    antragListController.selectedGesuchsperiode = savedState.search.predicateObject.gesuchsperiodeString;
                    antragListController.selectedAntragStatus = savedState.search.predicateObject.status;
                    antragListController.selectedBetreuungsangebotTyp = savedState.search.predicateObject.angebote;
                    this.setInstitutionFromName(antragListController, savedState.search.predicateObject.institutionen);
                    antragListController.selectedFallNummer = savedState.search.predicateObject.fallNummer;
                    antragListController.selectedFamilienName = savedState.search.predicateObject.familienName;
                    antragListController.selectedKinder = savedState.search.predicateObject.kinder;
                    antragListController.selectedAenderungsdatum = savedState.search.predicateObject.aenderungsdatum;
                    antragListController.selectedEingangsdatum = savedState.search.predicateObject.eingangsdatum;
                    antragListController.selectedDokumenteHochgeladen = savedState.search.predicateObject.dokumenteHochgeladen;
                    antragListController.selectedEingangsdatumSTV = savedState.search.predicateObject.eingangsdatumSTV;
                    this.setVerantwortlicherFromName(antragListController, savedState.search.predicateObject.verantwortlicher);
                    this.setVerantwortlicherSCHFromName(antragListController, savedState.search.predicateObject.verantwortlicherSCH);
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
    private setVerantwortlicherFromName(antragListController: DVAntragListController, verantwortlicherFullname: string): void {
        if (verantwortlicherFullname && antragListController) {
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

    /**
     * Extracts the user out of her name. This method is needed because the filter saves the user using its name
     * while the dropdownlist is constructed using the object TSUser. So in order to be able to select the right user
     * with need the complete object and not only its Fullname.
     */
    private setVerantwortlicherSCHFromName(antragListController: DVAntragListController, verantwortlicherSCHFullname: string): void {
        if (verantwortlicherSCHFullname && antragListController) {
            this.userRS.getBenutzerSCHorAdminSCH().then((response: any) => {
                let userList: TSUser[] = angular.copy(response);
                if (userList) {
                    for (let i = 0; i < userList.length; i++) {
                        if (userList[i] && userList[i].getFullName() === verantwortlicherSCHFullname) {
                            antragListController.selectedVerantwortlicherSCH = userList[i];
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
    private setInstitutionFromName(antragListController: DVAntragListController, institution: string): void {
        if (institution && antragListController) {
            this.institutionRS.getInstitutionenForCurrentBenutzer().then((institutionList: any) => {
                if (institutionList) {
                    for (let i = 0; i < institutionList.length; i++) {
                        if (institutionList[i].name === institution) {
                            antragListController.selectedInstitution = institutionList[i];
                            break;
                        }
                    }
                }
            });
        }
    }

    static factory(): IDirectiveFactory {
        const directive = (userRS: any, institutionRS: any, authServiceRS: any, dVsTPersistService: any) =>
            new DVSTPersistAntraege(userRS, institutionRS, authServiceRS, dVsTPersistService);
        directive.$inject = ['UserRS', 'InstitutionRS', 'AuthServiceRS', 'DVsTPersistService'];
        return directive;
    }

    /**
     * Setzt den aktuellen Benutzer als selectedVerantwotlicher wenn:
     * - es eine pendenzenListe ist: ctrl.pendenz===true
     * - es noch nicht gesetzt wurde, d.h. nichts war ausgewaehlt
     */
    private setCurrentUserAsVerantwortlicher(antragListController: DVAntragListController, savedState: any): any {
        let savedStateToReturn: any = angular.copy(savedState);
        if (antragListController.pendenz) {
            if (!savedStateToReturn) {
                savedStateToReturn = {search: {predicateObject: this.extractVerantwortlicherFullName()}};
            }
            if (!savedStateToReturn.search.predicateObject) {
                savedStateToReturn.search.predicateObject = this.extractVerantwortlicherFullName();
            }
            if (!savedStateToReturn.search.predicateObject.verantwortlicher) {
                if (this.authServiceRS.getPrincipal().role === TSRole.ADMINISTRATOR_SCHULAMT || this.authServiceRS.getPrincipal().role === TSRole.SCHULAMT) {
                    savedStateToReturn.search.predicateObject.verantwortlicherSCH = this.authServiceRS.getPrincipal().getFullName();
                } else { //JA
                    savedStateToReturn.search.predicateObject.verantwortlicher = this.authServiceRS.getPrincipal().getFullName();
                }
            }
        }
        return savedStateToReturn;
    }

    private extractVerantwortlicherFullName() {
        if (this.authServiceRS.getPrincipal().role === TSRole.ADMINISTRATOR_SCHULAMT || this.authServiceRS.getPrincipal().role === TSRole.SCHULAMT) {
            return {verantwortlicherSCH: this.authServiceRS.getPrincipal().getFullName()};
        } else { //JA
            return {verantwortlicher: this.authServiceRS.getPrincipal().getFullName()};
        }
    }
}
