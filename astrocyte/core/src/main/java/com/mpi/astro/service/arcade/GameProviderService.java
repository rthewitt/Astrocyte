package com.mpi.astro.service.arcade;

import com.mpi.astro.model.arcade.Game;

public interface GameProviderService {
	
	public Game[] getAllGames();
	public Game getGame(String name);

}
