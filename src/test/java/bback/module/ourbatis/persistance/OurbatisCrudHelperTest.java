package bback.module.ourbatis.persistance;

import bback.module.ourbatis.persistance.domain.AnnotatedMember;
import bback.module.ourbatis.persistance.domain.MemberCondition;
import bback.module.ourbatis.persistance.domain.NoAnnotatedMember;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

class OurbatisCrudHelperTest {

    private static String baseProjectPath = System.getProperty("user.dir");
    private static String mapperPropertiesPath = baseProjectPath + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    private static SqlSessionFactory sqlSessionFactory;
    private static String h2BaseFilePath;

    @BeforeEach
    void setUp() throws Exception {
        // create a SqlSessionFactory
        try (Reader reader = Resources.getResourceAsReader("bback/module/ourbatis/database/config/mybatis-mapper-config.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                Connection connection = sqlSession.getConnection();
                SqlRunner sqlRunner = new SqlRunner(connection);
                String memberDDL =
                        "create table if not exists member\n" +
                        "(\n" +
                        "    member_id      BIGINT      NOT NULL AUTO_INCREMENT,\n" +
                        "    name    VARCHAR(20) NOT NULL,\n" +
                        "    age     INTEGER     NOT NULL DEFAULT 0,\n" +
                        "    gender  CHAR(1)     NOT NULL DEFAULT 'M',\n" +
                        "    PRIMARY KEY (member_id)\n" +
                        ");\n";
                sqlRunner.run(memberDDL);
            }
        }

        InputStream is = Files.newInputStream(new File(mapperPropertiesPath + "/bback/module/ourbatis/database/config/mapper.properties").toPath());
        Properties props = new Properties();
        props.load(is);
        h2BaseFilePath = props.getProperty("url").replaceAll("jdbc:h2:~", "");
        is.close();
    }

    @AfterEach
    void after() {
        String homeDir = System.getProperty("user.home");
        String mvDbFilePath = homeDir + h2BaseFilePath + ".mv.db";
        String traceDbFilePath = homeDir + h2BaseFilePath + ".trace.db";
        File mvFile = new File(mvDbFilePath);
        File traceFile = new File(traceDbFilePath);
        if (mvFile.exists()){
            mvFile.delete();
        }

        if (traceFile.exists()) {
            traceFile.delete();
        }
    }

