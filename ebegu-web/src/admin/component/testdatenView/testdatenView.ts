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
        'ErrorService', 'ReindexRS', 'GesuchsperiodeRS', 'DatabaseMigrationRS'];

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

    /* @ngInject */
    constructor(testFaelleRS: TestFaelleRS, private dvDialog: DvDialog, private userRS: UserRS,
                private errorService: ErrorService, private reindexRS: ReindexRS,
                private gesuchsperiodeRS: GesuchsperiodeRS, private databaseMigrationRS: DatabaseMigrationRS) {
        this.testFaelleRS = testFaelleRS;
        this.fetchList();
    }

    fetchList() {
        this.userRS.getAllGesuchsteller().then((result: Array<TSUser>) => {
            this.gesuchstellerList = result;
        });
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((result: Array<TSGesuchsperiode>) => {
            this.gesuchsperiodeList = result;
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
}
