package bback.module.ourbatis.interceptors;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;

public interface PreCommandDelegator extends ListableDelegator {

    void doIntercept(Executor executor, MappedStatement ms, Object parameter) throws Throwable;
}
