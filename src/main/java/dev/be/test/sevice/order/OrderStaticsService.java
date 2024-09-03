package dev.be.test.sevice.order;

import dev.be.test.domain.order.Order;
import dev.be.test.domain.order.OrderRepository;
import dev.be.test.domain.order.OrderStatus;
import dev.be.test.sevice.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStaticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticMail(LocalDate orderDate, String email) {
        // 해당 일자에 결제 완료된 주문들을 가져와서
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 합계를 계산하고
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        // 메일전송
        Boolean result = mailService.sendMail("test@test.com", email, "[주문 통계]", String.valueOf(totalAmount));
        if(!result) {
            throw new IllegalArgumentException("메일전송에 실패했습니다");
        }
        return true;
    }
}
