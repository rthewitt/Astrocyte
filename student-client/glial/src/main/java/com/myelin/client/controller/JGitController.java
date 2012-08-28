package com.myelin.client.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import com.jcraft.jsch.Session;

public class JGitController extends HttpServlet {
	
	// TODO move into util or service, figure out persistence
	private static final String USER_NAME = "0000002";
	private static final String COURSE = "CS101";
	
	private static final String PROJECT_LOCATION = "/home/synapse/Desktop/student-project";
	private static final File PATH = new File(PROJECT_LOCATION);
	private static final String HOST = "localhost";
	private static final String SSH_CONFIG_PATH = "/home/synapse/.ssh";
	private static final String SSH_KEY = SSH_CONFIG_PATH + "/" + "id_rsa";
	private static final String MYELIN_GIT_DIR = "/home/myelin/git-repositories";
	
	private static final String ACTION = "action";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = null;
		
		if(!request.getParameterMap().containsKey(ACTION)) return; // TODO handle this better
		else action = request.getParameter(ACTION);
		
		if("update".equals(action)) updateBranch();
		else if("commit".equals(action)) commitBranch();
		else if("init".equals(action)) try {
			initLocalRepository();
			sendResponse(response, "Initialization Success", "Success!  Repository cloned at " + PROJECT_LOCATION);
		} catch(GitAPIException ge) {
			System.out.println("Error initializing repository for " + USER_NAME);
			ge.printStackTrace();
			throw new ServletException(ge);
		}
		else sendResponse(response, "Failure", "Operation not (yet) supported");
	}
	
	private void sendResponse(HttpServletResponse response, String title, String message) throws IOException {
		PrintWriter out = response.getWriter();
		out.println(String.format("<html><head><title>%s</title></head><body><h2>%s</h2></body></html>", title, message));
	}
	
	
	private void updateBranch() {
		
	}
	
	private void commitBranch() {
		
	}
	
	private void initLocalRepository() throws IOException, GitAPIException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		   Repository repo = builder.setGitDir(PATH)
				   .readEnvironment()
				   .findGitDir()
				   .build();
		   
		   Git git = new Git(repo);

		   CloneCommand clone = Git.cloneRepository();
		   clone.setBare(false);
		   
		   clone.setCloneAllBranches(false); // TODO understand why true doesn't work anyway 
//		   clone.setBranch(String.format("refs/heads/%s", USER_NAME)); // no longer, always master
		   clone.setDirectory(PATH);

		   clone.setURI(String.format("ssh://%s/%s/%s/%s.git", HOST, MYELIN_GIT_DIR, COURSE, USER_NAME));
		   
		   clone.setTransportConfigCallback(new TransportConfigCallback() {
				
				@Override
				public void configure(Transport transport) {
					if(!(transport instanceof SshTransport)) 
						throw new UnsupportedOperationException("Only supported protocol: SSH");
					
					((SshTransport)transport).setSshSessionFactory(new JschConfigSessionFactory() {
						
						@Override
						protected void configure(Host hc, Session session) {
							Properties config = new Properties();
							config.put("StrictHostKeyChecking", "no");
							config.put("PreferredAuthentications", "publickey");
							config.put("IdentityFile", SSH_KEY);
							session.setConfig(config);
						}
					});
						
				}
			});
		   
		   clone.call();
		   
		   
		   System.out.println(repo.getBranch());
		   
		   System.out.println("B num: " + git.branchList().setListMode(ListMode.ALL).call().size());
		
	}
}