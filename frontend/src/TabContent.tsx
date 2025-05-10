import React from "react";
import { User, Song, Playlist, PlaylistsColumns, SongsColumns, UsersColumns } from './Types'
import { Button, Space, Table, Typography } from "antd";
import { DeleteOutlined, EditOutlined, PlusOutlined } from "@ant-design/icons";

const { Text } = Typography;

interface TabContentProps {
    activeTab: string;
    data: unknown[];
    onEdit: (record: any) => void;
    onDelete: (record: any) => void;
    onCreatePlaylist?: (userId: number, username: string) => void;
}

// Вспомогательные функции для безопасного рендеринга
const renderUsername = (username?: string) => <Text strong>{username || 'Неизвестный пользователь'}</Text>;

const renderPlaylists = (playlists?: Playlist[]) => playlists?.map(p => p.name).join(', ') || 'Нет плейлистов';

const renderSongsList = (songTitles: string[]) => {
    if (!songTitles || songTitles.length === 0) return 'Нет песен';
    const uniqueTitles = Array.from(new Set(
        songTitles.filter(title => title?.trim())
    ));
    return uniqueTitles.join(', ');
};

const renderUserActions = (
    onEdit: () => void,
    onDelete: () => void,
    onCreatePlaylist?: () => void
) => (
    <Space size="middle">
        {onCreatePlaylist && (
            <Button
                type="text"
                icon={<PlusOutlined style={{ color: 'green', fontSize: '20px' }} />}
                onClick={onCreatePlaylist}
                title="Создать плейлист"
            />
        )}
        <Button
            type="text"
            icon={<EditOutlined style={{ color: 'blueviolet', fontSize: '20px' }} />}
            onClick={onEdit}
        />
        <Button
            type="text"
            icon={<DeleteOutlined style={{ color: 'mediumvioletred', fontSize: '20px' }} />}
            onClick={onDelete}
        />
    </Space>
);

const renderDefaultActions = (
    onEdit: () => void,
    onDelete: () => void
) => (
    <Space size="middle">
        <Button
            type="text"
            icon={<EditOutlined style={{ color: 'blueviolet', fontSize: '20px' }} />}
            onClick={onEdit}
        />
        <Button
            type="text"
            icon={<DeleteOutlined style={{ color: 'mediumvioletred', fontSize: '20px' }} />}
            onClick={onDelete}
        />
    </Space>
);

const TabContent = ({ activeTab, data = [], onEdit, onDelete, onCreatePlaylist }: TabContentProps) => {
    const safeData = Array.isArray(data) ? data : [];

    const usersColumns: UsersColumns = [
        {
            title: 'Пользователь',
            dataIndex: 'username',
            key: 'username',
            render: renderUsername,
        },
        {
            title: 'Плейлисты',
            dataIndex: 'playlists',
            key: 'playlists',
            render: renderPlaylists,
        },
        {
            title: 'Песни',
            key: 'songs',
            render: (_: unknown, record: User) => {
                const allSongs = record.playlists?.flatMap(p => p.songTitles || []) || [];
                return renderSongsList(allSongs);
            },
        },
        {
            title: 'Действия',
            key: 'actions',
            width: 150,
            render: (_: unknown, record: User) => renderUserActions(
                () => onEdit(record),
                () => onDelete(record),
                onCreatePlaylist ? () => onCreatePlaylist(record.id, record.username) : undefined
            ),
        },
    ];

    const playlistsColumns: PlaylistsColumns = [
        {
            title: 'Плейлист',
            dataIndex: 'name',
            key: 'name',
            render: renderUsername,
        },
        {
            title: 'Пользователь',
            dataIndex: 'user',
            key: 'user',
            render: (_: unknown, record: Playlist) => {
                return renderUsername(record.username);
            },
        },
        {
            title: 'Песни',
            dataIndex: 'songs',
            key: 'songs',
            render: (_: unknown, record: Playlist) => {
                return renderSongsList(record.songTitles);
            },
        },
        {
            title: 'Действия',
            key: 'actions',
            width: 120,
            render: (_: unknown, record: Playlist) => renderDefaultActions(
                () => onEdit(record),
                () => onDelete(record)
            ),
        },
    ];

    const songsColumns: SongsColumns = [
        {
            title: 'Песня',
            dataIndex: 'title',
            key: 'title',
            render: renderUsername,
        },
        {
            title: 'Исполнитель',
            dataIndex: 'artist',
            key: 'artist',
            render: (text?: string) => text || 'Неизвестный исполнитель',
        },
        {
            title: 'Плейлисты',
            key: 'playlists',
            render: (_: unknown, record: Song) => {
                const playlistNames = record.playlistNames || record.playlists?.map(p => p.name) || [];
                return playlistNames.join(', ') || 'Не в плейлистах';
            },
        },
        {
            title: 'Действия',
            key: 'actions',
            width: 120,
            render: (_: unknown, record: Song) => renderDefaultActions(
                () => onEdit(record),
                () => onDelete(record)
            ),
        },
    ];

    const getTable = () => {
        switch (activeTab) {
            case 'Пользователи':
                return (
                    <Table<User>
                        columns={usersColumns}
                        dataSource={safeData as User[]}
                        bordered
                        rowKey="id"
                        locale={{ emptyText: 'Нет данных для отображения' }}
                    />
                );
            case 'Плейлисты':
                return (
                    <Table<Playlist>
                        columns={playlistsColumns}
                        dataSource={safeData as Playlist[]}
                        bordered
                        rowKey="id"
                        locale={{ emptyText: 'Нет данных для отображения' }}
                    />
                );
            case 'Песни':
                return (
                    <Table<Song>
                        columns={songsColumns}
                        dataSource={safeData as Song[]}
                        bordered
                        rowKey="id"
                        locale={{ emptyText: 'Нет данных для отображения' }}
                    />
                );
            default:
                return <div>Неизвестный тип данных</div>;
        }
    };

    return getTable();
};

export default TabContent;