package carservice;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepairOrderRepository implements RepairOrderRepository {
    private final Map<String, RepairOrder> storage = new ConcurrentHashMap<>();

    @Override
    public void save(RepairOrder order) {
        if (order == null) throw new BusinessException("Cannot save null order");
        storage.put(order.getId(), order);
    }

    @Override
    public Optional<RepairOrder> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

}