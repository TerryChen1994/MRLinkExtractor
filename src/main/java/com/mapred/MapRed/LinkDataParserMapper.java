package com.mapred.MapRed;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import entity.TextPair;

public class LinkDataParserMapper extends Mapper<LongWritable, Text, TextPair, Text> {

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		long lastProgressTS = 0; // 上一次发心跳的时间点
		long heartBeatInterval = 100000L; // 主动发心跳的间隔，100s，默认600s超时
		String[] sList = value.toString().split("\t");
		// sList[0] = 目标URL
		// sList[1] = 目标URL被链接时的anchor text
		// sList[2] = 目标URL被链接时的元URL的pagerank值
		// sList[3] = 目标URL被链接时的元URL的关系，站内链接[0]或站外链接[1]
		
		if(sList[3].equals("1")){
			context.write(new TextPair(sList[0], "1"), new Text(sList[1] + "\t" + sList[2]));
		}
		
		// 主动发心跳
		if (System.currentTimeMillis() - lastProgressTS > heartBeatInterval) {
			context.progress();
			lastProgressTS = System.currentTimeMillis();
		}

	}

}