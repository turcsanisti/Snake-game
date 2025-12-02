package nhf.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;


public class Scores {

    public static class Entry implements Comparable<Entry> {
        private String name;
        private int score;
        private String time;
        private String result;

        public Entry(String name, int score, String time, String result) {
            this.name = name;
            this.score = score;
            this.time = time;
            this.result = result;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public String getTime() { return time; }
        public String getResult() { return result; }

        @Override
        public int compareTo(Entry o) {
            return Integer.compare(o.score, this.score); 
        }
    }

    public void saveScore(String playerName, int score, String time, String result) {
        JsonObject obj = Json.createObjectBuilder().add("playername", playerName).add("score", score).add("time", time).add("match result", result).build();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        File f = new File("scores.json");

        if (f.exists()) {
            try (JsonReader reader = Json.createReader(new FileReader(f))) {
                JsonArray current = reader.readArray();
                
                for(int i = 0; i <current.size(); i++) {
                    arrayBuilder.add(current.get(i));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        arrayBuilder.add(obj);

        Map<String, Object> map = new HashMap<>();
        map.put(JsonGenerator.PRETTY_PRINTING, true);

        JsonWriterFactory wf = Json.createWriterFactory(map);

        try (JsonWriter writer = wf.createWriter(new FileWriter(f))) {
            JsonArray ready = arrayBuilder.build();
            writer.writeArray(ready);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Entry> getTopScores() {
        List<Entry> scoreList = new ArrayList<>();
        File file = new File("scores.json");

        if (!file.exists()) {
            return scoreList; // Ha nincs fájl, üres listát adunk
        }

        try (JsonReader reader = Json.createReader(new FileReader(file))) {
            JsonArray array = reader.readArray();
            
            // Végigmegyünk a JSON tömbön és Entry objektumokat csinálunk
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.getJsonObject(i);
                String name = obj.getString("playername");
                int score = obj.getInt("score");
                String time = obj.getString("time");
                String result = obj.getString("match result");
                
                scoreList.add(new Entry(name, score, time, result));
            }
            
            // Rendezzük pontszám szerint csökkenőbe
            Collections.sort(scoreList);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return scoreList;
    }

}
