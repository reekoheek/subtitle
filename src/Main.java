
public class Main {
	public static String SOURCE1 = "D:/My Documents/My Videos/wall-e/Wall-E.2008.TS.XViD-PREVAiL.ENG.srt.orig";
//	public static String SOURCE2 = "E:/Downloads/300/300 CD2.srt";
	public static String DESTINATION = "D:/My Documents/My Videos/wall-e/Wall-E.2008.TS.XViD-PREVAiL.ENG.srt";

	public static void main(String[] args) {
		System.out.println("Start");
		SrtRewrite srt = new SrtRewrite(SOURCE1);
		srt.shiftTimeLine("00:00:-05,000");
//		srt.appendBegin(new SrtRewrite(SOURCE2), "00:57:26,689");
		srt.write(DESTINATION);
		System.out.println("Finish");
	}
}
