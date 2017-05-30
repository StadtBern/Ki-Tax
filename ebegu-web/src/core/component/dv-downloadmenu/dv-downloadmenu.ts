import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {DownloadRS} from '../../service/downloadRS.rest';
require('./dv-downloadmenu.less');
let template = require('./dv-downloadmenu.html');

export class DvDownloadmenuComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvDownloadmenuController;
    controllerAs = 'vm';
}

export class DvDownloadmenuController {

    static $inject: any[] = ['$state', 'DownloadRS'];
    display: boolean = false;

    constructor(private $state: IStateService, private downloadRS: DownloadRS) {
    }

    public toggleDisplay(): void {
        this.display = !this.display;
    }

    public download(): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenBenutzerhandbuch()
            .then((downloadFile: TSDownloadFile) => {
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                this.display = false;
            });
    }

}
