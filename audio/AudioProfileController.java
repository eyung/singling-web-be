package com.ey.singlingweb.audio;

import com.ey.singlingweb.Composer;
import com.ey.singlingweb.Instruction;
import com.ey.singlingweb.Producer;
import com.ey.singlingweb.TransformationManager;
import com.ey.singlingweb.text.TextProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("api/v1/audio-profile")
@CrossOrigin(origins = "*", allowedHeaders = "*")
//@CrossOrigin(origins = "http://localhost:3000")
public class AudioProfileController {

    private final AudioProfileService audioProfileService;

    List<TransformationManager.Instruction> instructions = new ArrayList<>();

    Set<String> passingWords = new HashSet<String>();

    // Create and init producer
    Producer producer = new Producer();

    // Create Composer
    Composer composer;

    //
    Pattern pattern;

    UUID thisID;

    @Autowired
    public AudioProfileController(AudioProfileService audioProfileService) {
        this.audioProfileService = audioProfileService;
    }

    @GetMapping(
            path = "test",
            produces = "application/json"
    )
    public String test() {
        return ("test");
    }

    @GetMapping(
            path = "testjsonGET",
            produces = "application/json"
    )
    public String testjsonGET() {

        ObjectMapper objectMapper = new ObjectMapper();
        Instruction instruction = new Instruction("1", "2", "1", "2", "1", "2", "1");
        String instructionString ="";

        try {
            objectMapper.writeValue(new File("target/test.json"), instruction);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            instructionString = objectMapper.writeValueAsString(instruction);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return instructionString;
    }

    @PostMapping(path = "testjsonPOST")
    public AudioProfile testjsonPOST(@RequestBody String payload) {

        ObjectMapper objectMapper = new ObjectMapper();
        //Instruction instruction = new Instruction();
        TextProfile textProfile = new TextProfile();
        UUID textprofileID;
        String textprofileData;

        try {
            //instruction = objectMapper.readValue(payload, Instruction.class);
            textProfile = objectMapper.readValue(payload, textProfile.getClass());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        textprofileID = textProfile.getTextID();
        textprofileData = textProfile.getTextData();

        System.out.println(textProfile.getTextID() + " : " + textProfile.getTextData());

        for (TransformationManager.Instruction i : textProfile.getInstructions()) {
            System.out.println(i);
        }

        //System.out.println("The payload is: " + instruction.toString());

        ////

        // Init Composer
        composer = new Composer
                .ComposerBuilder()
                .setInstrument("Piano")
                .setNoteLength(1.00)
                .setOctave(3)
                .setTempo(550.00)
                .setFrequency(200.00)
                .setRestLength(0.50)
                .setRestLengthLineBreak(1.00)
                .wantWord(true)
                .withOperation("LEXNAMEFREQ")
                .withOrdering(0)
                .useTransformations(textProfile.getInstructions())
                .excludeWords(passingWords)
                .build();

        // Process user input text
        composer.processString(textprofileData);

        // Init Producer using pattern created by Composer
        producer.setPlayer();

        // Pass created sound pattern to producer
        producer.setPattern(composer.getPattern());

        // Start player
        //producer.doStartPlayer(0.50);
        //producer.doPlay();

        // TESTING
        //this.pattern = composer.getPattern();
        //return producer.getSequence().toString();

        // TESTING 2
        //String tempDir = "C:\\Users\\Effiam\\Downloads\\singling-web\\temp\\";
        String tempDir = "/home/ec2-user/temp/";
        tempDir = "temp/";

        try {
            File f = new File(tempDir);

            // Delete temp directory
            FileUtils.cleanDirectory(f);
            FileUtils.forceDelete(f);

            // Create temp directory
            FileUtils.forceMkdir(f);

            // Save MIDI file
            //producer.doSaveAsMidi(tempDir + "temp1");
            //producer.doSaveAsMidi(tempDir);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            InputStream stream =  new FileInputStream(producer.doSaveAsWAV(tempDir, textProfile.getTextID().toString()));
            MultipartFile multipartFile = new MockMultipartFile("file", textProfile.getTextID().toString(), MediaType.TEXT_HTML_VALUE, stream);

            audioProfileService.uploadAudio(textProfile.getTextID(), multipartFile);
            //System.out.println("File successfully uploaded at: " + tempDir);

        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioProfile audio = audioProfileService
                .getAudioProfiles()
                .stream()
                .filter(audioProfile -> audioProfile.getAudioID().equals(textprofileID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found ID"));

        return audio;
    }

    @PostMapping(path = "processtext",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AudioProfile processtext(@RequestBody TextProfile input) {
        System.out.println("ID: " + input.getTextID());
        System.out.println("Text: " + input.getTextData());

        //thisID = input.getTextID();

        // Init Composer
        composer = new Composer
                .ComposerBuilder()
                .setInstrument("Piano")
                .setNoteLength(1.00)
                .setOctave(3)
                .setTempo(550.00)
                .setFrequency(200.00)
                .setRestLength(0.50)
                .setRestLengthLineBreak(1.00)
                .wantWord(true)
                .withOperation("LEXNAMEFREQ")
                .withOrdering(0)
                .useTransformations(instructions)
                .excludeWords(passingWords)
                .build();

        // Process user input text
        composer.processString(input.getTextData());

        // Init Producer using pattern created by Composer
        producer.setPlayer();

        // Pass created sound pattern to producer
        producer.setPattern(composer.getPattern());

        // Start player
        //producer.doStartPlayer(0.50);
        //producer.doPlay();

        // TESTING
        //this.pattern = composer.getPattern();
        //return producer.getSequence().toString();

        // TESTING 2
        //String tempDir = "C:\\Users\\Effiam\\Downloads\\singling-web\\temp\\";
        String tempDir = "/home/ec2-user/temp/";
        tempDir = "temp/";

        try {
            File f = new File(tempDir);

            // Delete temp directory
            FileUtils.cleanDirectory(f);
            FileUtils.forceDelete(f);

            // Create temp directory
            FileUtils.forceMkdir(f);

            // Save MIDI file
            //producer.doSaveAsMidi(tempDir + "temp1");
            //producer.doSaveAsMidi(tempDir);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            InputStream stream =  new FileInputStream(producer.doSaveAsWAV(tempDir, input.getTextID().toString()));
            MultipartFile multipartFile = new MockMultipartFile("file", input.getTextID().toString(), MediaType.TEXT_HTML_VALUE, stream);

            audioProfileService.uploadAudio(input.getTextID(), multipartFile);
            //System.out.println("File successfully uploaded at: " + tempDir);

        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioProfile audio = audioProfileService
                .getAudioProfiles()
                .stream()
                .filter(audioProfile -> audioProfile.getAudioID().equals(input.getTextID()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found ID"));

        return audio;

    }

    @PostMapping(path = "processTextPreset1",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AudioProfile processTextPreset1(@RequestBody TextProfile input) {
        System.out.println("ID: " + input.getTextID());
        System.out.println("Text: " + input.getTextData());

        //thisID = input.getTextID();

        // Test transformation
        TransformationManager.Instruction testInstruction = new TransformationManager.Instruction();
        testInstruction.setMod(TransformationManager.Instruction.Mods.WORDLENGTH);
        testInstruction.setModValue("3");
        testInstruction.setSoundMod(TransformationManager.Instruction.SoundMods.INSTRUMENT);
        testInstruction.setSoundModValue("KOTO");
        testInstruction.setModOperator(TransformationManager.Instruction.ModOperators.EQUALTO);
        testInstruction.setChangeMode(TransformationManager.Instruction.ChangeModes.SET);
        instructions.add(testInstruction);

        // Init Composer
        composer = new Composer
                .ComposerBuilder()
                .setInstrument("Piano")
                .setNoteLength(1.00)
                .setOctave(3)
                .setTempo(550.00)
                .setFrequency(200.00)
                .setRestLength(0.50)
                .setRestLengthLineBreak(1.00)
                .wantWord(true)
                .withOperation("LEXNAMEFREQ")
                .withOrdering(0)
                .useTransformations(instructions)
                .excludeWords(passingWords)
                .build();

        // Process user input text
        composer.processString(input.getTextData());

        // Init Producer using pattern created by Composer
        producer.setPlayer();

        // Pass created sound pattern to producer
        producer.setPattern(composer.getPattern());

        // Start player
        //producer.doStartPlayer(0.50);
        //producer.doPlay();

        // TESTING
        //this.pattern = composer.getPattern();
        //return producer.getSequence().toString();

        // TESTING 2
        //String tempDir = "C:\\Users\\Effiam\\Downloads\\singling-web\\temp\\";
        String tempDir = "/home/ec2-user/temp/";
        tempDir = "temp/";

        try {
            File f = new File(tempDir);

            // Delete temp directory
            FileUtils.cleanDirectory(f);
            FileUtils.forceDelete(f);

            // Create temp directory
            FileUtils.forceMkdir(f);

            // Save MIDI file
            //producer.doSaveAsMidi(tempDir + "temp1");
            //producer.doSaveAsMidi(tempDir);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            InputStream stream =  new FileInputStream(producer.doSaveAsWAV(tempDir, input.getTextID().toString()));
            MultipartFile multipartFile = new MockMultipartFile("file", input.getTextID().toString(), MediaType.TEXT_HTML_VALUE, stream);

            audioProfileService.uploadAudio(input.getTextID(), multipartFile);
            //System.out.println("File successfully uploaded at: " + tempDir);

        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioProfile audio = audioProfileService
                .getAudioProfiles()
                .stream()
                .filter(audioProfile -> audioProfile.getAudioID().equals(input.getTextID()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found ID"));

        return audio;

    }

    @PostMapping(path = "processTextPreset2")
    @ResponseBody
    public String processTextPreset2(@RequestBody TextProfile input) {
        System.out.println("ID: " + input.getTextID());
        System.out.println("Text: " + input.getTextData());

        //thisID = input.getTextID();

        ByteArrayOutputStream baos = null;

        // Init Composer
        composer = new Composer
                .ComposerBuilder()
                .setInstrument("Piano")
                .setNoteLength(1.00)
                .setOctave(3)
                .setTempo(550.00)
                .setFrequency(200.00)
                .setRestLength(0.50)
                .setRestLengthLineBreak(1.00)
                .wantWord(true)
                .withOperation("LEXNAMEFREQ")
                .withOrdering(0)
                .useTransformations(instructions)
                .excludeWords(passingWords)
                .build();

        // Process user input text
        composer.processString(input.getTextData());

        // Init Producer using pattern created by Composer
        producer.setPlayer();

        // Pass created sound pattern to producer
        producer.setPattern(composer.getPattern());

        // Start player
        //producer.doStartPlayer(0.50);
        //producer.doPlay();

        // Get sequence
        baos = new ByteArrayOutputStream();

        this.pattern = composer.getPattern();

        try {
            MidiFileManager.save(producer.getSequence(), baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return producer.getSequence().toString();
        return encoder(baos.toByteArray());
    }

    @PostMapping(path = "processTextTransform1",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void processTextTransform1(@RequestBody String payload) {

    }

    @GetMapping(
            path = "getaudio",
            produces = "application/json"
    )
    public String getaudio() {

        AudioProfile audio = audioProfileService
                .getAudioProfiles()
                .stream()
                .filter(audioProfile -> audioProfile.getAudioID().equals(thisID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found ID"));

        return audio.getAudioLink();
    }

    private static String encoder(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }
}
