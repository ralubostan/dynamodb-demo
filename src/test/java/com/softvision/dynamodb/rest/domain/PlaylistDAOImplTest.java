package com.softvision.dynamodb.rest.domain;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.softvision.dynamodb.rest.dto.PlaylistType;
import com.softvision.dynamodb.rest.repository.PlaylistDAOImpl;
import com.softvision.dynamodb.util.DynamoDBTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlaylistDAOImplTest.class)
@ComponentScan(basePackages = {"com.softvision.dynamodb.rest.domain", "com.softvision.dynamodb.rest.config"})
public class PlaylistDAOImplTest {

    private static final String TABLE_NAME = "Playlist";
    private static final String HASH_KEY_NAME = "id";
    private static final long THROUGHPUT = 1000L;

    private static final String SONG_NAME_1 = "Song name 1";
    private static final String SONG_ARTIST_1 = "Song artist 1";
    private static final String SONG_ALBUM_1 = "Song album 1";
    private static final Long SONG_LENGTH_1 = 180L;
    private static final String SONG_NAME_2 = "Song name 1";
    private static final String SONG_ARTIST_2 = "Song artist 1";
    private static final String SONG_ALBUM_2 = "Song album 1";
    private static final Long SONG_LENGTH_2 = 220L;
    private static final String PLAYLIST_NAME_1 = "Playlist name 1";
    private static final String PLAYLIST_GENRE_1 = "Playlist genre 1";
    private static final PlaylistType PLAYLIST_TYPE_1 = PlaylistType.PRIVATE;
    private static final String PLAYLIST_NAME_2 = "Playlist name 2";
    private static final String PLAYLIST_GENRE_2 = "Playlist genre 2";
    private static final PlaylistType PLAYLIST_TYPE_2 = PlaylistType.PUBLIC;

    private CreateTableResult table;
    private AmazonDynamoDB embeddedDynamoDb = DynamoDBTestUtil.createEmbeddedDatabase();

    @Spy
    private DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(embeddedDynamoDb);

