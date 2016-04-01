package com.twitter.heron;

import com.twitter.heron.api.Config;
import com.twitter.heron.api.HeronSubmitter;
import com.twitter.heron.api.topology.TopologyBuilder;
import com.twitter.heron.spouts.kafka.common.ByteArrayKeyValueScheme;
import com.twitter.heron.spouts.kafka.common.KeyValueSchemeAsMultiScheme;
import com.twitter.heron.spouts.kafka.old.KafkaSpout;
import com.twitter.heron.spouts.kafka.old.SpoutConfig;

public class StreamingLibraryDemo {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            throw new RuntimeException("Would need at least one argument:\n 0 - Topology name");
        }

        String consumerTopic = "elodina_t1";
        String zkURL = "zookeeper.service:2181";
        String zkRoot = "/brokers";
        String discoveryUrl = "http://monarch.service.cluster2:31500/metadata";
        String user = "elodina";
        String token = "5fe31334-b9dc-4382-ac09-cdb5ef3c2972";
        if (args.length > 1) {
            consumerTopic = args[1];
        }
        if (args.length > 2) {
            zkURL = args[2];
        }
        if (args.length > 3) {
            discoveryUrl = args[3];
        }
        if (args.length > 4) {
            user = args[4];
        }
        if (args.length > 5) {
            token = args[5];
        }
        if (args.length > 6) {
            zkRoot = args[6] + zkRoot;
        }
        TopologyBuilder builder = new TopologyBuilder();
        SpoutConfig config = new SpoutConfig(new SpoutConfig.ZkHosts(zkURL, zkRoot), consumerTopic, null, "spoutId");
        config.scheme = new KeyValueSchemeAsMultiScheme(new ByteArrayKeyValueScheme());
        config.bufferSizeBytes = 100; // Don't need buffer to be too big for showcase purposes
        builder.setSpout("spout", new KafkaSpout(config), 1);
        builder.setBolt("bolt",
                new StreamingLibraryBolt(discoveryUrl, user, token),
                1).shuffleGrouping("spout");

        Config conf = new Config();
        conf.setDebug(true);
        conf.setComponentRam("spout", 1024L * 1024 * 256);
        conf.setComponentRam("bolt", 1024L * 1024 * 256);

        conf.setNumStmgrs(1);
        conf.setContainerCpuRequested(0.2f);
        conf.setContainerRamRequested(1024L * 1024 * 512);

        HeronSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
}
