import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SrtRewrite {
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm:ss,SSS");

	private String filename;

	private List<Subtitle> subtitles;

	private int lineType = SrtLineType.EMPTY_LINE;

	public SrtRewrite(String filename) {
		this.filename = filename;
		this.read();
	}

	public void read() {
		subtitles = new ArrayList<Subtitle>();
		try {
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			Subtitle st = null;
			do {
				st = fetchSubtitle(br);
				if (st != null) {
					subtitles.add(st);
				}
			} while (st != null);

			// Close the input stream
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Subtitle fetchSubtitle(BufferedReader br) throws IOException {
		String strLine;
		Subtitle subtitle = new Subtitle();
		while ((strLine = br.readLine()) != null) {
			if (lineType == SrtLineType.EMPTY_LINE) {
				if (strLine.length() > 0) {
					subtitle.setId(Integer.parseInt(strLine));
					lineType = SrtLineType.ID_LINE;
				}
			} else if (lineType == SrtLineType.ID_LINE) {
				String[] timeStrings = strLine.split("-->");
				for (int i = 0; i < timeStrings.length; i++) {
					try {
						String timeString = timeStrings[i];
						if (i == 0) {
							subtitle.setBegin(TIME_FORMAT.parse(timeString));
						} else {
							subtitle.setEnd(TIME_FORMAT.parse(timeString));
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lineType = SrtLineType.TIME_LINE;
			} else if (lineType == SrtLineType.TIME_LINE) {
				subtitle.setText(subtitle.getText() + strLine);
				lineType = SrtLineType.TEXT_LINE;
			} else {
				if (strLine.length() == 0) {
					lineType = SrtLineType.EMPTY_LINE;
					break;
				} else {
					subtitle.setText(subtitle.getText() + '\n' + strLine);
				}
			}
		}
		if (strLine == null) {
			return null;
		} else {
			return subtitle;
		}
	}

	public void appendBegin(SrtRewrite appended, String timeString) {
		try {
			Date timeOffset = TIME_FORMAT.parse(timeString);
			Calendar calOffset = Calendar.getInstance();
			calOffset.setTime(timeOffset);
			long hours = calOffset.get(Calendar.HOUR_OF_DAY);
			long minutes = calOffset.get(Calendar.MINUTE);
			long seconds = calOffset.get(Calendar.SECOND);
			long millis = calOffset.get(Calendar.MILLISECOND);
			int idOffset = subtitles.get(subtitles.size() - 1).getId();

			List<Subtitle> appendedSub = appended.getSubtitles();
			for (Subtitle subtitle : appendedSub) {
				long begin = subtitle.getBegin().getTime();
				begin = begin + (millis) + (seconds * 1000)
						+ (minutes * 60 * 1000) + (hours * 60 * 60 * 1000);
				subtitle.getBegin().setTime(begin);

				long end = subtitle.getEnd().getTime();
				end = end + (millis) + (seconds * 1000) + (minutes * 60 * 1000)
						+ (hours * 60 * 60 * 1000);
				subtitle.getEnd().setTime(end);

				subtitle.setId(subtitle.getId() + idOffset);

				subtitles.add(subtitle);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Subtitle> getSubtitles() {
		return subtitles;
	}

	public void write(String destination) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(destination);
			BufferedWriter out = new BufferedWriter(fstream);

			for (Subtitle subtitle : subtitles) {
				out.write(subtitle.getId() + "\n");
				out.write(TIME_FORMAT.format(subtitle.getBegin()) + " --> "
						+ TIME_FORMAT.format(subtitle.getEnd()) + "\n");
				out.write(subtitle.getText() + "\n\n");
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public void shiftTimeLine(String timeString) {
		try {
			Date time = TIME_FORMAT.parse(timeString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			long hours = cal.get(Calendar.HOUR_OF_DAY);
			long minutes = cal.get(Calendar.MINUTE);
			long seconds = cal.get(Calendar.SECOND);
			long millis = cal.get(Calendar.MILLISECOND);
			long temp;

			for (Subtitle subtitle : subtitles) {
				temp = subtitle.getBegin().getTime();
				temp = temp + (millis) + (seconds * 1000)
						+ (minutes * 60 * 1000) + (hours * 60 * 60 * 1000);
				subtitle.getBegin().setTime(temp);
				
				temp = subtitle.getEnd().getTime();
				temp = temp + (millis) + (seconds * 1000)
						+ (minutes * 60 * 1000) + (hours * 60 * 60 * 1000);
				subtitle.getEnd().setTime(temp);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}