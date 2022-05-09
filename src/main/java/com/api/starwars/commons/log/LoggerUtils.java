package com.api.starwars.commons.log;

import com.api.starwars.domain.planets.enums.OperationsEnum;
import org.slf4j.MDC;

import java.util.UUID;

public abstract class LoggerUtils {

    private LoggerUtils() {}

    private static final String OPERATION = "operation";
    private static final String OPERATION_KEY = "operation_key";
    private static final String HTTP_METHOD = "http_method";

    private static final String PATH = "path";

    public static void setOperationInfoIntoMDC(String resourceKey, OperationsEnum operation) {
        setOperationInfoIntoMDC(operation);
        MDC.put(OPERATION_KEY, resourceKey);
    }

    public static void setOperationInfoIntoMDC(OperationsEnum operation) {
        setTID();
        MDC.put(OPERATION, operation.getName());
        MDC.put(HTTP_METHOD, operation.getMethod().name());
        MDC.put(PATH, operation.getPath());
    }

    public static void setTID() {
        String prefix = System.getProperty("tidPrefix");
        String tid = prefix != null ? prefix + "-" + UUID.randomUUID() : UUID.randomUUID().toString();
        MDC.put("tid", tid);
    }
}
