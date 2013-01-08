package nop.im.stormalog.util;

import nop.im.stormalog.StormalogTopology;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;

public final class StormRunner {

    private static final int MILLIS_IN_SEC = 1000;

    private StormRunner() {
    }

    public static void runTopologyLocally(StormTopology topology, String topologyName, Config conf, int runtimeInSeconds)
            throws InterruptedException {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName, conf, topology);
        Thread.sleep((long) runtimeInSeconds * MILLIS_IN_SEC);
        cluster.killTopology(topologyName);
        cluster.shutdown();
    }
    
    public static void runStormalogLocally(int runtime) throws InterruptedException {
		Config conf = new Config();
		conf.setDebug(false);

		conf.setMaxTaskParallelism(3);
		runTopologyLocally(StormalogTopology.createTopology(), "stormalog",
				conf, 15);

    }
    
    public static void main(String[] args) {
    	try {
			runStormalogLocally(15);
		} catch (InterruptedException e) {
		}
    }
}
