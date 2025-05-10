// DeleteModal.tsx
import {Modal} from "antd";

interface DeleteModalProps {
    visible: boolean;
    onClose: () => void;
    onConfirm: () => void;
    itemType: string;
}

const DeleteModal: React.FC<DeleteModalProps> = ({ visible, onClose, onConfirm, itemType }) => {
    const getCorrectWordForm = (type: string) => {
        const lowerType = type.toLowerCase();
        if (lowerType.includes('плейлист')) return 'плейлиста';
        if (lowerType.includes('песн')) return 'песни';
        if (lowerType.includes('пользовател')) return 'пользователя';

        return lowerType;
    };

    const itemName = getCorrectWordForm(itemType);

    return (
        <Modal
            title={`Удаление ${itemName}`}
            open={visible}
            onOk={onConfirm}
            onCancel={onClose}
            okText="Удалить"
            cancelText="Отмена"
            okButtonProps={{ danger: true }}
        >
            <p>Вы подтверждаете  удаление {itemName}? Это действие нельзя отменить.</p>
        </Modal>
    );
};
export default DeleteModal;