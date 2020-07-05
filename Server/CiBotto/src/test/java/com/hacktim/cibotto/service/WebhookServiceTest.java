package com.hacktim.cibotto.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
class WebhookServiceTest {

    @Autowired
    private WebhookService webhookService;

    @Value("${cibotto.sms.message}")
    private String cibottoSmsMessage;

    @Test
    void elabCb()  {
        String req = "{\n" +
            "  \"responseId\": \"3d3a845f-7a66-4ed7-a373-c972f044103a-425db6e2\",\n" +
            "  \"queryResult\": {\n" +
            "    \"queryText\": \"Mi servirebbero delle cravatte\",\n" +
            "    \"parameters\": {\n" +
            "      \"itemclothing\": \"cravatta\",\n" +
            "      \"number\": \"\",\n" +
            "      \"location\": \"\",\n" +
            "      \"clothingType\": \"\"\n" +
            "    },\n" +
            "    \"allRequiredParamsPresent\": true,\n" +
            "    \"fulfillmentMessages\": [{\n" +
            "      \"text\": {\n" +
            "        \"text\": [\"\"]\n" +
            "      }\n" +
            "    }],\n" +
            "    \"outputContexts\": [{\n" +
            "      \"name\": \"projects/mayai-282208/agent/sessions/123456_1593767927000/contexts/__system_counters__\",\n" +
            "      \"parameters\": {\n" +
            "        \"no-input\": 0.0,\n" +
            "        \"no-match\": 0.0,\n" +
            "        \"itemclothing\": \"cravatta\",\n" +
            "        \"itemclothing.original\": \"cravatte\",\n" +
            "        \"number\": \"\",\n" +
            "        \"number.original\": \"\",\n" +
            "        \"location\": \"\",\n" +
            "        \"location.original\": \"\",\n" +
            "        \"clothingType\": \"\",\n" +
            "        \"clothingType.original\": \"\"\n" +
            "      }\n" +
            "    }],\n" +
            "    \"intent\": {\n" +
            "      \"name\": \"projects/mayai-282208/agent/intents/44917f6f-5231-4008-ad29-84faa3cde748\",\n" +
            "      \"displayName\": \"itemLocation\",\n" +
            "      \"endInteraction\": true\n" +
            "    },\n" +
            "    \"intentDetectionConfidence\": 1.0,\n" +
            "    \"languageCode\": \"it\"\n" +
            "  },\n" +
            "  \"originalDetectIntentRequest\": {\n" +
            "    \"payload\": {\n" +
            "    }\n" +
            "  },\n" +
            "  \"session\": \"projects/mayai-282208/agent/sessions/123456_1593767927000\"\n" +
            "}";
        String webhookResponse = webhookService.elabCb(req);
        assertNotEquals(webhookResponse, "invalid token");
    }
    //@Test
    void sendSMS() {

        String result = webhookService.sendSms("Paolo "+cibottoSmsMessage + " " + "elettrodomestici");
        assertNotNull(result);
    }
}