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
    public List<CollectionPropertyDao> daos;

    @Inject
    public Map<String, CollectionPropertyDao> daoMap;
}
