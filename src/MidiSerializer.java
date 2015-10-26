import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.*;

public class MidiSerializer {

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final int PROG_CHANGE = 0xC0;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};



    public static List<List<String>> getNoteSeq(Path filename) throws Exception {

        List noteSeq = new ArrayList<List>();

        Sequence sequence = MidiSystem.getSequence(new File(filename.toString()));

        int trackNumber = 0;
        for (Track track : sequence.getTracks()) {

            List noteList = new ArrayList<String>();

            trackNumber++;
            //System.out.println("Track " + trackNumber + ": size = " + track.size());
            //System.out.println();

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);

                //System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {

                    ShortMessage sm = (ShortMessage) message;
                    //System.out.print("Channel: " + sm.getChannel() + " ");

                    if (sm.getCommand() == NOTE_ON) {

                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();

                        noteList.add(noteName + octave);

                        //System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        //System.out.print(noteName + octave + ",");

                    } else if (sm.getCommand() == NOTE_OFF) {

                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();

                        noteList.add(noteName + octave);

                        //System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);

                    } else if(sm.getCommand() == PROG_CHANGE) {

                        //System.out.println(sm.getMessage());
                        //int one = sm.getData1();
                        //int two = sm.getData2();
                        //System.out.println(one + " " + two);

                    } else {
                        //System.out.println("\nCommand:" + sm.getCommand());
                    }
                } else {
                    //System.out.println("Other message: " + message.getClass());
                }
            }

            noteSeq.add(noteList);
        }

        return noteSeq;
    }




    public static void addToCorpus(Path file, List corpus) {

        try {
            List<List<String>> song = getNoteSeq(file);

            for(List track: song) {
                if(track.size() > 0)
                    corpus.add(track);
            }

        } catch(Exception e) {

        }

    }



    public static List buildCorpus() {

        List corpus = new ArrayList<List<String>>();

        try {
            Files.walk(Paths.get("data/0")).forEach(p -> addToCorpus(p, corpus));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return corpus;
    }




    //public static void main(String[] args) throws Exception {
    //    buildCorpus();
    //}

}