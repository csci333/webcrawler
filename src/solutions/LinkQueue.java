package solutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LinkQueue {

	private Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	
    public LinkQueue() {
    	this.load(); // remembers previously queued urls
    }
    
    public void add(WebPage from, WebPage to) {
    	Node entry = this.nodes.get(from.id);
    	if (entry != null) {
    		entry.add(to);
    	} else {
    		this.nodes.put(from.id, new Node(from, to));
    	}
    }
    
    public int size() {
    	return this.nodes.size();
    }
    
    
    public void print() {
    	for (Node node : this.nodes.values()) {
    		System.out.println(node);
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
        	JSONArray nodeObjects = (JSONArray)parser.parse(reader);
            for (Object nodeObject : nodeObjects) {
            	Node node = new Node((JSONObject)nodeObject);
            	this.nodes.put(node.from, node);
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
		for (Integer key: this.nodes.keySet()) {
			pages.add(this.nodes.get(key).toJSON());
    	}
		
        try (FileWriter file = new FileWriter(this.getFilePath())) {
            file.write(pages.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
