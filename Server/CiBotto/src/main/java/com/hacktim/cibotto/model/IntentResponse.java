package com.hacktim.cibotto.model;

public class IntentResponse {

    private String fulfillmentText;
    private String imageProdUri;
    private String imageLocUri;
    private String outputAudio;

    public String getFulfillmentText() {
        return fulfillmentText;
    }

    public void setFulfillmentText(String fulfillmentText) {
        this.fulfillmentText = fulfillmentText;
    }

    public String getImageProdUri() {
        return imageProdUri;
    }

    public void setImageProdUri(String imageProdUri) {
        this.imageProdUri = imageProdUri;
    }

    public String getImageLocUri() {
        return imageLocUri;
    }

    public void setImageLocUri(String imageLocUri) {
        this.imageLocUri = imageLocUri;
    }

    public String getOutputAudio() {
        return outputAudio;
    }

    public void setOutputAudio(String outputAudio) {
        this.outputAudio = outputAudio;
    }

}
