package carservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RepairOrderTest {

    @Test
    @DisplayName("Should transit states correctly")
    void shouldTransitStatesCorrectly() {
        Client client = new Client("C1", "Ivan");
        Car car = new Car("VIN123", "Toyota", "Camry", 2020, client);
        RepairOrder order = new RepairOrder("O1", car);

        assertEquals(OrderStatus.CREATED, order.getStatus());

        order.addWork(new Work("Oil Change", new BigDecimal("100.00"), new BigDecimal("50.00")));
        order.approve();
        assertEquals(OrderStatus.APPROVED, order.getStatus());

        Mechanic mechanic = new Mechanic("M1", "Petro", Specialization.ENGINE);
        order.assignMechanic(mechanic);
        order.start();
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());

        order.completeWork(0);
        order.completeOrder();
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("Should calculate base cost")
    void shouldCalculateBaseCost() {
        Client client = new Client("C1", "Ivan");
        Car car = new Car("VIN123", "Toyota", "Camry", 2020, client);
        RepairOrder order = new RepairOrder("O1", car);

        order.addWork(new Work("Oil Change", new BigDecimal("100.00"), new BigDecimal("50.00")));
        order.addWork(new Work("Brake Replacement", new BigDecimal("200.00"), new BigDecimal("100.00")));

        assertEquals(new BigDecimal("450.00"), order.calculateBaseCost());
    }
}