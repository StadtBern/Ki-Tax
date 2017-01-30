import {EbeguWebCore} from '../core/core.module';
import {posteingangRun} from './posteingang.route';
import {PosteingangViewComponentConfig} from './component/posteingangView';
import {PosteingangFilter} from './filter/posteingangFilter';

export const EbeguWebPosteingang =
    angular.module('ebeguWeb.posteingang', [EbeguWebCore.name])
        .run(posteingangRun)
        .filter('posteingangFilter', PosteingangFilter)
        .component('posteingangView', new PosteingangViewComponentConfig());
