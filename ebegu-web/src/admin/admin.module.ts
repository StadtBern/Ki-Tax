import 'angular';
import 'angular-smart-table';
import {EbeguWebCore} from '../core/core.module';
import './admin.module.less';
import {AdminViewComponentConfig} from './component/adminView/adminView';
import {InstitutionViewComponentConfig} from './component/institutionView/institutionView';
import {ParameterViewComponentConfig} from './component/parameterView/parameterView';
import {ApplicationPropertyRS} from './service/applicationPropertyRS.rest';
import {adminRun} from './admin.route';
import {InstitutionRS} from '../core/service/institutionRS.rest';
import {EbeguParameterRS} from './service/ebeguParameterRS.rest';

export const EbeguWebAdmin = angular.module('ebeguWeb.admin', [EbeguWebCore.name, 'smart-table'])
    .service('ApplicationPropertyRS', ApplicationPropertyRS)
    .service('InstitutionRS', InstitutionRS)
    .service('EbeguParameterRS', EbeguParameterRS)
    .component('dvAdminView', new AdminViewComponentConfig())
    .component('dvInstitutionView', new InstitutionViewComponentConfig())
    .component('dvParameterView', new ParameterViewComponentConfig())
    .run(adminRun);

export default EbeguWebAdmin;
