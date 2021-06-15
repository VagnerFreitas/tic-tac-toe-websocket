package br.com.vagner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vagner.controller.dto.ConnectRequest;
import br.com.vagner.exception.InvalidGameException;
import br.com.vagner.exception.InvalidParamException;
import br.com.vagner.exception.NotFoundException;
import br.com.vagner.model.Game;
import br.com.vagner.model.GamePlay;
import br.com.vagner.model.Player;
import br.com.vagner.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

	private final GameService gameService;
	private final SimpMessagingTemplate simpleMessageTemplate;
	
	@PostMapping("/start")
	public ResponseEntity<Game> start(@RequestBody Player player){
		log.info("start game request {}",player);
		return ResponseEntity.ok(gameService.createGame(player));
	}
	
	@PostMapping("/connect")
	public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException{
		log.info("connect request {}", request);
		
		return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
	}
	
	@PostMapping("/connect/random")
	public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws InvalidParamException, InvalidGameException, NotFoundException{
		log.info("connect random {}", player);
		return ResponseEntity.ok(gameService.connectToRandomGame(player));
	}
	
	@PostMapping("/gameplay")
	public ResponseEntity<Game> gamePlay(@RequestBody GamePlay gamePlay) throws NotFoundException, InvalidGameException{
		log.info("gameplay {}",gamePlay);
		Game game = gameService.gamePlay(gamePlay);
		simpleMessageTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(),game);
		return ResponseEntity.ok(game);
	}
}
