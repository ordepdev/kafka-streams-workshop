package kafkastreams.javaexamples;

import com.fasterxml.jackson.databind.JsonNode;
import kafkastreams.serdes.JsonNodeSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

public class BranchExample extends KafkaStreamsApp {

    public static void main(String[] args) {
        new BranchExample().start("branch-app");
    }

    public Topology createTopology(StreamsBuilder builder) {
        Serde<String> strings = new Serdes.StringSerde();
        JsonNodeSerde json = new JsonNodeSerde();

        KStream<String, JsonNode> articles = builder.stream("Articles", Consumed.with(strings, json));

        KStream<String, JsonNode>[] articlesPerSite = articles.branch(
                (key, value) -> "bbc".equals(value.path("site").asText()),
                (key, value) -> "cnn".equals(value.path("site").asText()),
                (key, value) -> "foxnews".equals(value.path("site").asText()),
                (key, value) -> true // catch remaining events
        );

        articlesPerSite[0].to("BBC-Articles", Produced.with(strings, json));
        articlesPerSite[1].to("CNN-Articles", Produced.with(strings, json));
        articlesPerSite[2].to("FoxNews-Articles", Produced.with(strings, json));
        articlesPerSite[3].to("Other-Articles", Produced.with(strings, json));

        return builder.build();
    }
}
