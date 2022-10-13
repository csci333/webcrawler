package solutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class Database {
	private Map<String, WebPage> tracker;
	
	public Database() {
		this.tracker = new HashMap<String, WebPage>();
		this.load();
	}
	
	public void add(WebPage page) {
//		String key = this.generateKey(page.url);
		this.tracker.put(page.url, page);
	}
	
	public WebPage get(String url) {
//		String key = this.generateKey(url);
		return this.tracker.get(url);
	}
	
	public void print() {
		List<Map.Entry<String, WebPage>> list = new LinkedList<Map.Entry<String, WebPage>>(this.tracker.entrySet());
        list.sort(Entry.comparingByValue());
    	for (Map.Entry<String, WebPage> entry: list) {
    	    System.out.println(entry.getValue());
    	}
    }
	
	private String getFilePath() {
		String dir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        return dir + separator + "src/solutions/index.json";
	}
	
	public void load() {
		String filePath = this.getFilePath();
		File tempFile = new File(filePath);
		if (!tempFile.exists()) {
			return;
		}
		JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(filePath)) {

        	JSONArray pages = (JSONArray)parser.parse(reader);
            for (Object page : pages) {
            	this.add(new WebPage((JSONObject)page));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	public void save() {
		JSONArray pages = new JSONArray();
		for (String key: this.tracker.keySet()) {
			pages.add(this.tracker.get(key).toJSON());
    	}
		
        try (FileWriter file = new FileWriter(this.getFilePath())) {
            file.write(pages.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
