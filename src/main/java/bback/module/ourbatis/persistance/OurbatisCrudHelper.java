package bback.module.ourbatis.persistance;

import bback.module.ourbatis.helper.SnakeCaseHelper;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Optional;

public interface OurbatisCrudHelper<T, P> {

    Class<T> getClassType();

    @InsertProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.INSERT_HANDLER)
    int baseSave(T t);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECT_HANDLER)
    Optional<T> baseSelectById(Class<T> classType, P pk);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECTS_HANDLER)
    List<T> baseSelectAll(Class<T> classType);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.COUNT_HANDLER)
    int baseCountAll(Class<T> classType);

    @UpdateProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.UPDATE_HANDLER)
    int baseUpdateById(T t);

    @DeleteProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.DELETE_HANDLER)
    int baseDeleteById(Class<T> classType, P pk);

    default Optional<T> baseSelectById(P pk) {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        }
        return this.baseSelectById(classType, pk);
    }

    default List<T> baseSelectAll() {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        }
        return this.baseSelectAll(classType);
    }

    default int baseDeleteById(P pk) {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        }
        return this.baseDeleteById(classType, pk);
    }

    default int baseCountAll() {
        Class<T> classType = this.getClassType();
        if ( classType == null ) {
            throw new IllegalArgumentException(SnakeCaseHelper.CLASS_TYPE_WARNING);
        }
        return this.baseCountAll(classType);
    }
}
