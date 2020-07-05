DROP TABLE IF EXISTS prodotti;

CREATE TABLE prodotti (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  prod VARCHAR(80) NOT NULL,
  loc VARCHAR(80) NOT NULL,
  typ VARCHAR(80) NOT NULL,
  prod_img VARCHAR(80),
  loc_img VARCHAR(80),
  qty INT DEFAULT 0

);

INSERT INTO prodotti (prod, loc         , typ       , prod_img      , loc_img       , qty   ) VALUES
('uova'     , 'in corsia 1'             , 'cena'    ,'uova.jpg'     ,'store1.png'   , 53    ),
('verdura'  , 'in corsia 3'             , 'leggero' ,'verdura.jpg'  ,'store3.png'   , 55    ),
('carne'    , 'in corsia 2'             , 'cena'    ,'carne.jpg'    ,'store2.png'   , 51    ),
('pasta'    , 'in corsia 1'             , 'pranzo'  ,'pasta.jpg'    ,'store1.png'   , 44    ),
('pesce'    , 'in corsia 2'             , 'cena'    ,'pesce.jpg'    ,'store2.png'   , 42    ),
('torte'    , 'in corsia 4'             , 'dolce'   ,'torte.jpg'    ,'store4.png'   , 56    ),
('pizza'    , 'in corsia 3, scaffale 2' , 'salato'  ,'pizza.jpg'    ,'store3.png'   , 47    );

