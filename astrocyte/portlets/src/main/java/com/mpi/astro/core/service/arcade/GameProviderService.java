package com.mpi.astro.core.service.arcade;

import com.mpi.astro.core.model.arcade.Game;

public interface GameProviderService {
	
	public Game[] getAllGames();
	public Game getGame(String name);

}
