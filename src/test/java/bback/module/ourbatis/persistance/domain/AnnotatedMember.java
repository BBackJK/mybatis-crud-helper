package bback.module.ourbatis.persistance.domain;

import bback.module.ourbatis.persistance.OurbatisCrudHelper;

@OurbatisCrudHelper.Table(tableName = "member")
public class AnnotatedMember {

    @OurbatisCrudHelper.PK
    private Long memberId;
    private String name;
    private Integer age;
    private String gender;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public AnnotatedMember(Long memberId, String name, Integer age, String gender) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }

    public static AnnotatedMember of(
            Long memberId
            , String name
            , Integer age
            , String gender
    ) {
        return new AnnotatedMember(memberId, name, age, gender);
    }

    public String getName() {
        return this.name;
    }

    public Integer getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}
