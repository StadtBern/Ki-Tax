import {EbeguWebCore} from '../core/core.module';
import {MitteilungenViewComponentConfig} from './component/mitteilungenView/mitteilungenView';
import {mitteilungenRun} from './mitteilungen.route';

export const EbeguWebMitteilungen =
    angular.module('ebeguWeb.mitteilungen', [EbeguWebCore.name])
        .run(mitteilungenRun)
        .component('mitteilungenView', new MitteilungenViewComponentConfig());
