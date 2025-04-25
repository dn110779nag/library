package org.dnu.novomlynov.library.model;

public enum LendingStatus {
    ISSUED, // Book has been checked out to a subscriber
    RETURNED, // Book has been returned to the library
    OVERDUE // Book is overdue (not returned by due date)
}