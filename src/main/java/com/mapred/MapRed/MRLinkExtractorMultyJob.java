package com.mapred.MapRed;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import inputformat.RecordInputFormat;
import outputformat.AnchorURLOutputFormat;

public class MRLinkExtractorMultyJob {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("mapred.reduce.child.java.opts", "-Xmx1024m");
		conf.setBoolean("mapred.compress.map.output", true);
		conf.setClass("mapred.map.output.compression.codec", GzipCodec.class, CompressionCodec.class);
		conf.setDouble("mapred.job.shuffle.input.buffer.percent", 0.50);
		
		Job job15 = iniJob15(conf);
		Job job16 = iniJob16(conf);
		Job job17 = iniJob17(conf);
		Job job18 = iniJob18(conf);
		Job job19 = iniJob19(conf);
		
		ControlledJob cjob15 = new ControlledJob(conf);
		ControlledJob cjob16 = new ControlledJob(conf);
		ControlledJob cjob17 = new ControlledJob(conf);
		ControlledJob cjob18 = new ControlledJob(conf);
		ControlledJob cjob19 = new ControlledJob(conf);
		
        cjob15.setJob(job15);
        cjob16.setJob(job16); 
        cjob17.setJob(job17); 
        cjob18.setJob(job18); 
        cjob19.setJob(job19);
        
        cjob16.addDependingJob(cjob15);
        cjob17.addDependingJob(cjob16);
        cjob18.addDependingJob(cjob17);
        cjob19.addDependingJob(cjob18);
        
        JobControl jc = new JobControl("MRLinkExtracotr 15 to 19");  
        jc.addJob(cjob15);
        jc.addJob(cjob16);
        jc.addJob(cjob17);
        jc.addJob(cjob18);
        jc.addJob(cjob19);
        
