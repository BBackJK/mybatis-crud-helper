package bback.module.ourbatis.persistance.domain;

import bback.module.ourbatis.enums.ConditionSyntax;
import bback.module.ourbatis.persistance.OurbatisCrudHelper;
import bback.module.ourbatis.persistance.PageCondition;

@OurbatisCrudHelper.Table(tableName = "member")
public class MemberCondition extends PageCondition {

    private Long memberId;

    @OurbatisCrudHelper.ConditionColumn(syntax = ConditionSyntax.R_LIKE)
    private String name;

    @OurbatisCrudHelper.ConditionColumn(syntax = ConditionSyntax.BIGGER)
    private Integer age;

    @OurbatisCrudHelper.ConditionColumn
    private String gender;

    public MemberCondition(Long memberId, String name, Integer age, String gender) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public static MemberCondition of(
            Long memberId
            , String name
            , Integer age
            , String gender
    ) {
        return new MemberCondition(memberId, name, age, gender);
    }
}
