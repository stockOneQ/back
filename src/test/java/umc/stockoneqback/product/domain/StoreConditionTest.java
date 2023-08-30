package umc.stockoneqback.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.exception.ProductErrorCode;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StoreCondition 도메인 테스트")
class StoreConditionTest {
        @Test
        @DisplayName("존재하지 않는 보관방법이 입력되면 예외가 발생한다")
        void throwExceptionByWrongStoreCondition() {
            assertThatThrownBy(() -> StoreCondition.from(StoreCondition.ROOM.getValue() + "거짓"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ProductErrorCode.NOT_FOUND_STORE_CONDITION.getMessage());
        }
}
