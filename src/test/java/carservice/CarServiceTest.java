package carservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    private RepairOrderRepository repository;
    private PricingPolicy pricingPolicy;
    private CarService carService;

    @BeforeEach
    void setUp() {
        repository = mock(RepairOrderRepository.class);
        pricingPolicy = mock(PricingPolicy.class);
        carService = new CarService(repository, pricingPolicy);
    }

    @Test
    @DisplayName("Should initialize CarService correctly")
    void shouldInitializeCarService() {
        assertNotNull(carService);
    }
}