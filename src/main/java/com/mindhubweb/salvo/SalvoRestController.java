package com.mindhubweb.salvo;

//Importes necesarios
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.xml.soap.SAAJMetaFactory;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

//Controlador para métodos GET, POST y determinar dinámicas del juego
@RestController
@RequestMapping("/api")
public class SalvoRestController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;


//Map a juegos para obtener una lista de ellos y determinar si el jugador está logueado o no
    @GetMapping("/games")
    public Map<String, Object> getGamesId(Authentication authentication) {


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

//Crear un juego nuevo
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

//Unirse a un juego
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


//Obtener el set de barcos para un jugador
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


//Obtener el set de tiros para un jugador, determinar turnos y obtener los puntajes
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
        Enum<GameState> gameState = getGameState(gamePlayer.get());
        if ( (gameState == GameState.WIN) || (gameState == GameState.LOSE) || (gameState == GameState.DRAW)) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }

        salvoes.setTurn(gamePlayer.get().getSalvoes().size()+1);
        GamePlayer opponent = gamePlayer.get().getGame().getGamePlayers().stream().filter(gamePlayerItem -> gamePlayerItem.getId() != gamePlayer.get().getId()).findAny().orElse(null);
        if (opponent == null) {
            if (gamePlayer.get().getSalvoes().size() > 0) {
                return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
            }
        } else {
            if (Math.abs(opponent.getSalvoes().size() - salvoes.getTurn()) > 1) {
                return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
            }
            Salvo opponentMaxSalvo = opponent.getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
            if (salvoes.getCells().size() != shotsQuantity(gamePlayer.get()) &&  opponentMaxSalvo.getTurn() <= (salvoes.getTurn()) ) {
                return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
            }
        }

        Set<Salvo> salvoSet = new HashSet<>();
        salvoSet.add(salvoes);
        gamePlayer.get().addSalvoes(salvoes);
        GamePlayer saved = gamePlayerRepository.save(gamePlayer.get());
        if (saved != null && opponent != null) {
            Salvo opponentMaxSalvo = opponent.getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
            if(opponentMaxSalvo != null && opponentMaxSalvo.getTurn() == salvoes.getTurn()) {
                if (this.getGameState(gamePlayer.get()) == GameState.WIN) {
                    scoreRepository.save(new Score(gamePlayer.get().getGame(), gamePlayer.get().getPlayer(), LocalDateTime.now(), 1.0F));
                    scoreRepository.save(new Score(opponent.getGame(), opponent.getPlayer(), LocalDateTime.now(), 0.0F));
                } else if (this.getGameState(gamePlayer.get()) == GameState.LOSE) {
                    scoreRepository.save(new Score(gamePlayer.get().getGame(), gamePlayer.get().getPlayer(), LocalDateTime.now(), 0.0F));
                    scoreRepository.save(new Score(opponent.getGame(), opponent.getPlayer(), LocalDateTime.now(), 1.0F));
                } else if (this.getGameState(gamePlayer.get()) == GameState.DRAW) {
                    scoreRepository.save(new Score(gamePlayer.get().getGame(), gamePlayer.get().getPlayer(), LocalDateTime.now(), 0.5F));
                    scoreRepository.save(new Score(opponent.getGame(), opponent.getPlayer(), LocalDateTime.now(), 0.5F));
                }
            }
        }

        return new ResponseEntity<>(makeMap(Messages.KEY_STATUS, Messages.MSG_STATUS_CREATED), HttpStatus.CREATED);
    }


//Game view informativo de cada jugador
    @GetMapping ("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamePlayerView(@PathVariable Long gamePlayerId, Authentication authentication) {
        GamePlayer gpOwnerPlayer = gamePlayerRepository.findById(gamePlayerId).get();
        if (!authentication.getName().equals(gpOwnerPlayer.getPlayer().getUserName())) {
            return new ResponseEntity<>(makeMap(Messages.KEY_ERROR, Messages.MSG_ERROR_FORBIDDEN), HttpStatus.FORBIDDEN);
        }
        return
        new ResponseEntity<>(makeViewDTO(gpOwnerPlayer), HttpStatus.OK);

    }


//Crear un jugador
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


//DTO de juegos
    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::makeGamePlayersDTO));
        return dto;
    }

//DTO de gameplayer
    private Map<String, Object> makeGamePlayersDTO (GamePlayer gamePlayer) {
        Map <String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("gameState", getGameState(gamePlayer));
        dto.put("shots", shotsQuantity(gamePlayer));
        if (gamePlayer.getScore() != null)
            dto.put("points", gamePlayer.getScore().getPoints());
        else
            dto.put("points", null);
        return dto;
    }

//DTO de Game View informativo
    private Map<String, Object> makeViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(this::makeGamePlayersDTO));
        dto.put("ships", gamePlayer.getShips().stream().map(this::makeShipsDTO));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(item -> item.getSalvoes().stream().map(this::makeSalvoesDTO)));
        return dto;
    }

//DTO de jugador
    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("username", player.getUserName());
        return dto;
    }

//DTO de barcos
    private Map<String, Object> makeShipsDTO (Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getShipType());
        dto.put("locations", ship.getCells());
        return dto;
    }

