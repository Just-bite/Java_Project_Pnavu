import { Form, Input, Modal, Select, Tag, Space } from "antd";
import { useEffect, useState } from "react";
import { Playlist, Song, User } from "./Types";

interface EditModalProps {
    visible: boolean;
    onClose: () => void;
    onSave: (values: any) => void;
    itemType: string;
    initialValues?: User | Playlist | Song;
    allSongs?: Song[];
    allPlaylists?: Playlist[];
    allUsers?: User[];
}

const EditModal: React.FC<EditModalProps> = ({
                                                 visible,
                                                 onClose,
                                                 onSave,
                                                 itemType,
                                                 initialValues,
                                                 allSongs = [],
                                                 allPlaylists = [],
                                                 allUsers = []
                                             }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    const [selectedPlaylists, setSelectedPlaylists] = useState<Playlist[]>([]);

    const getModalTitle = () => {
        switch(itemType.toLowerCase()) {
            case 'пользовател':
                return 'Редактирование пользователя';
            case 'песн':
                return 'Редактирование песни';
            case 'плейлист':
                return 'Редактирование плейлиста';
            default:
                return `Редактирование ${itemType}`;
        }
    };

    useEffect(() => {
        if (initialValues) {
            const currentPlaylists = (initialValues as Song)?.playlists || [];
            setSelectedPlaylists(currentPlaylists);

            const formValues = {
                ...initialValues,
                songTitles: (initialValues as Playlist)?.songTitles || [],
                playlistNames: (initialValues as User)?.playlists?.map(p => p.name) || []
            };
            form.setFieldsValue(formValues);
        } else {
            setSelectedPlaylists([]);
            form.resetFields();
        }
    }, [initialValues, form]);

    const handleOk = async () => {
        try {
            setLoading(true);
            const values = await form.validateFields();
            await onSave(values);
        } catch (error) {
            console.error('Validation failed:', error);
        } finally {
            setLoading(false);
        }
    };

    const renderUserFields = () => {
        const userPlaylists = (initialValues as User)?.playlists || [];
        const fullUserPlaylists = allPlaylists.filter(p =>
            userPlaylists.some(up => up.name === p.name)
        );

        return (
            <>
                <Form.Item
                    name="username"
                    label="Имя пользователя"
                    rules={[
                        { required: true, message: 'Введите имя пользователя' },
                    ]}
                >
                    <Input placeholder="Введите имя пользователя" />
                </Form.Item>

                <Form.Item label="Плейлисты пользователя">
                    {fullUserPlaylists.length > 0 ? (
                        <Space direction="vertical" style={{ width: '100%' }}>
                            {fullUserPlaylists.map(playlist => (
                                <div key={playlist.name}>
                                    <div style={{ fontWeight: 'bold', marginBottom: 4 }}>
                                        {playlist.name}
                                    </div>
                                    {playlist.songTitles?.length > 0 ? (
                                        <Space wrap>
                                            {playlist.songTitles.map((title, index) => (
                                                <Tag key={`${playlist.name}-${index}`}>{title}</Tag>
                                            ))}
                                        </Space>
                                    ) : (
                                        <span>Нет песен в плейлисте</span>
                                    )}
                                </div>
                            ))}
                        </Space>
                    ) : (
                        <span>Нет плейлистов</span>
                    )}
                </Form.Item>

                <Form.Item
                    name="playlistNames"
                    label="Добавить плейлисты (необязательно)"
                >
                    <Select
                        mode="multiple"
                        placeholder="Выберите плейлисты"
                        options={allPlaylists
                            .filter(p => !userPlaylists.some(up => up.name === p.name))
                            .map(playlist => ({
                                value: playlist.name,
                                label: playlist.name
                            }))}
                    />
                </Form.Item>
            </>
        );
    };

    const renderPlaylistFields = () => {
        const currentSongTitles = (initialValues as Playlist)?.songTitles || [];
        const allUniqueSongTitles = Array.from(new Set(
            allSongs.map(song => song.title)
        ));

        return (
            <>
                <Form.Item
                    name="name"
                    label="Название плейлиста"
                    rules={[
                        { required: true, message: 'Введите название плейлиста' },
                        { max: 100, message: 'Максимум 100 символов' }
                    ]}
                >
                    <Input placeholder="Введите название плейлиста" />
                </Form.Item>

                <Form.Item label="Текущие песни">
                    {currentSongTitles.length > 0 ? (
                        <Space wrap>
                            {currentSongTitles.map((title, index) => (
                                <Tag key={index}>{title}</Tag>
                            ))}
                        </Space>
                    ) : (
                        <span>Нет песен в плейлисте</span>
                    )}
                </Form.Item>

                <Form.Item
                    name="songTitles"
                    label="Добавить песни (необязательно)"
                >
                    <Select
                        mode="multiple"
                        placeholder="Выберите песни"
                        showSearch
                        optionFilterProp="children"
                        filterOption={(input, option) =>
                            (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                        }
                        options={allUniqueSongTitles.map(title => ({
                            value: title,
                            label: title
                        }))}
                        style={{ width: '100%' }}
                    />
                </Form.Item>
            </>
        );
    };

    const renderSongFields = () => (
        <>
            <Form.Item
                name="title"
                label="Название песни"
                rules={[
                    { required: true, message: 'Введите название песни' },
                    { max: 100, message: 'Максимум 100 символов' }
                ]}
            >
                <Input placeholder="Введите название песни" />
            </Form.Item>

            <Form.Item
                name="artist"
                label="Исполнитель"
                rules={[
                    { required: true, message: 'Введите исполнителя' },
                    { max: 50, message: 'Максимум 50 символов' }
                ]}
            >
                <Input placeholder="Введите имя исполнителя" />
            </Form.Item>

            <Form.Item label="Содержится в плейлистах">
                {selectedPlaylists.length > 0 ? (
                    <Space wrap>
                        {selectedPlaylists.map(playlist => (
                            <Tag key={playlist.name}>{playlist.name}</Tag>
                        ))}
                    </Space>
                ) : (
                    <span>( Данная песня не содержится в плейлистах)</span>
                )}
            </Form.Item>
        </>
    );

    const getFormFields = () => {
        const type = itemType.toLowerCase();
        if (type.includes('пользовател')) return renderUserFields();
        if (type.includes('плейлист')) return renderPlaylistFields();
        if (type.includes('песн')) return renderSongFields();
        return null;
    };

    return (
        <Modal
            title={getModalTitle()}
            open={visible}
            onOk={handleOk}
            onCancel={onClose}
            confirmLoading={loading}
            okText="Сохранить"
            cancelText="Отмена"
            width={800}
            destroyOnClose
        >
            <Form form={form} layout="vertical">
                {getFormFields()}
            </Form>
        </Modal>
    );
};

export default EditModal;