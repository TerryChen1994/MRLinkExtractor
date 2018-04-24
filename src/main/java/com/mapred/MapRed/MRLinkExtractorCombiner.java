package com.mapred.MapRed;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MRLinkExtractorCombiner extends Reducer<Text, Text, Text, Text>{
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
		long lastProgressTS = 0; // 上一次发心跳的时间点
		long heartBeatInterval = 100000L; // 主动发心跳的间隔，100s，默认600s超时
		StringBuilder outputString = new StringBuilder();
		
		for (Text value : values) {
			outputString.append(value.toString());
			outputString.append("\t");
			
			if (System.currentTimeMillis() - lastProgressTS > heartBeatInterval) {
				context.progress();
				lastProgressTS = System.currentTimeMillis();
			}
			if (outputString.length() > 10000000) {
				context.write(key, new Text(outputString.toString().trim()));
				outputString.setLength(0);
			}
		}
		Text outputValue = new Text(outputString.toString().trim());
		context.write(key, outputValue);
		outputString.setLength(0);
	}
}
