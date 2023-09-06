package bback.module.spring;

import bback.module.ourbatis.interceptors.*;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class OurbatisAutoConfiguration implements InitializingBean {

    private static final Log LOGGER = LogFactory.getLog(OurbatisAutoConfiguration.class);

    @Bean
    @ConditionalOnBean(ListableDelegator.class)
    public ExecutorInterceptor executorInterceptor(
            List<PreQueryDelegator> preQueryDelegatorList
            , List<PostQueryDelegator> postQueryDelegatorList
            , List<PreCommandDelegator> preCommandDelegatorList
            , List<PostCommandDelegator> postCommandDelegatorList
    ) {
        return new ExecutorInterceptor(
                preQueryDelegatorList
                , postQueryDelegatorList
                , preCommandDelegatorList
                , postCommandDelegatorList
        );
    }

    @Bean
    @ConditionalOnBean(PrepareDelegator.class)
    public StatementInterceptor statementInterceptor(PrepareDelegator prepareDelegator) {
        return new StatementInterceptor(prepareDelegator);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("OurbatisAutoConfiguration register bean complete..");
    }
}