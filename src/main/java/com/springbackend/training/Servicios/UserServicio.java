package com.springbackend.training.Servicios;

import com.springbackend.training.Entidades.SongsDB;
import com.springbackend.training.Entidades.UserDB;
import com.springbackend.training.Repositorios.Base.RepositorioBase;
import com.springbackend.training.Repositorios.RepositorioSongs;
import com.springbackend.training.Repositorios.RepositorioUser;
import com.springbackend.training.Servicios.Base.ServicioBase;
import com.springbackend.training.Servicios.Interfaces.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;


@Service
@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
public class UserServicio extends ServicioBase<UserDB, Long> implements IUserService {

    @Autowired
    private RepositorioUser personaRepository;

    @Autowired
    private RepositorioSongs cancionesRepository;

    @Autowired
    private Environment env;


    public UserServicio(RepositorioBase<UserDB, Long> baseRepository, RepositorioUser personaRepository) {
        super(baseRepository);
    }

    @Override
    public SpotifyApi getProfile() {
        URI redirectedURL = SpotifyHttpManager.makeUri(env.getProperty("spotify.api.redirectURI"));
        return new SpotifyApi
                .Builder()
                .setClientId(env.getProperty("spotify.api.clientID"))
                .setClientSecret(env.getProperty("spotify.api.secretKey"))
                .setRedirectUri(redirectedURL)
                .build();
    }

    @Override
    public void savePlaylists(Paging<PlaylistSimplified> userPlaylists, SpotifyApi spotifyApiArmada) throws IOException, ParseException, SpotifyWebApiException {
        try {
            User userSpotify = spotifyApiArmada
                    .getCurrentUsersProfile()
                    .build()
                    .execute();
            String usernameSpotify = userSpotify.getDisplayName();
            UserDB newUserDB = new UserDB();

            if(personaRepository.existsByUsuarioSpotify(usernameSpotify)){
                newUserDB.setUsuarioSpotify(usernameSpotify);
                personaRepository.save(newUserDB);
            } else {
                throw new RuntimeException("Ya existe un usuario");
            }
            for(PlaylistSimplified userPlaylist : userPlaylists.getItems()){
                Playlist playlist = spotifyApiArmada
                        .getPlaylist(userPlaylist.getId())
                        .build()
                        .execute();
                for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()){
                    Track track = (Track) playlistTrack.getTrack();
                    List<ArtistSimplified> artists = List.of(track.getArtists());
                    if (!artists.isEmpty()) {
                        String artistName = artists.get(0).getName();
                        SongsDB newSongDB = new SongsDB(newUserDB, track.getName(), artistName, track.getPreviewUrl());
                        cancionesRepository.save(newSongDB);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
