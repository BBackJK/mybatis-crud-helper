package bback.module.ourbatis.interceptors;

import org.apache.ibatis.mapping.MappedStatement;

public interface PostCommandDelegator extends ListableDelegator {

    void doIntercept(MappedStatement ms, Object parameter, Object result) throws Throwable;
}
