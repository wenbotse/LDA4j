package com.hankcs.lda;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.travel.utils.CloneUtils;

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
	int[] nd;
	int[] nwsum;
	int[] ndsum;
	int[][] documents;
	int z[];
	int K;
	double beta;
	double alpha;
	int V;
	int[] zd;
	public void init(int m, int[][] nw, int[][] nd, int[] nwsum, int[] ndsum,
			int[][] documents, int z[][]) {
		log.debug("init gibbs task");
		this.nw = new int[nw.length][nw[0].length];
		this.nd = (int[])CloneUtils.deepClone(nd[m]);
		this.nwsum = new int[nwsum.length];
		this.ndsum = (int[])CloneUtils.deepClone(ndsum);
		this.documents = documents;
		this.z = (int[])CloneUtils.deepClone(z[m]);
	}
	
	public Void call() {
		log.debug("run document no."+m);
		for (int n = 0; n < z.length; n++) {
			
			int topic = z[n];
			nw[documents[m][n]][topic]--;
			nd[topic]--;
			nwsum[topic]--;
			ndsum[m]--;
			
			double[] p = new double[K];
			for (int k = 0; k < K; k++) {
				if(k != oldTopic){
					p[k] = (nw[documents[m][n]][k] + beta) / (nwsum[k] + V * beta)
							* (nd[k] + alpha) / (ndsum[m] + K * alpha);
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
			
			nw[documents[m][n]][topic]++;
			nd[topic]++;
			nwsum[topic]++;
			ndsum[m]++;
			
			z[n]=topic;
		}
		return null;
	}
	
	public void done(int[][] nw, int[][] nd, int[] nwsum, int[] ndsum,
			int[][] documents, int z[][]){
		try{
			
			z[m] = this.z;
			
			nd[m] = this.nd;
			for(int topic=0;topic<nwsum.length;topic++){
				nwsum[topic]+=this.nwsum[topic];
			}
			for(int n=0;n < z[m].length;n++){
				for(int topic=0;topic<nwsum.length;topic++){
					nw[documents[m][n]][topic]+=this.nw[documents[m][n]][topic];
				}
			}
			ndsum[m] = this.ndsum[m];
		}catch(Exception e){
			System.out.println("exception"
					+ " documents[m][n]="+documents[m][n]
					+ " newTopic="+newTopic);
		}
	}
}
