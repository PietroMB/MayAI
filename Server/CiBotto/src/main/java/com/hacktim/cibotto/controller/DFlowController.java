package com.hacktim.cibotto.controller;


import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.gson.Gson;
import com.hacktim.cibotto.model.IntentResponse;
import com.hacktim.cibotto.model.reqBody.BodyIntent;
import com.hacktim.cibotto.service.DFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;


@RestController
@RequestMapping(value={"/api/v1"})
public class DFlowController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private DFlowService dFlowService;

    @RequestMapping(value="/detectIntent", method= RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<IntentResponse> detectIntent(@RequestBody BodyIntent bodyIntent, HttpServletRequest req) {

        IntentResponse intentResponse = null;

        long timeApiCall = new Date().getTime();
        String retString = null;
        logger.trace("API BODY "+ new Gson().toJson(bodyIntent, BodyIntent.class));

        String iid = (String) req.getAttribute("iid");
//        if (iid == null) throw new IllegalArgumentException("iid not present");   //  TODO

        String projectId = "";
        String sessionId = bodyIntent.getIid()+"_"+ bodyIntent.getSid();

        if (bodyIntent.getMockupResponseFile() == null || bodyIntent.getMockupResponseFile().isEmpty()) {
            intentResponse = dFlowService.detectIntent(dFlowService.getProjectId(), bodyIntent);
        } else {
            try {
                String jsonData = Files.readString(Paths.get(bodyIntent.getMockupResponseFile() ));
                intentResponse = new Gson().fromJson(jsonData,IntentResponse.class);
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }

        logger.debug ("api/v1/detectIntent "+intentResponse.getFulfillmentText());
        return new ResponseEntity<>(intentResponse, HttpStatus.OK);
    }

}