//DTO de tiros
    private Map<String, Object> makeSalvoesDTO (Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        GamePlayer opponent = salvo.getGamePlayer().getGame().getGamePlayers().stream().filter(gamePlayerItem -> gamePlayerItem.getId() != salvo.getGamePlayer().getId()).findAny().orElse(null);

        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getCells());
        dto.put("hits", getHits(salvo, opponent.getShips()));
        dto.put("sinks", getSinks(salvo, opponent.getShips()));
        return dto;
    }

//DTO de puntos
    private Map<String, Object> makeScoresDTO (Score score) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("finished", score.getFinishDate());
        dto.put("points", score.getPoints());
        return dto;
    }


//Método para utilizar luego la autenticación
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


//Método para obtener golpeados
    public List<String> getHits (Salvo salvo, Set<Ship> ships) {

        List<String> hits = new ArrayList<>();

        for (Ship ship : ships) {
            for (String location : salvo.getCells()) {
                if (ship.getCells().indexOf(location) != -1) {
                    hits.add(location);
                }
            }
        }

        return hits;
    }


//Método para obtener hundidos
    public List<String> getSinks (Salvo salvo, Set<Ship> ships) {

        List<String> sinks = new ArrayList<>();
        if(salvo != null) {
        for (Ship ship : ships) {
            List<String> saveSalvos = new ArrayList<>();
            List<Salvo> salvos = salvo.getGamePlayer().getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(toList());
            for (Salvo salvoItem : salvos) {
                for (String cell : salvoItem.getCells()) {
                    if (ship.getCells().indexOf(cell) != -1) {
                        saveSalvos.add(cell);
                    }
                }
                if (saveSalvos.size() == ship.getCells().size() && salvoItem.getTurn() <= salvo.getTurn()) {
                    sinks.add(ship.getShipType());
                    break;
                }
            }
        }
        }
        return sinks;
    }


//Método para determinar los estados de juego
    public Enum<GameState> getGameState (GamePlayer gamePlayer) {
        Enum<GameState> gameStateEnum = GameState.UNDEFINED;
        Optional<GamePlayer> opponent = gamePlayer.getGame().getGamePlayers().stream().filter(gamePlayerItem -> gamePlayerItem.getId() != gamePlayer.getId()).findFirst();
        if (!opponent.isPresent()) {
            gameStateEnum = GameState.WAIT_OPPONENT_JOIN;
        } else {
            if (gamePlayer.getShips().isEmpty())
                gameStateEnum = GameState.PLACE_SHIPS;
            else if (opponent.get().getShips().isEmpty())
                gameStateEnum = GameState.WAIT_OPPONENT_SHIPS;
            else {
                int myTurn = gamePlayer.getSalvoes().stream().mapToInt(Salvo::getTurn).max().orElse(0);
                int opponentTurn = opponent.get().getSalvoes().stream().mapToInt(Salvo::getTurn).max().orElse(0);
                if (gamePlayer.getId() < opponent.get().getId() && myTurn == opponentTurn)
                    gameStateEnum = GameState.PLACE_SALVOS;
                else if (gamePlayer.getId() < opponent.get().getId() && myTurn > opponentTurn)
                    gameStateEnum = GameState.WAIT_OPPONENT_SALVOS;
                else if (gamePlayer.getId() > opponent.get().getId() && myTurn < opponentTurn)
                    gameStateEnum = GameState.PLACE_SALVOS;
                else if (gamePlayer.getId() > opponent.get().getId() && myTurn == opponentTurn)
                    gameStateEnum = GameState.WAIT_OPPONENT_SALVOS;
                Salvo maxSalvo = gamePlayer.getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
                Salvo opponentMaxSalvo = opponent.get().getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
                int sinksPlayer  = getSinks(maxSalvo, opponent.get().getShips()).size();
                int sinksOpponent= getSinks(opponentMaxSalvo, gamePlayer.getShips()).size();
                if (sinksPlayer== 5 && sinksOpponent== 5 && gamePlayer.getSalvoes().size() == opponent.get().getSalvoes().size())
                    gameStateEnum = GameState.DRAW;
                else if (sinksPlayer == 5 && sinksOpponent < 5 && gamePlayer.getSalvoes().size() == opponent.get().getSalvoes().size())
                    gameStateEnum = GameState.WIN;
                else if (sinksOpponent == 5 && sinksPlayer < 5 && gamePlayer.getSalvoes().size() == opponent.get().getSalvoes().size())
                    gameStateEnum = GameState.LOSE;


            }
        }
        return gameStateEnum;
    }


//Método para determinar cantidad de tiros según cantidad de barcos que queden
    public int shotsQuantity (GamePlayer gamePlayer) {
        int shots = 5;
        Optional <GamePlayer> opponentGamePlayer = gamePlayer.getGame().getGamePlayers().stream().filter(gamePlayer1 -> gamePlayer1.getId() != gamePlayer.getId()).findFirst();
        Salvo opponentMaxSalvo = null;
        Salvo maxSalvo = gamePlayer.getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
        if (opponentGamePlayer.isPresent() && maxSalvo != null) {
            opponentMaxSalvo = opponentGamePlayer.get().getSalvoes().stream().filter(salvoItem -> salvoItem.getTurn() == maxSalvo.getTurn()).findFirst().orElse(null);
            int aver = getSinks(opponentMaxSalvo, gamePlayer.getShips()).size();
            if(opponentMaxSalvo!=null)
                shots = shots - getSinks(opponentMaxSalvo, gamePlayer.getShips()).size();
        }
        return shots;
    }

}


