package outputformat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.ReflectionUtils;

public class AnchorURLOutputFormat<K, V> extends TextOutputFormat<K, V> {
	public RecordWriter<K, V> getRecordWriter(final TaskAttemptContext job) throws IOException, InterruptedException {
		final Configuration conf = job.getConfiguration();
		final boolean isCompressed = getCompressOutput((JobContext) job);
		final String keyValueSeparator = conf.get(TextOutputFormat.SEPERATOR, "\t");
		CompressionCodec codec = null;
		String extension = "";
		if (isCompressed) {
			final Class<? extends CompressionCodec> codecClass = (Class<? extends CompressionCodec>) getOutputCompressorClass(
					(JobContext) job, (Class) GzipCodec.class);
			codec = (CompressionCodec) ReflectionUtils.newInstance((Class) codecClass, conf);
			extension = codec.getDefaultExtension();
		}
		final Path file = this.getDefaultWorkFile(job, extension);
		final FileSystem fs = file.getFileSystem(conf);
		if (!isCompressed) {
			final FSDataOutputStream fileOut = fs.create(file, false);
			return new AnchorURLWriter<K, V>((DataOutputStream) fileOut, keyValueSeparator);
		}
		final FSDataOutputStream fileOut = fs.create(file, false);
		return new AnchorURLWriter<K, V>(
				new DataOutputStream((OutputStream) codec.createOutputStream((OutputStream) fileOut)),
				keyValueSeparator);
	}
	protected static class AnchorURLWriter<K, V> extends RecordWriter<K, V> {
		private static final byte[] newline;

		static {
			newline = "\n".getBytes();
		}
		protected DataOutputStream out;
		private final byte[] keyValueSeparator;
		private boolean dataWritten = false;

		public AnchorURLWriter(DataOutputStream out, String keyValueSeparator) {
			this.out = out;
			this.keyValueSeparator = keyValueSeparator.getBytes();
		}

		public AnchorURLWriter(DataOutputStream out) {
			this(out, "\t");
		}

		private void writeObject(Object o) throws IOException {
			if (o instanceof Text) {
				Text to = (Text) o;
				out.write(to.getBytes(), 0, to.getLength());
			} else {
				out.write(o.toString().getBytes());
			}
		}

		@Override
		public synchronized void write(K key, V value) throws IOException {

			boolean nullKey = key == null || key instanceof NullWritable;
			boolean nullValue = value == null || value instanceof NullWritable;
			if (nullKey && nullValue) {
				return;
			}

			if (!nullKey) {
				// if we've written data before, append a new line
				if (dataWritten) {
					out.write(newline);
				}

				// write out the key and separator
				writeObject(key);
				out.write(keyValueSeparator);
			}
			// write out the value
			writeObject(value);
			// track that we've written some data
			dataWritten = true;
		}

		public synchronized void close(Reporter reporter) throws IOException {
			// if we've written out any data, append a closing newline
			if (dataWritten) {
				out.write(newline);
			}

			out.close();
		}

		@Override
		public void close(TaskAttemptContext p0) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			if (dataWritten) {
				out.write(newline);
			}

			out.close();
		}
	}

	

}
