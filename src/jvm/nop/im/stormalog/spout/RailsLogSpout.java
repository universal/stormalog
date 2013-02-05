package nop.im.stormalog.spout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class RailsLogSpout extends BaseRichSpout {

	SpoutOutputCollector _collector;
	List<String> _requests;
	int _currentEmitted;
	int _totalEmitted;
	int _success;

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		_collector = collector;
		_requests = new ArrayList<String>();
		readRequests();
		_currentEmitted = 0;
		_totalEmitted = 0;
	}

	@Override
	public void nextTuple() {
		String request = _requests.get(_currentEmitted);
		_currentEmitted = (_currentEmitted + 1) % _requests.size();
		_totalEmitted++;
		_collector.emit(new Values(request), "" + _totalEmitted);

		if (_totalEmitted - _success > 25) {
			Utils.sleep(1);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("request"));
	}

	@Override
	public void ack(Object id) {
		_success++;
	}

	@Override
	public void fail(Object id) {
	}

	private void readRequests() {
		try {
			String[] requestLines = FileUtils.readFileToString(
					new File("production.log")).split("\\n");
			StringBuilder request = new StringBuilder();
			boolean first = false;
			for (String line : requestLines) {
				if (line.matches("^Started .*")) {
					if (first) {
						_requests.add(request.toString());
					}
					request = new StringBuilder(line);
					first = true;
				} else {
					request.append(line);
				}
				request.append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		System.out.println(_success + " -- " + _totalEmitted);
		super.close();
	}

}
