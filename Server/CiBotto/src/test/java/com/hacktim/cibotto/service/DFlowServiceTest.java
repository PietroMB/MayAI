package com.hacktim.cibotto.service;

import com.hacktim.cibotto.model.IntentResponse;
import com.hacktim.cibotto.model.reqBody.BodyIntent;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
class DFlowServiceTest {

    @Autowired
    private DFlowService dFlowService;

    // @Test
    void detectIntentTexts() {
        String projectId = dFlowService.getProjectId();

        String languageCode = "IT-it";

        BodyIntent bodyIntent = new BodyIntent();
        bodyIntent.setText("ciao");
        bodyIntent.setIid("123456");
        bodyIntent.setSid(1593767927000L);
        bodyIntent.setLc("it");
        bodyIntent.setOutputAudio(false);
        IntentResponse intentResponse = dFlowService.detectIntent( projectId,  bodyIntent);
        assertNotNull(intentResponse);
    }

    //@Test
    void getProjectId() {
        String projectId = dFlowService.getProjectId();
        assertEquals(projectId,"mayai-282208");
    }


}