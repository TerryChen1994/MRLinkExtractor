package com.mapred.MapRed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class MRLinkExtractorReducer extends Reducer<Text, Text, Text, Text> {
	// public void reduce(Text key, Iterable<Text> values, Context context)
	// throws IOException, InterruptedException {
	// long lastProgressTS = 0; // 上一次发心跳的时间点
	// long heartBeatInterval = 100000L; // 主动发心跳的间隔，100s，默认600s超时
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// StringBuilder sb = new StringBuilder();
	// for (Text value : values) {
	// sb.append(value);
	// sb.append("\t");
	//
	// if (System.currentTimeMillis() - lastProgressTS > heartBeatInterval) {
	// context.progress();
	// lastProgressTS = System.currentTimeMillis();
	// }
	// if(sb.length() > 10000000){
	// baos.write(sb.toString().getBytes());
	// sb.setLength(0);
	// }
	// }
	// baos.write(sb.toString().getBytes());
	// sb.setLength(0);
	//
	// Text outputValue = new Text(baos.toByteArray());
	// context.write(key, outputValue);
	// baos = null;
	// outputValue = null;
	// }
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		long lastProgressTS = 0; // 上一次发心跳的时间点
		long heartBeatInterval = 100000L; // 主动发心跳的间隔，100s，默认600s超时
		boolean firstKey = true;
		StringBuilder sb = new StringBuilder();
		for (Text value : values) {
			sb.append(value);
			sb.append("\t");
			if (sb.length() > 10000000) {
				context.write(firstKey ? key : null, new Text(sb.toString()));
				firstKey = false;
				sb.setLength(0);
			}
			if (System.currentTimeMillis() - lastProgressTS > heartBeatInterval) {
				context.progress();
				lastProgressTS = System.currentTimeMillis();
			}
		}
		context.write(firstKey ? key : null, new Text(sb.toString().trim()));
		sb.setLength(0);

	}
}
