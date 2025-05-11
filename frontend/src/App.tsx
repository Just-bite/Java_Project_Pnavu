import React, { useState, useEffect } from 'react';
import {
    createPlaylist,
    createSong,
    createUser,
    fetchUsers,
    fetchPlaylists,
    fetchSongs,
    updateUser,
    updateSong,
    deleteUser,
    deleteSong,
    deletePlaylist,
    addSongsToPlaylist, updatePlaylist
} from './api/api';
import SearchModal from './SearchModal'
import TabContent from "./TabContent";
import EditModal from "./EditModal";
import DeleteModal from "./DeleteModal";
import CreateUserModal from "./CreateUserModal";
import CreateSongModal from "./CreateSongModal";
import CreatePlaylistModal from "./CreatePlaylistModal";
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu, Button,Space, Typography, Row, Col, Card, Spin, message } from 'antd';
import { HomeOutlined, SearchOutlined, PlusOutlined } from '@ant-design/icons';
import {User,Song,Playlist} from './Types'

const { Header, Sider, Content } = Layout;
const { Title } = Typography;

const AppLayout: React.FC = () => {
    const [searchVisible, setSearchVisible] = useState<boolean>(false);
    const [activeTab, setActiveTab] = useState<string>('Пользователи');
    const [playlists, setPlaylists] = useState<string[]>(['Плейлист 1', 'Плейлист 2']);
    const [data, setData] = useState<any[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [deleteModalVisible, setDeleteModalVisible] = useState<boolean>(false);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [currentItem, setCurrentItem] = useState<any>(null);
    const [allSongs, setAllSongs] = useState<Song[]>([]);
    const [allUsers, setAllUsers] = useState<User[]>([]);
    const [allPlaylists, setAllPlaylists] = useState<Playlist[]>([]);
    const [selectedUserId, setSelectedUserId] = useState<number | undefined>(undefined);
    const [selectedUsername, setSelectedUsername] = useState<string>('');
    // Состояния для модалок создания
    const [createUserVisible, setCreateUserVisible] = useState(false);
    const [createSongVisible, setCreateSongVisible] = useState(false);
    const [createPlaylistVisible, setCreatePlaylistVisible] = useState(false);

    const loadAllData = async () => {
        try {
            const [songs, users, playlists] = await Promise.all([
                fetchSongs(),
                fetchUsers(),
                fetchPlaylists()
            ]);

            // Фильтрация плейлистов - удаляем те, у которых нет пользователя
            const validPlaylists = [];
            for (const playlist of playlists) {
                console.log(playlist.username);
                if (playlist.username == 'Неизвестный') {
                    // Если у плейлиста нет пользователя - удаляем его
                    try {
                        await deletePlaylist(playlist.id);
                        console.log(`Плейлист ${playlist.id} удален, так как не имеет пользователя`);
                    } catch (error) {
                        console.error(`Ошибка при удалении плейлиста ${playlist.id}:`, error);
                    }
                } else {
                    validPlaylists.push(playlist);
                }
            }

            setAllSongs(songs);
            setAllUsers(users);
            setAllPlaylists(validPlaylists);
            return true;
        } catch (error) {
            console.error("Ошибка при загрузке данных:", error);
            return false;
        }
    };

    // Улучшенная валидация с проверкой типов
    const validateFields = (values: Record<string, any>, exceptions: string[] = []): string[] => {
        return Object.entries(values)
            .filter(([field]) => !exceptions.includes(field))
            .map(([field, value]) => {
                if (value === undefined || value === null) {
                    return `Поле "${field}" обязательно для заполнения`;
                }
                if (typeof value === 'string' && !value.trim()) {
                    return `Поле "${field}" не может быть пустым`;
                }
                if (typeof value === 'number' && isNaN(value)) {
                    return `Поле "${field}" должно быть числом`;
                }
                return '';
            })
            .filter(Boolean);
    };

// Универсальный обработчик ошибок
    const showError = (error: unknown, defaultMessage: string) => {
        let errorMessage = defaultMessage;

        if (typeof error === 'string') {
            errorMessage = error;
        } else if (error instanceof Error) {
            errorMessage = error.message;
        } else if (error && typeof error === 'object' && 'message' in error) {
            errorMessage = String(error.message);
        }

        message.error(errorMessage);
        console.error(error);
    };

// Обновленные обработчики:
    const handleCreatePlaylistClick = async (userId: number, username: string) => {
        await loadAllData();
        setSelectedUserId(userId);
        setSelectedUsername(username);
        setCreatePlaylistVisible(true);
    };

    const handleCreateUser = async (values: { username: string }) => {
        const errors = validateFields(values);
        if (errors.length) {
            errors.forEach(msg => message.error(msg));
            return;
        }

        try {
            setLoading(true);
            await createUser(values.username);
            message.success('Пользователь создан');
            setCreateUserVisible(false);
            await Promise.all([loadAllData(), loadData()]);
        } catch (error) {
            showError(error, 'Ошибка при создании пользователя');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateSong = async (values: { title: string; artist: string }) => {
        const errors = validateFields(values);
        if (errors.length) {
            errors.forEach(msg => message.error(msg));
            return;
        }

        try {
            setLoading(true);
            await createSong(values);
            message.success('Песня создана');
            setCreateSongVisible(false);
            await Promise.all([loadAllData(), loadData()]);
        } catch (error) {
            showError(error, 'Ошибка при создании песни');
        } finally {
            setLoading(false);
        }
    };

    const handleCreatePlaylist = async (values: {
        name: string;
        userId: number;
        songTitles?: string[];
    }) => {
        const errors = validateFields(values, ['songTitles']);
        if (errors.length) {
            errors.forEach(msg => message.error(msg));
            return;
        }

        try {
            setLoading(true);
            const newPlaylist = await createPlaylist({
                name: values.name,
                userId: values.userId
            });

            if (values.songTitles?.length) {
                const songIds = values.songTitles
                    .flatMap(title => allSongs.filter(s => s.title === title).map(s => s.id))
                    .filter(Boolean);

                if (songIds.length) {
                    await addSongsToPlaylist(newPlaylist.id, songIds);
                    newPlaylist.songs = allSongs.filter(s => songIds.includes(s.id));
                }
            }

            message.success('Плейлист создан');
            setCreatePlaylistVisible(false);
            setAllPlaylists(prev => [...prev, newPlaylist]);
            await loadData();
        } catch (error) {
            showError(error, 'Ошибка при создании плейлиста');
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = async (item: any) => {
        const success = await loadAllData();
        if (success) {
            setCurrentItem(item);
            setEditModalVisible(true);
        }
    };

    const handleDelete = (record: any) => {
        setCurrentItem(record);
        setDeleteModalVisible(true);
    };

    const createNewPlaylist = () => {
        const newPlaylist = `Плейлист ${playlists.length + 1}`;
        setPlaylists([...playlists, newPlaylist]);
    };

    const loadData = async () => {
        setLoading(true);
        try {
            let response: User[] | Playlist[] | Song[];
            switch (activeTab) {
                case 'Пользователи':
                    response = await fetchUsers();
                    break;
                case 'Плейлисты':
                    response = await fetchPlaylists();
                    break;
                case 'Песни':
                    const songsData = await fetchSongs();
                    response = songsData.map((song: Song) => ({
                        ...song,
                        playlistNames: song.playlistNames || [],
                        userNames: song.userNames || []
                    }));
                    break;
                default:
                    response = [];
            }
            setData(response);
        } catch (error) {
            message.error(`Ошибка при загрузке ${activeTab}`);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, [activeTab]);

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Header style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '0 24px'
            }}>
                <Space size="large">
                    <Link to="/">

                    </Link>
                </Space>

                {activeTab !== 'Плейлисты' && (
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={() => {
                            switch(activeTab) {
                                case 'Пользователи':
                                    setCreateUserVisible(true);
                                    break;
                                case 'Песни':
                                    setCreateSongVisible(true);
                                    break;
                            }
                        }}
                    >
                        {activeTab === 'Пользователи' ? 'Добавить пользователя' :
                            activeTab === 'Песни' ? 'Добавить песню' :
                                `Добавить ${activeTab.slice(0, -1)}`}
                    </Button>
                )}
            </Header>

            <Layout>
                <Sider width="25%" style={{ background: '#fff', padding: '24px' }}>
                    <Title level={4}>Моя медиатека</Title>

                    {/* Добавленное изображение с анимацией */}
                    <div style={{
                        margin: '20px 0',
                        textAlign: 'center',
                        transition: 'transform 0.5s ease'
                    }}
                         onMouseEnter={(e) => e.currentTarget.style.transform = 'rotate(360deg)'}
                         onMouseLeave={(e) => e.currentTarget.style.transform = 'rotate(0deg)'}
                    >
                        <img
                            src="/g.png" // Убедитесь, что изображение находится в public-папке
                            alt="Логотип"
                            style={{
                                width: '320px',
                                height: '320px',
                                borderRadius: '50%',
                                objectFit: 'cover'
                            }}
                        />
                    </div>
                </Sider>

                <Content style={{ padding: '24px' }}>
                    <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
                        <Col span={8}>
                            <Button
                                block
                                size="large"
                                type={activeTab === 'Пользователи' ? 'primary' : 'default'}
                                onClick={() => setActiveTab('Пользователи')}
                            >
                                Пользователи
                            </Button>
                        </Col>
                        <Col span={8}>
                            <Button
                                block
                                size="large"
                                type={activeTab === 'Песни' ? 'primary' : 'default'}
                                onClick={() => setActiveTab('Песни')}
                            >
                                Песни
                            </Button>
                        </Col>
                        <Col span={8}>
                            <Button
                                block
                                size="large"
                                type={activeTab === 'Плейлисты' ? 'primary' : 'default'}
                                onClick={() => setActiveTab('Плейлисты')}
                            >
                                Плейлисты
                            </Button>
                        </Col>
                    </Row>

                    <Card bordered={false}>
                        <Spin spinning={loading}>
                            <TabContent
                                activeTab={activeTab}
                                data={data}
                                onEdit={handleEdit}
                                onDelete={handleDelete}
                                onCreatePlaylist={handleCreatePlaylistClick}
                            />
                        </Spin>
                    </Card>

                    {/* Модальные окна удаления и редактирования */}
                    <DeleteModal
                        visible={deleteModalVisible}
                        onClose={() => setDeleteModalVisible(false)}
                        onConfirm={async () => {
                            try {
                                const entityType = activeTab.slice(0, -1).toLowerCase();
                                if (!currentItem?.id) {
                                    throw new Error('Не выбран элемент для удаления');
                                }

                                switch (entityType) {
                                    case 'песн':
                                        await deleteSong(currentItem.id);
                                        break;
                                    case 'плейлист':
                                        await deletePlaylist(currentItem.id);
                                        break;
                                    case 'пользовател':
                                        await deleteUser(currentItem.id);
                                        break;
                                    default:
                                        throw new Error(`Неизвестный тип сущности: ${entityType}`);
                                }

                                message.success('Удаление прошло успешно');
                                loadData();
                            } catch (error) {
                                message.error('Ошибка при удалении');
                            } finally {
                                setDeleteModalVisible(false);
                            }
                        }}
                        itemType={activeTab.slice(0, -1)}
                    />

                    <EditModal
                        visible={editModalVisible}
                        onClose={() => setEditModalVisible(false)}
                        onSave={async (values) => {
                            try {
                                switch (activeTab.slice(0, -1).toLowerCase()) {
                                    case 'пользовател':
                                        await updateUser(currentItem.id, {
                                            username: values.username,
                                            playlistIds: values.playlistNames?.map((name: string) =>
                                                allPlaylists.find((p: Playlist) => p.name === name)?.id
                                            ).filter((id: number | undefined): id is number => id !== undefined)
                                        });
                                        break;
                                    case 'плейлист':
                                        await updatePlaylist(currentItem.id, {
                                            name: values.name,
                                            songIds: values.songTitles
                                               ?.map((title: string) => allSongs.find((s: Song) => s.title === title)?.id)
                                                .filter((id: number | undefined): id is number => id !== undefined) || []
                                        });
                                        break;
                                    case 'песн':
                                        await updateSong(currentItem.id, {
                                            title: values.title,
                                            artist: values.artist
                                        });
                                        break;
                                }
                                message.success('Изменения сохранены');
                                setEditModalVisible(false);
                                loadAllData();
                                loadData();
                            } catch (error) {
                                message.error('Ошибка при сохранении');
                            }
                        }}
                        itemType={activeTab.slice(0, -1)}
                        initialValues={currentItem}
                        allSongs={allSongs}
                        allUsers={allUsers}
                        allPlaylists={allPlaylists}
                    />

                    {/* Модальные окна создания */}
                    <CreateUserModal
                        visible={createUserVisible}
                        onCancel={() => setCreateUserVisible(false)}
                        onConfirm={handleCreateUser}
                    />

                    <CreateSongModal
                        visible={createSongVisible}
                        onCancel={() => setCreateSongVisible(false)}
                        onConfirm={handleCreateSong}
                    />

                    <CreatePlaylistModal
                        visible={createPlaylistVisible}
                        onCancel={() => {
                            setCreatePlaylistVisible(false);
                            setSelectedUserId(undefined);
                            setSelectedUsername('');
                        }}
                        onConfirm={handleCreatePlaylist}
                        allUsers={allUsers}
                        allSongs={allSongs}
                        initialUserId={selectedUserId}
                        initialUsername={selectedUsername}
                    />

                    <Routes>
                        <Route path="/" element={<div />} />
                        <Route path="/about" element={<div>О нас</div>} />
                    </Routes>
                </Content>
            </Layout>

            <SearchModal visible={searchVisible} onClose={() => setSearchVisible(false)} />
        </Layout>
    );
};

const App: React.FC = () => (
    <Router>
        <AppLayout />
    </Router>
);

export default App;