package com.insurance.common.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiResponse {
    private String status;
    private String message;
    private LocalDateTime timeStamp;
}
