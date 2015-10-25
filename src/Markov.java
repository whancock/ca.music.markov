import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by whancock on 10/22/15.
 */

//package ca.markov;

public class Markov {


    public static void main(String[] args) throws Exception {

        int ngram = 2;

        List<String> lines = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader("data/raw.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                lines.add(line);
            }
        }


        Map<String, Map> probs = new HashMap();


        for(String line: lines) {

            String[] notes = line.split(",");

            for(int idx=0; idx<=notes.length - ngram - 1; idx++) {

                //System.out.println(notes[idx]);
                String key = "";

                for(int ng=0; ng<ngram; ng++)
                    key += notes[idx + ng];

                if(!probs.containsKey(key))
                    probs.put(key, new HashMap<String, Integer>());

                Map<String, Integer> nsequence = probs.get(key);

                //now increment the next note of nsequence
                String output_key = notes[idx + ngram];

                if(!nsequence.containsKey(output_key))
                    nsequence.put(output_key, new Integer(0));

                nsequence.put(output_key, new Integer(nsequence.get(output_key) + 1));
            }
        }






        List<String> sequence = new ArrayList();
        sequence.add("A3");
        sequence.add("E3");

        int gen_len = 30;

        while(sequence.size() < gen_len) {

            int curidx = sequence.size() - ngram;

            String curNoteSeq = "";
            for(int idx=curidx; idx < curidx + ngram; idx++)
                curNoteSeq += sequence.get(idx);

            String nextNote = getNextNote(curNoteSeq, probs);

            if(nextNote == "")
                break;


            sequence.add(nextNote);
        }



        String result = "";
        for(String note: sequence)
            result += note + ",";


        System.out.println(result);



    }


    public static String getNextNote(String noteSeq, Map<String, Map> bagOfNotes) {

        Integer max = 0;
        String maxNote = "";
        Map<String, Integer> test = bagOfNotes.get(noteSeq);

        for(String key: test.keySet()) {

            Integer count = test.get(key);

            if(count > max) {
                max = count;
                maxNote = key;
            }
        }

        return maxNote;
    }

}
