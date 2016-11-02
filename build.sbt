name := "parallel-spark-stream-processor-lambda-function"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.11.43"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.1" excludeAll(
  ExclusionRule(organization = "io.dropwizard.metrics"),
  ExclusionRule(organization = "net.sf.py4j"),
  ExclusionRule(organization = "org.apache.mesos"),
  ExclusionRule(organization = "org.glassfish.*"),
  ExclusionRule(organization = "org.glassfish.hk2"),
  ExclusionRule(organization = "org.glassfish.hk2.external"),
  ExclusionRule(organization = "org.glassfish.jersey.bundles"),
  ExclusionRule(organization = "org.glassfish.jersey.containers"),
  ExclusionRule(organization = "org.glassfish.jersey.core"),
  ExclusionRule(organization = "org.glassfish.jersey.media"),
  ExclusionRule(organization = "org.apache.hadoop")
  )
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.2.0"

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case m if m.toLowerCase.endsWith("about.html") => MergeStrategy.discard
  case PathList("org", "apache", xs @ _*) => MergeStrategy.first
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

test in assembly := {}