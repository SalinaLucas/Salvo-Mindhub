package com.mindhubweb.salvo;


//Importes necesarios
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//Repositorio de jugador
@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByUserName(String userName);
}
