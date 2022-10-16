package solutions;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private Graph graph;
    private int numPagesCrawled = 0;
    private int maxPagesToCrawl = 10;

    public WebCrawler() {
    	this.graph = new Graph();
    }
    
    public void initialize(String baseURL) {
    	WebPage newPage = new WebPage(this.graph.nextId(), baseURL, null);
    	this.graph.add(newPage);
    }

    public void traverse(int iterations) {
    	this.maxPagesToCrawl = iterations;
    	System.out.println("Starting the traversal: " + this.graph.numUnprocessedPages + " unprocessed pages.");
    	while (this.graph.numUnprocessedPages > 0 && this.numPagesCrawled < this.maxPagesToCrawl) {
    		
    		// Fetch the HTML code
    		WebPage currentPage = this.graph.dequeue();
    		try {
    			System.out.println((numPagesCrawled + 1) + ". Processing " + currentPage.url + "...");
                
    			// pull down the current page and parse the data:
        		Document document = Jsoup.connect(currentPage.url).get();
        		currentPage.parseHTMLData(document);
        		HashSet<String> urls = this.getUncaLinksOnPageUnique(document);
                
                // Queue up the links that haven't been visited.
                for (String url : urls) {
                	// convert the link to a new page (or get a handle 
                	// to the page if it already exists in the graph):
                	WebPage newPage = this.graph.getOrCreate(url);
                	
                	// add page to the graph (if it's not there already):
                	this.graph.add(newPage);
                	currentPage.addOutboundPage(newPage);
                }
                
                // save the DB and the queue after every iteration:
                this.graph.save();
                
                ++this.numPagesCrawled;
                
            } catch (IOException e) {
                System.err.println("For '" + currentPage.url + "': " + e.getMessage());
                currentPage.crawled = true;
                this.graph.save();
            }
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	} 
    }
    
    private HashSet<String> getUncaLinksOnPageUnique(Document document) {
        Elements linksOnPage = document.select("a[href]");
    	HashSet<String> s = new HashSet<String>();
    	for (Element linkTag : linksOnPage) {
        	String validatedLink = this.getURL(linkTag);
        	if (validatedLink != null) {
        		s.add(validatedLink);
        	}
        }
    	return s;
    }
    
    private String getURL(Element linkTag) {
    	String url = linkTag.attr("abs:href").trim();
    	if (url.indexOf("unca.edu") == -1) {
    		return null;
    	}
    	if (!url.startsWith("http")) {
    		return null;
    	}
    	if (url.indexOf("#") != -1) {
    		url = url.substring(0, url.indexOf("#"));
    	}
    	if (url.charAt(url.length()-1) != '/') {
    		url = url + "/";
    	}
    	return url;
    }
    

    public static void main(String[] args) {
    	
        // initialize the crawler:
    	WebCrawler crawler = new WebCrawler();
    	
    	// if there are no links in the queue, initialize:
    	if (crawler.graph.size() == 0) {
    		crawler.initialize("https://www.unca.edu/");
    	}
    	
    	// begin crawling:
    	crawler.traverse(50);
    	
//    	// 90 works!
//    	crawler.graph.resetPageRanks();
//    	crawler.graph.print();
    	PageRank pageRanker = new PageRank(crawler.graph);
    	pageRanker.processPageRank(10);
    	crawler.graph.print();
    	
    }

}