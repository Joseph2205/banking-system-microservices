package com.banking.transactionservice.entity;

/**
 * TRANSACTION LIFECYCLE FL0W
 * PENDING -> PROCESSING -> COMPLETED [CLEAN TRANSACTION]
 *                       -> PENDING_VERIFICATION [SUSPICIOUS DETECTED]
 *                                -> COMPLETED (VERIFIED)
 *                                -> FLAGGED (IF NOT VERIFIED) (SAGA REFUND)
 *                       -> FAILED
 *                       -> FLAGGED
 */
public enum TransactionStatus {
    PENDING,
    PROCESSING,
    PENDING_VERIFICATION,
    COMPLETED,
    FAILED,
    FLAGGED



}
