package kafkastreams.javaexamples;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Produced;

public class HelloKafkaStreams extends KafkaStreamsApp {

    public static void main(String[] args) {
        new HelloKafkaStreams().start("hello-world-app");
    }

    public Topology createTopology(StreamsBuilder builder) {
        Serde<String> strings = new Serdes.StringSerde();

        builder.stream("names", Consumed.with(strings, strings))
                .mapValues(name -> "Hello, " + name + "!")
                .to("hello", Produced.with(strings, strings));

        return builder.build();
    }
}
