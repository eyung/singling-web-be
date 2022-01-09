package com.ey.singlingweb.text;

import com.ey.singlingweb.TransformationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TextProfile {

    private UUID textID;
    private String textData;
    //private List<Instruction> instructions;
    private List<TransformationManager.Instruction> instructions;

    public TextProfile() { }

    public TextProfile(UUID textID, String textData) {
        this.textID = textID;
        this.textData = textData;
        this.instructions = new ArrayList<>();
    }

    public TextProfile(UUID textID, String textData, List<TransformationManager.Instruction> instructions) {
        this.textID = textID;
        this.textData = textData;
        this.instructions = instructions;
    }

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

    public List<TransformationManager.Instruction> getInstructions() {
        return instructions;
    }
}
