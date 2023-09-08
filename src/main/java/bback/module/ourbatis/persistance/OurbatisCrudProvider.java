package bback.module.ourbatis.persistance;

import bback.module.ourbatis.helper.SnakeCaseHelper;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;

public final class OurbatisCrudProvider {

    private static final Log LOGGER = LogFactory.getLog(OurbatisCrudProvider.class);
    private static final Class<OurbatisCrudHelper.PK> PRIMARY_KEY = OurbatisCrudHelper.PK.class;
    private static final String DEFAULT_PK_COLUMN_NAME = "ID";
    private static final String COUNT_QUERY = "count(*)";
    public static final String INSERT_HANDLER = "insert";
    public static final String SELECT_HANDLER = "selectById";
    public static final String SELECTS_HANDLER = "selectAll";
    public static final String SELECT_CONDITION_HANDLER = "selectCondition";
    public static final String COUNT_HANDLER = "countAll";
    public static final String COUNT_CONDITION_HANDLER = "countCondition";
    public static final String UPDATE_HANDLER = "updateById";
    public static final String DELETE_HANDLER = "deleteById";

    public <T> String insert(T t) {
        if ( t == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Class<?> classType = t.getClass();
        sql = sql.INSERT_INTO(getTableName(classType));

        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            OurbatisCrudHelper.PK pk = f.getAnnotation(PRIMARY_KEY);
            if ( pk != null && pk.isAutoIncrement() ) {
                continue;
            }
            String fieldName = f.getName();
            sql = sql.INTO_COLUMNS(getColumnName(fieldName))
                    .INTO_VALUES(String.format("#{%s}", fieldName));
        }
        String query = sql.toString();
        logging(query);
        return query;
    }

    public <P> String selectById(P pk, ProviderContext context) throws IllegalAccessException {
        if ( pk == null ) throw new IllegalArgumentException();
        Class<?> domainType = this.getDomainClassType(context);
        SQL sql = new SQL();
        Field pkField = null;
        Field[] fields = domainType.getDeclaredFields();
        for (Field f : fields) {
            if ( f.getAnnotation(PRIMARY_KEY) != null ) {
                pkField = f;
            }
            sql = sql.SELECT(getColumnName(f));
        }
        sql = sql.FROM(getTableName(domainType));
        sql = sql.WHERE(String.format("%s = %s", getPrimaryKeyColumnName(pkField), pk));
        String query = sql.toString();
        logging(query);
        return query;
    }

    public String selectAll(ProviderContext context) throws IllegalAccessException {
        Class<?> domainType = this.getDomainClassType(context);
        SQL sql = new SQL();
        Field[] fields = domainType.getDeclaredFields();
        for (Field f : fields) {
            sql = sql.SELECT(getColumnName(f));
        }
        sql = sql.FROM(getTableName(domainType));
        String query = sql.toString();
        logging(query);
        return query;
    }

    public <C extends PageCondition> String selectCondition(C condition, ProviderContext context) throws IllegalAccessException {
        if ( condition == null ) {
            return selectAll(context);
        }

        SQL sql = new SQL();
        Class<? extends PageCondition> conditionClass = condition.getClass();
        Field[] fields = conditionClass.getDeclaredFields();
        for (Field f : fields) {
            String fieldName = f.getName();
            String columnName = getColumnName(fieldName);
            sql = sql.SELECT(columnName);

            OurbatisCrudHelper.ConditionColumn conditionColumn = f.getAnnotation(OurbatisCrudHelper.ConditionColumn.class);
            if ( conditionColumn == null ) continue;
            String mybatisParameterSyntax = String.format("#{%s}", fieldName);
            // String.format 은 따옴표 사용 시 Exception 발생
            String formatSyntax = MessageFormat.format(conditionColumn.syntax().get(), mybatisParameterSyntax);
            sql = sql.WHERE(String.format("%s %s", columnName, formatSyntax));
        }

        sql = sql.FROM(this.getTableName(conditionClass));

        if (condition.isPaging()) {
            sql = sql.LIMIT(condition.getPageSize());
            sql = sql.OFFSET(condition.getStartPage());
        }

        String query = sql.toString();
        logging(query);
        return query;
    }

