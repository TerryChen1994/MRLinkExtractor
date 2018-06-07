package com.mapred.MapRed;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import entity.TextPair;

public class URLParserMapper extends Mapper<LongWritable, Text, TextPair, Text>{

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String sValue = value.toString().trim();
		context.write(new TextPair(sValue, "0"), new Text("exist"));
	}
	
}