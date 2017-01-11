import ICacheFactoryService = angular.ICacheFactoryService;
import ICacheObject = angular.ICacheObject;
import {TSCacheTyp, getTSCacheTypValues} from '../../models/enums/TSCacheTyp';

/**
 * Class to store cache Global
 */
export default class GlobalCacheService {

    static $inject = ['$cacheFactory'];

    constructor(private $cacheFactory: ICacheFactoryService) {
        for (let cache of getTSCacheTypValues()) {
            $cacheFactory(TSCacheTyp[cache]);
        }
    }

    getCache(cacheType: TSCacheTyp): ICacheObject {
        return this.$cacheFactory.get(TSCacheTyp[cacheType]);
    }

}
