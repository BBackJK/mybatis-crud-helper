package bback.module.ourbatis.interceptors;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.lang.Nullable;

import java.sql.Connection;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class StatementInterceptor implements Interceptor {

    @Nullable
    private final PrepareDelegator prePrepareDelegator;

    public StatementInterceptor(PrepareDelegator prePrepareDelegator) {
        this.prePrepareDelegator = prePrepareDelegator;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        Object[] args = invocation.getArgs();
        Connection connection = (Connection) args[0];
        return this.prePrepareDelegator == null
                ? invocation.proceed()
                : this.prePrepareDelegator.doProceed(invocation, statementHandler, connection);
    }
}
