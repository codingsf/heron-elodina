licenses(["notice"])

package(default_visibility = ["//visibility:public"])

java_binary(
    name = "kafka-09-mirror",
    srcs = glob(["com/twitter/heron/KafkaMirrorTopology.java"]),
    main_class = "com.twitter.heron.KafkaMirrorTopology",
    deps = [
        "//heron/api/src/java:api-java-nl",
        "//contrib/kafka9/heron-kafka/src/java:heron-kafka-09-java",
    ],
)

java_binary(
    name = "kafka-08-mirror",
    srcs = glob(["com/twitter/heron/KafkaOldMirrorTopology.java"]),
    main_class = "com.twitter.heron.KafkaOldMirrorTopology",
    deps = [
        "//heron/api/src/java:api-java-nl",
        "//contrib/kafka9/heron-kafka/src/java:heron-kafka-08-java",
    ],
)

java_binary(
    name = "streaming-library-demo",
    srcs = glob(["com/twitter/heron/StreamingLibraryDemo.java", "com/twitter/heron/StreamingLibraryBolt.java"]),
    main_class = "com.twitter.heron.StreamingLibraryDemo",
    deps = [
        "//3rdparty/streaming-library:streaming-library-java",
        "//heron/api/src/java:api-java-nl",
        "//contrib/kafka9/heron-kafka/src/java:heron-kafka-08-java",
        "//3rdparty/logging:slf4j-api-java"
    ],
)


