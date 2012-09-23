package com.myelin.client.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
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
	
	private static String PROJECT_FOLDER = null;
	
	private static final String HOST = "localhost";
	private static final String SSH_CONFIG_PATH = "/home/synapse/.ssh";
	private static final String SSH_KEY = SSH_CONFIG_PATH + "/" + "id_rsa";
	private static final String MYELIN_GIT_DIR = "/home/myelin/git-repositories";
	
	private static TransportConfigCallback tCallback = null;
	
	private static final String ACTION = "action";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tCallback = new TransportConfigCallback() {
			
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
		};
		
		PROJECT_FOLDER = getServletContext().getRealPath("/");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		// TODO handle expired sessions
		String userId = session.getAttribute("userId").toString();
		String course = session.getAttribute("courseName").toString();
		String gitDir = PROJECT_FOLDER+"/"+course+"/"+".git";
		
		File courseFolder = new File(PROJECT_FOLDER+"/"+course);
		
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
		
		pull.call();
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