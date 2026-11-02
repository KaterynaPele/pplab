package carservice;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PricingPolicy {
    private final BigDecimal minimumFee;
    public PricingPolicy(BigDecimal minimumFee) {
        if (minimumFee == null) {
            this.minimumFee = BigDecimal.ZERO;
        } else {
            this.minimumFee = minimumFee;
        }
    }

    public BigDecimal calculateCommercialPrice(RepairOrder order) {
        if (order == null) {
            throw new BusinessException("Order is null");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        Client client = order.getCar().getOwner();

        for (Work work : order.getWorks()) {
            BigDecimal partsCost = work.getPartsCost();
            BigDecimal laborCost = work.getLaborCost();
            if (client instanceof CorporateClient) {
                CorporateClient corporateClient =
                        (CorporateClient) client;
                BigDecimal partsDiscount =
                        corporateClient.getPartsDiscount();
                BigDecimal laborDiscount =
                        corporateClient.getLaborDiscount();
                BigDecimal discountedParts =
                        partsCost.multiply(partsDiscount)
                                .divide(BigDecimal.valueOf(100),
                                        2,
                                        RoundingMode.HALF_UP);

                BigDecimal discountedLabor =
                        laborCost.multiply(laborDiscount)
                                .divide(BigDecimal.valueOf(100),
                                        2,
                                        RoundingMode.HALF_UP);

                partsCost = partsCost.subtract(discountedParts);
                laborCost = laborCost.subtract(discountedLabor);
            }
            totalPrice = totalPrice.add(partsCost);
            totalPrice = totalPrice.add(laborCost);
        }

        if (totalPrice.compareTo(minimumFee) < 0) {
            totalPrice = minimumFee;
        }

        return totalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}