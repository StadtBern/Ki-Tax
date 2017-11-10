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

import {IComponentOptions, IPromise} from 'angular';
import {TestFaelleRS} from '../../service/testFaelleRS.rest';
import {DatabaseMigrationRS} from '../../service/databaseMigrationRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {LinkDialogController} from '../../../gesuch/dialog/LinkDialogController';
import TSUser from '../../../models/TSUser';
import UserRS from '../../../core/service/userRS.rest';
import ErrorService from '../../../core/errors/service/ErrorService';
import {ReindexRS} from '../../service/reindexRS.rest';
import * as moment from 'moment';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import ZahlungRS from '../../../core/service/zahlungRS.rest';
import {ApplicationPropertyRS} from '../../service/applicationPropertyRS.rest';

import ZahlungRS from '../../../core/service/zahlungRS.rest';

require('./testdatenView.less');
let template = require('./testdatenView.html');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let linkDialogTempl = require('../../../gesuch/dialog/linkDialogTemplate.html');

export class TestdatenViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = TestdatenViewController;
    controllerAs: string = 'vm';
}

export class TestdatenViewController {
    static $inject = ['TestFaelleRS', 'DvDialog', 'UserRS',
        'ErrorService', 'ReindexRS', 'GesuchsperiodeRS', 'DatabaseMigrationRS', 'ZahlungRS', 'ApplicationPropertyRS'];

    testFaelleRS: TestFaelleRS;
    fallId: number;
    mutationsdatum: moment.Moment;
    aenderungperHeirat: moment.Moment;
    aenderungperScheidung: moment.Moment;

    creationType: string = 'verfuegt';
    selectedBesitzer: TSUser;
    gesuchstellerList: Array<TSUser>;

    selectedGesuchsperiode: TSGesuchsperiode;
    gesuchsperiodeList: Array<TSGesuchsperiode>;

    devMode: boolean;

    /* @ngInject */
    constructor(testFaelleRS: TestFaelleRS, private dvDialog: DvDialog, private userRS: UserRS,
                private errorService: ErrorService, private reindexRS: ReindexRS,
                private gesuchsperiodeRS: GesuchsperiodeRS, private databaseMigrationRS: DatabaseMigrationRS,
                private zahlungRS: ZahlungRS, private applicationPropertyRS: ApplicationPropertyRS) {
        this.testFaelleRS = testFaelleRS;
    }

    $onInit() {
        this.userRS.getAllGesuchsteller().then((result: Array<TSUser>) => {
            this.gesuchstellerList = result;
        });
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((result: Array<TSGesuchsperiode>) => {
            this.gesuchsperiodeList = result;
        });
        this.applicationPropertyRS.isDevMode().then((response: boolean) => {
            this.devMode = response;
        });
    }

    public createTestFallType(testFall: string): IPromise<any> {
        let bestaetigt: boolean = false;
        let verfuegen: boolean = false;
        if (this.creationType === 'warten') {
            bestaetigt = false;
            verfuegen = false;

        } else if (this.creationType === 'bestaetigt') {
            bestaetigt = true;
            verfuegen = false;

        } else if (this.creationType === 'verfuegt') {
            bestaetigt = true;
            verfuegen = true;
        }
        if (this.selectedBesitzer) {
            return this.createTestFallGS(testFall, this.selectedGesuchsperiode.id, bestaetigt, verfuegen, this.selectedBesitzer.username);
        } else {
            return this.createTestFall(testFall, this.selectedGesuchsperiode.id, bestaetigt, verfuegen);
        }
    }

    private createTestFall(testFall: string, gesuchsperiodeId: string, bestaetigt: boolean, verfuegen: boolean): IPromise<any> {
        return this.testFaelleRS.createTestFall(testFall, gesuchsperiodeId, bestaetigt, verfuegen).then((response) => {
            //einfach die letzten 36 zeichen der response als uuid betrachten, hacky ist aber nur fuer uns intern
            let uuidPartOfString = response.data ? response.data.slice(-36) : '';
            return this.dvDialog.showDialog(linkDialogTempl, LinkDialogController, {
                title: response.data,
                link: '#/gesuch/fall/false///' + uuidPartOfString + '/', //nicht alle Parameter werden benoetigt, deswegen sind sie leer
            }).then(() => {
                //do nothing
            });
        });
    }

    private createTestFallGS(testFall: string, gesuchsperiodeId: string, bestaetigt: boolean, verfuegen: boolean, username: string): IPromise<any> {
        return this.testFaelleRS.createTestFallGS(testFall, gesuchsperiodeId, bestaetigt, verfuegen, username).then((response) => {
            //einfach die letzten 36 zeichen der response als uuid betrachten, hacky ist aber nur fuer uns intern
            let uuidPartOfString = response.data ? response.data.slice(-36) : '';
            return this.dvDialog.showDialog(linkDialogTempl, LinkDialogController, {
                title: response.data,
                link: '#/gesuch/fall/false///' + uuidPartOfString + '/', //nicht alle Parameter werden benoetigt, deswegen sind sie leer
            }).then(() => {
                //do nothing
            });
        });
    }

    public removeGesucheGS() {
        this.testFaelleRS.removeFaelleOfGS(this.selectedBesitzer.username).then(() => {
            this.errorService.addMesageAsInfo('Gesuche entfernt fuer ' + this.selectedBesitzer.username);
        });
    }

    public removeGesuchsperiode() {
        this.gesuchsperiodeRS.removeGesuchsperiode(this.selectedGesuchsperiode.id).then(() => {
            this.errorService.addMesageAsInfo('Gesuchsperiode entfernt ' + this.selectedGesuchsperiode.gesuchsperiodeString);
        });
    }

    public mutiereFallHeirat(): IPromise<any> {
        return this.testFaelleRS.mutiereFallHeirat(this.fallId, '0621fb5d-a187-5a91-abaf-8a813c4d263a',
            this.mutationsdatum, this.aenderungperHeirat).then((response) => {
            return this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                title: response.data
            }).then(() => {
                //do nothing
            });
        });
    }

    public mutiereFallScheidung(): IPromise<any> {
        return this.testFaelleRS.mutiereFallScheidung(this.fallId, '0621fb5d-a187-5a91-abaf-8a813c4d263a',
            this.mutationsdatum, this.aenderungperScheidung).then((respone) => {
            return this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                title: respone.data
            }).then(() => {
                //do nothing
            });
        });
    }

    public resetSchulungsdaten(): IPromise<any> {
        return this.testFaelleRS.resetSchulungsdaten().then((response) => {
            return this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                title: response.data
            }).then(() => {
                //do nothing
            });
        });
    }

    public deleteSchulungsdaten(): IPromise<any> {
        return this.testFaelleRS.deleteSchulungsdaten().then((response) => {
            return this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                title: response.data
            }).then(() => {
                //do nothing
            });
        });
    }

    public startReindex() {
        return this.reindexRS.reindex();
    }

    public processScript(script: string): void {
        this.databaseMigrationRS.processScript(script);
    }

    public zahlungenKontrollieren(): void {
        this.zahlungRS.zahlungenKontrollieren();
    }

    public deleteAllZahlungsauftraege(): void {
        this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
            deleteText: 'ZAHLUNG_LOESCHEN_DIALOG_TEXT',
            title: 'ZAHLUNG_LOESCHEN_DIALOG_TITLE',
            parentController: undefined,
            elementID: undefined
        })
            .then(() => {   //User confirmed removal
                this.zahlungRS.deleteAllZahlungsauftraege();
            });
    }

}
