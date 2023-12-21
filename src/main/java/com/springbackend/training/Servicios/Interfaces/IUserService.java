package com.springbackend.training.Servicios.Interfaces;

import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Servicios.Base.IServicioBase;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.io.IOException;

public interface IUserService extends IServicioBase<UserDB, Long> {

    public  SpotifyApi getProfile();

    public void savePlaylists(Paging<PlaylistSimplified> userPlaylist, SpotifyApi spotifyApiArmada) throws IOException, ParseException, SpotifyWebApiException;
}
