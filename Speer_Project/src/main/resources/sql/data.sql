-- Insert dummy data into users table
INSERT INTO users (username, password_hash)
VALUES ('user1', '$2a$10$rv2NLonIfvEbrY1rFgyXsO1Far47lvdYGz54PX4.3O.8zCXx22rle'),
       ('user2', '$2a$10$rv2NLonIfvEbrY1rFgyXsO1Far47lvdYGz54PX4.3O.8zCXx22rle'),
       ('user3', '$2a$10$rv2NLonIfvEbrY1rFgyXsO1Far47lvdYGz54PX4.3O.8zCXx22rle'),
       ('user4', '$2a$10$rv2NLonIfvEbrY1rFgyXsO1Far47lvdYGz54PX4.3O.8zCXx22rle'),
       ('user5', '$2a$10$rv2NLonIfvEbrY1rFgyXsO1Far47lvdYGz54PX4.3O.8zCXx22rle');

-- Insert dummy data into notes table
INSERT INTO notes (user_id, title, content)
VALUES (1, 'Spring Security',
        'Spring Security is a powerful and highly customizable authentication and access-control framework for Java applications.'),
       (2, 'Spring Boot',
        'Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".'),
       (1, 'Hibernate ORM',
        'Hibernate ORM enables developers to more easily write applications whose data outlives the application process.'),
       (3, 'Microservices',
        'Microservices are an architectural style that structures an application as a collection of services.'),
       (2, 'JPA with Hibernate',
        'Java Persistence API (JPA) is a specification for accessing, persisting, and managing data between Java objects and a relational database.'),
       (4, 'RESTful Web Services',
        'RESTful Web Services are a type of web service that uses REST principles to provide interoperability between computer systems on the internet.'),
       (5, 'Docker Basics',
        'Docker is an open platform for developing, shipping, and running applications. Docker enables you to separate your applications from your infrastructure.'),
       (3, 'Kubernetes Introduction',
        'Kubernetes is an open-source system for automating the deployment, scaling, and management of containerized applications.'),
       (1, 'Spring Data JPA',
        'Spring Data JPA provides repository support for the Java Persistence API (JPA) to ease database access.'),
       (4, 'Spring Cloud',
        'Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems (e.g., configuration management, service discovery).'),
       (5, 'GraphQL',
        'GraphQL is a query language for your API, and a server-side runtime for executing queries by using a type system you define for your data.');

-- Insert dummy data into shared_notes table
-- INSERT INTO shared_notes (user_id, note_id)
-- VALUES (1, 2),  -- User 1 shares Note 2
--        (2, 1),  -- User 2 shares Note 1
--        (3, 1),  -- User 3 shares Note 1
--        (3, 2),  -- User 3 shares Note 2
--        (2, 5),  -- User 2 shares Note 5
--        (4, 6),  -- User 4 shares Note 6
--        (5, 7),  -- User 5 shares Note 7
--        (1, 8),  -- User 1 shares Note 8
--        (2, 9),  -- User 2 shares Note 9
--        (3, 10), -- User 3 shares Note 10
--        (4, 11); -- User 4 shares Note 11

-- INSERT INTO shared_notes (user_id, note_id, shared_with)
-- VALUES (1, 2, 2),  -- User 1 shares Note 2
--        (2, 1, 3),  -- User 2 shares Note 1
--        (3, 1, 5),  -- User 3 shares Note 1
--        (3, 2, 1),  -- User 3 shares Note 2
--        (2, 5, 1),  -- User 2 shares Note 5
--        (4, 6, 1),  -- User 4 shares Note 6
--        (5, 7, 2),  -- User 5 shares Note 7
--        (1, 8, 2),  -- User 1 shares Note 8
--        (2, 9, 3),  -- User 2 shares Note 9
--        (3, 10, 4), -- User 3 shares Note 10
--        (4, 11, 2); -- User 4 shares Note 11
