package xue_feature_deal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class Sequence {
	private Vector feature = new Vector();
	String inputfile, outputfile;

	// int n=1;
	public Sequence(String inputfile, String outputfile) {
		this.inputfile = inputfile;
		this.outputfile = outputfile;
	}

	public int[] findstem(String ss) {
		int[] pos = new int[4];
		for (int i = 0; i < ss.length(); i++) {
			if (ss.charAt(i) == '(') {
				pos[0] = i;
				break;
			}
		}
		for (int i = pos[0]; i < ss.length(); i++)
			if (ss.charAt(i) == ')') {
				pos[2] = i;
				break;
			}
		for (int i = ss.length() - 1; i > 0; i--)
			if (ss.charAt(i) == ')') {
				pos[3] = i;
				break;
			}
		for (int i = pos[3]; i > 0; i--)
			if (ss.charAt(i) == '(') {
				pos[1] = i;
				break;
			}
		return (pos);
	}

	public String Readssfeatures(String seq, String str, BufferedWriter bw) {
		int[] position = findstem(str);
		int totlenum = position[1] - position[0] + position[3] - position[2]
				+ 2;
		str = str.replace(')', '(');
		seq = seq.toLowerCase();
		seq = seq.replace('t', 'u');
		int[] count = new int[32];
		for (int j = 0; j < 3; j += 2)
			// two part of the stem
			for (int i = Math.max(position[j], 1); i < Math.min(
					position[j + 1], seq.length() - 2); i++) {
				String s = String.valueOf(seq.charAt(i)).concat(
						str.substring(i - 1, i + 2));
				if (s.equals("a((("))
					count[24]++;
				else if (s.equals("a((."))
					count[25]++;
				else if (s.equals("a(.("))
					count[26]++;
				else if (s.equals("a(.."))
					count[27]++;
				else if (s.equals("a.(("))
					count[28]++;
				else if (s.equals("a.(."))
					count[29]++;
				else if (s.equals("a..("))
					count[30]++;
				else if (s.equals("a..."))
					count[31]++;
				else if (s.equals("c((("))
					count[8]++;
				else if (s.equals("c((."))
					count[9]++;
				else if (s.equals("c(.("))
					count[10]++;
				else if (s.equals("c(.."))
					count[11]++;
				else if (s.equals("c.(("))
					count[12]++;
				else if (s.equals("c.(."))
					count[13]++;
				else if (s.equals("c..("))
					count[14]++;
				else if (s.equals("c..."))
					count[15]++;
				else if (s.equals("g((("))
					count[16]++;
				else if (s.equals("g((."))
					count[17]++;
				else if (s.equals("g(.("))
					count[18]++;
				else if (s.equals("g(.."))
					count[19]++;
				else if (s.equals("g.(("))
					count[20]++;
				else if (s.equals("g.(."))
					count[21]++;
				else if (s.equals("g..("))
					count[22]++;
				else if (s.equals("g..."))
					count[23]++;
				else if (s.equals("u((("))
					count[0]++;
				else if (s.equals("u((."))
					count[1]++;
				else if (s.equals("u(.("))
					count[2]++;
				else if (s.equals("u(.."))
					count[3]++;
				else if (s.equals("u.(("))
					count[4]++;
				else if (s.equals("u.(."))
					count[5]++;
				else if (s.equals("u..("))
					count[6]++;
				else if (s.equals("u..."))
					count[7]++;
			}

		String out = "";
		for (int i = 0; i < 32; i++)
			out = out.concat(
					String.valueOf((float) count[i] / (float) totlenum))
					.concat(",");
		return (out);

	}

	public String Readstrucfeatures(String line, BufferedWriter bw)
			throws Exception {
		String result = "";
		/*
		 * System.out.print(line); line=
		 * "((((((...((((.........)))).(((((.......))))).....(((((.......))))).)))))). (-29.40)"
		 * ;
		 */int i = 0;
		int basepairNum = 0;
		int len = line.length();
		float basepairPercent = 0;
		while ((line.charAt(i) != ' ')) {
			if (line.charAt(i) == '(')
				basepairNum++;
			i++;
		}

		String mfe = line.substring(i + 2, len - 2);
		basepairPercent = (float) 2 * basepairNum / len;
		result = mfe + ","
				+ new java.text.DecimalFormat("0.0000").format(basepairPercent);
		return result;

	}

	public String Readgccontent(String line, BufferedWriter bw)
			throws Exception {
		String result = "";
		int len = line.length();
		int GC = 0;
		float GCpercent = 0;
		String[] S = line.split("GC");
		GC = S.length - 1;
		GCpercent = (float) GC / (len - 2);
		S = null;
		result = new java.text.DecimalFormat("0.0000").format(GCpercent);
		bw.flush();
		return result;
	}

	public void run() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
			String feature = null;
			String line = br.readLine();
			String sb;
			String str = "", seq = "";
			while (br.ready()) {
				sb = "";
				if (line.length() != 0 && line.charAt(0) == '>') {
					line = br.readLine();
					while (br.ready() && line.length() == 0)
					{
						line = br.readLine();
					}
					while (line.length() != 0 && line.charAt(0) != '>') {

						if (br.ready() && !line.contains("(")
								&& !line.contains(")")) {
							sb += line;
							line = br.readLine();
						} else {
							str = line;
							break;
						}
					}
					line = sb;
					seq = line;
					bw.flush();
				} else {
					line = br.readLine();
				}
				line = br.readLine();
				//feature = Readgccontent(seq, bw) + ","+ Readstrucfeatures(str, bw) + ","+ Readssfeatures(seq, str, bw);//35
				feature = Readssfeatures(seq, str, bw)+"0";//32
				bw.write(feature);
				bw.newLine();
			}

			br.close();
			bw.close();
		} catch (Exception ex) {
			System.out.print(ex.getMessage());
			ex.printStackTrace();
			System.exit(0);
		}
	}
	public static void main(String[] args){
		Sequence sequence=new Sequence("G:/我的个人资料/数据集/薛成海数据集/7494.txt","G:/我的个人资料/数据集/薛成海数据集/7494.arff");
		sequence.run();
	}
}
