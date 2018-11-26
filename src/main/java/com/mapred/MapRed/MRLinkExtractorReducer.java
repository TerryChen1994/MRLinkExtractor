package com.mapred.MapRed;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import entity.TextPair;

public class MRLinkExtractorReducer extends Reducer<TextPair, Text, Text, Text> {
	public void reduce(TextPair key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		long lastProgressTS = 0; // 上一次发心跳的时间点
		long heartBeatInterval = 100000L; // 主动发心跳的间隔，100s，默认600s超时

		Iterator<Text> iter = values.iterator();
		Text exist = new Text(iter.next());
		
		String[] sList = exist.toString().split(" ");
		
		if(sList.length >= 3 && sList[0].equals("exist")){
			Text outKey = new Text(key.getFirst() + " " + sList[1] + " " + sList[2]);
			
			boolean firstKey = true;
			StringBuilder sb = new StringBuilder();
			int count = 0;
			while (iter.hasNext()) {
				count++;
				Text value = iter.next();
				sb.append(value);
				sb.append("\t");
				if (sb.length() > 10000000) {
					context.write(firstKey ? outKey : null, new Text(sb.toString()));
					firstKey = false;
					sb.setLength(0);
				}
				if (System.currentTimeMillis() - lastProgressTS > heartBeatInterval) {
					context.progress();
					lastProgressTS = System.currentTimeMillis();
				}
			}
			if (count > 0) {
				context.write(firstKey ? outKey : null, new Text(sb.toString().trim()));
				sb.setLength(0);
			}
		}
	}
}
