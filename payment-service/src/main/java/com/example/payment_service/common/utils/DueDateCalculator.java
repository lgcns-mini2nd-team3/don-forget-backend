package com.example.payment_service.common.utils;

import java.time.LocalDate;
import java.time.YearMonth;

public class DueDateCalculator {
    public static LocalDate calcDueDate(YearMonth ym, int dueDay) {
        int lastDay = ym.lengthOfMonth();
        int safeDay = Math.min(Math.max(dueDay, 1), lastDay); // 1보다 작으면 1, 말일보다 크면 말일로 보정
        return ym.atDay(safeDay);   
    }
}
