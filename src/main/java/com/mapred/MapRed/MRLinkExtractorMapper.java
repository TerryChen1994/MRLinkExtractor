package com.mapred.MapRed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import entity.RecordContent;
import entity.RecordHeader;
import parser.ContentParserGZIP;

public class MRLinkExtractorMapper extends Mapper<RecordHeader, RecordContent, Text, Text> {

	public void map(RecordHeader key, RecordContent value, Context context) throws IOException, InterruptedException {
		long lastProgressTS = 0; // 上一次发心跳的时间点
		long heartBeatInterval = 100000L; // 主动发心跳的间隔，100s，默认600s超时
		
		ContentParserGZIP contentParser;
		contentParser = new ContentParserGZIP();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		StringBuilder tarUri = null;
		StringBuilder anchor = null;
		StringBuilder linkUri = null;
		StringBuilder outputString = null;
		Text outputKey = null;
		Text outputValue = null;
		
		try {
			baos = contentParser.extractAnchorText(value.getContent(), key.getWarcTargetUri());
			if (baos.size() > 0) {
				String anchorList[] = baos.toString().split("\r");
				for (int i = 0; i < anchorList.length; i++) {
					String uriAnchor[] = anchorList[i].split("\t");
					tarUri = new StringBuilder(uriAnchor[0]);
					anchor = new StringBuilder(uriAnchor[1]);
					linkUri = new StringBuilder(key.getWarcTargetUri());
					outputString = new StringBuilder(anchor + "\t" + linkUri);
					outputKey = new Text(tarUri.toString());
					outputValue = new Text(outputString.toString());
					context.write(outputKey, outputValue);
				}
				tarUri.setLength(0);
				anchor.setLength(0);
				linkUri.setLength(0);
				outputString.setLength(0);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 主动发心跳
			if (System.currentTimeMillis() - lastProgressTS > heartBeatInterval) {
				context.progress();
				lastProgressTS = System.currentTimeMillis();
			}
			
		}

	}

}
