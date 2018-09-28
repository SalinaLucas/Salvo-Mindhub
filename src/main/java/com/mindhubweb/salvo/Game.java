package com.mindhubweb.salvo;



//Importes necesarios
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import javax.persistence.CascadeType;
import static java.util.stream.Collectors.toList;


//Entidad
@Entity
public class Game {
//Propiedades y relaciones
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scores = new HashSet<>();

//Constructor nulo
    public Game() { }


//Constructor del juego
    public Game(LocalDateTime localDateTime) {
        this.creationDate = localDateTime;

    }

//Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate (LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }


//Adds necesarios
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }


    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void addScore(Score score) {
        score.setGame(this);
        this.scores.add(score);
    }


    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }



}
