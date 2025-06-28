package com.project.shopapp.repositories;

import com.project.shopapp.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT od.product.id, SUM(od.numberOfProducts) AS totalSales " +
            "FROM OrderDetail od " +
            "WHERE od.order.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY od.product.id " +
            "ORDER BY totalSales DESC")
    List<Object[]> findTopSellingProducts(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
