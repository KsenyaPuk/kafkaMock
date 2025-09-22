package com.example.kafkaMock.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO {

    private String msg_id;
    private long timestamp;
    private String method;
    private String uri;

}
