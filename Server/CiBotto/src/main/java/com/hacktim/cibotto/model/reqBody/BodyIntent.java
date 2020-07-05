package com.hacktim.cibotto.model.reqBody;

public class BodyIntent {
    private String iid;  //  InstanceId
    private long   sid; //  timestamp inizio conversazione
    private String text;   // testo richiesta
    private String lc;  //  language_code
    private Boolean outputAudio = false;
    private String inputAudio;  //  audio richiesta
    private String mockupResponseFile = null;

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLc() {
        return lc;
    }

    public void setLc(String lc) {
        this.lc = lc;
    }

    public Boolean getOutputAudio() {
        return outputAudio;
    }

    public void setOutputAudio(Boolean outputAudio) {
        this.outputAudio = outputAudio;
    }

    public String getInputAudio() {
        return inputAudio;
    }

    public void setInputAudio(String inputAudio) {
        this.inputAudio = inputAudio;
    }

    public String getMockupResponseFile() {
        return mockupResponseFile;
    }

    public void setMockupResponseFile(String mockupResponseFile) {
        this.mockupResponseFile = mockupResponseFile;
    }
}
