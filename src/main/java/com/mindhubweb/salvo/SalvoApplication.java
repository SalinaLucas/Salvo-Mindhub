package com.mindhubweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
public class SalvoApplication {


	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

		return (args) -> {
			// save a couple of players
			/*Player player0 = new Player("N/A");
			playerRepository.save(player0);*/

			Player player1 = new Player("j.bauer@ctu.gov", "24");
			playerRepository.save(player1);

            Player player2 = new Player("c.obrian@ctu.gov", "42");
			playerRepository.save(player2);

			Player player3 = new Player("kim_bauer@gmail.com", "kb");
			playerRepository.save(player3);

			Player player4 = new Player("t.almeida@ctu.gov", "mole");
			playerRepository.save(player4);




			Game game1 = new Game( LocalDateTime.now());
			gameRepository.save(game1);

			Game game2 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(60)));
			gameRepository.save(game2);

			Game game3 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(120)));
			gameRepository.save(game3);

			Game game4 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(180)));
			gameRepository.save(game4);

			Game game5 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(240)));
			gameRepository.save(game5);

			Game game6 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(300)));
			gameRepository.save(game6);

			Game game7 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(360)));
			gameRepository.save(game7);

			Game game8 = new Game(LocalDateTime.from(LocalDateTime.now().plusMinutes(420)));
			gameRepository.save(game8);



			Set<Ship> setOne = new HashSet<>();
			setOne.add(new Ship("carrier", new ArrayList<>(Arrays.asList("H2", "H3", "H4", "H5", "H6"))));
			setOne.add(new Ship("battleship", new ArrayList<>(Arrays.asList("A2", "A3", "A4", "A5"))));
			setOne.add(new Ship("submarine", new ArrayList<>(Arrays.asList("E1", "F1", "G1"))));
			setOne.add(new Ship("destroyer", new ArrayList<>(Arrays.asList("B5", "C5", "D5"))));
			setOne.add(new Ship("patrol", new ArrayList<>(Arrays.asList("J1", "J2"))));

			Set<Ship> setTwo = new HashSet<>();
			setTwo.add(new Ship("carrier", new ArrayList<>(Arrays.asList("A2", "A3", "A4", "A5", "A6"))));
			setTwo.add(new Ship("battleship", new ArrayList<>(Arrays.asList("H1", "H2", "H3", "H4"))));
			setTwo.add(new Ship("submarine", new ArrayList<>(Arrays.asList("E5", "F5", "G5"))));
			setTwo.add(new Ship("destroyer", new ArrayList<>(Arrays.asList("B9", "C9", "D9"))));
			setTwo.add(new Ship("patrol", new ArrayList<>(Arrays.asList("G1", "G2"))));

			Set<Ship> setThree = new HashSet<>();
			setThree.add(new Ship("carrier", new ArrayList<>(Arrays.asList("C2", "D2", "E2", "F2", "G2"))));
			setThree.add(new Ship("battleship", new ArrayList<>(Arrays.asList("I5", "I6", "I7", "I8"))));
			setThree.add(new Ship("submarine", new ArrayList<>(Arrays.asList("E1", "F1", "G1"))));
			setThree.add(new Ship("destroyer", new ArrayList<>(Arrays.asList("B7", "B8", "B9"))));
			setThree.add(new Ship("patrol", new ArrayList<>(Arrays.asList("G1", "H1"))));

			Set<Ship> setFour = new HashSet<>();
			setFour.add(new Ship("carrier", new ArrayList<>(Arrays.asList("J2", "J3", "J4", "J5", "J6"))));
			setFour.add(new Ship("battleship", new ArrayList<>(Arrays.asList("H1", "H2", "H3", "H4"))));
			setFour.add(new Ship("submarine", new ArrayList<>(Arrays.asList("C5", "C6", "C7"))));
			setFour.add(new Ship("destroyer", new ArrayList<>(Arrays.asList("B9", "C9", "D9"))));
			setFour.add(new Ship("patrol", new ArrayList<>(Arrays.asList("E5", "E6"))));

			Set<Ship> setFive = new HashSet<>();
			setFive.add(new Ship("carrier", new ArrayList<>(Arrays.asList("A2", "B2", "C2", "D2", "E2"))));
			setFive.add(new Ship("battleship", new ArrayList<>(Arrays.asList("J6", "J7", "J8", "J9"))));
			setFive.add(new Ship("submarine", new ArrayList<>(Arrays.asList("H3", "H4", "H5"))));
			setFive.add(new Ship("destroyer", new ArrayList<>(Arrays.asList("B10", "C10", "D10"))));
			setFive.add(new Ship("patrol", new ArrayList<>(Arrays.asList("E8", "G8"))));

			Set<Ship> setSix = new HashSet<>();
			setSix.add(new Ship("carrier", new ArrayList<>(Arrays.asList("D1", "D2", "D3", "D4", "D5"))));
			setSix.add(new Ship("battleship", new ArrayList<>(Arrays.asList("G1", "H1", "I1", "J1"))));
			setSix.add(new Ship("submarine", new ArrayList<>(Arrays.asList("E6", "F6", "G6"))));
			setSix.add(new Ship("destroyer", new ArrayList<>(Arrays.asList("B10", "C10", "D10"))));
			setSix.add(new Ship("patrol", new ArrayList<>(Arrays.asList("G1", "G2"))));

			Set<Salvo> set1 = new HashSet<>();
			set1.add(new Salvo(new ArrayList<>(Arrays.asList("H2", "H3", "H4", "H5", "H6")) , 1));
			set1.add(new Salvo(new ArrayList<>(Arrays.asList("C3", "C4", "C5", "C6")), 2));
			set1.add(new Salvo(new ArrayList<>(Arrays.asList("E1", "F1", "G1")), 3));
			set1.add(new Salvo(new ArrayList<>(Arrays.asList("F5", "F6", "F7")), 4));
			set1.add(new Salvo(new ArrayList<>(Arrays.asList("F1", "F2")), 5));

			Set<Salvo> set2 = new HashSet<>();
			set2.add(new Salvo(new ArrayList<>(Arrays.asList("A2", "A3", "A4", "A5", "A6")), 1));
			set2.add(new Salvo(new ArrayList<>(Arrays.asList("F2", "F3", "F4", "F5")), 2));
			set2.add(new Salvo(new ArrayList<>(Arrays.asList("E5", "F5", "G5")), 3));
			set2.add(new Salvo(new ArrayList<>(Arrays.asList("G9", "H9", "I9")), 4));
			set2.add(new Salvo(new ArrayList<>(Arrays.asList("F1", "F2")), 5));

			Set<Salvo> set3 = new HashSet<>();
			set3.add(new Salvo(new ArrayList<>(Arrays.asList("J1", "J2", "J3", "J4", "J5")) , 1));
			set3.add(new Salvo(new ArrayList<>(Arrays.asList("C3", "C4", "C5", "C6")), 2));
			set3.add(new Salvo(new ArrayList<>(Arrays.asList("C1", "D1", "E1")), 3));
			set3.add(new Salvo(new ArrayList<>(Arrays.asList("B8", "B9", "B10")), 4));
			set3.add(new Salvo(new ArrayList<>(Arrays.asList("E5", "F5")), 5));

			Set<Salvo> set4 = new HashSet<>();
			set4.add(new Salvo(new ArrayList<>(Arrays.asList("H2", "H3", "H4", "H5", "H6")) , 1));
			set4.add(new Salvo(new ArrayList<>(Arrays.asList("I3", "I4", "I5", "I6")), 2));
			set4.add(new Salvo(new ArrayList<>(Arrays.asList("E1", "F1", "G1")), 3));
			set4.add(new Salvo(new ArrayList<>(Arrays.asList("F5", "F6", "F7")), 4));
			set4.add(new Salvo(new ArrayList<>(Arrays.asList("J1", "J2")), 5));

			Set<Salvo> set5 = new HashSet<>();
			set5.add(new Salvo(new ArrayList<>(Arrays.asList("D6", "D7", "D8", "D9", "D10")) , 1));
			set5.add(new Salvo(new ArrayList<>(Arrays.asList("G1", "G2", "G3", "G4")), 2));
			set5.add(new Salvo(new ArrayList<>(Arrays.asList("D1", "E1", "F1")), 3));
			set5.add(new Salvo(new ArrayList<>(Arrays.asList("B8", "B9", "B10")), 4));
			set5.add(new Salvo(new ArrayList<>(Arrays.asList("A1", "A2")), 5));

			Set<Salvo> set6 = new HashSet<>();
			set6.add(new Salvo(new ArrayList<>(Arrays.asList("B2", "B3", "B4", "B5", "B6")) , 1));
			set6.add(new Salvo(new ArrayList<>(Arrays.asList("C3", "C4", "C5", "C6")), 2));
			set6.add(new Salvo(new ArrayList<>(Arrays.asList("G3", "H3", "I3")), 3));
			set6.add(new Salvo(new ArrayList<>(Arrays.asList("C10", "D10", "E10")), 4));
			set6.add(new Salvo(new ArrayList<>(Arrays.asList("E7", "F7")), 5));


			gamePlayerRepository.save(new GamePlayer(game1, player1, setOne, set1));
			gamePlayerRepository.save(new GamePlayer(game1, player2, setTwo, set2));
			gamePlayerRepository.save(new GamePlayer(game2, player3, setThree, set3));
			gamePlayerRepository.save(new GamePlayer(game2, player1, setFour, set4));
			gamePlayerRepository.save(new GamePlayer(game3, player2, setFive, set5));
			gamePlayerRepository.save(new GamePlayer(game3, player3, setSix, set6));
			/*gamePlayerRepository.save(new GamePlayer(game4, player2));
			gamePlayerRepository.save(new GamePlayer(game4, player1));
			gamePlayerRepository.save(new GamePlayer(game5, player4));
			gamePlayerRepository.save(new GamePlayer(game5, player1));
			gamePlayerRepository.save(new GamePlayer(game6, player3));
			gamePlayerRepository.save(new GamePlayer(game6, player0));
			gamePlayerRepository.save(new GamePlayer(game7, player4));
			gamePlayerRepository.save(new GamePlayer(game7, player0));
			gamePlayerRepository.save(new GamePlayer(game8, player3));
			gamePlayerRepository.save(new GamePlayer(game8, player4));*/


			scoreRepository.save(new Score (game1, player1, LocalDateTime.now().plusMinutes(30), 1.0F));
			scoreRepository.save(new Score (game1, player2, LocalDateTime.now().plusMinutes(30), 0.0F));
			scoreRepository.save(new Score (game2, player3, LocalDateTime.now().plusMinutes(30), 0.0F));
			scoreRepository.save(new Score (game2, player1, LocalDateTime.now().plusMinutes(30), 1.0F));
			scoreRepository.save(new Score (game3, player2, LocalDateTime.now().plusMinutes(30), 0.0F));
			scoreRepository.save(new Score (game3, player3, LocalDateTime.now().plusMinutes(30), 1.0F));


		};
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityAuthConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(email -> {
			Player player = playerRepository.findByUserName(email);
			if (player != null) {
				/*if (player.getUserName().equals("t.almeida@ctu.gov")) {
					return new User(player.getUserName(), passwordEncoder().encode(player.getPassword()),
							AuthorityUtils.createAuthorityList("ADMIN"));
				} else {
					return new User(player.getUserName(), passwordEncoder().encode(player.getPassword()),
							AuthorityUtils.createAuthorityList("USER"));
				}*/
				return new User(player.getUserName(), passwordEncoder().encode(player.getPassword()),
						AuthorityUtils.createAuthorityList("USER"));
				} else {
				throw new UsernameNotFoundException("Unknown user: " + email);
			}
		}).passwordEncoder(passwordEncoder());
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
				.antMatchers("/web/game.html").hasAnyAuthority("USER", "ADMIN")
				.antMatchers("/web/scripts/game.js").hasAnyAuthority("USER", "ADMIN")
				.antMatchers("/api/game_view/**").hasAnyAuthority("USER", "ADMIN")
				.antMatchers("/favicon.ico").permitAll()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/**").permitAll();

        		/*.antMatchers("/rest/**").hasAuthority("ADMIN")
               .antMatchers("/web/**").hasAnyAuthority("USER", "ADMIN");*/
        http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
     }
}
