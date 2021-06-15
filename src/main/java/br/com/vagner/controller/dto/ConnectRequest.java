package br.com.vagner.controller.dto;

import br.com.vagner.model.Player;
import lombok.Data;

@Data
public class ConnectRequest {
	
	private Player player;
	private String gameId;
}
