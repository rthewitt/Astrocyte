package com.myelin.client.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.myelin.client.util.JSchCommonsLogger;

public class JGitController extends HttpServlet {
	
	private static String PROJECT_BASE = null;
	
	private static final String HOST = "localhost";
//	private static final String HOST = "23.23.248.141";
	private static final String SSH_CONFIG_PATH = "/home/synapse/.ssh";
	
	// Testing whether another key can be used.
	private static final String SSH_KEY = SSH_CONFIG_PATH + "/" + "MPI/stupid-test";
	private static final String SSH_USER = "myelin";
	private static final String MYELIN_GIT_DIR = "/home/myelin/git-repositories";
	
	private static JSchCommonsLogger jschLogger = new JSchCommonsLogger();
	
	private static TransportConfigCallback tCallback = null;
	
	private static final String ACTION = "action";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		com.jcraft.jsch.JSch.setLogger(jschLogger);

		
		tCallback = new TransportConfigCallback() {
			
			@Override
			public void configure(Transport transport) {
				if(!(transport instanceof SshTransport)) 
					throw new UnsupportedOperationException("Only supported protocol: SSH");
				
				((SshTransport)transport).setSshSessionFactory(new JschConfigSessionFactory() {
					
					// Going to try to add identity as well as add config.
					protected JSch createDefaultJSch(FS fs)
                            throws com.jcraft.jsch.JSchException {
						JSch orig = super.createDefaultJSch(fs);
						try {
							   byte [] privateKey = IOUtils.toByteArray(new FileInputStream(SSH_KEY));
							   byte [] publicKey = IOUtils.toByteArray(new FileInputStream(SSH_KEY+".pub"));
							   byte [] passphrase = "asshole".getBytes(); 
							   orig.addIdentity(SSH_USER, privateKey, publicKey, passphrase);
							  } catch (IOException e) {
							   jschLogger.log(Logger.ERROR, "Problem with key-byte arrays.");
							  }
						return orig;
					}
					
					
					
					@Override
					protected Session createSession(OpenSshConfig.Host hc, String user,
							String host, int port, FS fs) throws JSchException {
						jschLogger.log(1, "Attempting to change session creation name from " +
							user + " to " + SSH_USER);
						// Why does this fail?
						return super.createSession(hc, SSH_USER, host, port, fs);
					}
					
					@Override
					protected void configure(Host hc, Session session) {
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						config.put("PreferredAuthentications", "publickey");
						config.put("IdentityFile", SSH_KEY);
						session.setConfig(config);
						jschLogger.log(0, "Setting configuration.  Session userName is: " + session.getUserName());
					}
				});
					
			}
		};
		
		PROJECT_BASE = getServletContext().getRealPath("/");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		// TODO handle expired sessions
		String userId = session.getAttribute("userId").toString();
		String course = session.getAttribute("courseName").toString();
		String gitDir = PROJECT_BASE+"/"+course+"/"+".git";
		
		File courseFolder = new File(PROJECT_BASE+"/"+course);
		
		File gitDirectory = new File(gitDir);
		
		Repository courseRepo = getRepo(gitDirectory);
		
		String action = null;
		
		if(!request.getParameterMap().containsKey(ACTION)) return; // TODO handle this better
		else action = request.getParameter(ACTION);
		
		try {
			if("update".equals(action)) 
				updateBranch(courseRepo); 
			else if("commit".equals(action)) 
				commitBranch(courseRepo);
			else if("next-step".equals(action)) {
				commitBranch(courseRepo);
				pushBranch(courseRepo);
			}
			else if("init".equals(action)) 
				initLocalRepository(session, courseRepo, courseFolder);
			else sendResponse(response, "Failure", "Operation not (yet) supported");
			
		} catch(GitAPIException ge) {
			throw new ServletException(ge);
		}
	}
	
	private void sendResponse(HttpServletResponse response, String title, String message) throws IOException {
		PrintWriter out = response.getWriter();
		out.println(String.format("<html><head><title>%s</title></head><body><h2>%s</h2></body></html>", title, message));
	}
	
	
	private void updateBranch(Repository repo) throws IOException, GitAPIException {
		Git git = new Git(repo);
		
		PullCommand pull = git.pull();
		
		pull.setTransportConfigCallback(tCallback);
		
		PullResult result = pull.call();
		
		System.out.println("Pull result: " + result.isSuccessful());
	}
	
	private void commitBranch(Repository repo) throws GitAPIException {
		Git git = new Git(repo);
		
		CommitCommand commit = git.commit();
		
		commit.setMessage("Auto generated message from glial client");
		commit.call();
	}
	
	private void pushBranch(Repository repo) throws GitAPIException {
		Git git = new Git(repo);
		
		PushCommand push = git.push();
		
		push.setTransportConfigCallback(tCallback);
		
		push.call();
	}
	
	private Repository getRepo(File localFolder) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		
		   Repository repo = builder.setGitDir(localFolder)
				   .readEnvironment()
				   .findGitDir()
				   .build();
		   
		   return repo;
	}
	
	private String getRepoURIForSession(HttpSession session) {
		String userId = session.getAttribute("userId").toString();
		String course = session.getAttribute("courseName").toString();
//		return String.format("ssh://%s@%s/%s/%s/%s.git", SSH_USER, HOST, MYELIN_GIT_DIR, course, userId);
		return String.format("ssh://%s/%s/%s/%s.git", HOST, MYELIN_GIT_DIR, course, userId);
	}
	
	private void initLocalRepository(HttpSession session, Repository repo, File localFolder) throws IOException, GitAPIException {
		
	   Git git = new Git(repo);

	   CloneCommand clone = Git.cloneRepository();
	   clone.setBare(false);
	   
	   clone.setCloneAllBranches(false); // TODO understand why true doesn't work anyway 
//		   clone.setBranch(String.format("refs/heads/%s", USER_NAME)); // no longer, always master
	   clone.setDirectory(localFolder);

	   clone.setURI(getRepoURIForSession(session));
	   
	   clone.setTransportConfigCallback(tCallback);
	   
	   clone.call();
	   
	   //TODO revisit logging
//	   System.out.println(repo.getBranch());
//	   
//	   System.out.println("B num: " + git.branchList().setListMode(ListMode.ALL).call().size());
		
	}
	
}