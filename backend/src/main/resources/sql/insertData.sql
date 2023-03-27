-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE
FROM horse
where id < 0;

DELETE
FROM owner
WHERE id < 0;

INSERT INTO owner (id, first_name, last_name, email)
VALUES (-1, 'Maverick', 'Radcliffe', null),
       (-2, 'Ziggy', 'Ziglar', 'ziggy.ziglar@example.com'),
       (-3, 'Axel', 'Storm', 'axel.storm@example.com'),
       (-4, 'Luna', 'Moon', 'luna.moon@example.com'),
       (-5, 'Jazz', 'Jones', 'jazz.jones@example.com'),
       (-6, 'Ace', 'Wild', null),
       (-7, 'Maximus', 'Power', 'maximus.power@example.com'),
       (-8, 'Nova', 'Star', 'nova.star@example.com'),
       (-9, 'Phoenix', 'Fire', 'phoenix.fire@example.com'),
       (-10, 'Ryder', 'Blaze', null);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE', null, null, null),
       (-2, 'Candy', 'The sweet one!', '2020-10-10', 'MALE', -1, null, null),
       (-3, 'Sandy', 'The rough one!', '1976-03-03', 'FEMALE', null, null, null),
       (-4, 'Brandy', 'The adult!', '1977-10-10', 'MALE', -2, null, null),
       (-5, 'Mandy', 'The edgy one!', '2000-09-09', 'FEMALE', -3, -3, -4),
       (-6, 'Randy', null, '1970-04-12', 'MALE', -1, null, null),
       (-7, 'Gwendy', 'The flamboyant one!', '2010-02-01', 'FEMALE', -10, -5, null),
       (-8, 'Handy', 'The handy one!', '2005-02-12', 'MALE', null, null, -6),
       (-9, 'Bendy', 'The flexible one!', '2013-01-01', 'FEMALE', null, -7, null),
       (-10, 'Pandy', 'The cutest one!', '2022-10-10', 'MALE', -10, -7, -8);
;
