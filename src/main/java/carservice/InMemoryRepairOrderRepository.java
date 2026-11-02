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

    @Override
    public Collection<RepairOrder> findAll() {
        return Collections.unmodifiableCollection(storage.values());
    }

    @Override
    public Collection<RepairOrder> findByVin(String vin) {
        List<RepairOrder> result = new ArrayList<>();
        for (RepairOrder order : storage.values()) {
            if (order.getCar().getVin().equalsIgnoreCase(vin)) {
                result.add(order);
            }
        }
        return result;
    }
}