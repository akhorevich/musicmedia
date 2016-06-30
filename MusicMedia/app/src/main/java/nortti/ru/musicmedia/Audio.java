package nortti.ru.musicmedia;

import java.util.HashMap;
import java.util.Map;

public class Audio {
    String name;
    String url;

    public Audio() {
    }

    public Audio(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("url", url);

        return result;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
