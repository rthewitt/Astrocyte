package com.mpi.astro.service.edu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.model.edu.Student;
import com.mpi.astro.util.PropertiesUtil;

// TODO establish security of code-base using access qualifiers
// TODO establish centralized logging authority
public class StudentService {
	
	@Autowired
	private StudentDao studentDao;
	
	public String getPath() {
		return PropertiesUtil.getProperty(PropertiesUtil.PROP_DATA_DIR);
	}
	
	
	// TODO anticipate collisions, UUID and pass through to script
		public boolean writeStudentsToFile(String fileName) {
			String path = PropertiesUtil.getProperty(PropertiesUtil.PROP_DATA_DIR);
//			File file = new File(path+"/"+fileName);
			List<Student> students = studentDao.getStudents();
			
			
			try {
				File tmp = new File(path+"/"+fileName+".course");
				if(!tmp.exists())
					tmp.createNewFile();
//				File tmp = File.createTempFile(fileName, ".course", new File(path));
				
				FileWriter fw = new FileWriter(tmp);;
				
				// necessary to nest?
				try {
					int len = students.size();
					StringBuilder sb = new StringBuilder();
					StringBuilder nums = new StringBuilder();
					
					sb.append("NUM_STUDENTS=" + len);
					
					// add unix arrays to temp file
					for(int x=0; x<len; x++) {
						Student stud = students.get(x);
						// TODO move userName gen into studentService, add to dao
						String sId = stud.getStudentId();
						String lName = stud.getLastName();
						int nameClip = lName.length() < 5 ?  lName.length() : 5;
						
						String userName = String.format("%-4s", lName.substring(0, nameClip)).toLowerCase().replace(' ', '_')
								+ sId.substring(sId.length()-4);
						
						sb.append("\n");
						sb.append(String.format("names[%d]=\"%s\"", x, userName));
						nums.append("\n");
						nums.append(String.format("nums[%d]=\"%s\"", x, sId));
					}
					
					// add prototypical student
					sb.append("\n");
					nums.append("\n");
					sb.append( String.format("names[%d]=\"%s\"", 
							len, PropertiesUtil.getProperty(PropertiesUtil.PROP_PROTO)) );
					
					nums.append( String.format("nums[%d]=\"%s\"", 
							len, PropertiesUtil.getProperty(PropertiesUtil.PROP_PROTO_ID)) );
					sb.append("\n");
					sb.append(nums.toString());
					
					fw.write(sb.toString());
				} catch(Exception e) {
					throw new IOException(e);
				} finally {
					fw.close();
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		// Eventually this will be a Course object, not a String.
		public boolean initializeCourse(String courseName, HttpServletResponse response) {
			
			return initializeCourseCGI(courseName, response);
		}
		
		
		// TODO examine server load, bottlenecks
		// TODO disassociate with response, handle only according to booleans.  Pass string or writer.
		private boolean initializeCourseCGI(String courseName, HttpServletResponse response) {
			
			URLConnection con = null;
			PrintStream outStream = null;
			DataInputStream inStream = null;
			ServletOutputStream out = null;
			BufferedReader buffReader = null;
			
			try {
				URL url = new URL("http://localhost/cgi-bin/test.py");
				
				con = url.openConnection();
				con.setDoOutput(true);
				
				outStream = new PrintStream(con.getOutputStream());
				// sending a parameter (courseName)
				outStream.println("course=" + URLEncoder.encode(courseName, "UTF-8"));
				
				// handle in finally?
				outStream.flush();
				outStream.close();
				
				inStream = new DataInputStream(con.getInputStream());
//				out = response.getWriter();
				out = response.getOutputStream();
//				buffReader = new BufferedReader(new InputStreamReader(inStream));
				byte[] buffer = new byte[4096];
				int n = -1;
				
				while( (n=inStream.read(buffer)) != -1) {
					out.write(buffer, 0, n);
				}
				
//				String line;
				
				// Was an error here from CGI
//				 while (null != (line = buffReader.readLine())) {
//					 out.println(line);
//				 }
				 inStream.close();

			} catch(MalformedURLException me) {
				me.printStackTrace(); // TODO log
				return false;
			} catch(IOException me) {
				me.printStackTrace(); // TODO log
				return false;
			} finally {
				if(outStream != null) outStream.close();
				// Do I need to close the other streams here?
			}
			
			return true; // change
		}
	
}
