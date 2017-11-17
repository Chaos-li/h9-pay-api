package com.h9.api.pay.db.repository;

import com.h9.api.pay.db.entity.PaymentConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 14:24 星期四
 */
public interface PaymentConfigRepository extends JpaRepository<PaymentConfig, Long> {
}
