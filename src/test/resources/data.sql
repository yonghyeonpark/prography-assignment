INSERT INTO users (faker_id, name, email, status, created_at, updated_at)
VALUES (3, 'user1', 'user1@email.com', 'ACTIVE', NOW(), NOW()),
       (5, 'user2', 'user2@email.com', 'ACTIVE', NOW(), NOW()),
       (7, 'user3', 'user3@email.com', 'ACTIVE', NOW(), NOW()),
       (9, 'user4', 'user4@email.com', 'ACTIVE', NOW(), NOW()),
       (11, 'user5', 'user5@email.com', 'ACTIVE', NOW(), NOW()),
       (13, 'user6', 'user6@email.com', 'ACTIVE', NOW(), NOW()),
       (15, 'user7', 'user7@email.com', 'ACTIVE', NOW(), NOW()),
       (77, 'user8', 'user8@email.com', 'WAIT', NOW(), NOW()),
       (99, 'user9', 'user9@email.com', 'NON_ACTIVE', NOW(), NOW());

INSERT INTO room (host_id, title, room_type, status, created_at, updated_at)
VALUES (1, 'Single Room1', 'SINGLE', 'WAIT', NOW(), NOW()),
       (2, 'Single Room2', 'SINGLE', 'WAIT', NOW(), NOW()),
       (3, 'Double Room1', 'DOUBLE', 'WAIT', NOW(), NOW()),
       (4, 'Double Room2', 'DOUBLE', 'WAIT', NOW(), NOW());

INSERT INTO user_room (user_id, room_id, team)
VALUES (1, 1, 'RED'),
       (2, 2, 'BLUE'),
       (3, 3, 'RED'),
       (4, 4, 'BLUE');