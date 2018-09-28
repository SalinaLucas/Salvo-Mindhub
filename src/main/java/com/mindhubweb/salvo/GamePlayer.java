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
public class GamePlayer {
//Porpiedades y relaciones
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;
    //private Player player2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Salvo> salvoes = new HashSet<>();

//Constructor nulo
    public GamePlayer() {
    }

//Constructor de GamePlayer con parámetros necesarios
    public GamePlayer(Game game, Player player, Set<Ship> ships, Set<Salvo> salvoes) /*Player player2)*/ {
        this.game = game;
        this.player = player;
        this.addShips(ships);
        this.addSalvoes(salvoes);
        //this.player2 = player2;
    }

    public GamePlayer(Game game, Player player) /*Player player2)*/ {
        this.game = game;
        this.player = player;
    }

    public GamePlayer(Game game, Player player, Set<Ship> ships) /*Player player2)*/ {
        this.game = game;
        this.player = player;
        this.addShips(ships);
    }

//Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer(LocalDateTime localDateTime) {
        this.creationDate = localDateTime;

    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

//Adds necesarios
    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addShips(Set<Ship> ships){
        ships.stream().forEach(ship -> {
            ship.setGamePlayer(this);
            this.ships.add(ship);
        });
    }

    public void addSalvoes(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public void addSalvoes(Set<Salvo> salvoes){
            salvoes.stream().forEach(salvo -> {
            salvo.setGamePlayer(this);
            this.salvoes.add(salvo);
        });
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
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

    public Score getScore() {
        return this.player.getScore(this.game);
    }

}

