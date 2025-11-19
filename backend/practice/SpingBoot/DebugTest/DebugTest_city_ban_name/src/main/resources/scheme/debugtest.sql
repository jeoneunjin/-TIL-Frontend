DROP DATABASE IF EXISTS `debugtest`;
CREATE DATABASE IF NOT EXISTS `debugtest`;
USE `debugtest`;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` varchar(20) NOT NULL,
  `pw` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `birthdate` DATE NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `reg_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 가입 날짜 자동 반영
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users` VALUES ('admin', '1234', '관리자', '1990-01-01', '010-1234-5678', 'admin@example.com', NOW()),
                           ('ssafy', '1234', '김싸피', '1995-05-15', '010-8765-4321', 'ssafy@example.com', NOW());


DROP TABLE IF EXISTS `genres`;
CREATE TABLE `genres` (
  `genre_id` INT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`genre_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `genres` VALUES
(1, 'K-POP'),
(2, '발라드'),
(3, '힙합'),
(4, '록'),
(5, 'R&B');
                           
                           
DROP TABLE IF EXISTS `contents`;
CREATE TABLE `contents` (
  `code` varchar(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `genre` INT NOT NULL,
  `album` varchar(100) DEFAULT NULL,
  `artist` varchar(100) NOT NULL,
  `user_id` varchar(20) NOT NULL, 
  PRIMARY KEY (`code`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_genre` FOREIGN KEY (`genre`) REFERENCES `genres` (`genre_id`)ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `contents` VALUES
('SONG001', 'OMG', 1, 'OMG', 'NewJeans', 'admin'),
('SONG002', 'Ditto', 1, 'OMG', 'NewJeans', 'admin'),
('SONG003', '사건의지평선', 2, 'YOUNHA 6집', '윤하', 'ssafy'),
('SONG004', '헤어지자말해요', 2, '정키 프로젝트', '박재정', 'ssafy'),
('SONG005', 'HypeBoy', 1, 'NewJeans 1st EP', 'NewJeans', 'admin');

COMMIT;

SELECT * FROM contents;
SELECT * FROM users;

