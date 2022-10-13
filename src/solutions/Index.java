package solutions;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class Index {
	private Map<String, WebPage> tracker;
	
	public Index() {
		this.tracker = new HashMap<String, WebPage>();
		this.load();
	}
	
	public void add(WebPage page) {
		this.tracker.put(page.url, page);
	}
	
	public WebPage get(String url) {
		return this.tracker.get(url);
	}
	
	public void print() {
    	for (String key: this.tracker.keySet()) {
    	    System.out.println(this.tracker.get(key));
    	}
    }
	
	public void load() {
		JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(this.getFilePath())) {

        	JSONArray pages = (JSONArray) parser.parse(reader);
            System.out.println(pages);
            for (Object page : pages) {
            	this.add(new WebPage((JSONObject)page));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//		
//		
//		
//		
//		
//		JSONArray pages = new JSONArray();
//		for (String key: this.tracker.keySet()) {
//			pages.add(this.tracker.get(key).toJSON());
//    	}
//		String dir = System.getProperty("user.dir");
//        String separator = System.getProperty("file.separator");
//        String filePath = dir + separator + this.fileName;
//        System.out.println(separator);
//        try (FileWriter file = new FileWriter(filePath)) {
//            file.write(pages.toJSONString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	}
	
	private String getFilePath() {
		String dir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        return dir + separator + "src/solutions/index.json";
	}
	
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
