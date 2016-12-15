import {EbeguWebCore} from '../core/core.module';
import {faelleRun} from './faelle.route';
import {FaelleListViewComponentConfig} from './component/faelleListView';

export const EbeguWebFaelle =
    angular.module('ebeguWeb.faelle', [EbeguWebCore.name])
        .run(faelleRun)
        // .service('PendenzInstitutionRS', PendenzInstitutionRS)
        // .filter('pendenzInstitutionFilter', PendenzInstitutionFilter)
        .component('faelleListView', new FaelleListViewComponentConfig());
