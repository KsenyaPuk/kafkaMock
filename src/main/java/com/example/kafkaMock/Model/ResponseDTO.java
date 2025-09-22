package com.example.kafkaMock.Model;

import lombok.Data;

@Data
public class ResponseDTO {

    private String msg_id;
    private long timestamp;
    private String method;
    private String uri;

}
