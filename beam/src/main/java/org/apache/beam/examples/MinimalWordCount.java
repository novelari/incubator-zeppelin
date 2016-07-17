/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.examples;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.*;
import org.apache.spark.SparkContext;
import org.apache.beam.runners.direct.*;
import org.apache.beam.sdk.runners.*;
import org.apache.beam.sdk.options.*;
import org.apache.beam.runners.spark.*;
import org.apache.beam.runners.spark.io.ConsoleIO;
import org.apache.beam.runners.flink.*;
import org.apache.beam.runners.flink.examples.WordCount.Options;
import org.apache.beam.runners.dataflow.*;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;

/**
 * An example that counts words in Shakespeare.
 *
 * <p>
 * This class, {@link MinimalWordCount}, is the first in a series of four successively more detailed
 * 'word count' examples. Here, for simplicity, we don't show any error-checking or argument
 * processing, and focus on construction of the pipeline, which chains together the application of
 * core transforms.
 *
 * <p>
 * Next, see the {@link WordCount} pipeline, then the {@link DebuggingWordCount}, and finally the
 * {@link WindowedWordCount} pipeline, for more detailed examples that introduce additional
 * concepts.
 *
 * <p>
 * Concepts:
 * 
 * <pre>
 *   1. Reading data from text files
 *   2. Specifying 'inline' transforms
 *   3. Counting a PCollection
 *   4. Writing data to Cloud Storage as text files
 * </pre>
 *
 * <p>
 * To execute this pipeline, first edit the code to set your project ID, the temp location, and the
 * output location. The specified GCS bucket(s) must already exist.
 *
 * <p>
 * Then, run the pipeline as described in the README. It will be deployed and run with the selected
 * runner. No args are required to run the pipeline. You can see the results in your output bucket
 * in the GCS browser.
 */

/**
 * 
 * @author admin
 *
 */
public class MinimalWordCount {

  public static void main(String[] args) {
    // Create a PipelineOptions object. This object lets us set various execution
    // options for our pipeline, such as the associated Cloud Platform project and the location
    // in Google Cloud Storage to stage files.

    // DataflowPipelineOptions options =
    // PipelineOptionsFactory.create().as(DataflowPipelineOptions.class);
    // options.setRunner(DataflowPipelineRunner.class);
    //

    // DirectPipelineOptions options =
    // PipelineOptionsFactory.create().as(DirectPipelineOptions.class);
    // options.setRunner(DirectPipelineRunner.class);
    // options.setStreaming(true);

    // SparkPipelineOptions options =
    // PipelineOptionsFactory.create().as(SparkPipelineOptions.class);
    // options.setRunner(SparkPipelineRunner.class);

    Options options = PipelineOptionsFactory.create().as(Options.class);
    // List<String> jars = new ArrayList<>();
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/"
    // + "beam-runners-flink_2.10-0.2.0-incubating-SNAPSHOT.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/"
    // + "beam-sdks-java-core-0.2.0-incubating-SNAPSHOT.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-api-client-json-1.2.3-alpha.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-api-client-util-1.2.3-alpha.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-api-client-auth-1.2.3-alpha.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-oauth-client-1.22.0.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-http-client-1.22.0.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-api-client-1.22.0.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "google-cloud-dataflow-java-sdk-all-1.6.0.jar");
    // jars.add("/home/admin/mahmoud/work/bigdata/livy-zeppelin/"
    // + "incubator-zeppelin/interpreter/beam/" + "joda-time-2.4.jar");
    // options.setFilesToStage(jars);
    // options.setFlinkMaster("127.0.1.1:49459");

    // options.setJobName("flink-yarn");
    // options.setParallelism(-1);
    // options.setAuthorizationServerEncodedUrl("akka.tcp://flink@127.0.1.1:33239");
    // options.setStreaming(true);
    options.setRunner(FlinkPipelineRunner.class);

    // options.sets
    // CHANGE 1/3: Your project ID is required in order to run your pipeline on the Google Cloud.
    // options.setProject("SET_YOUR_PROJECT_ID_HERE");
    // CHANGE 2/3: Your Google Cloud Storage path is required for staging local files.
    // options.setTempLocation("gs://SET_YOUR_BUCKET_NAME_HERE/AND_TEMP_DIRECTORY");

    // Create the Pipeline object with the options we defined above.
    Pipeline p = Pipeline.create(options);

    // Apply the pipeline's transforms.

    // Concept #1: Apply a root transform to the pipeline; in this case, TextIO.Read to read a set
    // of input text files. TextIO.Read returns a PCollection where each element is one line from
    // the input text (a set of Shakespeare's texts).
    p.apply(TextIO.Read.from("/home/admin/mahmoud/work/bigdata/beam/shakespeare/input/file1.txt"))
    // Concept #2: Apply a ParDo transform to our PCollection of text lines. This ParDo invokes a
    // DoFn (defined in-line) on each element that tokenizes the text line into individual words.
    // The ParDo returns a PCollection<String>, where each element is an individual word in
    // Shakespeare's collected texts.
        .apply(ParDo.named("ExtractWords").of(new DoFn<String, String>() {
          @Override
          public void processElement(ProcessContext c) {

            for (String word : c.element().split("[^a-zA-Z']+")) {
              if (!word.isEmpty()) {
                c.output(word);
              }
            }
          }
        }))
        // Concept #3: Apply the Count transform to our PCollection of individual words. The Count
        // transform returns a new PCollection of key/value pairs, where each key represents a
        // unique
        // word in the text. The associated value is the occurrence count for that word.
        .apply(Count.<String> perElement())
        // Apply a MapElements transform that formats our PCollection of word counts into a
        // printable
        // string, suitable for writing to an output file.
        .apply("FormatResults", ParDo.of(new DoFn<KV<String, Long>, String>() {
          //
          // @Override
          // public String apply(KV<String, Long> input) {
          // System.out.println(input.getKey() + ": " + input.getValue());
          // return input.getKey() + ": " + input.getValue();
          // }

          @Override
          public void processElement(DoFn<KV<String, Long>, String>.ProcessContext arg0)
              throws Exception {
            System.out.println(arg0.element().getKey() + ": " + arg0.element().getValue());

          }
        }))
        // Concept #4: Apply a write transform, TextIO.Write, at the end of the pipeline.
        // TextIO.Write writes the contents of a PCollection (in this case, our PCollection of
        // formatted strings) to a series of text files in Google Cloud Storage.
        // CHANGE 3/3: The Google Cloud Storage path is required for outputting the results to.
        .apply(TextIO.Write.to("/home/admin/mahmoud/work/bigdata/beam/shakespeare/output/flink"));
    // .apply(ConsoleIO)
    // Run the pipeline.
    p.run();

    // EvaluationResult result = SparkPipelineRunner.create(options).run(p);
  }
}
