package carservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingPolicyTest {

    @ParameterizedTest
    @CsvSource({
            "10.00, 20.00, 50.00", // Сума 30.00 < мін. fee 50.00 -> 50.00
            "40.00, 30.00, 70.00"  // Сума 70.00 > мін. fee 50.00 -> 70.00
    })
    void shouldCalculateCommercialPriceWithMinimumFee(BigDecimal parts, BigDecimal labor, BigDecimal expected) {
        Client client = new Client("c1", "Standard Client");
        Car car = new Car("VIN1", "Audi", "A4", 2020, client);
        RepairOrder order = new RepairOrder("o1", car);
        order.addWork(new Work("Fix", parts, labor));

        PricingPolicy policy = new PricingPolicy(new BigDecimal("50.00"));

        BigDecimal result = policy.calculateCommercialPrice(order);
        assertEquals(expected, result);
    }

    @Test
    void shouldApplyCorporateDiscount() {
        CorporateClient corp = new CorporateClient("c2", "Taxi Co", new BigDecimal("10"), new BigDecimal("20"));
        Car car = new Car("VIN2", "Skoda", "Octavia", 2021, corp);
        RepairOrder order = new RepairOrder("o2", car);

        // Parts: 100 - 10% = 90; Labor: 100 - 20% = 80 -> Total = 170
        order.addWork(new Work("Engine overhaul", new BigDecimal("100.00"), new BigDecimal("100.00")));

        PricingPolicy policy = new PricingPolicy(new BigDecimal("50.00"));

        BigDecimal price = policy.calculateCommercialPrice(order);
        assertEquals(new BigDecimal("170.00"), price);
    }
}