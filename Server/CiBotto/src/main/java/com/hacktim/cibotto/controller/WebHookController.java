package com.hacktim.cibotto.controller;

import com.google.cloud.dialogflow.v2.WebhookResponse;
import com.hacktim.cibotto.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value={"/webhook"})
public class WebHookController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebhookService webhookService;

    @Value("${dialogFlow.token}")
    private String dialogFlowToken;

    @RequestMapping(value="/ping", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> ping(HttpServletRequest req) {
        String response = "pong !";
        String token = req.getHeader("AuthToken");
        logger.debug ("webhook/ping");
        if (token == null || !token.equals(dialogFlowToken)) {
            response = "invalid token";
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value="/cb", method= RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sendIntent(@RequestBody String rawRequest, HttpServletRequest req) {
        String token = req.getHeader("AuthToken");
        logger.debug ("webhook/cb -> "+rawRequest);
        if (token == null || !token.equals(dialogFlowToken)) {
            return new ResponseEntity<>("invalid token", HttpStatus.UNAUTHORIZED);
        } else {
            String webhookResponse = webhookService.elabCb(rawRequest);
            logger.debug ("webhook/cb <- "+webhookResponse);
            return new ResponseEntity<>(webhookResponse, HttpStatus.OK);
        }
    }

}
