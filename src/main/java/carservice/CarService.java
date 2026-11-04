package carservice;

import java.math.BigDecimal;
import java.util.*;

public class CarService {
    private final RepairOrderRepository orderRepository = new InMemoryRepairOrderRepository();
    private final PricingPolicy pricingPolicy;

    private final Map<String, Client> clients = new HashMap<>();
    private final Map<String, Car> cars = new HashMap<>();
    private final Map<String, Mechanic> mechanics = new HashMap<>();

    public CarService(PricingPolicy pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    public void registerClient(Client client) {
        if (client == null) throw new BusinessException("Client is null");
        if (clients.containsKey(client.getId())) throw new BusinessException("Client id already exists");
        clients.put(client.getId(), client);
    }

    public void registerCar(Car car) {
        if (car == null) throw new BusinessException("Car is null");
        if (cars.containsKey(car.getVin())) throw new BusinessException("VIN already registered");
        cars.put(car.getVin(), car);
    }

    public void registerMechanic(Mechanic m) {
        if (m == null) throw new BusinessException("Mechanic is null");
        if (mechanics.containsKey(m.getId())) throw new BusinessException("Mechanic id exists");
        mechanics.put(m.getId(), m);
    }

    public RepairOrder createOrderFor(String orderId, String vin) {
        Car car = cars.get(vin);
        if (car == null) throw new BusinessException("Car not registered");

        if (orderId == null || orderId.isBlank()) {
            throw new BusinessException("Order id cannot be null or blank");
        }

        if (orderRepository.findById(orderId).isPresent()) {
            throw new BusinessException("Order id already exists");
        }

        RepairOrder order = new RepairOrder(orderId, car);
        orderRepository.save(order);
        return order;
    }

    public RepairOrder findOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found with id: " + id));
    }

    public BigDecimal getCommercialPrice(String orderId) {
        RepairOrder order = findOrderById(orderId);
        return pricingPolicy.calculateCommercialPrice(order);
    }

    public RepairOrder getOrderOrThrow(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + id));
    }

    public void cancelOrder(String orderId) {
        RepairOrder order = getOrderOrThrow(orderId);
        order.cancel();
        orderRepository.save(order);
    }
}