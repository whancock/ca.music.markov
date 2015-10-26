import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by whancock on 10/22/15.
 */
public class Markov {


    public static void main(String[] args) throws Exception {

        List<List<String>> corpus = MidiSerializer.buildCorpus();

        List<String> sequence = new ArrayList();
        sequence.add("A3");
        sequence.add("G3");

        List<String> notes = generateSong(corpus, sequence, 50);





        String result = "";
        for(String note: notes)
            result += note + ",";

        System.out.println(result);


        /*
         * now write the sequence into a midi file that can be played
         */
        SeqGen.writeSeq(sequence, "data/test_out_2.mid");

    }




    public static List generateSong(List<List<String>> corpus, List<String> sequence, int gen_len) throws Exception {

        int ngram = sequence.size();

        /*for(List<String> track: corpus) {
            for(String note: track) {
                System.out.print(note);
            }
            System.out.println();
        }*/

        Map<String, Map> probs = new HashMap();

        for(List<String> track: corpus) {

            for(int idx=0; idx<=track.size() - ngram - 1; idx++) {

                //System.out.println(notes[idx]);
                String key = "";

                for(int ng=0; ng<ngram; ng++)
                    key += track.get(idx + ng);

                if(!probs.containsKey(key))
                    probs.put(key, new HashMap<String, Integer>());

                Map<String, Integer> nsequence = probs.get(key);

                //now increment the next note of nsequence
                String output_key = track.get(idx + ngram);

                if(!nsequence.containsKey(output_key))
                    nsequence.put(output_key, new Integer(0));

                nsequence.put(output_key, new Integer(nsequence.get(output_key) + 1));
            }
        }

        /*for (String key : probs.keySet()) {
            System.out.println(key);
        }*/

        while(sequence.size() < gen_len) {

            int curidx = sequence.size() - ngram;

            List curNoteSeq = new ArrayList<String>();

            //String curNoteSeq = "";
            for(int idx=curidx; idx < curidx + ngram; idx++) {
                curNoteSeq.add(sequence.get(idx));
                //curNoteSeq += sequence.get(idx);
            }

            String nextNote = getNextNote(curNoteSeq, probs);

            if(nextNote == "")
                break;

            sequence.add(nextNote);
        }

        return sequence;
    }




    public static String getNextNote(List<String> noteSeq, Map<String, Map> bagOfNotes) {

        String noteSeqTxt = "";
        for(String note: noteSeq) {
            noteSeqTxt += note;
        }

        Integer max = 0;
        String maxNote = "";
        Map<String, Integer> test = bagOfNotes.get(noteSeqTxt);


        if(test == null) {
            System.out.println("Sequence broke at " + noteSeq);
            return "";
        }


        for(String key: test.keySet()) {

            Integer count = test.get(key);

            //use the most probable key unless it's the same as the ones before
            if(count > max && key != noteSeq.get(noteSeq.size() - 1)) {
                max = count;
                maxNote = key;
            }
        }

        return maxNote;
    }

}
