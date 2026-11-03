package carservice;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DefaultPricingPolicy implements PricingPolicy {
    private final BigDecimal minimumFee;

    public DefaultPricingPolicy(BigDecimal minimumFee) {
        if (minimumFee == null || minimumFee.compareTo(BigDecimal.ZERO) < 0) {
            this.minimumFee = BigDecimal.ZERO;
        } else {
            this.minimumFee = minimumFee;
        }
    }

    @Override
    public BigDecimal calculateCommercialPrice(RepairOrder order) {
        if (order == null) {
            throw new BusinessException("Order is null");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        Client client = order.getCar().getOwner();

        for (Work work : order.getWorks()) {
            BigDecimal partsCost = work.getPartsCost();
            BigDecimal laborCost = work.getLaborCost();

            if (client instanceof CorporateClient corporateClient) {
                BigDecimal partsDiscount = corporateClient.getPartsDiscount();
                BigDecimal laborDiscount = corporateClient.getLaborDiscount();

                if (partsDiscount != null && partsDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal discountedParts = partsCost.multiply(partsDiscount)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    partsCost = partsCost.subtract(discountedParts);
                }

                if (laborDiscount != null && laborDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal discountedLabor = laborCost.multiply(laborDiscount)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    laborCost = laborCost.subtract(discountedLabor);
                }
            }

            totalPrice = totalPrice.add(partsCost).add(laborCost);
        }

        if (totalPrice.compareTo(minimumFee) < 0) {
            totalPrice = minimumFee;
        }

        return totalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}