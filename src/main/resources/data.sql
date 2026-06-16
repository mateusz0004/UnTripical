USE untripical;

-- ====================================================================
-- KROK 0: BEZPIECZNE CZYSZCZENIE BAZY
-- ====================================================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM guide_announcement_tables;
DELETE FROM announcements;
DELETE FROM reviews;
DELETE FROM trip_stops;
DELETE FROM trip_plans;
DELETE FROM places;
DELETE FROM guide_details;
DELETE FROM region;
DELETE FROM users;

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- KROK 1: USERS (Wszyscy mają teraz pewny i sprawdzony hasz dla: haslo1)
-- ====================================================================
INSERT INTO users (id, email, username, password, is_active, user_role, created_at) VALUES
(1, 'admin@untripical.com', 'boss_admin', '$2a$12$D0YDli4ffKIo9lN2A7kcHusqq8t.oH1V99GEI8v6BqlftagU6q9OK', 1, 'ADMIN', NOW()),
(2, 'jan.kowalski@guide.pl', 'jan_tatry', '$2a$12$D0YDli4ffKIo9lN2A7kcHusqq8t.oH1V99GEI8v6BqlftagU6q9OK', 1, 'GUIDE', NOW()),
(3, 'tomasz.podroznik@gmail.com', 'tomek99', '$2a$12$D0YDli4ffKIo9lN2A7kcHusqq8t.oH1V99GEI8v6BqlftagU6q9OK', 1, 'USER', NOW());

-- ====================================================================
-- KROK 2: REGION
-- ====================================================================
INSERT INTO region (id, type, is_active, closest_big_city) VALUES
(1, 'MOUNTAIN', 1, 'Zakopane'),
(2, 'HISTORICAL', 1, 'Kraków'),
(3, 'FOREST', 1, 'Ustrzyki Górne');

-- ====================================================================
-- KROK 3: GUIDE_DETAILS
-- ====================================================================
INSERT INTO guide_details (id, phone_number, specialisation, closest_big_city, experience_level, counter_of_did_journey, average_rating, number_of_announcements, region_id) VALUES
(2, '+48123456789', 3, 'Zakopane', 3, 42, 5.0, 1, 1);

-- ====================================================================
-- KROK 4: PLACES
-- ====================================================================
INSERT INTO places (id, name, city, address_street, address_number, place_type, postal_code, is_active, photo_url, average_rating, status, region_id, user_id) VALUES
(1, 'Schronisko nad Morskim Okiem', 'Zakopane', 'Droga Oswalda Balzera', 'Na szczycie', 1, '34-500', 1, 'http://image.com/morskie.jpg', 5.0, 1, 1, 2),
(2, 'Zamek Królewski na Wawelu', 'Kraków', 'Wawel', '5', 0, '31-001', 1, 'http://image.com/wawel.jpg', 4.5, 1, 2, 1),
(3, 'Tarnica - Szczyt', 'Wołosate', 'Szlak Niebieski', 'Brak', 1, '38-713', 1, 'http://image.com/tarnica.jpg', 4.8, 1, 3, 3);

-- ====================================================================
-- KROK 5: REVIEWS
-- ====================================================================
INSERT INTO reviews (id, order_index, is_active, number_of_stars, description, created_at, place_id, user_id, guide_details_id) VALUES
(1, 1, 1, 5.0, 'Niezapomniane widoki, schronisko klimatyczne!', NOW(), 1, 3, 2),
(2, 1, 1, 4.5, 'Zamek robi wrażenie, warto odwiedzić ekspozycję.', NOW(), 2, 3, 2),
(3, 1, 1, 4.8, 'Magiczne Bieszczady. Podejście ostre, ale warto dla panoramy.', NOW(), 3, 3, 2);

-- ====================================================================
-- KROK 6: ANNOUNCEMENTS
-- ====================================================================
INSERT INTO announcements (id, name_of_journey, description, announcement_type, date, price, location_info, max_participants, is_active, created_at, place_id) VALUES
(1, 'Wyprawa na Rysy z przewodnikiem', 'Trudny trekking dla wymagających.', 'GROUP', '2026-07-25', 250.0, 'Tatry Wysokie', 8, 1, NOW(), 1),
(2, 'Tajemnice i legendy Wawelu', 'Spacer po zamkowych dziedzińcach.', 'INDIVIDUAL', '2026-07-22', 45.0, 'Kraków', 20, 1, NOW(), 2),
(3, 'Bieszczadzkie Połoniny o wschodzie słońca', 'Wschód słońca na szczycie Tarnicy.', 'GROUP', '2026-08-05', 120.0, 'Wołosate', 12, 1, NOW(), 3);

-- ====================================================================
-- KROK 7: GUIDE_ANNOUNCEMENT_TABLES
-- ====================================================================
INSERT INTO guide_announcement_tables (id, announcement_id, guide_details_id) VALUES
(1, 1, 2);

COMMIT;