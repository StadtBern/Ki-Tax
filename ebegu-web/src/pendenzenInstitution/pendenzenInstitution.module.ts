import {EbeguWebCore} from '../core/core.module';
import {PendenzenInstitutionListViewComponentConfig} from './component/pendenzenInstitutionListView/pendenzenInstitutionListView';
import {pendenzRun} from './pendenzenInstitution.route';
import PendenzInstitutionRS from './service/PendenzInstitutionRS.rest';

export const EbeguWebPendenzenInstitution =
    angular.module('ebeguWeb.pendenzenInstitution', [EbeguWebCore.name])
        .run(pendenzRun)
        .service('PendenzInstitutionRS', PendenzInstitutionRS)
        .component('pendenzenInstitutionListView', new PendenzenInstitutionListViewComponentConfig());
