package jsp;

import java.io.*;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.*;

public class jspReader {

	private String jspName;
	private String jspContent = null;
	private String ServeletContent = null;

	public jspReader(String uri) {
		jspName = uri;
		Read();
		makeServlet();
	}

	public void init() {

	}

	public void Read() {
		File f = new File(System.getProperty("user.dir") + File.separator + "jsp" + File.separator + jspName);

		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(f));

			String line = br.readLine();
			
			if(line.contains("<%@"))
			{
				line =  "<% response.setContentType(\"text/html; charset=gb2312\");%>";
				line = line.replace("\"", "&&&&");
			}

			jspContent = line;

			while ((line = br.readLine()) != null) {
				
				if(line.contains("<%="))
				{
					line = line.replace("<%=", "$$out.println(");
					line = line.replace("%>", ");&&");
					line = line.replace("\"", "&&&&");
				}

				jspContent = jspContent + " " + line;
			}

			br.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		System.out.println(jspContent);
	}

	public void makeServlet() {
		
		File f1 = new File(System.getProperty("user.dir") + File.separator + "part" + File.separator + "part_1.txt");
		File f2 = new File(System.getProperty("user.dir") + File.separator + "part" + File.separator + "part_2.txt");
		File f3 = new File(System.getProperty("user.dir") + File.separator + "part" + File.separator + "part_3.txt");
		File f4 = new File(System.getProperty("user.dir") + File.separator + "part" + File.separator + "part_4.txt");

		BufferedReader br1, br2, br3, br4;

		try {
		
			br4 = new BufferedReader(new FileReader(f4));

			String line = null;

			String[] sign = new String[7];

			sign[0] = br4.readLine();
			sign[1] = "\n" + br4.readLine() ;
			sign[2] = sign[0] + br4.readLine();
			sign[3] = br4.readLine();
			sign[4] = br4.readLine();
			sign[5] = br4.readLine();
			sign[6] = br4.readLine();

			br4.close();
			
			String ServeletContent1 = jspContent.replace("$$out.println(", sign[2]);
		    ServeletContent1 = ServeletContent1.replace(");&&",");" + sign[1] );
			ServeletContent1 = ServeletContent1.replace("<%", sign[0]);
			ServeletContent1 = ServeletContent1.replace("%>", sign[1]);	
			ServeletContent1 = ServeletContent1.replace("&&&&", sign[5]);
			
			
			br1 = new BufferedReader(new FileReader(f1));

			line = null;

			ServeletContent = br1.readLine();

			while ((line = br1.readLine()) != null) {

				ServeletContent = ServeletContent + "\n" + line;
			}

			br1.close();

			ServeletContent = ServeletContent + "\n" + jspName.split(".jsp")[0] + "Servlet\n";
			
			br2 = new BufferedReader(new FileReader(f2));

			line = null;

			ServeletContent = ServeletContent + br2.readLine();

			while ((line = br2.readLine()) != null) {

				ServeletContent = ServeletContent + "\n" + line;
			}

			br2.close();
		
			ServeletContent = ServeletContent + ServeletContent1;
		
			br3 = new BufferedReader(new FileReader(f3));

			line = null;

			ServeletContent = ServeletContent + br3.readLine();

			while ((line = br3.readLine()) != null) {

				ServeletContent = ServeletContent + "\n" + line;
			}

			br3.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
		File f = new File(System.getProperty("user.dir") + File.separator + "work" + File.separator
				+ jspName.split(".jsp")[0] + "Servlet.java");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);

			fw.write(ServeletContent);

			fw.close();
			
			String dir = System.getProperty("user.dir") + File.separator+ "work";
			String filename = jspName.split(".jsp")[0] + "Servlet.java";
			
			
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		
			Iterable fileObject = fileManager.getJavaFileObjects(new File(dir,filename));
		
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-d", "./work"),
					null, fileObject);
			task.call();

			fileManager.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public String getServletName()
	{
		return jspName.split(".jsp")[0] + "Servlet";
	}
}
