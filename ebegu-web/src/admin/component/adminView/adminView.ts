import TSApplicationProperty from '../../../models/TSApplicationProperty';
import {ApplicationPropertyRS} from '../../service/applicationPropertyRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IHttpPromiseCallbackArg, IComponentOptions, IPromise} from 'angular';
import {TestFaelleRS} from '../../service/testFaelleRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {LinkDialogController} from '../../../gesuch/dialog/LinkDialogController';
import TSUser from '../../../models/TSUser';
import UserRS from '../../../core/service/userRS.rest';
import ErrorService from '../../../core/errors/service/ErrorService';
import {ReindexRS} from '../../service/reindexRS.rest';
require('./adminView.less');
let template = require('./adminView.html');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let linkDialogTempl = require('../../../gesuch/dialog/linkDialogTemplate.html');

export class AdminViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        applicationProperties: '<'
    };
    template: string = template;
    controller: any = AdminViewController;
    controllerAs: string = 'vm';
}

export class AdminViewController {
    static $inject = ['ApplicationPropertyRS', 'MAX_LENGTH', 'EbeguRestUtil', 'TestFaelleRS', 'DvDialog', 'UserRS',
        'ErrorService', 'ReindexRS'];

    length: number;
    applicationProperty: TSApplicationProperty;
    applicationPropertyRS: ApplicationPropertyRS;
    applicationProperties: TSApplicationProperty[];
    ebeguRestUtil: EbeguRestUtil;
    testFaelleRS: TestFaelleRS;
    fallId: number;
    mutationsdatum: moment.Moment;
    aenderungperHeirat: moment.Moment;
    aenderungperScheidung: moment.Moment;


    creationType: string = 'verfuegt';
    selectedBesitzer: TSUser;
    gesuchstellerList: Array<TSUser>;


    /* @ngInject */
    constructor(applicationPropertyRS: ApplicationPropertyRS, MAX_LENGTH: number, ebeguRestUtil: EbeguRestUtil,
                testFaelleRS: TestFaelleRS, private dvDialog: DvDialog, private userRS: UserRS,
                private errorService: ErrorService, private reindexRS: ReindexRS) {
        this.length = MAX_LENGTH;
        this.applicationProperty = undefined;
        this.applicationPropertyRS = applicationPropertyRS;
        this.ebeguRestUtil = ebeguRestUtil;
        this.testFaelleRS = testFaelleRS;
        this.fetchList();
    }

    fetchList() {
        this.userRS.getAllGesuchsteller().then((result: Array<TSUser>) => {
            this.gesuchstellerList = result;
        });
    }

    submit(): void {
        //testen ob aktuelles property schon gespeichert ist
        if (this.applicationProperty.timestampErstellt) {
            this.applicationPropertyRS.update(this.applicationProperty.name, this.applicationProperty.value)
                .then((response: any) => {
                    let index = this.getIndexOfElementwithID(response.data);
                    let items: TSApplicationProperty[] = this.ebeguRestUtil.parseApplicationProperties(response.data);
                    if (items != null && items.length > 0) {
                        this.applicationProperties[index] = items[0];
                    }
                });

        } else {
            this.applicationPropertyRS.create(this.applicationProperty.name, this.applicationProperty.value)
                .then((response: any) => {
                    let items: TSApplicationProperty[] = this.ebeguRestUtil.parseApplicationProperties(response.data);
                    if (items != null && items.length > 0) {
                        //todo pruefen ob das item schon existiert hat wie oben
                        this.applicationProperties = this.applicationProperties.concat(items[0]);
                        //this.applicationProperties.push(items[0]);
                    }
                });
        }
        this.resetForm();
        //todo team fehlerhandling
    }

    removeRow(row: any): void { // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        this.applicationPropertyRS.remove(row.name).then((reponse: IHttpPromiseCallbackArg<any>) => {
            let index = this.applicationProperties.indexOf(row);
            if (index !== -1) {
                this.applicationProperties.splice(index, 1);
                this.resetForm();
            }
        });
    }

    createItem(): void {
        this.applicationProperty = new TSApplicationProperty('', '');
    }

    editRow(row: any): void { // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        this.applicationProperty = row;
    }

    resetForm(): void {
        this.applicationProperty = undefined;
    }

    private getIndexOfElementwithID(prop: TSApplicationProperty) {
        let idToSearch = prop.id;
        for (let i = 0; i < this.applicationProperties.length; i++) {
            if (this.applicationProperties[i].id === idToSearch) {
                return i;
            }
        }
        return -1;

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
            return this.createTestFallGS(testFall, bestaetigt, verfuegen, this.selectedBesitzer.username);
        } else {
            return this.createTestFall(testFall, bestaetigt, verfuegen);
        }
    }

    private createTestFall(testFall: string, bestaetigt: boolean, verfuegen: boolean): IPromise<any> {
        return this.testFaelleRS.createTestFall(testFall, bestaetigt, verfuegen).then((response) => {
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

    private createTestFallGS(testFall: string, bestaetigt: boolean, verfuegen: boolean, username: string): IPromise<any> {
        return this.testFaelleRS.createTestFallGS(testFall, bestaetigt, verfuegen, username).then((response) => {
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
}
