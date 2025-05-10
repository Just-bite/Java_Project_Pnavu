import { Form, Input, Modal } from "antd";
import React, { useState } from "react";

interface CreateSongModalProps {
    visible: boolean;
    onCancel: () => void;
    onConfirm: (values: { title: string; artist: string }) => Promise<void>;
}

const CreateSongModal: React.FC<CreateSongModalProps> = ({
                                                             visible,
                                                             onCancel,
                                                             onConfirm
                                                         }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);

    const handleOk = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);
            await onConfirm(values);
            form.resetFields();
        } catch (error) {
            console.error('Validation failed:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            title="Добавить песню"
            visible={visible}
            onOk={handleOk}
            onCancel={onCancel}
            confirmLoading={loading}
            okText="Создать"
            cancelText="Отмена"
        >
            <Form form={form} layout="vertical">
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
            </Form>
        </Modal>
    );
};

export default CreateSongModal;