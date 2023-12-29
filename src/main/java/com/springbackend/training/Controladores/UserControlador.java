package com.springbackend.training.Controladores;


import com.springbackend.training.Controladores.Base.ControladorBase;
import com.springbackend.training.Controladores.Response.TrackResponse;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.UserServicio;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserControlador extends ControladorBase<UserDB, UserServicio> {

    private final UserServicio userServicio;
    private static SpotifyApi spotifyApi;

    @Autowired
    public void setSpotifyApi(UserServicio userServicio) {
        UserControlador.spotifyApi = userServicio.getProfile();
    }


    @GetMapping("login")
    @ResponseBody
    public RedirectView spotifyLogin() {
        AuthorizationCodeUriRequest authCodeURIRequest = spotifyApi
                .authorizationCodeUri()
                .scope("user-read-private, user-read-email")
                .show_dialog(true)
                .build();
        final URI uri = authCodeURIRequest.execute();

        return new RedirectView(uri.toString());
    }

    @GetMapping("/success")
    public String getAccessToken(@RequestParam("code") String code) throws IOException, ParseException, SpotifyWebApiException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();
        String accessToken = credentials.getAccessToken();
        return "{" + accessToken + "}";
    }

    @GetMapping("/readMyPlaylist")
    public Page<TrackResponse> savePlaylist(@RequestParam("jwt") String accessToken, @PageableDefault(size = 10) Pageable pageable) throws IOException, ParseException, SpotifyWebApiException {
        try {
            spotifyApi.setAccessToken(accessToken);
            return userServicio.getMyPlaylist(spotifyApi, pageable);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/savePlaylists")
    public Page<TrackResponse> getUserPlaylists(@RequestParam("jwt") String accessToken, @PageableDefault Pageable pageable) throws IOException, ParseException, SpotifyWebApiException {
        try {
            spotifyApi.setAccessToken(accessToken);
            return userServicio.savePlaylists(spotifyApi,pageable);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
