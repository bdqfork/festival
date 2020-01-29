package cn.bdqfork.security.util;

import cn.bdqfork.security.common.LogicType;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.vertx.reactivex.ext.auth.User;

import java.util.List;

/**
 * @author bdq
 * @since 2020/1/28
 */
public class SecurityUtils {
    public static Observable<Boolean> isPermited(User user, String[] permitsOrRoles, LogicType logicType) {
        return Observable.fromArray(permitsOrRoles)
                .flatMap(new Function<String, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(String role) throws Exception {
                        return user.rxIsAuthorized(role)
                                .toObservable();
                    }
                })
                .toList()
                .map(new Function<List<Boolean>, Boolean>() {
                    @Override
                    public Boolean apply(List<Boolean> results) throws Exception {
                        boolean finalResult = results.get(0);
                        for (int i = 1; i < results.size(); i++) {
                            if (logicType == LogicType.AND) {
                                finalResult = finalResult && results.get(i);
                            }
                            if (logicType == LogicType.OR) {
                                finalResult = finalResult || results.get(i);
                            }
                        }
                        return finalResult;
                    }
                }).toObservable();
    }
}
