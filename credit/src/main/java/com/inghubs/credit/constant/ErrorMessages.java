package com.inghubs.credit.constant;

public final class ErrorMessages {

    public static final String CUSTOMER_NOT_FOUND = "Customer not found.";
    public static final String INSUFFICIENT_CREDIT_LIMIT = "Customer does not have enough credit limit.";
    public static final String INVALID_INSTALLMENT_COUNT = "Number of installments must be 6, 9, 12, or 24.";
    public static final String INVALID_INTEREST_RATE = "Interest rate must be between 0.1 and 0.5.";
    public static final String INSUFFICIENT_AMOUNT_FIRST_INSTALLMENT = "Insufficient amount to pay the first installment.";
    public static final String INSTALLMENT_PAYMENT_RESTRICTION = "Installments with a due date more than 3 calendar months ahead cannot be paid. You can only pay for the current and the next two months' installments.";


}
