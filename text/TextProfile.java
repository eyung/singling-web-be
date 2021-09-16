package com.ey.singlingweb.text;

import java.util.UUID;

public class TextProfile {

    private UUID textID;
    private String textData;
    private String instruction;

    public TextProfile(UUID textID, String textData) {
        this.textID = textID;
        this.textData = textData;
        this.instruction = "";
    }

//    public TextProfile(UUID textID, String textData, String instruction) {
//        this.textID = textID;
//        this.textData = textData;
//        this.instruction = instruction;
//    }

    public UUID getTextID() {
        return textID;
    }

    public void setTextID(UUID textID) {
        this.textID = textID;
    }

    public String getTextData() {
        return textData;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
