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
		String[] sList = value.toString().split("\t");
		//sList[0] = 目标URL
		//sList[1] = 目标URL的WarcRecordID
		//sList[2] = 目标URL的pagerank值
		context.write(new TextPair(sList[0], "0"), new Text("exist" + " " + sList[1] + " " + sList[2]));
		
	}
	
}