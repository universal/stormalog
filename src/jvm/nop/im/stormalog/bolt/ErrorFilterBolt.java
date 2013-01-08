package nop.im.stormalog.bolt;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class ErrorFilterBolt extends BaseRichBolt {
	private OutputCollector _collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		String request = input.getString(0);
		boolean error = false;
		if(request.indexOf("Completed 500") >= 0 || request.indexOf("Completed") == -1) {
			error = true;
		} 
		
		if(error) {
			_collector.emit("error_requests", input, new Values(request));
		} else {
			_collector.emit("successful_requests", input, new Values(request));
		}
      	_collector.ack(input);
 	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("error_requests", new Fields("request"));
		declarer.declareStream("successful_requests", new Fields("request"));
	}	
}
