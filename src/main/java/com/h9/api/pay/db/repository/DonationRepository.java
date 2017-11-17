package com.h9.api.pay.db.repository;

import com.h9.api.pay.db.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 16:14 星期四
 */
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Donation findByOrderNo(String orderNo);
}
