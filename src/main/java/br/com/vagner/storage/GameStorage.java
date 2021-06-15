package br.com.vagner.storage;

import java.util.HashMap;
import java.util.Map;

import br.com.vagner.model.Game;
import lombok.Getter;

public class GameStorage {

	@Getter
	private Map<String,Game> games;
	private static GameStorage instance;
	
	private GameStorage() {
		games = new HashMap<>();
	}
	
	public static synchronized GameStorage getInstance() {
		if(instance == null) {
			instance = new GameStorage();
		}
		
		return instance;
	}
	
	public void setGame(Game game) {
		games.put(game.getGameId(), game);
	}
	
}
