package com.ey.singlingweb;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import javax.sound.midi.Sequence;
import javax.sound.sampled.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Producer {

    // Instantiate player classes
    private Player player;
    private PlayerManager singlingPlayer;
    private Thread threadPlayer;
    private Pattern pattern;

    private SourceDataLine sLine;

    /**
     *
     * @param
     */
    public Producer() {
        //pattern = thisPattern;
    }

    /**
     *
     * @param pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     *
     */
    public void setPlayer() {
        if (player == null) {
            player = new Player();
        } else if (player != null) {
            try {
                player.getManagedPlayer().finish();
            } catch (Exception e) {
            }
            player = new Player();
        }

        if (singlingPlayer == null) {
            singlingPlayer = new PlayerManager();
        } else if (singlingPlayer != null) {
            singlingPlayer.stop();
            singlingPlayer = new PlayerManager();
        }

        threadPlayer = new Thread(singlingPlayer);
    }

    /**
     *
     * @param baseNoteLength
     */
    public void doStartPlayer(double baseNoteLength) {
        singlingPlayer.setPattern(pattern, player, baseNoteLength);

        System.out.println("Start player:" + threadPlayer.getId());
        if (threadPlayer.getState() == Thread.State.NEW) {
            threadPlayer.start();
        }
    }

    /**
     *
     */
    public void doPause() {
        try {
            if (player.getManagedPlayer().isPlaying()) {
                player.getManagedPlayer().pause();
                singlingPlayer.stop();
            } else if (player.getManagedPlayer().isPaused()) {
                player.getManagedPlayer().resume();
                singlingPlayer.resume();
            }
        } catch (Exception e) {
        }
    }

    /**
     *
     */
    public void doPlay() {
        player = new Player();

        player.play(pattern);
        player.getManagedPlayer().finish();
    }

    /**
     *
     * @param
     * @param output
     * @throws Exception
     */
    public String doSaveAsMidi(String output) throws Exception {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        File file = new File(output + fileName + ".mid");
        MidiFileManager midiFileManager = new MidiFileManager();
        midiFileManager.savePatternToMidi(pattern, file);

        return file.getAbsolutePath();
    }

    /**
     *
     * @param
     * @param output
     * @throws Exception
     */
    public File doSaveAsWAV(String output, String fileName) throws Exception {
        //String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Save to MIDI first
        File midiFile = new File(output + fileName + ".mid");
        MidiFileManager midiFileManager = new MidiFileManager();
        midiFileManager.savePatternToMidi(pattern, midiFile);

        // Convert MIDI to WAV
        AudioInputStream stream = AudioSystem.getAudioInputStream(midiFile);
        File file = new File(output + fileName + ".wav");
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);

        // Delete MIDI file
        midiFile.delete();

        // Close stream
        stream.close();

        return file;
    }

    public Sequence getSequence() {
        Sequence sequence = this.player.getSequence(this.pattern);
        System.out.println(sequence);
        return sequence;
    }

    public byte[] audioByte(String output) throws Exception {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Save to MIDI first
        File midiFile = new File(output + fileName + ".mid");
        MidiFileManager midiFileManager = new MidiFileManager();
        midiFileManager.savePatternToMidi(pattern, midiFile);

        // Convert MIDI to WAV
        AudioInputStream stream = AudioSystem.getAudioInputStream(midiFile);
        File file = new File(output + fileName + ".wav");
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);

        // Delete MIDI file
        midiFile.delete();

        // Close stream
        stream.close();


        // TEST?
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        //AudioInputStream in = AudioSystem.getAudioInputStream(file);

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();
        byte[] audioBytes = out.toByteArray();

        return audioBytes;
    }

    public File test(String output, String fileName) throws Exception {
        //String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Save to MIDI first
        //File midiFile = new File(output + fileName + ".mid");
        MidiFileManager midiFileManager = new MidiFileManager();
        //midiFileManager.savePatternToMidi(pattern, midiFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        midiFileManager.savePatternToMidi(pattern, byteArrayOutputStream);
        System.out.println("Size of the outputStream : " + byteArrayOutputStream.size());

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        System.out.println("Size of byte array : " + byteArray.length);

        ByteArrayInputStream byteArrayInputStream  = new ByteArrayInputStream(byteArray);

        // Convert MIDI to WAV
        //AudioInputStream stream = AudioSystem.getAudioInputStream(midiFile);
        AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
        AudioInputStream stream = new AudioInputStream(byteArrayInputStream, audioFormat, byteArray.length);
        System.out.println(audioFormat.getFrameSize());

        File file = new File(output + fileName + ".wav");
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);

        // Delete MIDI file
        //midiFile.delete();

        // Close stream
        stream.close();

        //return file.getAbsolutePath();
        return file;
    }
}