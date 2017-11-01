package com.hankcs.lda;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GibbsSampleTask implements Callable<Void>{
	static public final Log log = LogFactory.getLog(GibbsSampleTask.class);
	public int m;//document
	public int n;//word
	
	public int oldTopic;
	public int newTopic;
	
	public int documents_m_n;
	public int nw_documents_m_n_oldTopic;
	
	public int nd_m_oldTopic;
	public int nwsum_oldTopic;
	public int ndsum_m;
	
	
	int[][] nw;
	int[][] nd;
	int[] nwsum;
	int[] ndsum;
	int[][] documents;
	int z[][];
	int K;
	double beta;
	double alpha;
	int V;
	int[] zd;
	public void init(int m, int[][] nw, int[][] nd, int[] nwsum, int[] ndsum,
			int[][] documents, int z[][]) {
		this.nw = nw;
		this.nd = nd;
		this.nwsum = nwsum;
		this.ndsum = ndsum;
		this.documents = documents;
		this.z = z;
		oldTopic = z[m][n];
		
		documents_m_n = nw[documents[m][n]][oldTopic] - 1;
		nw_documents_m_n_oldTopic = nd[m][oldTopic] - 1;
		nd_m_oldTopic = nd[m][oldTopic] - 1;
		
		nwsum_oldTopic = nwsum[oldTopic]-1;
		ndsum_m = ndsum[m]-1;
		zd = new int[z[m].length];
	}
	
	public Void call() {
		log.info("run document no."+m);
		for (int n = 0; n < z[m].length; n++) {
			double[] p = new double[K];
			for (int k = 0; k < K; k++) {
				if(k != oldTopic){
					p[k] = (nw[documents[m][n]][k] + beta) / (nwsum[k] + V * beta)
							* (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
				}else{
					p[k] = (nw_documents_m_n_oldTopic + beta) / (ndsum_m + V * beta)
							* (nd_m_oldTopic + alpha) / (ndsum_m + K * alpha);
				}
			}
			for (int k = 1; k < p.length; k++) {
				p[k] += p[k - 1];
			}
			double u = Math.random() * p[K - 1];
			for (newTopic = 0; newTopic < p.length; newTopic++) {
				if (u < p[newTopic])
					break;
			}
			zd[n] = newTopic;
		}
		return null;
	}
	
	public void done(){
		try{
			nw[documents[m][n]][oldTopic]--;
			nd[m][oldTopic]--;
			nwsum[oldTopic]--;
			ndsum[m]--;
			
			nw[documents[m][n]][newTopic]++;
			nd[m][newTopic]++;
			nwsum[newTopic]++;
			ndsum[m]++;
		}catch(Exception e){
			System.out.println("exception"
					+ " documents[m][n]="+documents[m][n]
					+ " newTopic="+newTopic);
		}
	}
}