    @Test
    @DisplayName("Member DDL 이 정상적으로 등록됐는지 확인")
    void check_created_member_ddl() throws SQLException {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Connection connection = sqlSession.getConnection();
            SqlRunner sqlRunner = new SqlRunner(connection);
            String selectMember = "select * from member";
            List<Map<String, Object>> result = sqlRunner.selectAll(selectMember);
            Assertions.assertEquals(0, result.size());
        }
    }

    @Test
    @DisplayName("Member Insert 테스트")
    void mapper_save_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            int insertAffected = memberMapper.baseSave(
                    AnnotatedMember.of(
                            null
                            , "홍길동1"
                            , 10
                            , "M"
                    )
            );
            Assertions.assertEquals(1, insertAffected);
        }
    }

    @Test
    @DisplayName("Member Insert PersistenceException 테스트")
    void mapper_save_exception_test() {
        Assertions.assertThrows(PersistenceException.class, () -> {
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                NoAnnotatedMemberMapper memberMapper = sqlSession.getMapper(NoAnnotatedMemberMapper.class);
                int insertAffected = memberMapper.baseSave(
                        NoAnnotatedMember.of(
                                null
                                , "홍길동1"
                                , 10
                                , "M"
                        )
                );
                Assertions.assertEquals(1, insertAffected);
            }
        });
    }

    @Test
    @DisplayName("Member Select All 테스트")
    void mapper_select_all_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            int insertAffected = memberMapper.baseSave(
                    AnnotatedMember.of(
                            null
                            , "홍길동1"
                            , 10
                            , "M"
                    )
            );
            Assertions.assertEquals(1, insertAffected);
            List<AnnotatedMember> memberList = memberMapper.baseSelectAll();
            Assertions.assertEquals(1, memberList.size());
            AnnotatedMember firstMember = memberList.get(0);
            Assertions.assertEquals("홍길동1", firstMember.getName());
        }
    }

    @Test
    @DisplayName("Member Select Detail 테스트")
    void mapper_select_detail_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            int insertAffected = memberMapper.baseSave(
                    AnnotatedMember.of(
                            null
                            , "홍길동1"
                            , 10
                            , "M"
                    )
            );
            Assertions.assertEquals(1, insertAffected);
            Optional<AnnotatedMember> unknownMember = memberMapper.baseSelectById(1L);
            AnnotatedMember member = unknownMember.get();
            Assertions.assertEquals("홍길동1", member.getName());
        }
    }

    @Test
    @DisplayName("Member Select Condition 테스트")
    void member_select_condition_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동1", 10, "M"));
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동2", 100, "W"));
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동3", 5, "W"));
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동4", 50, "M"));
            memberMapper.baseSave(AnnotatedMember.of(null, "1홍길동", 1, "W"));

            List<AnnotatedMember> out1 = memberMapper.baseSelectCondition(
                    MemberCondition.of(
                                null
                            , "홍길동"
                            ,  50
                            , "W"
                    )
            );
            Assertions.assertEquals(1, out1.size());

            List<AnnotatedMember> out2 = memberMapper.baseSelectCondition(
                    MemberCondition.of(
                            null
                            , "1"
                            ,  50
                            , "W"
                    )
            );
            Assertions.assertEquals(0, out2.size());

            List<AnnotatedMember> out3 = memberMapper.baseSelectCondition(
                    MemberCondition.of(
                            null
                            , "홍길동"
                            ,  5
                            , "M"
                    )
            );
            Assertions.assertEquals(2, out3.size());
            MemberCondition memberCondition = MemberCondition.of(null, "홍길동",  5, "M");
            memberCondition.setPageIndex(1);
            memberCondition.setPageSize(1);
            memberCondition.enablePaging();

            List<AnnotatedMember> members = memberMapper.baseSelectCondition(memberCondition);
            Assertions.assertEquals(1, members.size());
            Assertions.assertEquals("홍길동1", members.get(0).getName());
        }
    }

    @Test
    @DisplayName("Member Count All 테스트")
    void mapper_count_all_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동1", 10, "M"));
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동2", 100, "W"));
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동3", 5, "M"));

            int count = memberMapper.baseCountAll();
            Assertions.assertEquals(3, count);
        }
    }

    @Test
    @DisplayName("Member Update 테스트")
    void mapper_update_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동1", 10, "M"));
            AnnotatedMember member = memberMapper.baseSelectById(1L).orElseThrow(RuntimeException::new);

            Assertions.assertEquals(10, member.getAge());

            member.setAge(5);
            member.setGender("W");
            int updateAffected = memberMapper.baseUpdateById(member);
            AnnotatedMember modifiedMember = memberMapper.baseSelectById(1L).orElseThrow(RuntimeException::new);

            Assertions.assertEquals(1, updateAffected);
            Assertions.assertEquals(5, modifiedMember.getAge());
        }
    }

    @Test
    @DisplayName("Member Delete 테스트")
    void mapper_delete_test() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AnnotatedMemberMapper memberMapper = sqlSession.getMapper(AnnotatedMemberMapper.class);
            memberMapper.baseSave(AnnotatedMember.of(null, "홍길동1", 10, "M"));
            int id5DeleteAffected = memberMapper.baseDeleteById(5L);
            int id1DeleteAffected = memberMapper.baseDeleteById(1L);
            Assertions.assertEquals(0, id5DeleteAffected);
            Assertions.assertEquals(1, id1DeleteAffected);
        }
    }
}