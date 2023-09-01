# Ourbatis

## 1. Overview

Mybatis Provider 를 활용하여 기본적인 Crud 에 대해 xml 로 쿼리를 작성하지 않아도 Crud 를 도와주는 헬퍼 라이브러리.

## 2. Quick Start

### 2.1 Maven

```xml
...
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
...
<dependency>
    <groupId>com.github.BBackJK</groupId>
    <artifactId>mybatis-crud-helper</artifactId>
    <version>v1.0.0</version>
</dependency>
```

### 2.2 Gradle

```groovy
...
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
...
dependencies {
    implementation 'com.github.BBackJK:mybatis-crud-helper:v0.1.0'
}
...
```

## 3. How To Use

Jpa 와 비슷하게 사용.

```sql
DROP TABLE IF EXISTS member;

CREATE TABLE member
(
    member_id      BIGINT      NOT NULL AUTO_INCREMENT,
    name    VARCHAR(20) NOT NULL,
    age     INTEGER     NOT NULL DEFAULT 0,
    gender  CHAR(1)     NOT NULL DEFAULT 'M',
    PRIMARY KEY (member_id)
);
```

위와 같은 테이블이 있을 경우, 다음과 같은 도메인 POJO 작성

```java
@Getter
@AllArgsConstructor(staticName = "of")  // 객체를 생성하기 위한 lombok
@ToString               // 로깅을 위한 lombok
public class Member {

    /**
     * @PK 어노테이션으로 해당 field 가 PK 임을 선언한다. PK가 없을 시, **ById 메소드 및 baseUpdateById 및 baseSave 가 에러가 발생.
     * 이 @PK 어노테이션은 기본적으로 AutoIncrement PK 라고 인식. 따라서 AutoIncrement PK 가 아닐 시
     * 다음과 같이 사용 
     *      @PK(isAutoIncrement=false)
     */
    @PK
    private Long memberId;
    private String name;
    private Integer age;
    private String gender;
}
```

그에 맞는 Dao Mapper 작성

```java
@Mapper
public interface MemberDao extends OurbatisCrudHelper<Member, Long> {

    @Override
    default Class<Member> getClassType() {  // 해당 메소드를 꼭 구현해야 정상적으로 동작함. (Reflect 로 동작하기 때문.)
        return Member.class;
    }
}
```

그럼 다음과 같이 사용 가능.

```java
@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberDao memberDao;
    
    public void test() {
        Member dummy = Member.of(null, "더미1", 10, "W");
        
        // save 동작
        int insertAffected = memberDao.baseSave(dummu);
        
        // Id 가 1인 Member 를 찾는다.
        Optional<Member> detail = memberDao.baseSelectById(1L);
        
        // 모든 Member 목록 반환
        List<Member> listAll = memberDao.baseSelectAll();
        
        // Member 의 field 를 기반으로 동적 where 생성
        // 배포버전 0.1.0 기준으로 `like` 및 `in` 과 같은 쿼리는 사용 X. 무조건 `=`
        Member conditionDummy = Member.of(null, null, null, "W");
        List<Member> filteringList = memberDao.baseSelectCondition(conditionDummy);
        
        // 모든 Member 의 수를 반환 
        int countAll = memberDao.baseCountAll();

        // filteringList 와 같은 동작으로 count 를 반환
        Member conditionCountDummy = Member.of(null, null, null, "W");
        int countCondition = memberDao.baseCountCondition(conditionCountDummy);

        // save 했던 dummy 를 나이가 10 에서 100 으로, 성별이 W 에서 M 으로 변경하려고 할 때,
        // side effect warning : field 가 null 일 경우 그대로 null 로 update 쳐짐. 따라서, baseSelectById 로 가져온 객체에 변경하고 싶은 값을 변경 후 update.
        Member updatingDummy = Member.of(1L, "더미1", 100, "M");
        int updateAffected = memberDao.baseUpdateById(updatingDummy);
        
        // 저장했던 1번 dummy Member 에 대해서 삭제
        int deleteAffected = memberDao.baseDeleteById(1L);
    }
    
}

```