package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("select o from Order o left join fetch o.items where o.id = :id")
	Optional<Order> findByIdWithItems(@Param("id") Long id);
}
