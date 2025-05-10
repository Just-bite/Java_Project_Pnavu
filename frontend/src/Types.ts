import {ColumnsType} from "antd/es/table";

export type UsersColumns = ColumnsType<User>;
export type PlaylistsColumns = ColumnsType<Playlist>;
export type SongsColumns = ColumnsType<Song>;

export interface User {
    id: number;
    username: string;
    playlists: Playlist[];
}

export interface Playlist {
    id: number;
    name: string;
    username: string;
    songTitles: string[];
    songs?: Song[];
}

export interface Song {
    id: number;
    title: string;
    artist: string;
    playlistNames?: string[];
    playlists?: Playlist[];
    userNames: string[]; // Новое поле для имен пользователей
}

export interface UserUpdateData {
    username?: string;
    playlists?: Playlist[];
}