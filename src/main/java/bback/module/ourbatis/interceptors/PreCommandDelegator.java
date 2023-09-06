package bback.module.ourbatis.interceptors;

import org.apache.ibatis.mapping.MappedStatement;

public interface PreCommandDelegator extends ListableDelegator {

    void doIntercept(MappedStatement ms, Object parameter) throws Throwable;
}
