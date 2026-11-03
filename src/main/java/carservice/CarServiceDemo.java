package carservice;
import java.math.BigDecimal;

public class CarServiceDemo {
    public static void main(String[] args) {
        RepairOrderRepository repository = new InMemoryRepairOrderRepository();
        PricingPolicy pricingPolicy = new DefaultPricingPolicy(new BigDecimal("100.00"));
        CarService svc = new CarService(repository, pricingPolicy);

        System.out.println("\nScenario 1: Register client, car, and mechanics");
        Client alice = new Client("c1", "Alice");
        svc.registerClient(alice);
        Car car1 = new Car("VIN123", "Toyota", "Corolla", 2015, alice);
        svc.registerCar(car1);
        System.out.println("Registered client and car: " + alice + " / " + car1);

        Mechanic m1 = new Mechanic("m1", "Ivan", Specialization.ENGINE);
        Mechanic m2 = new Mechanic("m2", "Petro", Specialization.ELECTRICS);
        svc.registerMechanic(m1);
        svc.registerMechanic(m2);
        System.out.println("Registered mechanics: " + m1 + ", " + m2);

        System.out.println("\nScenario 2: Full successful cycle for order o1");
        try {
            RepairOrder o1 = svc.createOrderFor("o1", "VIN123");
            o1.addWork(new Work("Replace brake pads", new BigDecimal("50.00"), new BigDecimal("30.00")));
            o1.addWork(new Work("Oil change", new BigDecimal("20.00"), new BigDecimal("15.00")));
            System.out.println("Order created: " + o1);

            o1.diagnose();
            System.out.println("After diagnose: " + o1.getStatus());

            o1.approve();
            System.out.println("After approve: " + o1.getStatus());

            o1.assignMechanic(m1);
            System.out.println("Mechanic assigned: " + o1.getMechanic());

            o1.start();
            System.out.println("Order started: " + o1.getStatus());

            o1.completeWork(0);
            o1.completeWork(1);
            System.out.println("Works completed:");
            o1.getWorks().forEach(w -> System.out.println("  " + w));

            o1.completeOrder();
            System.out.println("Final order status: " + o1.getStatus());

            // Base price (Lab 1) & Commercial price via service (Lab 2)
            System.out.println("Base cost (calculateBaseCost): " + o1.calculateBaseCost());
            System.out.println("Commercial price (getCommercialPrice): " + svc.getCommercialPrice("o1"));
        } catch (BusinessException ex) {
            System.out.println("Unexpected error in Scenario 2: " + ex.getMessage());
        }

        System.out.println("\nScenario 3: Attempt to approve empty order o2");
        try {
            RepairOrder o2 = svc.createOrderFor("o2", "VIN123");
            System.out.println("Attempting to approve empty order o2...");
            o2.approve();
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        System.out.println("\nScenario 4: Attempt to start o3 without mechanic");
        try {
            RepairOrder o3 = svc.createOrderFor("o3", "VIN123");
            o3.addWork(new Work("Check lights", BigDecimal.ZERO, new BigDecimal("10.00")));
            o3.diagnose();
            o3.approve();
            System.out.println("Attempting to start o3 without mechanic...");
            o3.start();
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        System.out.println("\nScenario 5: Attempt to assign busy mechanic m2");
        try {
            RepairOrder o4 = svc.createOrderFor("o4", "VIN123");
            o4.addWork(new Work("Engine tune", BigDecimal.ZERO, new BigDecimal("100.00")));
            o4.diagnose();
            o4.approve();
            o4.assignMechanic(m2);
            o4.start();

            RepairOrder o5 = svc.createOrderFor("o5", "VIN123");
            o5.addWork(new Work("Battery check", BigDecimal.ZERO, new BigDecimal("20.00")));
            o5.diagnose();
            o5.approve();
            System.out.println("Attempting to assign busy mechanic m2 to o5...");
            o5.assignMechanic(m2);
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        System.out.println("\nScenario 6: Attempt to modify completed order o6");
        try {
            RepairOrder o6 = svc.createOrderFor("o6", "VIN123");
            o6.addWork(new Work("Simple check", BigDecimal.ZERO, new BigDecimal("5.00")));
            o6.diagnose();
            o6.approve();
            o6.assignMechanic(m1);
            o6.start();
            o6.completeWork(0);
            o6.completeOrder();

            System.out.println("Attempting to add work to completed order o6...");
            o6.addWork(new Work("Extra work", BigDecimal.ZERO, new BigDecimal("1.00")));
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        System.out.println("\nScenario 7: Forbidden transition (start directly from CREATED)");
        try {
            RepairOrder o7 = svc.createOrderFor("o7", "VIN123");
            System.out.println("Attempting to start o7 directly from CREATED...");
            o7.start();
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }


        System.out.println("\nScenario 8: Corporate client, discounts, and PricingPolicy");
        try {
            CorporateClient corpClient = new CorporateClient("c2", "Taxi Service", new BigDecimal("10"), new BigDecimal("20"));
            svc.registerClient(corpClient);
            Car corpCar = new Car("VIN777", "Skoda", "Rapid", 2021, corpClient);
            svc.registerCar(corpCar);

            RepairOrder o8 = svc.createOrderFor("o8", "VIN777");
            o8.addWork(new Work("Engine fix", new BigDecimal("100.00"), new BigDecimal("50.00")));
            o8.diagnose();
            o8.approve();
            o8.assignMechanic(m1);
            o8.start();
            o8.completeWork(0);
            o8.completeOrder();

            System.out.println("Order o8 created for corporate client.");
            System.out.println("Base cost without discounts: " + o8.calculateBaseCost());
            System.out.println("Commercial price (Parts -10%, Labor -20%): " + svc.getCommercialPrice("o8"));
        } catch (BusinessException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        System.out.println("\nScenario 9: Successful order cancellation via service");
        try {
            RepairOrder o9 = svc.createOrderFor("o9", "VIN123");
            o9.addWork(new Work("Filter replacement", new BigDecimal("15.00"), new BigDecimal("10.00")));
            System.out.println("Order o9 status before cancel: " + o9.getStatus());

            svc.cancelOrder("o9");
            System.out.println("Order o9 status after cancel via service: " + o9.getStatus());
        } catch (BusinessException ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        }

        System.out.println("\nScenario 10: Attempt to add work to cancelled order o9");
        try {
            System.out.println("Attempting to add work to cancelled order o9...");
            RepairOrder cancelledOrder = svc.getOrderOrThrow("o9");
            cancelledOrder.addWork(new Work("New work", BigDecimal.ZERO, new BigDecimal("20.00")));
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }
    }
}