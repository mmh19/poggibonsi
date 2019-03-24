package it.croccio.poggibonsi.core.restclient

import it.croccio.poggibonsi.model.CreateMatch
import it.croccio.poggibonsi.model.Match
import it.croccio.poggibonsi.model.User
import retrofit2.Call
import retrofit2.http.*


interface RestService {

    @POST("signup")
    fun signup(@Body signup: User): Call<User>

    @GET("api/players")
    fun players(): Call<List<User>>

    @GET("/api/players/{playerId}")
    fun playerById(@Path("playerId") playerId: String): Call<User>

    @GET("api/matches/pending")
    fun pendingMatch(): Call<Match>

    @GET("api/matches/active")
    fun currentMatch(): Call<Match>

    @GET("api/matches")
    fun allMatches(): Call<List<Match>>

    @POST("api/matches")
    fun createMatch(@Body createMatch: CreateMatch): Call<Match>

    @PUT("api/players/{playerId}")
    fun modifyPlayer(@Body user: User, @Path("playerId") playerId: String): Call<User>

    @PUT("api/matches/{matchId}")
    fun modifyMatch(@Body match: Match, @Path("matchId") matchId: String): Call<Match>

}