import { Modal, Form, Input, Select } from 'antd';
import { User, Song } from "./Types";
import { useEffect, useState } from "react";

interface CreatePlaylistModalProps {
    visible: boolean;
    onCancel: () => void;
    onConfirm: (values: {
        name: string;
        userId: number;
        songTitles?: string[];
    }) => Promise<void>;
    allUsers: User[];
    allSongs: Song[];
    initialUserId?: number;
    initialUsername?: string;
}

const CreatePlaylistModal: React.FC<CreatePlaylistModalProps> = ({
                                                                     visible,
                                                                     onCancel,
                                                                     onConfirm,
                                                                     allUsers,
                                                                     allSongs,
                                                                     initialUserId,
                                                                     initialUsername
                                                                 }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    const [songOptions, setSongOptions] = useState<{value: string, label: string}[]>([]);
    const [userOptions, setUserOptions] = useState<{value: number, label: string}[]>([]);

    useEffect(() => {
        if (visible) {
            // Загружаем песни
            const uniqueTitles = Array.from(new Set(allSongs.map(song => song.title)));
            setSongOptions(uniqueTitles.map(title => ({ value: title, label: title })));

            // Загружаем пользователей
            const users = allUsers.map(user => ({
                value: user.id,
                label: user.username
            }));
            setUserOptions(users);

            // Находим пользователя по initialUserId для отображения
            const selectedUser = initialUserId
                ? allUsers.find(user => user.id === initialUserId)
                : null;

            // Устанавливаем начальные значения
            form.setFieldsValue({
                userId: initialUserId,
                name: initialUsername ? `Плейлист ${initialUsername}` : '',
                // Добавляем поле для отображения имени пользователя
                userDisplay: selectedUser ? selectedUser.username : undefined
            });
        } else {
            form.resetFields();
        }
    }, [visible, allSongs, allUsers, form, initialUserId, initialUsername]);

    const handleOk = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);
            await onConfirm(values);
            onCancel();
        } catch (error) {
            console.error('Validation failed:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            title="Создать плейлист"
            open={visible}
            onOk={handleOk}
            onCancel={onCancel}
            confirmLoading={loading}
            width={600}
            okText="Создать"
            cancelText="Отмена"
            destroyOnClose
            forceRender
        >
            <Form form={form} layout="vertical">
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

                <Form.Item
                    name="userId"
                    label="Владелец"
                    rules={[{ required: true, message: 'Выберите владельца' }]}
                >
                    <Select
                        placeholder="Выберите пользователя"
                        options={userOptions}
                        optionFilterProp="label"
                        showSearch
                        filterOption={(input, option) =>
                            (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                        }
                    />
                </Form.Item>

                <Form.Item
                    name="songTitles"
                    label="Добавить песни (необязательно)"
                >
                    <Select
                        mode="multiple"
                        placeholder="Выберите песни"
                        showSearch
                        allowClear
                        options={songOptions}
                        filterOption={(input, option) =>
                            (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                        }
                    />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CreatePlaylistModal;