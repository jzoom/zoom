import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.http.Http;
import com.jzoom.zoom.http.Http.Response;

import junit.framework.TestCase;

public class Test extends TestCase {

public class CpuMemSnapshot {
	private Process proc;
	private BufferedReader br;
	
	public CpuMemSnapshot(){
		
	}
	
	
	public void startup() throws IOException, InterruptedException{
		proc = Runtime.getRuntime().exec("cp /Users/jzoom/Downloads/apache-tomcat-7.0.88.zip /Users/jzoom/Downloads/apache-tomcat-7.0.88.zip1");
		proc.waitFor();
		br = new BufferedReader(new InputStreamReader(proc
				.getInputStream()));
	}
	
	
	public String takeSnapshot() throws IOException{
		String info = br.readLine();
		if(info == null) {
			return null;
		}
		StringBuilder s = new StringBuilder(info);
		s.append("\n");
		int index = 0;
		while (info != null) {
			info = br.readLine();
			++index;
			if(info.contains("kernel_task")){
				break;
			}
			if(index<=30){
				s.append(info);
				s.append("\n");
			}
		}
		return s.toString();
	}
	
	
	public void close(){
		if(proc!=null){
			proc.destroy();
			proc = null;
		}
		if(br!=null){
			Io.close(br);
			br = null;
		}
		
	}
	
}

	public void test() throws IOException, InterruptedException {
		CpuMemSnapshot snapshot = new CpuMemSnapshot();
		snapshot.startup();
		String str = snapshot.takeSnapshot();
		System.out.print(str);
	}
}
