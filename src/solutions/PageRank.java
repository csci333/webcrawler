package solutions;

import java.util.Collection;
import java.util.List;

public class PageRank {
	
	private Graph graph;
	
	public PageRank(Graph graph) {
		this.graph = graph;
	}

	public void processPageRank(int iterations) {
		if (iterations == 0) {
			// when it hits the base case, save:
			this.graph.save();
			return;
		}

		Collection<WebPage> crawledPages = graph.getCrawledPages();

		// move pageRank to pageRankOld 
		double oldTotal = 0.0;
		for (WebPage voter : crawledPages) {
			voter.pageRankOld = voter.pageRank;
			voter.pageRank = 0;
			oldTotal = oldTotal + voter.pageRankOld;
		}
		
		// assign pageRanks
		System.out.println("Calculating ranks...");
		double newTotal = 0.0;
		for (WebPage voter : crawledPages) {
			// only include crawled outbound links in the analysis:
			List<WebPage> votees = voter.getCrawledOutboundPages(this.graph);
			double kudoPoints = voter.pageRankOld / votees.size();
			for (WebPage votee : votees) {
				votee.pageRank += kudoPoints;
				newTotal += kudoPoints;
			}
		}
		
		// evaporation:
		System.out.println("Evaporation...");
		double evap = (oldTotal - newTotal) / crawledPages.size();
		for (WebPage voter : crawledPages) {
			voter.pageRank += evap;
		}
		
		/*************************************************************/
		/* output some statistics so we can see page rank converging */
		/*************************************************************/
		
		// calculate new total again:
		System.out.println("Calculate new totals...");
		newTotal = 0.0;
		for (WebPage voter : crawledPages) {
			newTotal += voter.pageRank;
		}
		
		double totalDiff = 0.0;
		for (WebPage voter : crawledPages) {
			totalDiff += Math.abs(voter.pageRankOld - voter.pageRank);
		}
		double avgDiff = totalDiff / crawledPages.size();
		System.out.println("Average diff (" + iterations + "): " + avgDiff);
		
		
		// recurse:
		processPageRank(iterations - 1);
	}
	

	
	public void resetPageRanks() {
		Collection<WebPage> pagesOfInterest = this.graph.getCrawledPages();
		for (WebPage voter : pagesOfInterest) {
			voter.pageRankOld = 0;
			voter.pageRank = 1;
		}
		this.graph.save();	
	}
	
}
