INSERT INTO user (faker_id, name, email, status, created_at, updated_at)
VALUES (13, 'userA', 'userA@email.com', 'ACTIVE', NOW(), NOW()),
       (17, 'userB', 'userB@email.com', 'ACTIVE', NOW(), NOW()),
       (33, 'userC', 'userC@email.com', 'ACTIVE', NOW(), NOW()),
       (35, 'userD', 'userD@email.com', 'ACTIVE', NOW(), NOW()),
       (77, 'userE', 'userE@email.com', 'WAIT', NOW(), NOW()),
       (99, 'userF', 'userF@email.com', 'NON_ACTIVE', NOW(), NOW());

INSERT INTO room (host_id, title, room_type, status, created_at, updated_at)
VALUES (1, '단식방1', 'SINGLE', 'WAIT', NOW(), NOW()),
       (2, '단식방2', 'SINGLE', 'WAIT', NOW(), NOW()),
       (3, '복식방1', 'DOUBLE', 'WAIT', NOW(), NOW()),
       (4, '복식방2', 'DOUBLE', 'WAIT', NOW(), NOW());

INSERT INTO user_room (user_id, room_id, team)
VALUES (1, 1, 'RED'),
       (2, 2, 'BLUE'),
       (3, 3, 'RED'),
       (4, 4, 'BLUE');