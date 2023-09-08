package bback.module.ourbatis.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class SnakeCaseHelperTest {

    @Test
    @DisplayName("문자열 카멜케이스를 스네이크케이스로 변환 Test")
    void convertCamel2Snake() {
        String testValue1 = "memberId";
        String testValue2 = "memberBizNo";

        String convertedValue1 = SnakeCaseHelper.translate(testValue1);
        String convertedValue2 = SnakeCaseHelper.translate(testValue2);

        Assertions.assertEquals("member_id", convertedValue1);
        Assertions.assertEquals("member_biz_no", convertedValue2);
    }
}