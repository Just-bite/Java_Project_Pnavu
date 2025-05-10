import React, {useState} from "react";
import {Input, Modal} from "antd";

const SearchModal: React.FC<{ visible: boolean; onClose: () => void }> = ({ visible, onClose }) => {
    const [searchText, setSearchText] = useState<string>('');

    return (
        <Modal
            title="Поиск"
            open={visible}
            onOk={() => {
                console.log('Ищем:', searchText);
                onClose();
            }}
            onCancel={onClose}
        >
            <Input
                placeholder="Введите поисковый запрос"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
            />
        </Modal>
    );
};
export default SearchModal;