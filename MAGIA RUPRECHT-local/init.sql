-- character 初期化
UPDATE character SET level=5, exp=0, next_exp=50, hp=100, mp=100, atk=10, def=1, Money=3000, mycustomize_number=1;

-- customizes 初期化
DELETE FROM customizes;
INSERT INTO customizes (customize_id,name,magic1,magic2,magic3,magic4,magic5,magic6) VALUES
(1,'ファイアアロー',1,7,null,null,null,null),
(2,'ヒール',1,31,null,null,null,null),
(3,'アローレイン',4,null,null,null,null,null);

-- inventory_items 初期化
DELETE FROM inventory_items;
INSERT INTO inventory_items (item_id, owned) VALUES
(1, 10),
(6, 5);

-- mycustomize 初期化
DELETE FROM mycustomize;
INSERT INTO mycustomize (My_customize,my_magic1,my_magic2,my_magic3,my_magic4,my_magic5,my_magic6,set_name) VALUES
(1,1,2,3,0,0,0,'初期装備');

-- player_magic 初期化
DELETE FROM player_magic;
INSERT INTO player_magic (player_magic) VALUES
(1),(4),(7),(11),(16),(19),(31);

-- quest_staus 初期化
DELETE FROM quest_status;

-- game_state 初期化
UPDATE game_state SET story_finished = 0 WHERE id = 1;


-- repayment 初期化
UPDATE repayment SET repayment=1000000 WHERE id=1;

-- RewardLog 初期化
DELETE FROM RewardLog;

-- shop_magic 初期化
DELETE FROM shop_magic;
INSERT INTO shop_magic (magic_id) VALUES
(8),(9),(10),(12),(15);

-- stages 初期化
UPDATE stages SET cleared='false';