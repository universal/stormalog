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

		builder.setBolt("actionfilter", new ErrorFilterBolt(false), 1)
				.fieldsGrouping("spout", new Fields("request"));
		builder.setBolt("actioncount", new ActionCounterBolt(), 2)
				.shuffleGrouping("actionfilter");

		builder.setBolt("errorfilter", new ErrorFilterBolt(true), 1)
				.fieldsGrouping("spout", new Fields("request"));
		builder.setBolt("errorhandler", new ErrorHandlerBolt(), 2)
				.shuffleGrouping("errorfilter");

		return builder.createTopology();
	}
}
