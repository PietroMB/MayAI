package com.hacktim.cibotto.service;

import com.google.cloud.dialogflow.v2.*;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.hacktim.cibotto.model.GoogleApplicationCredentials;
import com.hacktim.cibotto.model.IntentResponse;
import com.hacktim.cibotto.model.reqBody.BodyIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static com.hacktim.cibotto.Constants.*;


@Service
public class DFlowService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public IntentResponse detectIntent(String projectId, BodyIntent bodyIntent) {
        IntentResponse intentResponse = new IntentResponse();
        DetectIntentResponse response = null;
        // Instantiates a client
        //
        OutputAudioConfig outputAudioConfig = null;
        if (Boolean.TRUE.equals(bodyIntent.getOutputAudio())) {
            OutputAudioEncoding audioEncoding = OutputAudioEncoding.OUTPUT_AUDIO_ENCODING_LINEAR_16;
            int sampleRateHertz = 16000;
            outputAudioConfig =
                OutputAudioConfig.newBuilder()
                    .setAudioEncoding(audioEncoding)
                    .setSampleRateHertz(sampleRateHertz)
                    .build();
        }
        //
        QueryResult queryResult = null;
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            String sessionId = bodyIntent.getIid()+"_"+ bodyIntent.getSid();
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input

            QueryInput queryInput;
            // Set the text (hello) and language code (en-US) for the query
            DetectIntentRequest dr;
            if (bodyIntent.getInputAudio() != null && !bodyIntent.getInputAudio().isEmpty()) {
                // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
                // Audio encoding of the audio content sent in the query request.
                AudioEncoding audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16;
                int sampleRateHertz = 16000;

                InputAudioConfig inputAudioConfig =
                    InputAudioConfig.newBuilder()
                        .setAudioEncoding(audioEncoding)
                        .setLanguageCode(bodyIntent.getLc()) // languageCode = "en-US"
                        .setSampleRateHertz(sampleRateHertz)
                        .build();

                // Build the query with the InputAudioConfig
                queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();

                // Read the bytes from the audio file
                byte [] inputAudio = Base64.getDecoder().decode(bodyIntent.getInputAudio());
                if (outputAudioConfig != null) {
                    dr = DetectIntentRequest.newBuilder()
                        .setSession(session.toString())
                        .setQueryInput(queryInput)
                        .setOutputAudioConfig(outputAudioConfig)
                        .setInputAudio(ByteString.copyFrom(inputAudio))
                        .build();
                } else {
                    dr = DetectIntentRequest.newBuilder()
                        .setSession(session.toString())
                        .setQueryInput(queryInput)
                        .setInputAudio(ByteString.copyFrom(inputAudio))
                        .build();
                }
            } else {
                String text = bodyIntent.getText();
                if (text == null || text.isEmpty()) {
                    text = "non capisco";   //  TODO controllare
                    logger.error ("both audio & text empty");
                }
                TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(bodyIntent.getLc());
                // Build the query with the TextInput
                queryInput = QueryInput.newBuilder().setText(textInput).build();
                if (outputAudioConfig != null) {
                    dr = DetectIntentRequest.newBuilder()
                        .setSession(session.toString())
                        .setQueryInput(queryInput)
                        .setOutputAudioConfig(outputAudioConfig)
                        .build();
                } else {
                    dr = DetectIntentRequest.newBuilder()
                        .setSession(session.toString())
                        .setQueryInput(queryInput)
                        .build();
                }
            }

            response = sessionsClient.detectIntent(dr);
            logger.debug("API RESPONSE "+ new Gson().toJson(response, DetectIntentResponse.class));

            // Display the query result
            queryResult = response.getQueryResult();

            logger.info("====================");
            logger.info(String.format("Query Text: '%s'\n", queryResult.getQueryText()));
            logger.info(String.format("Detected Intent: %s (confidence: %f)\n",
                queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence()));
            logger.info(String.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (queryResult != null) {
            intentResponse.setFulfillmentText(queryResult.getFulfillmentText());
            com.google.protobuf.Struct webhookPayload = queryResult.getWebhookPayload();
            if (webhookPayload != null) {
                if (webhookPayload.containsFields(PAYLOAD_IMAGE_PROD_URI)) {
                    intentResponse.setImageProdUri(webhookPayload.getFieldsMap().get(PAYLOAD_IMAGE_PROD_URI).getStringValue());
                }
                if (webhookPayload.containsFields(PAYLOAD_IMAGE_LOC_URI)) {
                    intentResponse.setImageLocUri(webhookPayload.getFieldsMap().get(PAYLOAD_IMAGE_LOC_URI).getStringValue());
                }
            }
        }
        if (Boolean.TRUE.equals(bodyIntent.getOutputAudio()) && response!= null && response.getOutputAudio() != null && response.getOutputAudio().size() > 0) {
            intentResponse.setOutputAudio(Base64.getEncoder().encodeToString(response.getOutputAudio().toByteArray()));
        }
        return intentResponse;
    }

    public String getProjectId()  {
        String projectId = "";
        String credentials = System.getenv(ENV_GOOGLE_CREDENTIALS);
        try {
            String jsonData =  Files.readString(Paths.get(credentials));
            GoogleApplicationCredentials googleApplicationCredentials = new Gson().fromJson(jsonData, GoogleApplicationCredentials.class);
            projectId = googleApplicationCredentials.getProject_id();
        }
        catch (IOException e)  {
            logger.error (e.toString());
            e.printStackTrace();
        }
        return projectId;
    }



}
