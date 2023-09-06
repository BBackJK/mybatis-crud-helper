package bback.module.ourbatis.persistance;

import bback.module.ourbatis.annotations.PK;
import bback.module.ourbatis.helper.SnakeCaseHelper;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.Field;

public final class OurbatisCrudProvider {

    private static final Log LOGGER = LogFactory.getLog(OurbatisCrudProvider.class);
    private static final Class<PK> PRIMARY_KEY = PK.class;
    private static final String DEFAULT_PK_COLUMN_NAME = "ID";
    private static final String COUNT_QUERY = "count(*)";
    public static final String INSERT_HANDLER = "insert";
    public static final String SELECT_HANDLER = "selectById";
    public static final String SELECTS_HANDLER = "selectAll";
    public static final String COUNT_HANDLER = "countAll";
    public static final String UPDATE_HANDLER = "updateById";
    public static final String DELETE_HANDLER = "deleteById";

    public <T> String insert(T t) {
        if ( t == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Class<?> classType = t.getClass();

        sql = sql.INSERT_INTO(getTableName(classType));

        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            PK pk = f.getAnnotation(PRIMARY_KEY);
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

    public <T, P> String selectById(Class<T> classType, P pk) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        if ( pk == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        Field pkField = null;
        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            if ( f.getAnnotation(PRIMARY_KEY) != null ) {
                pkField = f;
            }
            sql = sql.SELECT(getColumnName(f));
        }
        sql = sql.FROM(getTableName(classType));
        sql = sql.WHERE(String.format("%s = %s", getPrimaryKeyColumnName(pkField), pk));

        String query = sql.toString();
        logging(query);
        return query;
    }

    public <T> String selectAll(Class<T> classType) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        SQL sql = new SQL();
        Field[] fields = classType.getDeclaredFields();
        for (Field f : fields) {
            sql = sql.SELECT(getColumnName(f));
        }
        sql = sql.FROM(getTableName(classType));

        String query = sql.toString();
        logging(query);
        return query;
    }

    public <T> String countAll(Class<T> classType) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        SQL sql = new SQL();
        sql = sql.SELECT(COUNT_QUERY);
        sql = sql.FROM(getTableName(classType));
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
            PK candidate = f.getAnnotation(PRIMARY_KEY);
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

    public <T, P> String deleteById(Class<T> classType, P pk) {
        if ( classType == null ) throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        if ( pk == null ) throw new IllegalArgumentException();
        SQL sql = new SQL();
        sql.DELETE_FROM(getTableName(classType));
        Field[] fields = classType.getDeclaredFields();
        Field pkField = null;
        for (Field f : fields) {
            PK candidate = f.getAnnotation(PRIMARY_KEY);
            if ( candidate != null ) {
                pkField = f;
            }
        }
        sql = sql.WHERE(String.format("%s = %s", getPrimaryKeyColumnName(pkField), pk));

        String query = sql.toString();
        logging(query);
        return query;
    }

    private String getTableName(Class<?> classType) {
        return SnakeCaseHelper.translate(classType.getSimpleName());
    }

    private String getColumnName(Field f) {
        return getColumnName(f.getName());
    }

    private String getColumnName(String fieldName) {
        return SnakeCaseHelper.translate(fieldName);
    }

    private String getPrimaryKeyColumnName(Field pkField) {
        return pkField == null ? DEFAULT_PK_COLUMN_NAME : getColumnName(pkField);
    }

    private void logging(String query) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query :: \n" + query);
        }
    }
}