        Thread jcThread = new Thread(jc);
        jcThread.start();    
        while(true){    
            if(jc.allFinished()){
                System.out.println(jc.getSuccessfulJobList());    
                jc.stop(); 
                System.exit(0);
            }    
            if(jc.getFailedJobList().size() > 0){
                System.out.println(jc.getFailedJobList());    
                jc.stop(); 
                System.exit(1);
            }    
        }

	}

	public static Job iniJob15(Configuration conf) throws Exception{
		Job job15 = Job.getInstance(conf);
		job15.setJarByClass(MRLinkExtractorMultyJob.class);
		job15.setJobName("MRLinkExtractor15");

		job15.setNumReduceTasks(100);

		String prePath = "/user/terrier/ClueWeb12/Corpus/Disk4/ClueWeb12_15/15";
		String allPath = new String();
		for (int i = 0; i < 17; i++) {
			String curS = String.valueOf(i);
			if (i < 10) {
				curS = "0" + curS;
			}
			String curPath = prePath + curS + "wb";
			if (i != 16) {
				allPath = allPath + curPath + ",";
			} else {
				allPath = allPath + curPath;
			}
		}
		FileInputFormat.addInputPaths(job15, allPath);
		FileOutputFormat.setOutputPath(job15, new Path("/user/s1721710/output/output15"));
		FileOutputFormat.setCompressOutput(job15, true); // job使用压缩
		FileOutputFormat.setOutputCompressorClass(job15, GzipCodec.class);

		job15.setOutputFormatClass(AnchorURLOutputFormat.class);
		job15.setMapperClass(MRLinkExtractorMapper.class);
		job15.setInputFormatClass(RecordInputFormat.class);

		job15.setCombinerClass(MRLinkExtractorCombiner.class);

		job15.setReducerClass(MRLinkExtractorReducer.class);
		job15.setOutputKeyClass(Text.class);
		job15.setOutputValueClass(Text.class);
		
		return job15;
	}

	public static Job iniJob16(Configuration conf) throws Exception{
		Job job16 = Job.getInstance(conf);
		job16.setJarByClass(MRLinkExtractorMultyJob.class);
		job16.setJobName("MRLinkExtractor16");

		job16.setNumReduceTasks(100);

		String prePath = "/user/terrier/ClueWeb12/Corpus/Disk4/ClueWeb12_16/16";
		String allPath = new String();
		for (int i = 0; i < 18; i++) {
			String curS = String.valueOf(i);
			if (i < 10) {
				curS = "0" + curS;
			}
			String curPath = prePath + curS + "wb";
			if (i != 17) {
				allPath = allPath + curPath + ",";
			} else {
				allPath = allPath + curPath;
			}
		}
		FileInputFormat.addInputPaths(job16, allPath);
		FileOutputFormat.setOutputPath(job16, new Path("/user/s1721710/output/output16"));
		FileOutputFormat.setCompressOutput(job16, true); // job使用压缩
		FileOutputFormat.setOutputCompressorClass(job16, GzipCodec.class);

		job16.setOutputFormatClass(AnchorURLOutputFormat.class);
		job16.setMapperClass(MRLinkExtractorMapper.class);
		job16.setInputFormatClass(RecordInputFormat.class);

		job16.setCombinerClass(MRLinkExtractorCombiner.class);

		job16.setReducerClass(MRLinkExtractorReducer.class);
		job16.setOutputKeyClass(Text.class);
		job16.setOutputValueClass(Text.class);
		
		return job16;
	}

	public static Job iniJob17(Configuration conf) throws Exception{
		Job job17 = Job.getInstance(conf);
		job17.setJarByClass(MRLinkExtractorMultyJob.class);
		job17.setJobName("MRLinkExtractor17");

		job17.setNumReduceTasks(100);

		String prePath = "/user/terrier/ClueWeb12/Corpus/Disk4/ClueWeb12_17/17";
		String allPath = new String();
		for (int i = 0; i < 18; i++) {
			String curS = String.valueOf(i);
			if (i < 10) {
				curS = "0" + curS;
			}
			String curPath = prePath + curS + "wb";
			if (i != 17) {
				allPath = allPath + curPath + ",";
			} else {
				allPath = allPath + curPath;
			}
		}
		FileInputFormat.addInputPaths(job17, allPath);
		FileOutputFormat.setOutputPath(job17, new Path("/user/s1721710/output/output17"));
		FileOutputFormat.setCompressOutput(job17, true); // job使用压缩
		FileOutputFormat.setOutputCompressorClass(job17, GzipCodec.class);

		job17.setOutputFormatClass(AnchorURLOutputFormat.class);
		job17.setMapperClass(MRLinkExtractorMapper.class);
		job17.setInputFormatClass(RecordInputFormat.class);

		job17.setCombinerClass(MRLinkExtractorCombiner.class);

		job17.setReducerClass(MRLinkExtractorReducer.class);
		job17.setOutputKeyClass(Text.class);
		job17.setOutputValueClass(Text.class);
		
		return job17;
	}

	public static Job iniJob18(Configuration conf) throws Exception{
		Job job18 = Job.getInstance(conf);
		job18.setJarByClass(MRLinkExtractorMultyJob.class);
		job18.setJobName("MRLinkExtractor18");

		job18.setNumReduceTasks(100);

		String prePath = "/user/terrier/ClueWeb12/Corpus/Disk4/ClueWeb12_18/18";
		String allPath = new String();
		for (int i = 0; i < 16; i++) {
			String curS = String.valueOf(i);
			if (i < 10) {
				curS = "0" + curS;
			}
			String curPath = prePath + curS + "wb";
			if (i != 15) {
				allPath = allPath + curPath + ",";
			} else {
				allPath = allPath + curPath;
			}
		}
		FileInputFormat.addInputPaths(job18, allPath);
		FileOutputFormat.setOutputPath(job18, new Path("/user/s1721710/output/output18"));
		FileOutputFormat.setCompressOutput(job18, true); // job使用压缩
		FileOutputFormat.setOutputCompressorClass(job18, GzipCodec.class);

		job18.setOutputFormatClass(AnchorURLOutputFormat.class);
		job18.setMapperClass(MRLinkExtractorMapper.class);
		job18.setInputFormatClass(RecordInputFormat.class);

		job18.setCombinerClass(MRLinkExtractorCombiner.class);

		job18.setReducerClass(MRLinkExtractorReducer.class);
		job18.setOutputKeyClass(Text.class);
		job18.setOutputValueClass(Text.class);
		
		return job18;
	}
	
	public static Job iniJob19(Configuration conf) throws Exception{
		Job job19 = Job.getInstance(conf);
		job19.setJarByClass(MRLinkExtractorMultyJob.class);
		job19.setJobName("MRLinkExtractor19");

		job19.setNumReduceTasks(100);

		String prePath = "/user/terrier/ClueWeb12/Corpus/Disk4/ClueWeb12_19/19";
		String allPath = new String();
		for (int i = 0; i < 15; i++) {
			String curS = String.valueOf(i);
			if (i < 10) {
				curS = "0" + curS;
			}
			String curPath = prePath + curS + "wb";
			if (i != 14) {
				allPath = allPath + curPath + ",";
			} else {
				allPath = allPath + curPath;
			}
		}
		FileInputFormat.addInputPaths(job19, allPath);
		FileOutputFormat.setOutputPath(job19, new Path("/user/s1721710/output/output19"));
		FileOutputFormat.setCompressOutput(job19, true); // job使用压缩
		FileOutputFormat.setOutputCompressorClass(job19, GzipCodec.class);
		
		job19.setOutputFormatClass(AnchorURLOutputFormat.class);
		job19.setMapperClass(MRLinkExtractorMapper.class);
		job19.setInputFormatClass(RecordInputFormat.class);

		job19.setCombinerClass(MRLinkExtractorCombiner.class);

		job19.setReducerClass(MRLinkExtractorReducer.class);
		job19.setOutputKeyClass(Text.class);
		job19.setOutputValueClass(Text.class);
		
		return job19;
	}
	
}
