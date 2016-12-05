import ICacheFactoryService = angular.ICacheFactoryService;
import ICacheObject = angular.ICacheObject;

/**
 * Class to store cache Global
 */
export default class GlobalCacheService {

    static $inject = ['$cacheFactory'];

    cacheName: string = 'ebeguCache';

    constructor(private $cacheFactory: ICacheFactoryService) {
        $cacheFactory(this.cacheName);
    }

    getCache(): ICacheObject {
        return this.$cacheFactory.get(this.cacheName);
    }

}
