package solutions;

import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;

public class WebPage {
	public String url;
	public Boolean crawled = false;
	public int numInboundLinks = 0;
	private String words = "";
	List<WebPage> outboundLinks = new LinkedList<WebPage>();
	
	public WebPage(String url) {
		this.url = url;	
		++this.numInboundLinks;
	}
	
	public WebPage(JSONObject obj) {
		this.url = (String)obj.get("url");
		this.words = (String)obj.get("words");
		this.crawled = (boolean)obj.get("crawled");
		Long pageRank = (Long)obj.get("page_rank");
		this.numInboundLinks = pageRank.intValue();
	}
	
	@Override
	public String toString() {
		return "[" + this.numInboundLinks + "] " + this.url;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject page = new JSONObject();
		page.put("url", this.url);
		page.put("page_rank", this.numInboundLinks);
		page.put("words", this.words);
		page.put("crawled", this.crawled);
		return page;
	}

}
