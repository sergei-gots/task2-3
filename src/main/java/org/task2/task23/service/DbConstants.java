package org.task2.task23.service;

public interface DbConstants {
    int TABLE_I_COUNT_BY_DEFAULT = 30;
    int TOTAL_TABLE_COUNT = TABLE_I_COUNT_BY_DEFAULT + 1;
     int COL_I_COUNT = 5;
     int GRAND_TOTAL_COL_I_COUNT
            = COL_I_COUNT * TOTAL_TABLE_COUNT;
     int TOTAL_COL_COUNT = COL_I_COUNT + 1;
     int GRAND_TOTAL_COL_COUNT = TOTAL_COL_COUNT * TOTAL_TABLE_COUNT;
     int CUSTOMER_COUNT_BY_DEFAULT = 10_000;
}
