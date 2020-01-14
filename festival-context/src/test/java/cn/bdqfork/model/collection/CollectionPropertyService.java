package cn.bdqfork.model.collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/1/6
 */
@Singleton
@Named
public class CollectionPropertyService {
    @Inject
    private List<CollectionPropertyDao> daos;

    @Inject
    private Map<String, CollectionPropertyDao> daoMap;

    public List<CollectionPropertyDao> getDaos() {
        return daos;
    }

    public Map<String, CollectionPropertyDao> getDaoMap() {
        return daoMap;
    }
}
