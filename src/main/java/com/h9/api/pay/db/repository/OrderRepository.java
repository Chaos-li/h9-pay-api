package com.h9.api.pay.db.repository;

import com.h9.api.pay.db.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/12/7 20:00 星期四
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByOrderNo(String orderNo);

}
