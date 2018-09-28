package com.mindhubweb.salvo;

//Importes necesarios
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


//Entidad
@Entity
public class Score {
//Propiedades y relaciones
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private float points;
    private LocalDateTime finishDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;


//Constructor nulo
    public Score() {
    }

//Constructor de puntos
    public Score(Game game, Player player, LocalDateTime localDateTime, float points)  {
        this.game = game;
        game.addScore(this);
        this.player = player;
        player.addScore(this);
        this.finishDate = localDateTime;
        this.points = points;
    }

//Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


}

