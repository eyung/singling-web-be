package com.ey.singlingweb;

import org.jfugue.devtools.DiagnosticParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.temporal.TemporalPLP;
import org.staccato.StaccatoParser;

public class PlayerManager implements Runnable {

    private Pattern pattern;
    private Player player;
    private double delay;

    // Initialise Parsers
    private StaccatoParser parser = new StaccatoParser();
    private TemporalPLP plp = new TemporalPLP();

    // Initialise Parser Listeners
    private DiagnosticParserListener dpl = new DiagnosticParserListener();
    private LyricParserListener lpl = new LyricParserListener();
    private MarkerParserListener mpl = new MarkerParserListener();

    public void setPattern (Pattern myPattern, Player myPlayer, double myDelay) {
        pattern = myPattern;
        player = myPlayer;
        delay = myDelay;
    }

    @Override
    public void run() {
        try {
            parser.addParserListener(plp);
            parser.parse(pattern);

            // Output diagnostic data to console
            //plp.addParserListener(dpl);

            // Highlight lyrics as music is played
            plp.addParserListener(lpl);

            // Output marker data to console
            plp.addParserListener(mpl);

            //player.play(pattern);
            //player.delayPlay(1000, pattern);
            player.delayPlay((long) delay*1000, pattern);

            // Start temporal parsing
            plp.parse();

            //player.getManagedPlayer().finish();

        } catch (Exception e) {
        }
    }

    public void stop() {
        lpl.stop();
    }

    public void resume() {
        lpl.resume();
    }
}

class LyricParserListener extends ParserListenerAdapter {
    int offset=0;
    private volatile boolean paused = false;

    @Override
    public void onLyricParsed(String lyric) {
        synchronized(this) {
            while (paused) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(lyric);
            //doHighlight(lyric);
            sendNLPOutput(lyric);
        }
    }

    public void stop() {
        synchronized(this) {
            this.paused = true;
            notifyAll();
        }
    }

    public void resume() {
        synchronized(this) {
            this.paused = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notifyAll();
        }
    }

    private void sendNLPOutput(String lyric) {
        try {
            //Main.console.appendText(lyric + ": ");
        } catch (Exception e) {}
    }
}

class MarkerParserListener extends ParserListenerAdapter {
    private volatile boolean paused = false;

    @Override
    public void onMarkerParsed(String marker) {
        synchronized(this) {
            while (paused) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(marker);
            sendNLPOutput(marker);
        }
    }

    public void stop() {
        synchronized(this) {
            this.paused = true;
            notifyAll();
        }
    }

    public void resume() {
        synchronized(this) {
            this.paused = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notifyAll();
        }
    }

    private void sendNLPOutput(String marker) {
        try {
            //ain.console.appendText(marker + "\n");
        } catch (Exception e) {}
    }
}