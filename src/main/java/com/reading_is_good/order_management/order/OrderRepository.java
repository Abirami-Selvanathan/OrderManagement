package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findAllByUserOrderByCreatedDateDesc(User user);
}
