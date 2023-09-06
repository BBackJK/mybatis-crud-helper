package bback.module.ourbatis.interceptors;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Invocation;

import java.sql.Connection;

public interface PrepareDelegator {

    Object doProceed(Invocation invocation, StatementHandler statementHandler, Connection connection) throws Throwable;
}
