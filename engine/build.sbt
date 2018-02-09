import sbtassembly.AssemblyPlugin.autoImport._

name := "ingestengine" 
organization := "vegas@b2wdigital.com"
version := "0.7"
scalaVersion := "2.10.6"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  Resolver.bintrayRepo("hseeberger", "maven"))

assemblyMergeStrategy in assembly := {

    case "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat"  => MergeStrategy.first
    case "kryo*" => MergeStrategy.discard
    case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.first
    case PathList("org", "apache", xs @ _*) => MergeStrategy.first
    case PathList("com", "google", xs @ _*) => MergeStrategy.first
    case "overview.html" => MergeStrategy.first
    case "parquet.thrift" => MergeStrategy.first
    case "plugin.xml" => MergeStrategy.first
    case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

libraryDependencies ++= Seq(
    "org.apache.spark"   %% "spark-core"                  % "1.6.0" % "provided",
    "org.clapper"        %  "grizzled-slf4j_2.10"         % "1.3.1",
    "org.apache.spark"   %% "spark-hive"                  % "1.6.0" % "provided",
    "org.apache.spark"   %  "spark-streaming-kafka_2.10"  % "1.6.0",
    "org.apache.kafka"   %  "kafka_2.10"                  % "0.8.2.0",
    "org.apache.kafka"   %  "kafka-clients"               % "0.8.2.0",
    "com.typesafe.play"  %  "play-json_2.10"              % "2.4.0",
    "org.apache.spark"   %  "spark-streaming_2.10"        % "1.6.0" % "provided",
    "org.apache.spark"   %% "spark-sql"                   % "1.6.0" % "provided",
    "com.amazonaws"      %  "aws-java-sdk"                % "1.10.22",
    "org.scalamock"      %% "scalamock-scalatest-support" % "3.2.2" % "test",
    "org.scalamock"      %% "scalamock-core"              % "3.2.2" % "test",
    "net.manub"          %% "scalatest-embedded-kafka"    % "0.5.0-kafka08" % "test",
    "com.holdenkarau"    %% "spark-testing-base"          % "1.6.0_0.4.7" % "test"
)

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4" force()
//dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.6" force()
//dependencyOverrides += "org.apache.httpcomponents" % "httpclient" % "4.5.2" force()

parallelExecution in Test := false
