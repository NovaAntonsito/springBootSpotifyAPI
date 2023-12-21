package com.springbackend.training.Controladores;


import com.springbackend.training.Controladores.Base.ControladorBase;
import com.springbackend.training.Controladores.DTO.TrackDTO;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.UserServicio;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private UserServicio userServicio;

    private static SpotifyApi spotifyApi;

    @Autowired
    private Environment env;

    private se.michaelthelin.spotify.model_objects.specification.User userSpotify;


    @Autowired
    public void setSpotifyApi(UserServicio userServicio) {
        UserControlador.spotifyApi = userServicio.getProfile();
    }


    @GetMapping("login")
    @ResponseBody
    public RedirectView spotifyLogin() {
        AuthorizationCodeUriRequest authCodeURIRequest = spotifyApi
                .authorizationCodeUri()
                .scope("user-top-read")
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
    public List<TrackDTO> getPlaylist(@RequestParam("jwt") String accessToken) throws IOException, ParseException, SpotifyWebApiException {
        try {
            spotifyApi.setAccessToken(accessToken);
            //TODO: Todo esto se tiene que ir al servicio, que costumbre de mierda de hacer todo en el controlador
            ArrayList<TrackDTO> trackList = new ArrayList<>();
            int index = 0;
            String trackName = "";
            String previewUrl = "";

            User userSpotify = spotifyApi
                    .getCurrentUsersProfile()
                    .build()
                    .execute();

            String mySpotifyID = env.getProperty("spotify.anton.PlaylistID");

            GetListOfUsersPlaylistsRequest userPlayLists = spotifyApi
                    .getListOfUsersPlaylists(mySpotifyID)
                    .build();


            Paging<PlaylistSimplified> userPlaylistPage = userPlayLists.execute();

            String playlistID = userPlaylistPage
                    .getItems()[0]
                    .getId();

            Playlist playlist = spotifyApi
                    .getPlaylist(playlistID)
                    .build()
                    .execute();
            for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {
                Track track = (Track) playlistTrack.getTrack();
                trackName = track.getName();
                previewUrl = track.getPreviewUrl();
                trackList.add(new TrackDTO(index, trackName, previewUrl));
                index++;
            }
            return trackList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SpotifyWebApiException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/readPlaylists")
    public List<TrackDTO> getUserPlaylists(@RequestParam("jwt") String accessToken) throws IOException, ParseException, SpotifyWebApiException {
        try {
            //TODO: Todo esto se tiene que ir al servicio, que costumbre de mierda de hacer todo en el controlador
            spotifyApi.setAccessToken(accessToken);
            User userSpotify = spotifyApi
                    .getCurrentUsersProfile()
                    .build()
                    .execute();
            int index = 0;
            String trackName = "";
            String previewUrl = "";
            ArrayList<TrackDTO> trackList = new ArrayList<>();

            String userID = userSpotify.getId();

            GetListOfUsersPlaylistsRequest usersPlaylistRequest = spotifyApi
                    .getListOfUsersPlaylists(userID)
                    .build();
            Paging<PlaylistSimplified> userPlaylists = usersPlaylistRequest.execute();
            userServicio.savePlaylists(userPlaylists, spotifyApi);
            Playlist playlist = spotifyApi
                    .getPlaylist(userID)
                    .build()
                    .execute();
            for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {
                Track track = (Track) playlistTrack.getTrack();
                trackName = track.getName();
                previewUrl = track.getPreviewUrl();
                trackList.add(new TrackDTO(index, trackName, previewUrl));
                index++;
            }
            return trackList;
        } catch (IOException | SpotifyWebApiException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
