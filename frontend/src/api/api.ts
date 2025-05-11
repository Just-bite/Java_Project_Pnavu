import axios from 'axios';
import {User,Playlist,Song,} from "../Types";

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
    timeout: 5000,
});

export const fetchUsers = async (): Promise<User[]> => {
    try {
        const response = await api.get<User[]>('/users');
        return response.data;
    } catch (error) {
        console.error('Ошибка при запросе пользователей:', error);
        throw error;
    }
};

export const fetchPlaylists = async (): Promise<Playlist[]> => {
    try {
        const response = await api.get<Playlist[]>('/playlists');

        return response.data.map(playlist => ({
            id: playlist.id,
            name: playlist.name,
            username: playlist.username || 'Неизвестный',
            songTitles: playlist.songs?.map(s => s.title) || playlist.songTitles || [],
            songs: playlist.songs || []
        }));
    } catch (error) {
        console.error('Ошибка при запросе плейлистов:', error);
        throw error;
    }
};

export const fetchSongs = async (): Promise<Song[]> => {
    try {
        const [songsResponse, playlistsResponse] = await Promise.all([
            api.get<Song[]>('/songs'),
            api.get<Playlist[]>('/playlists')
        ]);

        return songsResponse.data.map(song => {
            const relatedPlaylists = playlistsResponse.data.filter(playlist =>
                playlist.songs?.some(s => s.id === song.id) ||
                playlist.songTitles?.includes(song.title)
            );

            const userNames = Array.from(new Set(
                relatedPlaylists
                    .map(playlist => playlist.username)
                    .filter(Boolean)
            ));

            return {
                ...song,
                playlistNames: relatedPlaylists.map(p => p.name),
                playlists: relatedPlaylists,
                userNames: userNames.length ? userNames : ['Не в плейлистах пользователя']
            };
        });
    } catch (error) {
        console.error('Error fetching songs:', error);
        throw error;
    }
};

export const updateUser = async (
    userId: number,
    updateData: {
        username: string;
        playlistIds?: number[];
    }
): Promise<void> => {
    try {
        const requestData = {
            username: updateData.username,
            playlistIds: updateData.playlistIds ?? null
        };

        await api.put(`/users/${userId}`, requestData);
    } catch (error) {
        console.error('Error updating user:', error);
        throw error;
    }
};

export const updatePlaylist = async (
    playlistId: number,
    data: {
        name: string;
        songIds: number[];
    }
): Promise<Playlist> => {
    try {
        const response = await api.put(`/playlists/u/${playlistId}`, {
            name: data.name,
            songIds: data.songIds
        });
        return response.data;
    } catch (error) {
        console.error('Error updating playlist:', error);
        throw error;
    }
};

export const updateSong = async (id: number, data: { title: string; artist: string }): Promise<Song> => {
    try {
        const response = await api.put(`/songs/${id}`, {
            title: data.title,
            artist: data.artist
        });
        return response.data;
    } catch (error) {
        console.error('Error updating song:', error);
        throw error;
    }
};

export const deleteSong = async (songId: number): Promise<void> => {
    try {
        await api.delete(`/songs/${songId}`);
    } catch (error) {
        console.error(`Error deleting song ${songId}:`, error);
        throw error;
    }
};

export const deletePlaylist = async (playlistId: number): Promise<void> => {
    try {
        await api.delete(`/playlists/${playlistId}`);
    } catch (error) {
        console.error(`Error deleting playlist ${playlistId}:`, error);
        throw error;
    }
};

export const deleteUser = async (userId: number): Promise<void> => {
    try {
        await api.delete(`/users/${userId}`);
    } catch (error) {
        console.error(`Error deleting user ${userId}:`, error);
        throw error;
    }
};

export const createUser = async (username: string): Promise<User> => {
    try {
        const response = await api.post('/users', [{ username }]);
        return response.data[0];
    } catch (error) {
        console.error('Error creating user:', error);
        throw error;
    }
};

export const createPlaylist = async (data: {
    name: string;
    userId: number;
}): Promise<Playlist> => {
    try {
        const response = await api.post(`/playlists/create/${data.userId}`,data);
        console.log(response.data);
        return response.data;
    } catch (error) {
        console.error('Error creating playlist:', error);
        throw error;
    }
};

export const addSongsToPlaylist = async (playlistId: number, songIds: number[]): Promise<void> => {
    try {
        await api.post(`/playlists/${playlistId}/add-songs`,  songIds );
    } catch (error) {
        console.error('Error adding songs to playlist:', error);
        throw error;
    }
};

export const createSong = async (data: {
    title: string;
    artist: string;
}): Promise<Song> => {
    try {
        const response = await api.post('/songs', [data]);
        return response.data;
    } catch (error) {
        console.error('Error creating song:', error);
        throw error;
    }
};