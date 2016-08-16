import 'angular';
import './app.module.less';
import {EbeguWebCore} from './core/core.module';
import {EbeguWebAdmin} from './admin/admin.module';
import {EbeguWebGesuch} from './gesuch/gesuch.module';
import {EbeguWebPendenzen} from './pendenzen/pendenzen.module';
import {EbeguWebPendenzenInstitution} from './pendenzenInstitution/pendenzenInstitution.module';

export default angular.module('ebeguWeb', [EbeguWebCore.name, EbeguWebAdmin.name, EbeguWebGesuch.name, EbeguWebPendenzen.name, EbeguWebPendenzenInstitution.name]);
