package br.com.vagner.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.vagner.exception.InvalidGameException;
import br.com.vagner.exception.InvalidParamException;
import br.com.vagner.exception.NotFoundException;
import br.com.vagner.model.Game;
import br.com.vagner.model.GamePlay;
import br.com.vagner.model.GameStatus;
import br.com.vagner.model.Player;
import br.com.vagner.model.TicTacToe;
import br.com.vagner.storage.GameStorage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameService {

	public Game createGame(Player player) {
		Game game = new Game();
		game.setBoard(new int[3][3]);
		game.setGameId(UUID.randomUUID().toString());
		game.setPlayer1(player);
		game.setStatus(GameStatus.NEW);
		GameStorage.getInstance().setGame(game);
		return game;
	}
	
	public Game connectToGame(Player player2,String gameId) throws InvalidParamException, InvalidGameException {
		GameStorage gameStorage = GameStorage.getInstance();
		Game game = gameStorage.getGames().get(gameId);
		if(game == null) {
			throw new InvalidParamException("The provided gameID doesn't exist.");
		}
		
		if(game.getPlayer2() != null) {
			throw new InvalidGameException("Game is not valid anymore!");
		}
		
		if(player2 == null || !StringUtils.hasText(player2.getName())) {
			throw new InvalidGameException("You need to provide a player.");
		}
		
		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		gameStorage.setGame(game);
		return game;
	}
	
	public Game connectToRandomGame(Player player2) throws NotFoundException {
		GameStorage gameStorage = GameStorage.getInstance();
		Game game = gameStorage.getGames().values().stream()
			.filter(g -> g.getStatus().equals(GameStatus.NEW))
			.findFirst().orElseThrow(() -> new NotFoundException("Game not found."));
		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		gameStorage.setGame(game);
		return game;
	}
	
	public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
		Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
		if(game == null) {
			throw new NotFoundException("Game not found!");
		}
		
		if(game.getStatus().equals(GameStatus.FINISHED)) {
			throw new InvalidGameException("Game is already finhed.");
		}
		int[][] board = game.getBoard();
		board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();
		
		boolean xWon = checkWinner(game.getBoard(), TicTacToe.X);
		boolean oWon = checkWinner(game.getBoard(), TicTacToe.O);
		
		if(xWon) {
			game.setWinner(TicTacToe.X);
		}else if(oWon) {
			game.setWinner(TicTacToe.O);
		}
		
		GameStorage.getInstance().setGame(game);
		
		return game;
	}

	private boolean checkWinner(int[][] board, TicTacToe ticTacToe) {
		int[] boardArray = new int[9];
		int counterIndex = 0;
		for (int i = 0; i < boardArray.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				boardArray[counterIndex] = board[i][j];
				counterIndex++;
			}
		}
		
		int [][] winCombinations = {{0,1,2},{3,4,5},{6,7,8},
				{0,3,6},{1,4,7},{2,5,8},{2,4,6}};
		for (int i = 0; i < winCombinations.length; i++) {
			int counter = 0;
			for(int j=0; j<winCombinations[i].length;j++) {
				if(boardArray[winCombinations[i][j]] == ticTacToe.getValue()) {
					counter++;
					if(counter == 3) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
}