    public String countAll(ProviderContext context) throws IllegalAccessException {
        Class<?> domainType = this.getDomainClassType(context);
        SQL sql = new SQL();
        sql = sql.SELECT(COUNT_QUERY);
        sql = sql.FROM(getTableName(domainType));
        String query = sql.toString();
        logging(query);
        return query;
    }

    public <C extends PageCondition> String countCondition(C condition, ProviderContext context) throws IllegalAccessException{
        if ( condition == null ) {
            return countAll(context);
        }

        Class<?> conditionClassType = condition.getClass();
        SQL sql = new SQL();
        sql = sql.SELECT(COUNT_QUERY);
        sql = sql.FROM(getTableName(conditionClassType));

        Field[] fields = conditionClassType.getDeclaredFields();
        for (Field f : fields) {
            String fieldName = f.getName();
            OurbatisCrudHelper.ConditionColumn conditionColumn = f.getAnnotation(OurbatisCrudHelper.ConditionColumn.class);
            if ( conditionColumn == null ) continue;
            String mybatisParameterSyntax = String.format("#{%s}", fieldName);
            // String.format 은 따옴표 사용 시 Exception 발생
            String formatSyntax = MessageFormat.format(conditionColumn.syntax().get(), mybatisParameterSyntax);
            sql = sql.WHERE(String.format("%s %s", getColumnName(fieldName), formatSyntax));
        }

        String query = sql.toString();
        logging(query);
        return query;
    }

    public <T> String updateById(T t) {
        if ( t == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Class<?> classType = t.getClass();
        sql = sql.UPDATE(getTableName(classType));

        Field[] fields = classType.getDeclaredFields();
        Field pkField = null;
        for (Field f : fields) {
            OurbatisCrudHelper.PK candidate = f.getAnnotation(PRIMARY_KEY);
            if ( candidate != null ) {
                pkField = f;
                if ( candidate.isAutoIncrement() ) {
                    continue;
                }
            }
            String fieldName = f.getName();
            sql = sql.SET(String.format("%s = #{%s}", getColumnName(fieldName), fieldName));
        }

        String pkColumnName = pkField == null ? DEFAULT_PK_COLUMN_NAME : pkField.getName();
        sql = sql.WHERE(String.format("%s = #{%s}", SnakeCaseHelper.translate(pkColumnName), pkColumnName));

        String query = sql.toString();
        logging(query);
        return query;
    }

    public <P> String deleteById(P pk, ProviderContext context) throws IllegalAccessException {
        Class<?> domainType = this.getDomainClassType(context);
        if ( pk == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        sql.DELETE_FROM(getTableName(domainType));
        Field[] fields = domainType.getDeclaredFields();
        Field pkField = null;
        for (Field f : fields) {
            OurbatisCrudHelper.PK candidate = f.getAnnotation(PRIMARY_KEY);
            if ( candidate != null ) {
                pkField = f;
            }
        }
        sql = sql.WHERE(String.format("%s = %s", getPrimaryKeyColumnName(pkField), pk));

        String query = sql.toString();
        logging(query);
        return query;
    }

    private Class<?> getDomainClassType(ProviderContext context) throws IllegalAccessException {
        Class<?> mapperType = context.getMapperType();
        Type[] genericInterfaces = mapperType.getGenericInterfaces();
        return Arrays.stream(genericInterfaces)
                .filter(ParameterizedType.class::isInstance)
                .map(type -> (Class<?>)((ParameterizedType) type).getActualTypeArguments()[0])
                .findFirst()
                .orElseThrow(IllegalAccessException::new);
    }

    private String getTableName(Class<?> domainClassType) {
        OurbatisCrudHelper.Table table = domainClassType.getAnnotation(OurbatisCrudHelper.Table.class);
        String tableName = (table == null || table.tableName() == null || table.tableName().isEmpty())
                ? domainClassType.getSimpleName() : table.tableName();
        return getCamel2Snake(tableName);
    }

    private String getColumnName(Field f) {
        return getColumnName(f.getName());
    }

    private String getColumnName(String fieldName) {
        return getCamel2Snake(fieldName);
    }

    private String getPrimaryKeyColumnName(Field pkField) {
        return pkField == null ? DEFAULT_PK_COLUMN_NAME : getColumnName(pkField);
    }

    private String getCamel2Snake(String value) {
        return SnakeCaseHelper.translate(value);
    }

    private void logging(String query) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
    }
}
