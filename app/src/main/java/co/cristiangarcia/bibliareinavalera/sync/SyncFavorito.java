package co.cristiangarcia.bibliareinavalera.sync;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncFavorito {
    private JSONArray array = new JSONArray();
    private String synctime;

    public String getSyntime() {
        return this.synctime;
    }

    public void setSyntime(String synctime) {
        this.synctime = synctime;
    }

    public void addNodeFavorito(int id, int libro, int capitulo, int versiculoi, int versiculof, String text, long date, int state) {
        Map<String, String> jsonMap = new HashMap();
        jsonMap.put("id", String.valueOf(id));
        jsonMap.put("lib", String.valueOf(libro));
        jsonMap.put("cap", String.valueOf(capitulo));
        jsonMap.put("veri", String.valueOf(versiculoi));
        jsonMap.put("verf", String.valueOf(versiculof));
        jsonMap.put("text", text);
        jsonMap.put("fecha", String.valueOf(date));
        jsonMap.put("state", String.valueOf(state));
        this.array.put(new JSONObject(jsonMap));
    }

    public String getJSONData() {
        JSONObject finalObject = new JSONObject();
        try {
            finalObject.put("oldsynctime", this.synctime);
            finalObject.put("datas", this.array);
        } catch (JSONException e) {
        }
        return finalObject.toString();
    }
}
