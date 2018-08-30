var stats = {
    "positioning": []
}

//Función JQuery para traer los datos
function getDta() {
 stats.positioning = [];
$.get("/api/games").done(function(data){
    app.games = data.games;
    app.player = data.player;
    createStatsObject();
    change();
})
}
$(getDta());

//Creación del objeto JSON dinámico
function createStatsObject() {
  for (i in app.games) {
    for (j in app.games[i].gamePlayers) {
      var examine = stats.positioning.find(function(player){ return player.id == app.games[i].gamePlayers[j].player.id });
        if (examine == undefined) {
          var statsObject = new Object();
          statsObject.username = app.games[i].gamePlayers[j].player.username;
          statsObject.id = app.games[i].gamePlayers[j].player.id;
          statsObject.points = 0;
          statsObject.win = 0;
          statsObject.lose = 0;
          statsObject.tie = 0;
          stats.positioning.push(statsObject);
   }
          evaluate(app.games[i].gamePlayers[j].points, app.games[i].gamePlayers[j].player.id);
  }
 }
}


//Función que realiza el cálculo de la tabla de puntos
function evaluate (score, playerId) {
    stats.positioning.map(function(positioning) {
        if (playerId == positioning.id) {
            positioning.points += score;
            if (score == 1.0) {
               positioning.win += 1
            }
            else if (score == 0.0) {
               positioning.lose += 1
            }
            else if (score == 0.5) {
               positioning.tie += 1
            }
  }
 })
}

//Obtención de datos con VUE
var app = new Vue ({
	el: '#app',
	data: {
        games: [],
        stats: stats,
        player: []
}});

//Log in
function logIn () {
     if(correctEmailFormat($('#user').val())) { $.post("/api/login", { username: $('#user').val(), password: $('#password').val() })
         .done(function(data) {
              $("#msg").html("");
              getDta()
              console.log('logged in!');
         })
         .fail(function() {
              $("#msg").html("Wrong user or password").addClass("spanColor");
         })
     } else if ((!$("#user").val()) || !$("#password").val()) {
            $("#msg").html("Please enter an user and password").addClass("spanColor");
     }
     else {
        $("#msg").html("Wrong email").addClass("spanColor");
     }
}

$("#btn").click(logIn);

//Log out
$("#btn-logout").click(function(){
  $.post("/api/logout")
        .done(function(data) {
            getDta();
            console.log('logged out!');
        })
})

//Ocultar Log in o Log out
function change() {
    if (app.player == 'GUEST') {
        $('#logForm').removeClass('hide');
        $('#btn-logout').addClass('hide');

    } else {
        $('#btn-logout').removeClass('hide');
        $('#logForm').addClass('hide');
    }
}


/*function change (a,b,c,d) {

	$(a).toggle();
	$(b).toggle();
	$(c).toggle();
    $(d).toggle();

}*/


//Sign up
$("#btn-sign-up").click(function() {


    if (correctEmailFormat($('#user').val())) { $.post("/api/players", { username: $('#user').val(), password: $('#password').val() })
            .done(function(data) {
                  logIn()
                  $("#msg").html("");
                  console.log('Signed up!');
            })
            .fail(function() {
                 $("#msg").html("Sign up fail").addClass("spanColor");
                 //alert('Sign up fail');
            })
    } else if ((!$("#user").val()) || !$("#password").val()) {
                  $("#msg").html("Please enter an user and password").addClass("spanColor");
    }  else {
               $("#msg").html("Wrong email").addClass("spanColor");
    }

})

//Email necesario en el formulario
function correctEmailFormat(email){
    var RegExp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/

    return RegExp.test(email);
}
