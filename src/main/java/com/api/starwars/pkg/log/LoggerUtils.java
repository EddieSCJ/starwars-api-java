package com.api.starwars.pkg.log;

import com.api.starwars.planets.model.enums.OperationsEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public abstract class LoggerUtils {

    private LoggerUtils() {
    }

    private static final String OPERATION = "operation";
    private static final String OPERATION_KEY = "operation_key";
    private static final String HTTP_METHOD = "http_method";
    private static final String PATH = "path";

    public static void setOperationInfoIntoMDC(String resourceKey, OperationsEnum operation, HttpServletRequest request) {
        setOperationInfoIntoMDC(operation, request);
        MDC.put(OPERATION_KEY, resourceKey);
    }

    public static void setOperationInfoIntoMDC(OperationsEnum operation, HttpServletRequest request) {
        setTID(request);
        MDC.put(OPERATION, operation.getName());
        MDC.put(HTTP_METHOD, operation.getMethod().name());
        MDC.put(PATH, operation.getPath());
    }

    public static void setTID(HttpServletRequest request) {
        String tid = request.getHeader("tid");
        if (StringUtils.isNotEmpty(tid)) {
            MDC.put("tid", tid);
            return;
        }

        String prefix = System.getProperty("TID_PREFIX");
        tid = prefix != null ? prefix + "-" + UUID.randomUUID() : UUID.randomUUID().toString();
        MDC.put("tid", tid);
    }
}
