package solutions;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private Index index;
    private Queue<String> queue;
    private int numPagesCrawled = 0;
    private int maxPagesToCrawl = 10;

    public WebCrawler(String baseURL) {
    	this.index = new Index();
        this.queue = new ArrayDeque<String>();
        this.queue.add(baseURL);
    }

    public void start() {
    	System.out.println("Starting!");
    	while (this.queue.size() > 0 && this.numPagesCrawled <= maxPagesToCrawl) {
    		//1. Fetch the HTML code
    		String url = this.queue.remove();
    		this.index.add(new WebPage(url));
        	try {
        		System.out.println("Processing " + url + "...");
                Document document = Jsoup.connect(url).get();
                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                //5. For each extracted URL... go back to Step 4.
                for (String link : this.getLinksOnPageUnique(linksOnPage)) {
                	if (this.index.get(link) == null) {
                		this.queue.add(link);
                	} else {
                		//this.tracker.put(link, );
                		this.index.get(link).numInboundLinks += 1;
                	}
                }
                ++this.numPagesCrawled;
            } catch (IOException e) {
                System.err.println("For '" + url + "': " + e.getMessage());
            }
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} 
    }
    
    private HashSet<String> getLinksOnPageUnique(Elements linksOnPage) {
    	HashSet<String> s = new HashSet<String>();
    	for (Element linkTag : linksOnPage) {
        	String link = this.getURL(linkTag);
        	s.add(link);
        }
    	return s;
    }
    
    private String getURL(Element linkTag) {
    	String link = linkTag.attr("abs:href").trim();
    	if (link.indexOf("#") != -1) {
    		link = link.substring(0, link.indexOf("#"));
    	}
    	if (link.charAt(link.length()-1) != '/') {
    		link = link + "/";
    	}
    	return link;
    }
    
    public void printQueue() {
    	int counter = 1;
    	for (String link : this.queue) {
    		System.out.println(counter + ". " + link);
    		++counter;
    	}
    }
    

    public static void main(String[] args) {
        //1. Pick a URL from the frontier
    	WebCrawler crawler = new WebCrawler("https://www.unca.edu/");
    	crawler.start();
    	System.out.println("Links in the Queue");
    	crawler.printQueue();
    	System.out.println("Links that have been crawled");
    	crawler.index.print();
    	crawler.index.save();
    }

}