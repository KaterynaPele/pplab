package carservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepairOrder {
    private final String id;
    private final Car car;
    private List<Work> works;
    private OrderStatus status;
    private Mechanic mechanic;

    public RepairOrder(String id, Car car) {
        if (id == null || id.isBlank()) {
            throw new BusinessException("Order id is required");
        }
        if (car == null) {
            throw new BusinessException("Car is required");
        }

        this.id = id;
        this.car = car;
        works = new ArrayList<>();
        status = OrderStatus.CREATED;
    }

    public String getId() {
        return id;
    }
    public Car getCar() {
        return car;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public Mechanic getMechanic() {
        return mechanic;
    }
    public List<Work> getWorks() {
        return Collections.unmodifiableList(works);
    }
    public void addWork(Work work) {

        checkIfEditable();

        if (work == null) {
            throw new BusinessException("Work cannot be null");
        }

        works.add(work);
    }
    public void diagnose() {

        checkIfEditable();

        if (status != OrderStatus.CREATED) {
            throw new BusinessException(
                    "Order can be diagnosed only after creation");
        }

        status = OrderStatus.DIAGNOSED;
    }
    public void approve() {
        checkIfEditable();
        if (works.isEmpty()) {
            throw new BusinessException(
                    "Add at least one work before approval");
        }
        if (status != OrderStatus.CREATED
                && status != OrderStatus.DIAGNOSED) {
            throw new BusinessException(
                    "Order cannot be approved in current status");
        }
        status = OrderStatus.APPROVED;
    }
    public void assignMechanic(Mechanic mechanic) {
        checkIfEditable();
        if (mechanic == null) {
            throw new BusinessException("Mechanic is required");
        }
        if (mechanic.isBusy()) {
            throw new BusinessException("Mechanic is already busy");
        }
        if (status != OrderStatus.APPROVED) {
            throw new BusinessException(
                    "Assign mechanic only after approval");
        }
        this.mechanic = mechanic;
        mechanic.setBusy(true);
    }

    public void start() {
        checkIfEditable();
        if (status != OrderStatus.APPROVED) {
            throw new BusinessException(
                    "Order must be approved before start");
        }
        if (mechanic == null) {
            throw new BusinessException(
                    "Assign mechanic before starting work");
        }
        status = OrderStatus.IN_PROGRESS;
    }

    public void completeWork(int index) {
        checkIfEditable();
        if (status != OrderStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Works can be completed only during repair");
        }
        if (index < 0 || index >= works.size()) {
            throw new BusinessException("Wrong work index");
        }
        works.get(index).markDone();
    }

    public void completeOrder() {
        checkIfEditable();
        if (status != OrderStatus.IN_PROGRESS) {
            throw new BusinessException(
                    "Order is not in progress");
        }
        for (Work work : works) {
            if (!work.isDone()) {
                throw new BusinessException(
                        "Finish all works first");
            }
        }

        status = OrderStatus.COMPLETED;
        if (mechanic != null) {
            mechanic.setBusy(false);
        }
    }

    public void cancel() {
        checkIfEditable();
        boolean canCancel =
                status == OrderStatus.CREATED
                        || status == OrderStatus.DIAGNOSED
                        || status == OrderStatus.APPROVED;
        if (!canCancel) {
            throw new BusinessException(
                    "This order cannot be cancelled");
        }

        status = OrderStatus.CANCELLED;
        if (mechanic != null) {
            mechanic.setBusy(false);
        }
    }

    public BigDecimal calculateBaseCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (Work work : works) {
            total = total.add(work.getPartsCost());
            total = total.add(work.getLaborCost());
        }
        return total;
    }

    private void checkIfEditable() {
        if (status == OrderStatus.COMPLETED
                || status == OrderStatus.CANCELLED) {
            throw new BusinessException(
                    "Completed or cancelled order cannot be changed");
        }
    }

    @Override
    public String toString() {
        String mechanicName = "No mechanic";
        if (mechanic != null) {
            mechanicName = mechanic.getName();
        }

        return "Order #" + id
                + " | Status: " + status
                + " | VIN: " + car.getVin()
                + " | Mechanic: " + mechanicName;
    }
}