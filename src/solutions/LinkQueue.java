package solutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LinkQueue {

	private Queue<String> queue;
	
    public LinkQueue() {
    	this.queue = new ArrayDeque<String>();	
    	this.load(); // remembers previously queued urls
    }
    
    public void add(String url) {
    	this.queue.add(url);
    }
    
    public int size() {
    	return this.queue.size();
    }
    
    public String remove() {
    	return this.queue.remove();
    }
    
    public boolean contains(String url) {
//    	String key = this.generateKey(url);
    	return this.queue.contains(url);
    }
    
    public void print() {
    	int counter = 1;
    	for (String link : this.queue) {
    		System.out.println(counter + ". " + link);
    		++counter;
    	}
    }
    
    private String getFilePath() {
		String dir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        return dir + separator + "src/solutions/queue.json";
	}
    
	@SuppressWarnings("unchecked")
	public void load() {
		String filePath = this.getFilePath();
		File tempFile = new File(filePath);
		if (!tempFile.exists()) {
			return;
		}
		JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(filePath)) {
        	JSONArray urls = (JSONArray)parser.parse(reader);
            for (String url : (List<String>)urls) {
            	this.queue.add(url);
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
		 for (String item: this.queue) {
			 pages.add(item);
        }
		
        try (FileWriter file = new FileWriter(this.getFilePath())) {
            file.write(pages.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
