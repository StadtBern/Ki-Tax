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
            })
            .catch((ex) => {
                win.close();
            });
    }
}
