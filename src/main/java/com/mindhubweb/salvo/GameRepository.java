package com.mindhubweb.salvo;


//Importes necesarios
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//Repositorio de juego
@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {

}