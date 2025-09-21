package com.example.kafkaMock.Controller;

import com.example.kafkaMock.Model.RequestDTO;
import com.example.kafkaMock.Model.ResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
public class MainController {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public MainController(KafkaTemplate<String, String> kafkaTemplate,
                          ObjectMapper mapper,
                          @Value("${kafka.topic:test}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }


    @PostMapping(
            value = "/json/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> postBalances(@RequestBody RequestDTO requestDTO,
                                                    HttpServletRequest request) throws JsonProcessingException {
        try {
            ResponseDTO responseDTO = new ResponseDTO();

        String msg_id = requestDTO.getMsg_id();
        responseDTO.setMsg_id(msg_id);

        long timestamp = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        Map<String, Object> kafkaMessage = new LinkedHashMap<>();
        kafkaMessage.put("msg_id", String.valueOf(requestDTO.getMsg_id()));
        kafkaMessage.put("timestamp", String.valueOf(timestamp));
        kafkaMessage.put("method", request.getMethod());
        kafkaMessage.put("uri", request.getRequestURI());

        String payload = mapper.writeValueAsString(kafkaMessage);

        // отправка в Kafka
        kafkaTemplate.send(topic, payload);

        log.info("Message sent to Kafka: {}", payload);
            log.error("*********** RequestDTO **********" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
            log.error("*********** ResponseDTO **********" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO));

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}