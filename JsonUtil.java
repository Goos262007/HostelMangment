package com.hostel.server;

import com.hostel.model.*;

import java.util.*;

/**
 * Lightweight JSON utility for zero-dependency operation.
 * Eliminates external Maven dependencies (Jackson/Gson) while ensuring
 * standard JSON responses for the frontend.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        String t = "000" + Integer.toHexString(ch);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    public static String toJson(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof String) {
            return "\"" + escape((String) obj) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Student) {
            return studentToJson((Student) obj);
        } else if (obj instanceof Room) {
            return roomToJson((Room) obj);
        } else if (obj instanceof Payment) {
            return paymentToJson((Payment) obj);
        } else if (obj instanceof Complaint) {
            return complaintToJson((Complaint) obj);
        } else if (obj instanceof RoomAllocation) {
            return allocationToJson((RoomAllocation) obj);
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                sb.append(toJson(list.get(i)));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sb.append("\"").append(escape(entry.getKey().toString())).append("\":");
                sb.append(toJson(entry.getValue()));
                if (++i < map.size()) sb.append(",");
            }
            sb.append("}");
            return sb.toString();
        }

        return "\"" + escape(obj.toString()) + "\"";
    }

    private static String studentToJson(Student s) {
        return "{" +
            "\"id\":" + s.getId() + "," +
            "\"studentId\":\"" + escape(s.getStudentId()) + "\"," +
            "\"fullName\":\"" + escape(s.getFullName()) + "\"," +
            "\"rollNumber\":\"" + escape(s.getRollNumber()) + "\"," +
            "\"department\":\"" + escape(s.getDepartment()) + "\"," +
            "\"yearOfStudy\":\"" + escape(s.getYearOfStudy()) + "\"," +
            "\"dob\":\"" + escape(s.getDob()) + "\"," +
            "\"gender\":\"" + escape(s.getGender()) + "\"," +
            "\"phone\":\"" + escape(s.getPhone()) + "\"," +
            "\"email\":\"" + escape(s.getEmail()) + "\"," +
            "\"parentContact\":\"" + escape(s.getParentContact()) + "\"," +
            "\"address\":\"" + escape(s.getAddress()) + "\"," +
            "\"assignedRoomId\":" + (s.getAssignedRoomId() != null ? s.getAssignedRoomId() : "null") + "," +
            "\"assignedRoomNumber\":" + (s.getAssignedRoomNumber() != null ? "\"" + escape(s.getAssignedRoomNumber()) + "\"" : "null") + "," +
            "\"status\":\"" + escape(s.getStatus()) + "\"," +
            "\"admissionDate\":\"" + escape(s.getAdmissionDate()) + "\"," +
            "\"role\":\"" + escape(s.getRole()) + "\"" +
            "}";
    }

    private static String roomToJson(Room r) {
        return "{" +
            "\"id\":" + r.getId() + "," +
            "\"roomNumber\":\"" + escape(r.getRoomNumber()) + "\"," +
            "\"roomType\":\"" + escape(r.getRoomType()) + "\"," +
            "\"capacity\":" + r.getCapacity() + "," +
            "\"occupiedBeds\":" + r.getOccupiedBeds() + "," +
            "\"availableBeds\":" + r.getAvailableBeds() + "," +
            "\"monthlyFee\":" + r.getMonthlyFee() + "," +
            "\"status\":\"" + escape(r.getStatus()) + "\"," +
            "\"description\":\"" + escape(r.getDescription()) + "\"" +
            "}";
    }

    private static String paymentToJson(Payment p) {
        return "{" +
            "\"id\":" + p.getId() + "," +
            "\"receiptNo\":\"" + escape(p.getReceiptNo()) + "\"," +
            "\"studentId\":" + p.getStudentId() + "," +
            "\"studentName\":\"" + escape(p.getStudentName() != null ? p.getStudentName() : "") + "\"," +
            "\"studentRollNo\":\"" + escape(p.getStudentRollNo() != null ? p.getStudentRollNo() : "") + "\"," +
            "\"totalAmount\":" + p.getTotalAmount() + "," +
            "\"amountPaid\":" + p.getAmountPaid() + "," +
            "\"pendingAmount\":" + p.getPendingAmount() + "," +
            "\"paymentDate\":\"" + escape(p.getPaymentDate()) + "\"," +
            "\"paymentMethod\":\"" + escape(p.getPaymentMethod()) + "\"," +
            "\"transactionId\":\"" + escape(p.getTransactionId()) + "\"," +
            "\"paymentStatus\":\"" + escape(p.getPaymentStatus()) + "\"," +
            "\"remarks\":\"" + escape(p.getRemarks()) + "\"" +
            "}";
    }

    private static String complaintToJson(Complaint c) {
        return "{" +
            "\"id\":" + c.getId() + "," +
            "\"complaintId\":\"" + escape(c.getComplaintId()) + "\"," +
            "\"studentId\":" + c.getStudentId() + "," +
            "\"studentName\":\"" + escape(c.getStudentName() != null ? c.getStudentName() : "") + "\"," +
            "\"roomNumber\":\"" + escape(c.getRoomNumber() != null ? c.getRoomNumber() : "") + "\"," +
            "\"title\":\"" + escape(c.getTitle()) + "\"," +
            "\"description\":\"" + escape(c.getDescription()) + "\"," +
            "\"category\":\"" + escape(c.getCategory()) + "\"," +
            "\"priority\":\"" + escape(c.getPriority()) + "\"," +
            "\"status\":\"" + escape(c.getStatus()) + "\"," +
            "\"reportedDate\":\"" + escape(c.getReportedDate()) + "\"," +
            "\"resolvedDate\":\"" + escape(c.getResolvedDate() != null ? c.getResolvedDate() : "") + "\"," +
            "\"adminRemarks\":\"" + escape(c.getAdminRemarks() != null ? c.getAdminRemarks() : "") + "\"" +
            "}";
    }

    private static String allocationToJson(RoomAllocation a) {
        return "{" +
            "\"id\":" + a.getId() + "," +
            "\"studentId\":" + a.getStudentId() + "," +
            "\"studentName\":\"" + escape(a.getStudentName() != null ? a.getStudentName() : "") + "\"," +
            "\"studentRollNo\":\"" + escape(a.getStudentRollNo() != null ? a.getStudentRollNo() : "") + "\"," +
            "\"roomId\":" + a.getRoomId() + "," +
            "\"roomNumber\":\"" + escape(a.getRoomNumber() != null ? a.getRoomNumber() : "") + "\"," +
            "\"roomType\":\"" + escape(a.getRoomType() != null ? a.getRoomType() : "") + "\"," +
            "\"allocationDate\":\"" + escape(a.getAllocationDate()) + "\"," +
            "\"vacatingDate\":\"" + escape(a.getVacatingDate() != null ? a.getVacatingDate() : "") + "\"," +
            "\"status\":\"" + escape(a.getStatus()) + "\"," +
            "\"remarks\":\"" + escape(a.getRemarks() != null ? a.getRemarks() : "") + "\"" +
            "}";
    }

    /**
     * Parses simple JSON key-value pairs into a Map.
     */
    public static Map<String, String> parseJsonMap(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        String content = json.trim();
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);

        boolean insideQuotes = false;
        StringBuilder currentToken = new StringBuilder();
        List<String> pairs = new ArrayList<>();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '\"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                insideQuotes = !insideQuotes;
            }
            if (c == ',' && !insideQuotes) {
                pairs.add(currentToken.toString());
                currentToken = new StringBuilder();
            } else {
                currentToken.append(c);
            }
        }
        if (currentToken.length() > 0) {
            pairs.add(currentToken.toString());
        }

        for (String pair : pairs) {
            int colonIdx = pair.indexOf(':');
            if (colonIdx > 0) {
                String key = cleanValue(pair.substring(0, colonIdx));
                String val = cleanValue(pair.substring(colonIdx + 1));
                map.put(key, val);
            }
        }
        return map;
    }

    private static String cleanValue(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n");
    }
}
