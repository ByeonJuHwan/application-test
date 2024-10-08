package dev.be.test.domain.stock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다.")
    @Test
    void isQuantityLessThan() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        // when
        boolean result = stock.isQuantityLessThan(quantity);

        // then
        assertTrue(result);
    }


    @DisplayName("재고를 주어진 개수만큼 차감할 수 있다")
    @Test
    void deductQuantity() {
        Stock stock = Stock.create("001", 1);
        int quantity = 1;

        stock.deductQuantity(quantity);

        assertEquals(stock.getQuantity(), 0);
    }

    @DisplayName("재고보다 많은 수량으로 차감 시도하는 경우 예외가 발생한다")
    @Test
    void deductQuantity2() {
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        assertThatThrownBy(() -> stock.deductQuantity(quantity))
                .isInstanceOf(IllegalArgumentException.class);
    }
}