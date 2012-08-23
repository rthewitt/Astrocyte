package com.mpi.astro.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mpi.astro.dao.StudentDao;
import com.mpi.astro.model.arcade.impl.JavaGame;
import com.mpi.astro.model.edu.Student;

public class AstrocyteUtils {
	
	private static final JavaGame alphaGame = new JavaGame();
	
	static {
		alphaGame.setAppName("com.alpha.game.Alpha Game");
		alphaGame.setArtifact("alpha-game-1.0-SNAPSHOT.jar");
		alphaGame.setAuthor("Fierfek");
		alphaGame.setJavaVersion("1.6");
		alphaGame.setMainClass("com.alpha.game.Alpha");
		alphaGame.setThumbNail("http://www.freevectorgraphics.org/d/file/201103/81b147bb0534b59dded39b95f5bbd267.png");
		alphaGame.setTitle("Alpha");
		alphaGame.setVendor("Myelin Price Interactive");
	}
	
	
	// eventually, this will come from a service
	public static JavaGame getAlphaGame() {
		return alphaGame;
	}
	
}