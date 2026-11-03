package carservice;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CarService {
    private final RepairOrderRepository orderRepository;
    private final PricingPolicy pricingPolicy;
    private final Map<String, Client> clients = new HashMap<>();
    private final Map<String, Car> cars = new HashMap<>();
    private final Map<String, Mechanic> mechanics = new HashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(1);

    public CarService(RepairOrderRepository orderRepository, PricingPolicy pricingPolicy) {
        this.orderRepository = orderRepository;
        this.pricingPolicy = pricingPolicy;
    }

    public void registerClient(Client c) {
        if (c == null) throw new BusinessException("Client is null");
        if (clients.containsKey(c.getId())) throw new BusinessException("Client id already exists");
        clients.put(c.getId(), c);
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
        String id = (orderId == null || orderId.isBlank()) ? String.valueOf(idGen.getAndIncrement()) : orderId;

        if (orderRepository.findById(id).isPresent()) {
            throw new BusinessException("Order id already exists");
        }

        RepairOrder order = new RepairOrder(id, car);
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

    public RepairOrder cancelOrder(String orderId) {
        RepairOrder order = getOrderOrThrow(orderId);
        order.cancel();
        orderRepository.save(order);
        return order;
    }

    public Mechanic findMechanicById(String id) { return mechanics.get(id); }
    public Collection<Mechanic> getMechanics() { return mechanics.values(); }
    public Client getClientById(String id) { return clients.get(id); }
    public Collection<RepairOrder> getOrders() { return orderRepository.findAll(); }
}