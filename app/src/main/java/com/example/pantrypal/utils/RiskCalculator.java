package com.example.pantrypal.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RiskCalculator {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static int calculateRisk(String expiryDateStr, int quantity) {

        try {

            LocalDate today = LocalDate.now();
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, formatter);

            long daysRemaining =
                    ChronoUnit.DAYS.between(today, expiryDate);

            int expiryRisk;

            if (daysRemaining > 7) {
                expiryRisk = 20;
            } else if (daysRemaining >= 3) {
                expiryRisk = 50;
            } else if (daysRemaining >= 1) {
                expiryRisk = 75;
            } else {
                expiryRisk = 100;
            }

            double stockPressure = 0;

            if (daysRemaining > 0) {
                stockPressure =
                        ((double) quantity / (daysRemaining + 1)) * 20;
            }

            if (stockPressure > 100) {
                stockPressure = 100;
            }

            int urgencyBoost = 0;

            if (daysRemaining <= 0) {
                urgencyBoost = 20;
            } else if (daysRemaining == 1) {
                urgencyBoost = 10;
            }

            double finalRisk =
                    (expiryRisk * 0.6)
                            + (stockPressure * 0.3)
                            + (urgencyBoost * 0.1);

            if (finalRisk > 100) {
                finalRisk = 100;
            }

            return (int) finalRisk;

        } catch (Exception e) {
            return 0;
        }
    }

    // 🔥 NEW METHOD (FIXES YOUR ERROR)
    public static int getDaysLeft(String expiryDateStr) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, formatter);

            return (int) ChronoUnit.DAYS.between(today, expiryDate);

        } catch (Exception e) {
            return 0;
        }
    }
}