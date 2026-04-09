package com.innowise.orderservice.enums;

import java.util.Arrays;

/**
 * Enum representing the status of an order.
 * <p>
 * Possible values:
 * <ul>
 *     <li>NEW – newly created order</li>
 *     <li>PAID – order has been paid</li>
 *     <li>CANCELLED – order has been cancelled</li>
 * </ul>
 */
public enum Status {
    NEW,
    PAID,
    CANCELLED;

    /**
     * Checks if the given {@link Status} value is a valid enum constant.
     *
     * @param value the status to check
     * @return true if value is one of the defined enum constants, false otherwise
     */
    public static boolean isValid(Status value) {
        return Arrays.asList(values()).contains(value);
    }
}