package bback.module.ourbatis.persistance;

import bback.module.ourbatis.enums.ConditionSyntax;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface OurbatisCrudHelper<T, P> {

    @InsertProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.INSERT_HANDLER)
    int baseSave(T t);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECT_HANDLER)
    Optional<T> baseFindById(P pk);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECTS_HANDLER)
    List<T> baseFindAll();

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.SELECT_CONDITION_HANDLER)
    <C extends PageCondition> List<T> baseFindListCondition(C pageCondition);

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.COUNT_HANDLER)
    int baseCountAll();

    @SelectProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.COUNT_CONDITION_HANDLER)
    <C extends PageCondition> int baseCountCondition(C pageCondition);

    @UpdateProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.UPDATE_HANDLER)
    int baseUpdateById(T t);

    @DeleteProvider(type = OurbatisCrudProvider.class, method = OurbatisCrudProvider.DELETE_HANDLER)
    int baseDeleteById(P pk);

    default T baseGetById(P pk) {
        return this.baseFindById(pk).orElseThrow(() -> new RuntimeException("Not Found Model."));
    }

    default <X extends Throwable> T baseGetById(P pk, Supplier<? extends X> exceptionSupplier) throws X {
        return this.baseFindById(pk).orElseThrow(exceptionSupplier);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Table {
        String tableName() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface PK {
        boolean isAutoIncrement() default true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ConditionColumn {
        ConditionSyntax syntax() default ConditionSyntax.EQ;
    }
}
