import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;

public class WebServerr 
{
 
 public static void main(String args[]) 
 { 
	 ServerSocket sock;
	 try 
	 	{ sock = new ServerSocket(9091);
	 	  System.out.println("WebServer started.....");
	 	  while(true) 
	 	  { 
	 		  Socket socket = sock.accept();
              System.out.println("New connection accepted at " +
              socket.getInetAddress() + ":" + socket.getPort());
              try 
              { 
            	  GetRequest request =  new GetRequest(socket);
            	  Thread mythread = new Thread(request);
            	  mythread.start();
              }
              catch(Exception e) 
              {
            	  e.printStackTrace();
              }
	 	  }
	 	}	
   catch (Exception e) 
   { 
	   e.printStackTrace();
   }
 }
}
        
class GetRequest implements Runnable
{ 
	String CRLF = "\r\n";
	Socket sock;
	InputStream input;
	OutputStream output;
	BufferedReader buf;

	public GetRequest(Socket s) throws Exception 
	{ 
		this.sock = s;
		this.input = s.getInputStream();
		this.output = s.getOutputStream();
		this.buf= new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
    
	public void run()
	{ 
		while(true) 
		{ 
			try
			{
				String GET_line = buf.readLine();
				System.out.println("==>"+GET_line);
				
				if(GET_line.equals(CRLF) || GET_line.equals(""))
				{
					break;
				}
				
				StringTokenizer str = new StringTokenizer(GET_line);
				String str1 = str.nextToken();
				if(str1.equals("GET")) 
				{ 
					String filename = str.nextToken();
					filename = "." + filename ;
					FileInputStream inFile = null ;
					boolean file_Exists = true ;
					try 
					{
						inFile = new FileInputStream( filename ) ;
					} 
					catch ( FileNotFoundException e ) 
					{ 
						file_Exists = false ;
					}
					
          String serverln = "WebServer"+CRLF;
          String statusln = null;
          String contentTypeln = null;
          String Four_of_Four = null;
          String contentLengthln = "error";
          String mime="text/html";
          
          if (!(filename.endsWith(".html")))
            mime="text/plain";
          
          if (file_Exists)
          { 
        	  statusln = "HTTP/1.0 200 OK" + CRLF ;
        	  contentTypeln = "Content-type: "+mime+CRLF;
        	  contentLengthln = "Content-Length: " +
               (new Integer(inFile.available())).toString() + CRLF;
          }
          else
          { 
        	  statusln = "HTTP/1.0 404 Not Found" + CRLF ;
        	  contentTypeln = "Content-type: " + "text/html" + CRLF;
        	  Four_of_Four = 
        	  "<HTML>" + 
              " <HEAD>" +
              "  <TITLE>" +
              "   404 Not Found" +
              "  </TITLE>" +
              " </HEAD>" +
              " <BODY>"+
              "  404 Not Found "+filename+
              " </BODY>"+
              "</HTML>";
        	  contentLengthln = "Content-Length: " +
               (new Integer(Four_of_Four.length())).toString() + CRLF;
          }
          
          output.write(statusln.getBytes());
          System.out.print("<=="+statusln);
          
          output.write(serverln.getBytes());
          System.out.print("<=="+serverln);
          
          output.write(contentTypeln.getBytes());
          System.out.print("<=="+contentTypeln);
          
          output.write(contentLengthln.getBytes());
          System.out.print("<=="+contentLengthln);
          
          output.write(CRLF.getBytes());
          System.out.print("<=="+CRLF);
          System.out.flush();
          
          if (file_Exists)
          { 
        	  byte[] buffer = new byte[1024] ;
        	  int bytes = 0 ;
        	  
        	  while ((bytes = inFile.read(buffer)) != -1 ) 
        	  {
        		  output.write(buffer, 0, bytes);
        	  }
        	  
        	  inFile.close();
        	  System.out.println("<== 200: file sent: "+filename);
          }
          else
          { 
        	  output.write(Four_of_Four.getBytes());
        	  System.out.println("<== 404: not found: "+filename);
          }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    try 
    { 
    	output.close();
    	buf.close();
    	sock.close();
    }
    
    catch(Exception e) {}
  }

}
