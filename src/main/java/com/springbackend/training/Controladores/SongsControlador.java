package com.springbackend.training.Controladores;


import com.springbackend.training.Config.SlackErrorConfig;
import com.springbackend.training.Controladores.Base.ControladorBase;


import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.Response.TrackResponse;
import com.springbackend.training.Servicios.UserServicio;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;

import org.jmusixmatch.MusixMatchException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/song")
@RequiredArgsConstructor
@Slf4j
public class SongsControlador extends ControladorBase<UserDB, UserServicio> {

    private final UserServicio userServicio;
    private static SpotifyApi spotifyApi;

    private final SlackErrorConfig slackErrorConfig;


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
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", true , "body" , accessToken));
    }



    @GetMapping("/playlists")
    public List<Map<String, String>> getPlaylistID(@RequestParam("accessToken") String accessToken)
            throws IOException, ParseException, SpotifyWebApiException, MissingServletRequestParameterException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new MissingServletRequestParameterException("accessToken", "String");
        }
        return userServicio.getPlaylistIDFromUser(accessToken);
    }

    @GetMapping("/getSongFromSpotify")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getSongsfromSpotify (@RequestParam("id") String playlistID, @RequestParam("accessToken") String token) throws IOException, ParseException, SpotifyWebApiException, MissingServletRequestParameterException{
      if(token == null || token.isEmpty()) {
          throw new MissingServletRequestParameterException("accessToken", "String");
      }
        userServicio.savePlaylistToUser(userServicio.getSpotifyPlaylist(token, playlistID), token);
      return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", true , "body" , "Los items fueron guardado"));
    }

    @GetMapping("/songs")
    public ResponseEntity<?> getPlaylistDB (@RequestParam("id") Long id, @PageableDefault Pageable pageable){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userServicio.getUserPlayListFromDB(id, pageable));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", false , "body" , e.getMessage()));
        }
    }

    @GetMapping("/actualPlaying")
    public ResponseEntity<?> getActualPlaying(@RequestParam("accessToken") String token) throws IOException, ParseException, SpotifyWebApiException, MissingServletRequestParameterException, MusixMatchException {
        if(token == null || token.isEmpty()) {
            throw new MissingServletRequestParameterException("accessToken", "String");
        }
        TrackResponse track = userServicio.getCurrentSongPlaying(token);

        return ResponseEntity.status(HttpStatus.OK).body(track);
    }

}
