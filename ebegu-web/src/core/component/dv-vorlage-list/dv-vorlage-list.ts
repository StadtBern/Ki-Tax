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

import {IComponentOptions} from 'angular';
import TSEbeguVorlage from '../../../models/TSEbeguVorlage';
import {DownloadRS} from '../../service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {EbeguVorlageRS} from '../../../admin/service/ebeguVorlageRS.rest';
import EbeguUtil from '../../../utils/EbeguUtil';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import ILogService = angular.ILogService;
import IScope = angular.IScope;
let template = require('./dv-vorlage-list.html');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');

export class DVVorlageListConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        ebeguVorlageList: '<',
        isReadonly: '&',
        gesuchsperiode: '<',
        proGesuchsperiode: '<'
    };
    template = template;
    controller = DVVorlageListController;
    controllerAs = 'vm';
}

export class DVVorlageListController {
    ebeguVorlageList: TSEbeguVorlage[];
    isReadonly: () => void;
    gesuchsperiode: TSGesuchsperiode;
    proGesuchsperiode: boolean;

    static $inject: any[] = ['DownloadRS', '$log', 'EbeguVorlageRS', 'DvDialog',
        'EbeguUtil', '$scope'];
    /* @ngInject */
    constructor(private downloadRS: DownloadRS, private $log: ILogService,
                private ebeguVorlageRS: EbeguVorlageRS, private dvDialog: DvDialog,
                private ebeguUtil: EbeguUtil, private $scope: IScope) {
    }

    $onInit() {
        this.updateVorlageList();
        if (this.proGesuchsperiode) {
            this.$scope.$watch(() => {
                return this.gesuchsperiode;
            }, (newValue, oldValue) => {
                if (newValue !== oldValue) {
                    this.updateVorlageList();
                }
            });
        }
    }

    private updateVorlageList() {
        if (this.proGesuchsperiode) {
            if (this.gesuchsperiode) {
                this.ebeguVorlageRS.getEbeguVorlagenByGesuchsperiode(this.gesuchsperiode.id).then((response: TSEbeguVorlage[]) => {
                    this.ebeguVorlageList = response;
                });
            }

        } else {
            this.ebeguVorlageRS.getEbeguVorlagenWithoutGesuchsperiode().then((response: TSEbeguVorlage[]) => {
                this.ebeguVorlageList = response;
            });
        }
    }

    hasVorlage(selectVorlage: TSEbeguVorlage): boolean {
        if (selectVorlage.vorlage) {
            return true;
        }
        return false;
    }

    isListReadonly(): void {
        this.isReadonly();
    }

    download(ebeguVorlage: TSEbeguVorlage, attachment: boolean) {
        this.$log.debug('download vorlage ' + ebeguVorlage.vorlage.filename);
        let win: Window = this.downloadRS.prepareDownloadWindow();

        this.downloadRS.getAccessTokenVorlage(ebeguVorlage.vorlage.id)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, attachment, win);
            })
            .catch((ex) => {
                win.close();
                this.$log.error('An error occurred downloading the document, closing download window.');
            });
    }

    uploadAnhaenge(files: any[], selectEbeguVorlage: TSEbeguVorlage) {
        this.$log.debug('Uploading files ');

        let gesuchsperiodeID = undefined;
        if (this.proGesuchsperiode && this.gesuchsperiode) {
            gesuchsperiodeID = this.gesuchsperiode.id;
        }
        this.ebeguVorlageRS.uploadVorlage(files[0], selectEbeguVorlage, gesuchsperiodeID, this.proGesuchsperiode).then((response) => {
            this.addResponseToCurrentList(response);
        });
    }

    private addResponseToCurrentList(response: TSEbeguVorlage) {
        let returnedDG: TSEbeguVorlage = angular.copy(response);
        let index = this.getIndexOfElement(returnedDG, this.ebeguVorlageList);

        if (index > -1) {
            //this.$log.debug('add dokument to dokumentList');
            this.ebeguVorlageList[index] = returnedDG;
        }
        this.ebeguUtil.handleSmarttablesUpdateBug(this.ebeguVorlageList);
    }

    private getIndexOfElement(entityToSearch: TSEbeguVorlage, listToSearchIn: TSEbeguVorlage[]): number {
        let idToSearch = entityToSearch.name;
        for (let i = 0; i < listToSearchIn.length; i++) {
            if (listToSearchIn[i].name === idToSearch) {
                return i;
            }
        }
        return -1;
    }

    public remove(ebeguVorlage: TSEbeguVorlage) {
        this.$log.debug('component -> remove dokument ' + ebeguVorlage.vorlage.filename);
        this.dvDialog.showRemoveDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'FILE_LOESCHEN',
            parentController: undefined,
            elementID: undefined,
            form: undefined
        })
            .then(() => {   //User confirmed removal

                this.ebeguVorlageRS.deleteEbeguVorlage(ebeguVorlage.id).then((response) => {

                    let index = EbeguUtil.getIndexOfElementwithID(ebeguVorlage, this.ebeguVorlageList);
                    if (index > -1) {
                        this.$log.debug('remove Vorlage in EbeguVorlage');
                        ebeguVorlage.vorlage = null;
                        this.ebeguVorlageList[index] = ebeguVorlage;
                    }
                });
                this.ebeguUtil.handleSmarttablesUpdateBug(this.ebeguVorlageList);

            });
    }
}
