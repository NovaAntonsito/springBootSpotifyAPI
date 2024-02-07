package com.springbackend.training.Controladores;


import com.springbackend.training.Config.SlackErrorConfig;
import com.springbackend.training.Controladores.Base.ControladorBase;
import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.UserServicio;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserControlador extends ControladorBase<UserDB, UserServicio> {

    private final UserServicio userServicio;
    private static SpotifyApi spotifyApi;

    private final SlackErrorConfig slackErrorConfig;

    @Autowired
    public void setSpotifyApi(UserServicio userServicio) {
        UserControlador.spotifyApi = userServicio.getProfile();
    }


    @GetMapping("login")
    public RedirectView spotifyLogin() {
        AuthorizationCodeUriRequest authCodeURIRequest = spotifyApi
                .authorizationCodeUri()
                .scope("user-read-private, user-read-email, streaming, user-read-playback-state, user-modify-playback-state")
                .show_dialog(true)
                .build();
        final URI uri = authCodeURIRequest.execute();

        return new RedirectView(uri.toString());
    }

    @GetMapping("/success")
    public ResponseEntity<?> getAccessToken(@RequestParam("code") String code) throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();
        String accessToken = credentials.getAccessToken();
        spotifyApi.setAccessToken(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", false , "body" , "El token fue aceptado"));
    }



    @GetMapping("/playlists")
    public List<Map<String, String>> getPlaylistID (){
        try {
            return userServicio.getPlaylistIDFromUser(spotifyApi);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/getSongFromSpotify")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getSongsfromSpotify (@RequestParam("id") String playlistID){
        try {
            userServicio.savePlaylistToUser(userServicio.getSpotifyPlaylist(spotifyApi, playlistID), spotifyApi);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", true , "body" , "Se guardaron las canciones en el usuario"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", false , "body" , "Hubo un error en el guardado de canciones"));
        }
    }

    @GetMapping("/songs")
    public ResponseEntity<?> getPlaylistDB (@RequestParam("id") Long id, @PageableDefault Pageable pageable){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userServicio.getUserPlayListFromDB(id, pageable));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", false , "body" , e.getMessage()));
        }
    }

    @GetMapping("/development")
    public ResponseEntity<?> getAccessToken(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success" , true, "body", spotifyApi.getAccessToken()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", false , "body" , e.getMessage()));
        }
    }




}
