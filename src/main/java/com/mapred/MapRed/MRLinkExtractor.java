package com.mapred.MapRed;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import entity.TextPair;
import inputformat.RecordInputFormat;
import outputformat.AnchorURLOutputFormat;

public class MRLinkExtractor {
	public static class KeyPartitioner extends Partitioner<TextPair, Text> {

		@Override
		public int getPartition(TextPair key, Text value, int numPartitions) {
			// TODO Auto-generated method stub
			return (key.getFirst().hashCode() & Integer.MAX_VALUE) % numPartitions;
		}

	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.setBoolean("mapred.compress.map.output", true);
		conf.setClass("mapred.map.output.compression.codec", GzipCodec.class, CompressionCodec.class);
		conf.setDouble("mapred.job.shuffle.input.buffer.percent", 0.50);

		Job job = Job.getInstance(conf);
		job.setJarByClass(MRLinkExtractor.class);
		job.setJobName("InterServerInverseIndex from LinkData");

		job.setNumReduceTasks(100);
		
		
		String prePath = "/user/s1721710/LinkData/output";
		for (int i = 0; i < 20; i++) {
			String curS = String.valueOf(i);
			if (i < 10) {
				curS = "0" + curS;
			}
			
			MultipleInputs.addInputPath(job, new Path(prePath + curS), TextInputFormat.class, LinkDataParserMapper.class);
		}
		
		
		String urlPrePath = "/user/s1721710/UrlIdPagerankIndex/output";
		for (int i = 0; i < 20; i++) {
			String curS = String.valueOf(i);
			if(i < 10){
				curS = "0" + curS;
			}
			MultipleInputs.addInputPath(job, new Path(urlPrePath + curS), TextInputFormat.class, URLParserMapper.class);
		}

		FileOutputFormat.setOutputPath(job, new Path("/user/s1721710/InterServerInverseIndex"));
		FileOutputFormat.setCompressOutput(job, true); // job使用压缩
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

		job.setPartitionerClass(KeyPartitioner.class);
		job.setGroupingComparatorClass(TextPair.FirstComparator.class);

		job.setMapOutputKeyClass(TextPair.class);
		job.setReducerClass(MRLinkExtractorReducer.class);
		job.setOutputFormatClass(AnchorURLOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
