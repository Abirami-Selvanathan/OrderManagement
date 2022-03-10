package com.reading_is_good.order_management.order;

import com.reading_is_good.order_management.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findAllByUserOrderByCreatedDateDesc(User user);

    List<Order> findByUser(User user, Pageable paging);
}
