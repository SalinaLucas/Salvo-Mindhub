package com.mindhubweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping("/api")
public class SalvoRestController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;



    @GetMapping("/games")
    public Map<String, Object> getGamesId(Authentication authentication) {
        /*return
                gameRepository.findAll().stream()
                        .map(game -> makeGameDTO(game))
                        .collect(toList());*/

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

            if (isGuest(authentication)) {
                dto.put("player", "GUEST");
            } else {
                dto.put("player", makePlayerDTO(playerRepository.findByUserName(authentication.getName())));
            }

            dto.put("games", gameRepository.findAll().stream()
                    .map(this::makeGameDTO).collect(toList()));
            return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame (Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        Game newGame = new Game( LocalDateTime.now());
        gameRepository.save(newGame);

        GamePlayer newGamePlayer = new GamePlayer(newGame, player);
        gamePlayerRepository.save(newGamePlayer);

        return new ResponseEntity<>(makeMap("ok", makeMap("gpid", newGamePlayer.getId())), HttpStatus.CREATED);

    }

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication){
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
        }
        Optional<Game> game = gameRepository.findById(gameId);
        if(!game.isPresent()){
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }
        if(game.get().getGamePlayers().size() > 1){
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(game.get(), player));

        return new ResponseEntity<>(makeMap("ok", makeMap("gpid", newGamePlayer.getId())), HttpStatus.CREATED);
    }


    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> shipList(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Set<Ship> ships) {
        if(isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        if(!authentication.getName().equals(gamePlayer.get().getPlayer().getUserName())) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.get().getShips().size() > 0 || ships.size() != 5) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }


        gamePlayer.get().addShips(ships);
        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>(makeMap(Messages.KEY_STATUS, Messages.MSG_STATUS_CREATED), HttpStatus.CREATED);

    }


    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> salvosList(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo salvoes) {
        if(isGuest(authentication)) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        if(!authentication.getName().equals(gamePlayer.get().getPlayer().getUserName())) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.get().getSalvoes().stream().anyMatch(item -> item.getTurn() == salvoes.getTurn())) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }


        Set<Salvo> salvoSet = new HashSet<>();
        salvoSet.add(salvoes);
        gamePlayer.get().addSalvoes(salvoes);
        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>(makeMap(Messages.KEY_STATUS, Messages.MSG_STATUS_CREATED), HttpStatus.CREATED);
    }


    @GetMapping ("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamePlayerView(@PathVariable Long gamePlayerId, Authentication authentication) {
        GamePlayer gpOwnerPlayer = gamePlayerRepository.findById(gamePlayerId).get();
        if (!authentication.getName().equals(gpOwnerPlayer.getPlayer().getUserName())) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }
        return
        new ResponseEntity<>(makeViewDTO(gpOwnerPlayer), HttpStatus.OK);

    }


    @PostMapping(path = "/players")
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        Player player = playerRepository.findByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_CONFLICT), HttpStatus.CONFLICT);
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
        else
            dto.put("points", null);
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

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

}


