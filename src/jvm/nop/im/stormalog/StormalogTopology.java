package nop.im.stormalog;

import nop.im.stormalog.bolt.ActionCounterBolt;
import nop.im.stormalog.bolt.ErrorFilterBolt;
import nop.im.stormalog.bolt.ErrorHandlerBolt;
import nop.im.stormalog.spout.RailsLogSpout;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class StormalogTopology {

	
	public static StormTopology createTopology()  {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout", new RailsLogSpout(), 1);
		builder.setBolt("requestfilter", new ErrorFilterBolt(), 1).shuffleGrouping("spout");
		
		builder.setBolt("actioncount", new ActionCounterBolt(), 2).shuffleGrouping("requestfilter", "successful_requests");
		builder.setBolt("errorhandler", new ErrorHandlerBolt(), 2).shuffleGrouping("requestfilter", "error_requests");

		return builder.createTopology();
	}
}
