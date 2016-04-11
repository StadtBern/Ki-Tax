import 'angular';
import 'angular-smart-table';
import {EbeguWebCore} from '../core/core.module';
import './admin.module.less';
import {AdminViewComponentConfig} from './component/adminView/adminView';
import ApplicationPropertyRS from './service/applicationPropertyRS.rest';
import {adminRun} from './admin.route';

export const EbeguWebAdmin = angular.module('ebeguWeb.admin', [EbeguWebCore.name, 'smart-table'])
    .service('ApplicationPropertyRS', ApplicationPropertyRS)
    .component('dvAdminView', new AdminViewComponentConfig())
    .run(adminRun);

export default EbeguWebAdmin;
