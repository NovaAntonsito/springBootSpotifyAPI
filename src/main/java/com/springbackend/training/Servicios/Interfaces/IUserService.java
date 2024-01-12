package com.springbackend.training.Servicios.Interfaces;

import com.springbackend.training.Controladores.Response.TrackResponse;
import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.Base.IServicioBase;
import org.apache.hc.core5.http.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IUserService extends IServicioBase<UserDB, Long> {

     SpotifyApi getProfile();

     List<Map<String, String>> getPlaylistIDFromUser (SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException;

     void savePlaylistToUser (List<PlaylistTrack> playlistFromUser, SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException;

     List<PlaylistTrack> getSpotifyPlaylist (SpotifyApi spotifyApi, String playlistID) throws IOException, ParseException, SpotifyWebApiException;

     Page<SongsDB> getUserPlayListFromDB(Long id, Pageable pageable);


}
