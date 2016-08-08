import {EbeguWebCore} from '../core/core.module';
import {PendenzenInstitutionListViewComponentConfig} from './component/pendenzenInstitutionListView/pendenzenInstitutionListView';
import {pendenzRun} from './pendenzenInstitution.route';
import PendenzInstitutionRS from './service/PendenzInstitutionRS.rest';
import {PendenzInstitutionFilter} from './filter/pendenzInstitutionFilter';

export const EbeguWebPendenzenInstitution =
    angular.module('ebeguWeb.pendenzenInstitution', [EbeguWebCore.name])
        .run(pendenzRun)
        .service('PendenzInstitutionRS', PendenzInstitutionRS)
        .filter('pendenzInstitutionFilter', PendenzInstitutionFilter)
        .component('pendenzenInstitutionListView', new PendenzenInstitutionListViewComponentConfig());
