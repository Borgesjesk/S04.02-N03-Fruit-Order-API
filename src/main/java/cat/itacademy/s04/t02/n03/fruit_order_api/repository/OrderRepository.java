package cat.itacademy.s04.t02.n03.fruit_order_api.repository;

import cat.itacademy.s04.t02.n03.fruit_order_api.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
}