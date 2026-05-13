package group_5.banking_system_application.Ui;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

final class AccountMetrics {

    private AccountMetrics() {}

    static final class Trend {
        final String text;
        final boolean positive;

        Trend(String text, boolean positive) {
            this.text = text;
            this.positive = positive;
        }
    }

    static Trend balanceWeekTrend(double currentBalance, List<QueryDocumentSnapshot> chronological) {
        ZonedDateTime weekAgo = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(7);
        double baseline = balanceAtOrBefore(chronological, weekAgo);
        if (baseline < 0) {
            return new Trend("No balance history from last week yet", true);
        }
        if (baseline == 0) {
            if (currentBalance == 0) {
                return new Trend("Flat vs last week", true);
            }
            return new Trend("Up from $0 last week", true);
        }
        double pct = ((currentBalance - baseline) / baseline) * 100.0;
        return new Trend(formatSignedPercent(pct) + " vs 7 days ago", pct >= 0);
    }

    static Trend spendingMonthTrend(List<QueryDocumentSnapshot> chronological) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        double thisMonth = sumOutgoingInMonth(chronological, now.getYear(), now.getMonthValue());
        ZonedDateTime prev = now.minusMonths(1);
        double lastMonth = sumOutgoingInMonth(chronological, prev.getYear(), prev.getMonthValue());
        if (lastMonth == 0) {
            if (thisMonth == 0) {
                return new Trend("No spending this month", true);
            }
            return new Trend("First spending recorded this month", false);
        }
        double pct = ((thisMonth - lastMonth) / lastMonth) * 100.0;
        return new Trend(formatSignedPercent(pct) + " vs prior month", pct <= 0);
    }

    static double sumOutgoingInMonth(List<QueryDocumentSnapshot> chronological, int year, int month) {
        double sum = 0;
        for (QueryDocumentSnapshot d : chronological) {
            if (!Transactions.FLOW_OUT.equals(d.getString("flow"))) {
                continue;
            }
            ZonedDateTime z = FirestoreMoney.toDate(d, "createdAt").toInstant().atZone(ZoneId.systemDefault());
            if (z.getYear() == year && z.getMonthValue() == month) {
                sum += FirestoreMoney.toDouble(d.get("amount"));
            }
        }
        return sum;
    }

    static double sumIncomingInMonth(List<QueryDocumentSnapshot> chronological, int year, int month) {
        double sum = 0;
        for (QueryDocumentSnapshot d : chronological) {
            if (!Transactions.FLOW_IN.equals(d.getString("flow"))) {
                continue;
            }
            ZonedDateTime z = FirestoreMoney.toDate(d, "createdAt").toInstant().atZone(ZoneId.systemDefault());
            if (z.getYear() == year && z.getMonthValue() == month) {
                sum += FirestoreMoney.toDouble(d.get("amount"));
            }
        }
        return sum;
    }

    static double balanceAtOrBefore(List<QueryDocumentSnapshot> chronological, ZonedDateTime cutoff) {
        double last = -1;
        for (QueryDocumentSnapshot d : chronological) {
            ZonedDateTime when = FirestoreMoney.toDate(d, "createdAt").toInstant().atZone(ZoneId.systemDefault());
            if (when.isAfter(cutoff)) {
                break;
            }
            last = FirestoreMoney.toDouble(d.get("balanceAfter"));
        }
        return last;
    }

    private static String formatSignedPercent(double pct) {
        return String.format(Locale.US, "%+.1f%%", pct);
    }
}
