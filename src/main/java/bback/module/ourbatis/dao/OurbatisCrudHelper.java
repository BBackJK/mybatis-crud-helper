package bback.module.ourbatis.dao;

import bback.module.ourbatis.provider.OurbatisCrudProvider;
import bback.module.ourbatis.util.SnakeCaseUtils;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Optional;

public interface OurbatisCrudHelper<T, R> {

    Class<T> getClassType();

    @InsertProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.INSERT_HANDLER)
    int baseSave(T t);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECT_HANDLER)
    Optional<T> baseSelectById(Class<T> classType, R r);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECTS_HANDLER)
    List<T> baseSelectAll(Class<T> classType);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECTS_CONDITION_HANDLER)
    List<T> baseSelectCondition(Class<T> classType, T condition);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.COUNT_HANDLER)
    int baseCountAll(Class<T> classType);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.COUNT_CONDITION_HANDLER)
    int baseCountCondition(Class<T> classType, T condition);

    @UpdateProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.UPDATE_HANDLER)
    int baseUpdateById(T t);

    @DeleteProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.DELETE_HANDLER)
    int baseDeleteById(Class<T> classType, R r);

    default Optional<T> baseSelectById(R r) {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        }
        return this.baseSelectById(classType, r);
    }

    default List<T> baseSelectAll() {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        }
        return this.baseSelectAll(classType);
    }

    default List<T> baseSelectCondition(T t) {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        }
        return this.baseSelectCondition(classType, t);
    }

    default int baseCountCondition(T t) {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        }
        return this.baseCountCondition(classType, t);
    }

    default int baseDeleteById(R r) {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        }
        return this.baseDeleteById(classType, r);
    }

    default int baseCountAll() {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseUtils.CLASS_TYPE_WARNING);
        }
        return this.baseCountAll(classType);
    }
}
