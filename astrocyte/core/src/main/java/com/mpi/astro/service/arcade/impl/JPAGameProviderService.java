package com.mpi.astro.service.arcade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.astro.model.arcade.Game;
import com.mpi.astro.service.arcade.GameProviderService;

@Service
public class JPAGameProviderService implements GameProviderService {
	
	@Autowired
	private Game exampleGame;

	@Override
	public Game[] getAllGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Game getGame(String name) {
		return exampleGame;
	}
	
}