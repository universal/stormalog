package nop.im.stormalog.bolt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class ActionCounterBolt extends BaseRichBolt {
	OutputCollector _collector;
    Map<String, Integer> _counts = new HashMap<String, Integer>();
    Pattern _proccessing;
    Jedis _jedis;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector = collector;
		_proccessing = Pattern.compile(".*Processing by ([:\\w]+\\#\\w+).*", Pattern.DOTALL);
		_jedis = new Jedis("localhost");
		_jedis.del("actioncount");
}

	@Override
	public void execute(Tuple input) {
        String request= input.getString(0);
        Matcher matcher = _proccessing.matcher(request);
        String action = "notmatched";
        if(matcher.matches()) {
        	 action = matcher.group(1);
        } else {
        	System.err.println(request);
        }
       	_jedis.hincrBy("actioncount", action, 1);
       	_collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
}
