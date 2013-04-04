package com.mpi.astro.core.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mpi.astro.core.model.arcade.Game;
import com.mpi.astro.core.model.arcade.impl.JavaGame;
import com.mpi.astro.core.service.arcade.GameProviderService;
import com.mpi.astro.core.util.AstrocyteConstants;


//@Controller
//@RequestMapping("/arcade")
public class ArcadeController {
	/*
	@Autowired
	private GameProviderService gameService;
	
	@RequestMapping(value = "/game/{gameName}", method = RequestMethod.GET)
	public ModelAndView getGameLandingPage(@PathVariable String gameName) {
		Game theGame = gameService.getGame(gameName);
		return new ModelAndView("arcade/game/landing").addObject("game", theGame);
	}
	
	@RequestMapping(value = "/game/{gameName}", method = RequestMethod.GET, params = "format=java-ws")
	public ModelAndView getWebStartVersion(@PathVariable String gameName, HttpServletRequest request,
			HttpServletResponse response) {
		// generics, maybe
		JavaGame theGame = (JavaGame)gameService.getGame(gameName);
		
		Map modelMap = new HashMap();
		modelMap.put("game", theGame);
		modelMap.put("location", AstrocyteConstants.ARCADE_URL);
		modelMap.put("fileName", gameName);
		
		response.setContentType("application/x-java-jnlp-file");
		ModelAndView mav = new ModelAndView("jnlp", modelMap);
		return mav;
	}
	*/
}