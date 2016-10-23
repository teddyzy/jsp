package jsp;

import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jsp.Request;
import jsp.Response;
import jsp.jspReader;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

public class HttpServer {

	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (true) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();

				Request request = new Request(input);
				request.parse();

				Response response = new Response(output);
				response.setRequest(request);

				String url = request.getUri();

				String filename = System.getProperty("user.dir")+ File.separator +"jsp" + File.separator + url;
				if(new File(filename).exists())
				{
					jspReader jsp = new jspReader(url.split("/")[1]);
					ServletProcessor processor = new ServletProcessor(); 
					processor.process(request,response,jsp.getServletName()); 
				}
				else
				{
					System.out.println("找不到该文件！");
				}
				
				socket.close();

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public Boolean findurl(String url) {
		Element element = null;
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);
			
			element = dt.getDocumentElement();

			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			for (int i = 0; i < childNodes.getLength(); i++) {
	
				Node node1 = childNodes.item(i);

				if ("servlet-mapping".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("url-pattern".equals(detail.getNodeName())) {
					
							if (url.equals(detail.getTextContent()))
								return true;
						}

					}
				}
			}
		}

		catch (Exception e) {
			System.out.println(e.toString());
		}

		return false;
	}

	public String getServeletName(String url) {
		String ServeletName = null;

		Element element = null;
	
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);

			element = dt.getDocumentElement();

			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node1 = childNodes.item(i);

				if ("servlet-mapping".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("url-pattern".equals(detail.getNodeName())) {
							if (url.equals(detail.getTextContent()))
								theNodeList = nodeDetail;
						}

					}
				}
			}

			String Sname = null;
	
			for (int j = 0; j < theNodeList.getLength(); j++) {
				Node detail = theNodeList.item(j);

				if ("servlet-name".equals(detail.getNodeName())) 
				{
					Sname = detail.getTextContent();
				}

			}

			NodeList theNodeList1 = null;


			for (int i = 0; i < childNodes.getLength(); i++) {
				
				Node node1 = childNodes.item(i);
				
				if ("servlet".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("servlet-name".equals(detail.getNodeName())) 
							if (Sname.equals(detail.getTextContent()))
								theNodeList1 = nodeDetail;
					}
				}
			}
		
			for (int j = 0; j < theNodeList1.getLength(); j++) {
				Node detail = theNodeList1.item(j);

				if ("servlet-class".equals(detail.getNodeName())) 
				{
					ServeletName = detail.getTextContent();
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return ServeletName;
	}
}
