package com.app.expensetracker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class Utils {


    public static Date convertLocalDateTimeToDate(LocalDateTime dateTime) {
        Date dateToReturn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        return dateToReturn;
    }

    public static void changeResponse(HttpServletResponse response, int status, Object object) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (ServletOutputStream out = response.getOutputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            out.print(jsonString);
            out.flush();
        }
    }
}
