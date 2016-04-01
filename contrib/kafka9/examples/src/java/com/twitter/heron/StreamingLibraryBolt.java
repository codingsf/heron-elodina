package com.twitter.heron;

import com.twitter.heron.api.bolt.BaseRichBolt;
import com.twitter.heron.api.bolt.OutputCollector;
import com.twitter.heron.api.topology.OutputFieldsDeclarer;
import com.twitter.heron.api.topology.TopologyContext;
import com.twitter.heron.api.tuple.Tuple;
import com.twitter.heron.bolts.kafka.mapper.KafkaMirrorMapper;
import com.twitter.heron.bolts.kafka.mapper.TupleToKafkaMapper;
import net.elodina.streaming.Client;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;

public class StreamingLibraryBolt extends BaseRichBolt {

    //public static final Logger LOG = LoggerFactory.getLogger(StreamingLibraryBolt.class);

    private OutputCollector collector;

    private Client<String, String> slClient;

    private static boolean isTick(Tuple tuple) {
        return tuple != null && "__system".equals(tuple.getSourceComponent()) && "__tick".equals(tuple.getSourceStreamId());
    }

    private TupleToKafkaMapper<byte[], byte[]> mapper = new KafkaMirrorMapper<>();

    private String discoveryUrl;

    private String user;

    private String token;

    public StreamingLibraryBolt(String discoveryUrl,
                                String user,
                                String token) {
        this.discoveryUrl = discoveryUrl;
        this.user = user;
        this.token = token;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        //creating client
        slClient = new Client<>(discoveryUrl, user,
                token, new StringSerializer(), new StringDeserializer(),
                new StringSerializer(), new StringDeserializer());
    }

    @Override
    public void execute(final Tuple input) {
        //LOG.debug("Got new tuple input, processing..");
        if (isTick(input)) {
            collector.ack(input);
            return; // Do not try to send ticks to Kafka
        }
        byte[] key;
        byte[] message;
        try {
            key = mapper.getKeyFromTuple(input);
            message = mapper.getMessageFromTuple(input);
            String payload = slClient.receive(key, message);
            slClient.respond(key, payload + "_processed");
            collector.ack(input);
        } catch (Exception ex) {
            //LOG.warn("An unexpected error occurred: " + ex);
            collector.reportError(ex);
            collector.fail(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    }

    @Override
    public void cleanup() {
    }
}
