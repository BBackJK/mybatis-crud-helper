package bback.module.ourbatis.interceptors;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
        , @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class })
})
public class ExecutorInterceptor implements Interceptor {

    private static final String QUERY = "query";
    private static final String UPDATE = "update";

    private final List<PreQueryDelegator> preQueryDelegatorList;
    private final List<PostQueryDelegator> postQueryDelegatorList;
    private final List<PreCommandDelegator> preCommandDelegatorList;
    private final List<PostCommandDelegator> postCommandDelegatorList;

    public ExecutorInterceptor(
            List<PreQueryDelegator> preQueryDelegatorList
            , List<PostQueryDelegator> postQueryDelegatorList
            , List<PreCommandDelegator> preCommandDelegatorList
            , List<PostCommandDelegator> postCommandDelegatorList
    ) {
        Collections.sort(preQueryDelegatorList);
        Collections.sort(postQueryDelegatorList);
        Collections.sort(preCommandDelegatorList);
        Collections.sort(postCommandDelegatorList);
        this.preQueryDelegatorList = preQueryDelegatorList;
        this.postQueryDelegatorList = postQueryDelegatorList;
        this.preCommandDelegatorList = preCommandDelegatorList;
        this.postCommandDelegatorList = postCommandDelegatorList;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor) invocation.getTarget();
        String signatureMethodName = invocation.getMethod().getName();
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object queryParameter = args[1];  // query parameter

        Object result = null;

        switch (signatureMethodName) {
            case QUERY:
                RowBounds rowBounds = (RowBounds) args[2];
                ResultHandler<?> resultHandler = (ResultHandler<?>) args[3];
                for (PreQueryDelegator delegator : this.preQueryDelegatorList) {
                    delegator.doIntercept(executor, mappedStatement, queryParameter, rowBounds, resultHandler);
                }
                result = invocation.proceed();
                for (PostQueryDelegator delegator : this.postQueryDelegatorList) {
                    delegator.doIntercept(executor, mappedStatement, queryParameter, rowBounds, resultHandler, result);
                }

                break;
            case UPDATE:
                for (PreCommandDelegator delegator : this.preCommandDelegatorList) {
                    delegator.doIntercept(mappedStatement, queryParameter);
                }
                result = invocation.proceed();
                for (PostCommandDelegator delegator : this.postCommandDelegatorList) {
                    delegator.doIntercept(mappedStatement, queryParameter, result);
                }
                break;
            default:
                throw new SQLException();
        }
        return result;
    }
}