    @InjectMocks
    private PlaylistDAOImpl playlistDAO;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        table = DynamoDBTestUtil.createTable(embeddedDynamoDb, TABLE_NAME, HASH_KEY_NAME, null);
    }

    @After
    public void after() {
        DynamoDBTestUtil.deleteTable(embeddedDynamoDb, TABLE_NAME);
    }

    @Test
    public void createTable_withHashKey_returnCreatedTable() {
        // when
        TableDescription tableDescription = table.getTableDescription();
        ListTablesResult tablesResult = embeddedDynamoDb.listTables();

        // then
        assertEquals(TABLE_NAME, tableDescription.getTableName());
        assertEquals(String.format("[{AttributeName: %s,KeyType: HASH}]", HASH_KEY_NAME),
                tableDescription.getKeySchema().toString());
        assertEquals(String.format("[{AttributeName: %s,AttributeType: S}, {AttributeName: %s,AttributeType: S}]", HASH_KEY_NAME, "genre"),
                tableDescription.getAttributeDefinitions().toString());
        assertEquals(Long.valueOf(THROUGHPUT), tableDescription.getProvisionedThroughput().getReadCapacityUnits());
        assertEquals(Long.valueOf(THROUGHPUT), tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
        assertEquals("ACTIVE", tableDescription.getTableStatus());
        assertEquals("arn:aws:dynamodb:ddblocal:000000000000:table/Playlist", tableDescription.getTableArn());
        assertEquals(1, tablesResult.getTableNames().size());
    }

    @Test
    public void create_whenPlaylistIsNew_createsPlaylistSuccessfully() {
        // given
        List<Song> songList = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList);

        // when
        final Playlist savedPlaylist = playlistDAO.create(playlist);

        // then
        assertNotNull(savedPlaylist);
        assertNotNull(savedPlaylist.getId());
        assertEquals(1, savedPlaylist.getVersion());
        assertEquals(PLAYLIST_NAME_1, savedPlaylist.getPlaylistName());
        assertEquals(PLAYLIST_GENRE_1, savedPlaylist.getGenre());
        assertEquals(PLAYLIST_TYPE_1, savedPlaylist.getType());
        assertEquals(1, savedPlaylist.getSongList().size());
        assertEquals(songList.get(0).getId(), savedPlaylist.getSongList().get(0).getId());
    }

    @Test
    public void update_whenPlaylistExists_updatesPlaylistSuccessfully() {
        // given
        List<Song> songList = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList);
        Playlist savedPlaylist = playlistDAO.create(playlist);
        List<Song> newSongList = Collections.singletonList(createTestSong(SONG_NAME_2, SONG_ARTIST_2, SONG_ALBUM_2, SONG_LENGTH_2));
        savedPlaylist.setSongList(newSongList);

        // when
        final Playlist updatedPlaylist = playlistDAO.update(savedPlaylist);

        // then
        assertNotNull(updatedPlaylist);
        assertNotNull(updatedPlaylist.getId());
        assertEquals(2, updatedPlaylist.getVersion());
        assertEquals(PLAYLIST_NAME_1, updatedPlaylist.getPlaylistName());
        assertEquals(PLAYLIST_GENRE_1, updatedPlaylist.getGenre());
        assertEquals(PLAYLIST_TYPE_1, updatedPlaylist.getType());
        assertEquals(1, updatedPlaylist.getSongList().size());
        assertEquals(newSongList.get(0).getId(), updatedPlaylist.getSongList().get(0).getId());
    }

    @Test
    public void delete_whenPlaylistExists_deletesPlaylistSuccessfully() {
        // given
        List<Song> songList = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList);
        Playlist savedPlaylist = playlistDAO.create(playlist);

        // when
        final boolean deleted = playlistDAO.delete(savedPlaylist.getId());

        // then
        assertTrue(deleted);
    }

    @Test
    public void findById_whenPlaylistExists_returnsPlaylistSuccessfully() {
        // given
        List<Song> songList = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList);
        Playlist savedPlaylist = playlistDAO.create(playlist);

        // when
        final Optional<Playlist> optionalPlaylist = playlistDAO.findById(savedPlaylist.getId());

        // then
        assertTrue(optionalPlaylist.isPresent());

        final Playlist returnedPlaylist = optionalPlaylist.get();
        assertNotNull(returnedPlaylist);
        assertEquals(savedPlaylist.getId(), returnedPlaylist.getId());
        assertEquals(1, returnedPlaylist.getVersion());
        assertEquals(PLAYLIST_NAME_1, returnedPlaylist.getPlaylistName());
        assertEquals(PLAYLIST_GENRE_1, returnedPlaylist.getGenre());
        assertEquals(PLAYLIST_TYPE_1, returnedPlaylist.getType());
        assertEquals(1, returnedPlaylist.getSongList().size());
        assertEquals(songList.get(0).getId(), returnedPlaylist.getSongList().get(0).getId());
    }

    @Test
    public void findAllByGenre_whenPlaylistsExist_returnsPlaylistsSuccessfully() {
        // given
        List<Song> songList1 = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist1 = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList1);

        List<Song> songList2 = Collections.singletonList(createTestSong(SONG_NAME_2, SONG_ARTIST_2, SONG_ALBUM_2, SONG_LENGTH_2));
        Playlist playlist2 = createTestPlaylist(PLAYLIST_NAME_2, PLAYLIST_GENRE_2, PLAYLIST_TYPE_2, songList2);

        Playlist savedPlaylist1 = playlistDAO.create(playlist1);
        Playlist savedPlaylist2 = playlistDAO.create(playlist2);

        // when
        final PaginatedQueryList<Playlist> allByGenreAndName = playlistDAO.findAllByGenre(PLAYLIST_GENRE_1);

        // then
        assertEquals(1, allByGenreAndName.size());
        assertEquals(savedPlaylist1.getId(), allByGenreAndName.get(0).getId());
        assertEquals(1, allByGenreAndName.get(0).getVersion());
        assertEquals(PLAYLIST_NAME_1, allByGenreAndName.get(0).getPlaylistName());
        assertEquals(PLAYLIST_GENRE_1, allByGenreAndName.get(0).getGenre());
        assertEquals(PLAYLIST_TYPE_1, allByGenreAndName.get(0).getType());
        assertEquals(1, allByGenreAndName.get(0).getSongList().size());
        assertEquals(songList1.get(0).getId(), allByGenreAndName.get(0).getSongList().get(0).getId());
    }

    @Test
    public void findAll_whenPlaylistsExist_returnsPlaylistsSuccessfully() {
        // given
        List<Song> songList1 = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist1 = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList1);

        List<Song> songList2 = Collections.singletonList(createTestSong(SONG_NAME_2, SONG_ARTIST_2, SONG_ALBUM_2, SONG_LENGTH_2));
        Playlist playlist2 = createTestPlaylist(PLAYLIST_NAME_2, PLAYLIST_GENRE_2, PLAYLIST_TYPE_2, songList2);

        playlistDAO.create(playlist1);
        playlistDAO.create(playlist2);

        // when
        final List<Playlist> playlists = playlistDAO.findAll();

        // then
        assertEquals(2, playlists.size());
    }

    @Test
    public void deleteAll_whenPlaylistsExist_returnsEmptyCollection() {
        // given
        List<Song> songList1 = Collections.singletonList(createTestSong(SONG_NAME_1, SONG_ARTIST_1, SONG_ALBUM_1, SONG_LENGTH_1));
        Playlist playlist1 = createTestPlaylist(PLAYLIST_NAME_1, PLAYLIST_GENRE_1, PLAYLIST_TYPE_1, songList1);

        List<Song> songList2 = Collections.singletonList(createTestSong(SONG_NAME_2, SONG_ARTIST_2, SONG_ALBUM_2, SONG_LENGTH_2));
        Playlist playlist2 = createTestPlaylist(PLAYLIST_NAME_2, PLAYLIST_GENRE_2, PLAYLIST_TYPE_2, songList2);

        playlistDAO.create(playlist1);
        playlistDAO.create(playlist2);

        // when
        playlistDAO.deleteAllPlaylists();
        final List<Playlist> playlists = playlistDAO.findAll();

        // then
        assertTrue(CollectionUtils.isEmpty(playlists));
    }

    private static Song createTestSong(String name, String artist, String album, Long length) {
        return Song.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .artist(artist)
                .album(album)
                .length(length)
                .added(DateTime.now().toString())
                .build();
    }

    private static Playlist createTestPlaylist(String name, String genre, PlaylistType type, List<Song> songList) {
        return Playlist.builder()
                .id(UUID.randomUUID().toString())
                .playlistName(name)
                .genre(genre)
                .type(type)
                .songList(songList)
                .createdDate(DateTime.now())
                .build();
    }
}
