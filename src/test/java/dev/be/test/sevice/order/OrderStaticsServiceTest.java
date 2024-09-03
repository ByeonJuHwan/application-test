package dev.be.test.sevice.order;

import dev.be.test.client.MailSendClient;
import dev.be.test.domain.history.MailSendHistory;
import dev.be.test.domain.history.MailSendHistoryRepository;
import dev.be.test.domain.order.Order;
import dev.be.test.domain.order.OrderRepository;
import dev.be.test.domain.order.OrderStatus;
import dev.be.test.domain.orderproduct.OrderProductRepository;
import dev.be.test.domain.product.Product;
import dev.be.test.domain.product.ProductRepository;
import dev.be.test.domain.product.ProductType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static dev.be.test.domain.product.ProductSellingStatus.SELLING;
import static dev.be.test.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class OrderStaticsServiceTest {

    @Autowired
    private OrderStaticsService orderStaticsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @MockBean
    private MailSendClient mailSendClient;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @DisplayName("결제완료 주문을 조회하여 매출 통계 메일을 전송한다")
    @Test
    void sendOrderStatisticMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 9, 1, 15, 0);

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 2000);
        Product product3 = createProduct(HANDMADE, "003", 3000);
        List<Product> productList = List.of(product1, product2, product3);
        productRepository.saveAll(productList);

        Order order1 = createPaymentCompletedOrder(productList, LocalDateTime.of(2024, 8, 30, 23, 59, 59));
        Order order2 = createPaymentCompletedOrder(productList, now);
        Order order3 = createPaymentCompletedOrder(productList, LocalDateTime.of(2024, 9, 1, 23, 59, 59));
        Order order4 = createPaymentCompletedOrder(productList, LocalDateTime.of(2024, 9, 2, 0, 0));

        when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(true);

        // when
        boolean result = orderStaticsService.sendOrderStatisticMail(LocalDate.of(2024, 9, 1), "test@test.com");

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("12000");
    }

    private Order createPaymentCompletedOrder(List<Product> productList, LocalDateTime now) {
        Order order = Order.builder()
                .products(productList)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();

        return orderRepository.save(order);
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }
}