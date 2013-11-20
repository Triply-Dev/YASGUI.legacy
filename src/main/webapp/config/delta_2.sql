ALTER TABLE  `DisabledPropertyEndpoints` CHANGE  `Method`  `Method` ENUM(  'lazy', 'property', 'query',  'queryResults' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;
UPDATE DisabledPropertyEndpoints SET Method = 'query' WHERE Method = 'lazy';
UPDATE DisabledPropertyEndpoints SET Method = 'queryResults' WHERE Method = 'property';
ALTER TABLE  `DisabledPropertyEndpoints` CHANGE  `Method`  `Method` ENUM(  'query',  'queryResults' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

ALTER TABLE  `Properties` CHANGE  `Method`  `Method` ENUM(  'lazy', 'property', 'query',  'queryResults' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;
UPDATE Properties SET Method = 'query' WHERE Method = 'lazy';
UPDATE Properties SET Method = 'queryResults' WHERE Method = 'property';
ALTER TABLE  `Properties` CHANGE  `Method`  `Method` ENUM(  'query',  'queryResults' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;
