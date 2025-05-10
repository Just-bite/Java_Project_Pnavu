import { Form, Input, Modal } from "antd";
import React, { useState } from "react";

interface CreateUserModalProps {
    visible: boolean;
    onCancel: () => void;
    onConfirm: (values: { username: string }) => Promise<void>;
}

const CreateUserModal: React.FC<CreateUserModalProps> = ({
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
            title="Добавить пользователя"
            visible={visible}
            onOk={handleOk}
            onCancel={onCancel}
            confirmLoading={loading}
            okText="Создать"
            cancelText="Отмена"
        >
            <Form form={form} layout="vertical">
                <Form.Item
                    name="username"
                    label="Имя пользователя"
                    rules={[
                        { required: true, message: 'Введите имя пользователя' },
                    ]}
                >
                    <Input placeholder="Введите имя пользователя" />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CreateUserModal;