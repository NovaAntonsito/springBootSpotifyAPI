package com.springbackend.training.Servicios;

import com.springbackend.training.Controladores.DTO.TrackResponse;
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

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
public class UserServicio extends ServicioBase<UserDB, Long> implements IUserService {


    private final RepositorioUser personaRepository;

    private final RepositorioSongs cancionesRepository;

    private final Environment env;


    public UserServicio(RepositorioBase<UserDB, Long> baseRepository, RepositorioUser personaRepository, RepositorioUser repositorioUser, RepositorioSongs cancionesRepository, Environment env) {
        super(baseRepository);
        this.personaRepository = repositorioUser;
        this.cancionesRepository = cancionesRepository;
        this.env = env;
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
    public List<TrackResponse> savePlaylists(SpotifyApi spotifyApiArmada) throws IOException, ParseException, SpotifyWebApiException {
        try {
            User userSpotify = spotifyApiArmada
                    .getCurrentUsersProfile()
                    .build()
                    .execute();
            String usernameSpotify = userSpotify.getDisplayName();
            if(personaRepository.existsByUsuarioSpotify(usernameSpotify)){
                throw new RuntimeException("Ya existe un usuario");
            }
            Integer index = 0;
            String userID = userSpotify.getId();
            ArrayList<TrackResponse> trackResponses = new ArrayList<>();
            UserDB newUserDB = new UserDB();
            newUserDB.setUsuarioSpotify(usernameSpotify);
            personaRepository.save(newUserDB);
            GetListOfUsersPlaylistsRequest usersPlaylistsRequest = spotifyApiArmada
                    .getListOfUsersPlaylists(userID)
                    .build();
            Paging<PlaylistSimplified> userPlaylists = usersPlaylistsRequest.execute();
            for(PlaylistSimplified userPlaylist : userPlaylists.getItems()){
                Playlist playlist = spotifyApiArmada
                        .getPlaylist(userPlaylist.getId())
                        .build()
                        .execute();
                for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()){
                    Track track = (Track) playlistTrack.getTrack();
                    List<ArtistSimplified> artists = List.of(track.getArtists());
                    if (!artists.isEmpty()) {
                        String trackName = track.getName();
                        String previewUrl = track.getPreviewUrl();
                        String artistName = artists.get(0).getName();
                        SongsDB newSongDB = new SongsDB(newUserDB, trackName, artistName, previewUrl);
                        trackResponses.add(new TrackResponse(index,trackName,previewUrl,artistName));
                        cancionesRepository.save(newSongDB);
                        index++;
                    }

                }
            }
            trackResponses.forEach(System.out::println);
            return trackResponses;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
