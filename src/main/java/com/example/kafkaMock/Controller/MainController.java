package com.example.kafkaMock.Controller;

import com.example.kafkaMock.Kafka.KafkaSender;
import com.example.kafkaMock.Model.RequestDTO;
import com.example.kafkaMock.Model.ResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;


@RestController
public class MainController {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private KafkaSender kafkaSender;

    @PostMapping(
            value = "/json/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> postBalances(@RequestBody RequestDTO requestDTO,
                                                    HttpServletRequest request) throws JsonProcessingException {
        try {

            String msg_id = requestDTO.getMsg_id();
            long timestamp = System.currentTimeMillis();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            ResponseDTO responseDTO = new ResponseDTO(msg_id, timestamp, method, uri);

            String kafkaMessage = mapper.writeValueAsString(responseDTO);
            kafkaSender.sendMessage("test", kafkaMessage);

            log.info("Message sent to Kafka: {}", kafkaMessage);
            log.error("*********** RequestDTO **********" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
            log.error("*********** ResponseDTO **********" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO));

            return ResponseEntity.status(HttpStatus.OK).build();
    } catch (Exception e) {
        return ResponseEntity.status(500).build();
        }
    }
}