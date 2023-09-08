package bback.module.ourbatis.persistance.domain;

public class NoAnnotatedMember {

    private Long memberId;
    private String name;
    private Integer age;
    private String gender;

    public NoAnnotatedMember(Long memberId, String name, Integer age, String gender) {
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

    public static NoAnnotatedMember of(
            Long memberId
            , String name
            , Integer age
            , String gender
    ) {
        return new NoAnnotatedMember(memberId, name, age, gender);
    }

    public String getName() {
        return this.name;
    }
}
