import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String path1 = "C:/Users/ysxin/Desktop/data/task3.txt";
		String path2 = "C:/Users/ysxin/Desktop/data/task4.txt";
		File f1=new File(path1);
		FileReader fr=new FileReader(f1);
		BufferedReader br=new BufferedReader(fr);
		int num=1;
		File f = new File(path2);
	    FileWriter fw = new FileWriter(f);
	    BufferedWriter bw = new BufferedWriter(fw);
	    while(br.ready()){
	    	bw.write(num+","+br.readLine().split(",")[1]);
	    	bw.newLine();
	    	num++;
	    }
	    br.close();
	    bw.close();
	}

}
