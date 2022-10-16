package solutions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

public class Node {
	public Integer from;
	public Set<Integer> to = new HashSet<Integer>();
	
	public Node(WebPage from, WebPage to) {
		this.from = from.id;
		this.to.add(to.id);
	}
	
	@SuppressWarnings("unchecked")
	public Node(JSONObject obj) {
		Long from = (Long)obj.get("from");
		this.from = from.intValue();
		List<Long> to = (List<Long>)obj.get("to");
		for (Long id : to) {
			this.to.add(id.intValue());
		}
	}
	
	public void add(WebPage to) {
		this.to.add(to.id);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject page = new JSONObject();
		page.put("from", this.from);
		page.put("to", this.to);
		return page;
	}
}
