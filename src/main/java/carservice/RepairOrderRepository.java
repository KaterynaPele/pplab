package carservice;

import java.util.Collection;
import java.util.Optional;

public interface RepairOrderRepository {
    void save(RepairOrder order);
    Optional<RepairOrder> findById(String id);
    Collection<RepairOrder> findAll();
    Collection<RepairOrder> findByVin(String vin);
}