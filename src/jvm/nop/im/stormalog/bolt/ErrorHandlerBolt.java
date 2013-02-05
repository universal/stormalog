package nop.im.stormalog.bolt;

import java.util.Map;

import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;

public class ErrorHandlerBolt extends ShellBolt implements IRichBolt{
    public ErrorHandlerBolt() {
        super("/Users/jhedtrich/.rvm/rubies/ruby-1.9.3-p327/bin/ruby", "-EUTF-8", "errorhandler.rb");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}

