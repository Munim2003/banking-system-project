package group_5.banking_system_application.Ui;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Date;

final class FirestoreMoney {

    private FirestoreMoney() {}

    static double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    static Date toDate(DocumentSnapshot doc, String field) {
        Object raw = doc.get(field);
        if (raw instanceof Timestamp ts) {
            return ts.toDate();
        }
        if (raw instanceof Date d) {
            return d;
        }
        return new Date();
    }
}
