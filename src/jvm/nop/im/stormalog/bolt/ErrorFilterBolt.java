package nop.im.stormalog.bolt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class ErrorFilterBolt extends BaseRichBolt {
	private boolean _emitErrors;
	private OutputCollector _collector;


	public ErrorFilterBolt(boolean emitErrors) {
		super();
		this._emitErrors = emitErrors;
	}
	
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
		
		if((_emitErrors && error) || (!_emitErrors && !error)) {
//			_collector.emit(new Values(request));
			Set<Tuple> anchors = new HashSet<Tuple>();
			anchors.add(input);
			_collector.emit(anchors, new Values(request));
		}
      	_collector.ack(input);
 	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("request"));		
	}	
}
