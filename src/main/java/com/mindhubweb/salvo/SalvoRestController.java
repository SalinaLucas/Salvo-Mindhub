package com.mindhubweb.salvo;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping("/api")
public class SalvoRestController {

    @Autowired
    private GameRepository repository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;


    @RequestMapping("/games")
    public Map<String, Object> getGamesId(Authentication authentication) {
        /*return
                repository.findAll().stream()
                        .map(game -> makeGameDTO(game))
                        .collect(toList());*/

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                dto.put("player", "GUEST");
            } else {
                dto.put("player", makePlayerDTO(playerRepository.findByUserName(authentication.getName())));
            }

            dto.put("games", repository.findAll().stream()
                    .map(this::makeGameDTO).collect(toList()));
            return dto;
    }


    @RequestMapping ("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamePlayerView(@PathVariable Long gamePlayerId, Authentication authentication) {
        if (authentication != null && gamePlayerId == ) {
            return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        } else {
        return
        new ResponseEntity<>(makeViewDTO(gamePlayerRepository.findById(gamePlayerId).get()));
        }
    }


    @PostMapping(path = "/players")
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.BAD_REQUEST);
        }
        Player player = playerRepository.findByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = playerRepository.save(new Player(username, password));
        return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::makeGamePlayersDTO));
        /*Set<GamePlayer> gps = game.getGamePlayers();
        for(GamePlayer gp : gps){
            dto.put("ships", gp.getShips().stream().map(this::makeShipsDTO));
        }*/
        //dto.put("ships", game.getGamePlayers().stream().map(this::makeShipsDTO));
        return dto;
    }

    private Map<String, Object> makeGamePlayersDTO (GamePlayer gamePlayer) {
        Map <String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        if (gamePlayer.getScore() != null)
            dto.put("points", gamePlayer.getScore().getPoints());
        return dto;
    }

    private Map<String, Object> makeViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(this::makeGamePlayersDTO));
        dto.put("ships", gamePlayer.getShips().stream().map(this::makeShipsDTO));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(item -> item.getSalvoes().stream().map(this::makeSalvoesDTO)));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("username", player.getUserName());
        return dto;
    }

    private Map<String, Object> makeShipsDTO (Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getShipType());
        dto.put("locations", ship.getCells());
        return dto;
    }

    private Map<String, Object> makeSalvoesDTO (Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getCells());
        return dto;
    }

    private Map<String, Object> makeScoresDTO (Score score) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("finished", score.getFinishDate());
        dto.put("points", score.getPoints());
        return dto;
    }

}


