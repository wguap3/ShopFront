DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM items;

INSERT INTO items (title, description, img_path, price) VALUES
('Мяч футбольный', 'Профессиональный мяч для футбола', '/images/ball.jpg', 1500),
('Бутсы Nike', 'Футбольные бутсы профессиональные', '/images/boots.jpg', 5000),
('Форма спортивная', 'Комплект формы для футбола', '/images/form.jpg', 3000),
('Гетры защитные', 'Гетры футбольные длинные', '/images/getry.jpg', 500),
('Перчатки вратаря', 'Вратарские перчатки с защитой', '/images/gloves.jpg', 2000),
('Щитки футбольные', 'Защитные щитки для ног', '/images/shchitki.jpg', 800),
('Насос для мяча', 'Компактный насос с иглой', '/images/nasos.jpg', 300),
('Сумка спортивная', 'Вместительная сумка для формы', '/images/sumka.jpg', 2500);