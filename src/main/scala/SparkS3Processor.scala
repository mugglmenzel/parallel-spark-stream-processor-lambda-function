import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.s3.event.S3EventNotification
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions._

/**
  * Created by menzelmi on 30/09/16.
  */
class SparkS3Processor extends RequestHandler[S3EventNotification, Boolean] {


  override def handleRequest(input: S3EventNotification, context: Context): Boolean = {

    lazy val ss = SparkSession.builder().appName("SparkS3Processor").master("local[*]").config("spark.submit.deployMode", "client").getOrCreate()
    import ss.implicits._

    lazy val objects = input.getRecords.map(_.getS3).map(s3e => (s3e.getBucket, s3e.getObject))

    lazy val dfs = objects.map(o => ss.read.csv(s"s3a://${o._1}/${o._2}"))
    dfs.map(df => df.take(10)).foreach(println)
    dfs.foreach(df => df.write.parquet(s"${df.inputFiles.head.reverse.dropWhile(_ != '.').reverse}parquet"))
    dfs.foreach(df => df.write.json(s"${df.inputFiles.head.reverse.dropWhile(_ != '.').reverse}json"))

    true
  }


}

object SparkS3Processor extends App {

  lazy val s3event = S3EventNotification.parseJson(
    """
      |{
      |  "Records": [
      |    {
      |      "eventVersion": "2.0",
      |      "eventTime": "1970-01-01T00:00:00.000Z",
      |      "requestParameters": {
      |        "sourceIPAddress": "127.0.0.1"
      |      },
      |      "s3": {
      |        "configurationId": "testConfigRule",
      |        "object": {
      |          "eTag": "0123456789abcdef0123456789abcdef",
      |          "sequencer": "0A1B2C3D4E5F678901",
      |          "key": "SampleCSVFile_119kb.csv",
      |          "size": 1024
      |        },
      |        "bucket": {
      |          "arn": "arn:aws:s3:::parallel-spark-stream-input",
      |          "name": "parallel-spark-stream-input",
      |          "ownerIdentity": {
      |            "principalId": "EXAMPLE"
      |          }
      |        },
      |        "s3SchemaVersion": "1.0"
      |      },
      |      "responseElements": {
      |        "x-amz-id-2": "EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH",
      |        "x-amz-request-id": "EXAMPLE123456789"
      |      },
      |      "awsRegion": "eu-central-1",
      |      "eventName": "ObjectCreated:Put",
      |      "userIdentity": {
      |        "principalId": "EXAMPLE"
      |      },
      |      "eventSource": "aws:s3"
      |    }
      |  ]
      |}
    """.stripMargin)

  new SparkS3Processor().handleRequest(s3event, null)

}
