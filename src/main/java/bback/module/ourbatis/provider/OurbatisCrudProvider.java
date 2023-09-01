package bback.module.ourbatis.provider;

import bback.module.ourbatis.annotations.PK;
import bback.module.ourbatis.util.SnakeCaseUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.Field;

public class OurbatisCrudProvider {

    private static final Log LOGGER = LogFactory.getLog(OurbatisCrudProvider.class);
    private static final Class<PK> PRIMARY_KEY = PK.class;
    private static final String DEFAULT_PK_COLUMN_NAME = "ID";
    private static final String COUNT_QUERY = "count(*)";
    public static final String INSERT_HANDLER = "insert";
    public static final String SELECT_HANDLER = "selectById";
    public static final String SELECTS_HANDLER = "selectAll";
    public static final String SELECTS_CONDITION_HANDLER = "selectAllCondition";
    public static final String COUNT_HANDLER = "countAll";
    public static final String COUNT_CONDITION_HANDLER = "countAllCondition";
    public static final String UPDATE_HANDLER = "updateById";
    public static final String DELETE_HANDLER = "deleteById";

    public <T> String insert(T t) {
        if ( t == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Class<?> classType = t.getClass();

        sql = sql.INSERT_INTO(SnakeCaseUtils.translate(classType.getSimpleName()));

        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            PK pk = f.getAnnotation(PRIMARY_KEY);
            if ( pk != null && pk.isAutoIncrement() ) {
                continue;
            }
            String fieldName = f.getName();
            sql = sql.INTO_COLUMNS(SnakeCaseUtils.translate(fieldName))
                    .INTO_VALUES(String.format("#{%s}", fieldName));
        }
        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T, R> String selectById(Class<T> classType, R r) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        if ( r == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Field[] fields = classType.getDeclaredFields();
        Field pkField = null;
        for (Field f : fields) {
            if ( f.getAnnotation(PRIMARY_KEY) != null ) {
                pkField = f;
            }
            sql = sql.SELECT(SnakeCaseUtils.translate(f.getName()));
        }
        sql = sql.FROM(SnakeCaseUtils.translate(classType.getSimpleName()));

        String pkColumnName = pkField == null ? DEFAULT_PK_COLUMN_NAME : SnakeCaseUtils.translate(pkField.getName());
        sql = sql.WHERE(String.format("%s = %s", pkColumnName, r));

        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T> String selectAll(Class<T> classType) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        SQL sql = new SQL();
        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            sql = sql.SELECT(SnakeCaseUtils.translate(f.getName()));
        }
        sql = sql.FROM(SnakeCaseUtils.translate(classType.getSimpleName()));

        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T> String selectAllCondition(Class<T> classType, T condition) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        SQL sql = new SQL();
        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            sql = sql.SELECT(SnakeCaseUtils.translate(f.getName()));
        }
        sql = sql.FROM(SnakeCaseUtils.translate(classType.getSimpleName()));

        if (condition != null) {
            for (Field f : fields) {
                f.setAccessible(true);

                Object value;
                try {
                    value = f.get(condition);
                } catch (IllegalAccessException ignore) {
                    value = null;
                }
                if ( value != null ) {
                    sql = sql.WHERE(String.format("%s = #{param2.%s}", SnakeCaseUtils.translate(f.getName()), f.getName()));
                }
            }
        }
        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T> String countAll(Class<T> classType) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        SQL sql = new SQL();
        sql = sql.SELECT(COUNT_QUERY);
        sql = sql.FROM(SnakeCaseUtils.translate(classType.getSimpleName()));

        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T> String countAllCondition(Class<T> classType, T condition) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        SQL sql = new SQL();
        sql = sql.SELECT(COUNT_QUERY);
        sql = sql.FROM(SnakeCaseUtils.translate(classType.getSimpleName()));

        Field[] fields = classType.getDeclaredFields();
        if (condition != null) {
            for (Field f : fields) {
                f.setAccessible(true);

                Object value;
                try {
                    value = f.get(condition);
                } catch (IllegalAccessException ignore) {
                    value = null;
                }
                if ( value != null ) {
                    sql = sql.WHERE(String.format("%s = #{param2.%s}", SnakeCaseUtils.translate(f.getName()), f.getName()));
                }
            }
        }

        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T> String updateById(T t) {
        if ( t == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Class<?> classType = t.getClass();
        sql = sql.UPDATE(SnakeCaseUtils.translate(classType.getSimpleName()));

        Field[] fields = classType.getDeclaredFields();
        Field pkField = null;
        for (Field f : fields) {
            PK candidate = f.getAnnotation(PRIMARY_KEY);
            if ( candidate != null ) {
                pkField = f;
                if ( candidate.isAutoIncrement() ) {
                    continue;
                }
            }
            String fieldName = f.getName();
            sql = sql.SET(String.format("%s = #{%s}", SnakeCaseUtils.translate(fieldName), fieldName));
        }

        String pkColumnName = pkField == null ? DEFAULT_PK_COLUMN_NAME : pkField.getName();
        sql = sql.WHERE(String.format("%s = #{%s}", SnakeCaseUtils.translate(pkColumnName), pkColumnName));

        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }

    public <T, R> String deleteById(Class<T> classType, R r) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        if ( r == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        sql.DELETE_FROM(SnakeCaseUtils.translate(classType.getSimpleName()));
        Field[] fields = classType.getDeclaredFields();
        Field pk = null;
        for (Field f : fields) {
            PK candidate = f.getAnnotation(PRIMARY_KEY);
            if ( candidate != null ) {
                pk = f;
            }
        }
        String pkColumnName = pk == null ? DEFAULT_PK_COLUMN_NAME : SnakeCaseUtils.translate(pk.getName());
        sql = sql.WHERE(String.format("%s = %s", pkColumnName, r));

        String query = sql.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
        return query;
    }
}
