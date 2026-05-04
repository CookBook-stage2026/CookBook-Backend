-- ---------------------------------------------------------
-- INGREDIENTS
-- ---------------------------------------------------------
INSERT INTO ingredients (ingredient_id, name, unit)
VALUES
    ('91b60a9d-be30-46e1-8499-ee668a4c2d7d', 'Spaghetti', 'GRAM'),
    ('df2e8703-4a38-45a7-a747-bb3f28afbf82', 'Pancetta', 'GRAM'),
    ('1f821767-f0a9-4e03-ad70-c288f73efd9a', 'Eggs', 'PIECE'),
    ('046835e1-0e45-424f-9885-68b228b6f00b', 'Chicken breast', 'GRAM'),
    ('fca5db5d-f38f-4d59-96a2-7c06200748eb', 'Yogurt', 'GRAM'),
    ('ff1ac84b-ee97-40d2-9f38-ae919d48f9e8', 'Tomato sauce', 'GRAM'),
    ('fcc8d46c-780b-484c-bfe6-5b677805ca3c', 'Heavy cream', 'MILLILITER'),
    ('b89b2345-fc75-44c0-89b3-3e04d74124b4', 'Garam masala', 'TEASPOON'),
    ('eade830d-1bc4-47de-8052-d1b91df42f1d', 'Ground beef', 'GRAM'),
    ('3f03ff9b-31e5-4d48-a6a4-28d5fd81c898', 'Tortillas', 'PIECE'),
    ('3eddeece-5867-4416-824d-c627f2aa5979', 'Cheddar cheese', 'GRAM'),
    ('307f5492-e16a-431a-9cdb-75c9c6815a3f', 'Salsa', 'GRAM'),
    ('4b434f28-cef0-4d8b-82a3-01fbf534cf22', 'Sour cream', 'GRAM'),
    ('09b582cb-a0ea-4c18-bd4f-c814e66c50dd', 'Bell peppers', 'PIECE'),
    ('f6e83d4b-4d67-4044-9ee8-eedd8b347d4a', 'Broccoli', 'GRAM'),
    ('683a34ed-13d9-4c92-bf04-27e852716210', 'Carrots', 'PIECE'),
    ('69639b8d-d1e8-4cb7-879f-5a9bf362d87c', 'Soy sauce', 'TABLESPOON'),
    ('39d1ac57-8bf9-478e-ac53-3036a240610a', 'Sesame oil', 'TABLESPOON'),
    ('cb2e829b-fa70-4a66-a667-5473e3e8b723', 'Onions', 'PIECE'),
    ('cffb78b8-2cd4-4658-91ca-1da17e4f7a3a', 'Beef broth', 'LITER'),
    ('246de5df-8917-4530-8b91-06f7d7d32412', 'Gruyere cheese', 'GRAM'),
    ('6cd4fe2d-c2c0-4e1e-aa5a-ed773e116747', 'Baguette', 'PIECE'),
    ('1b194de6-4e86-4b46-b3a0-bebcbad0e15e', 'Butter', 'GRAM'),
    ('cc95f369-dda7-42d4-8035-bbaaacea963c', 'Bananas', 'PIECE'),
    ('b83a4fc3-e819-4edc-9f40-6841beaa3b80', 'Flour', 'GRAM'),
    ('48b21301-e07c-4118-92b9-f173336093ba', 'Milk', 'MILLILITER'),
    ('8ea17cd5-fea4-45f4-bfb6-35f0f04e583c', 'Tomatoes', 'PIECE'),
    ('1834b787-3ceb-49e2-9207-e3de61020049', 'Cucumber', 'PIECE'),
    ('4725715a-2570-4737-a62c-ac43cbd7ea90', 'Feta cheese', 'GRAM'),
    ('7cf382d1-b6c9-4424-93b8-59ad6e3c879e', 'Kalamata olives', 'GRAM'),
    ('a4378573-1d8d-44f5-80f6-22175b0c4f9c', 'Olive oil', 'TABLESPOON'),
    ('f63a1464-ce64-44e7-9088-046f044645e7', 'Beef chuck', 'GRAM'),
    ('084ed9bf-48c4-42c7-afd1-ca63e6a95b0d', 'Red wine', 'MILLILITER'),
    ('5245895d-bffe-4af2-89eb-7ed5e729d394', 'Mushrooms', 'GRAM'),
    ('655c0e6e-a675-4a10-a0e7-55cde4ec19c6', 'Bacon', 'GRAM'),
    ('332343ec-3fde-4ef0-ae83-97d42ab8f861', 'Pizza dough', 'GRAM'),
    ('373507a5-b659-4ca9-868c-8f39319946b2', 'Fresh mozzarella', 'GRAM'),
    ('6c81d1a3-66f7-42de-9ad1-05a5d748bcc8', 'Fresh basil', 'GRAM'),
    ('00885e1e-20a0-4c3d-aa23-ce1bd5ddc385', 'Dark chocolate', 'GRAM'),
    ('78223711-7e76-4f37-99c1-6c80daa63f2e', 'Sugar', 'GRAM');

-- ---------------------------------------------------------
-- INGREDIENT CATEGORIES
-- ---------------------------------------------------------
INSERT INTO ingredient_categories (ingredient_id, category)
VALUES
    ('91b60a9d-be30-46e1-8499-ee668a4c2d7d', 'GRAIN'),
    ('df2e8703-4a38-45a7-a747-bb3f28afbf82', 'MEAT'),
    ('1f821767-f0a9-4e03-ad70-c288f73efd9a', 'EGG'),
    ('046835e1-0e45-424f-9885-68b228b6f00b', 'POULTRY'),
    ('fca5db5d-f38f-4d59-96a2-7c06200748eb', 'DAIRY'),
    ('ff1ac84b-ee97-40d2-9f38-ae919d48f9e8', 'SAUCE_CONDIMENT'),
    ('fcc8d46c-780b-484c-bfe6-5b677805ca3c', 'DAIRY'),
    ('b89b2345-fc75-44c0-89b3-3e04d74124b4', 'SPICE'),
    ('eade830d-1bc4-47de-8052-d1b91df42f1d', 'MEAT'),
    ('3f03ff9b-31e5-4d48-a6a4-28d5fd81c898', 'GRAIN'),
    ('3eddeece-5867-4416-824d-c627f2aa5979', 'DAIRY'),
    ('307f5492-e16a-431a-9cdb-75c9c6815a3f', 'SAUCE_CONDIMENT'),
    ('4b434f28-cef0-4d8b-82a3-01fbf534cf22', 'DAIRY'),
    ('09b582cb-a0ea-4c18-bd4f-c814e66c50dd', 'VEGETABLE'),
    ('f6e83d4b-4d67-4044-9ee8-eedd8b347d4a', 'VEGETABLE'),
    ('683a34ed-13d9-4c92-bf04-27e852716210', 'VEGETABLE'),
    ('69639b8d-d1e8-4cb7-879f-5a9bf362d87c', 'SAUCE_CONDIMENT'),
    ('39d1ac57-8bf9-478e-ac53-3036a240610a', 'OIL_FAT'),
    ('cb2e829b-fa70-4a66-a667-5473e3e8b723', 'VEGETABLE'),
    ('cffb78b8-2cd4-4658-91ca-1da17e4f7a3a', 'SAUCE_CONDIMENT'),
    ('246de5df-8917-4530-8b91-06f7d7d32412', 'DAIRY'),
    ('6cd4fe2d-c2c0-4e1e-aa5a-ed773e116747', 'GRAIN'),
    ('1b194de6-4e86-4b46-b3a0-bebcbad0e15e', 'DAIRY'),
    ('cc95f369-dda7-42d4-8035-bbaaacea963c', 'FRUIT'),
    ('b83a4fc3-e819-4edc-9f40-6841beaa3b80', 'GRAIN'),
    ('48b21301-e07c-4118-92b9-f173336093ba', 'DAIRY'),
    ('8ea17cd5-fea4-45f4-bfb6-35f0f04e583c', 'VEGETABLE'),
    ('1834b787-3ceb-49e2-9207-e3de61020049', 'VEGETABLE'),
    ('4725715a-2570-4737-a62c-ac43cbd7ea90', 'DAIRY'),
    ('7cf382d1-b6c9-4424-93b8-59ad6e3c879e', 'VEGETABLE'),
    ('a4378573-1d8d-44f5-80f6-22175b0c4f9c', 'OIL_FAT'),
    ('f63a1464-ce64-44e7-9088-046f044645e7', 'MEAT'),
    ('084ed9bf-48c4-42c7-afd1-ca63e6a95b0d', 'BEVERAGE'),
    ('5245895d-bffe-4af2-89eb-7ed5e729d394', 'FUNGI'),
    ('655c0e6e-a675-4a10-a0e7-55cde4ec19c6', 'MEAT'),
    ('332343ec-3fde-4ef0-ae83-97d42ab8f861', 'GRAIN'),
    ('373507a5-b659-4ca9-868c-8f39319946b2', 'DAIRY'),
    ('6c81d1a3-66f7-42de-9ad1-05a5d748bcc8', 'HERB'),
    ('00885e1e-20a0-4c3d-aa23-ce1bd5ddc385', 'SWEETENER'),
    ('78223711-7e76-4f37-99c1-6c80daa63f2e', 'SWEETENER');

-- ---------------------------------------------------------
-- RECIPES
-- ---------------------------------------------------------
INSERT INTO recipes (recipe_id, name, description, duration_in_minutes, servings)
VALUES ('1ecba55b-a63c-43f6-a849-d736e6773f2d', 'A classic Italian pasta dish originating from Rome, made with spaghetti, eggs, aged cheese, pancetta, and freshly ground black pepper, known for its rich texture and simple, traditional ingredients',
        'Spaghetti Carbonara prepared with al dente pasta, crisp pancetta, eggs, Pecorino Romano cheese, black pepper, and a silky sauce created by emulsifying the ingredients without using cream', 30, 2),
       ('8d96d734-11ea-408d-adc5-763311de5573', 'Chicken Tikka Masala',
        'Creamy and spiced Indian curry with tender chicken', 45, 4),
       ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', 'Beef Tacos', 'Mexican street-style tacos with seasoned ground beef',
        25, 4),
       ('203138be-1e83-4dcb-8130-07a60f2860b4', 'Vegetable Stir Fry', 'Quick and healthy Asian-inspired stir fry', 20,
        2),
       ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 'French Onion Soup',
        'Rich and hearty French classic topped with melted cheese', 60, 4),
       ('ba581df1-3a58-474d-a824-f62f63ce0858', 'Banana Pancakes', 'Fluffy pancakes with a hint of banana', 20, 2),
       ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', 'Greek Salad', 'Fresh Mediterranean salad with feta and olives', 15, 2),
       ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 'Beef Bourguignon', 'Classic French braised beef stew in red wine', 180,
        6),
       ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 'Margherita Pizza',
        'Simple Neapolitan pizza with fresh basil and mozzarella', 40, 2),
       ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 'Chocolate Lava Cake',
        'Decadent warm chocolate cake with a molten center', 25, 4);

-- ---------------------------------------------------------
-- RECIPE INGREDIENTS
-- ---------------------------------------------------------
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, base_quantity)
VALUES
    -- Spaghetti Carbonara
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', '91b60a9d-be30-46e1-8499-ee668a4c2d7d', 200),
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', 'df2e8703-4a38-45a7-a747-bb3f28afbf82', 100),
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', '1f821767-f0a9-4e03-ad70-c288f73efd9a', 2),

    -- Chicken Tikka Masala
    ('8d96d734-11ea-408d-adc5-763311de5573', '046835e1-0e45-424f-9885-68b228b6f00b', 500),
    ('8d96d734-11ea-408d-adc5-763311de5573', 'fca5db5d-f38f-4d59-96a2-7c06200748eb', 200),
    ('8d96d734-11ea-408d-adc5-763311de5573', 'ff1ac84b-ee97-40d2-9f38-ae919d48f9e8', 400),
    ('8d96d734-11ea-408d-adc5-763311de5573', 'fcc8d46c-780b-484c-bfe6-5b677805ca3c', 100),
    ('8d96d734-11ea-408d-adc5-763311de5573', 'b89b2345-fc75-44c0-89b3-3e04d74124b4', 2),

    -- Beef Tacos
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', 'eade830d-1bc4-47de-8052-d1b91df42f1d', 400),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', '3f03ff9b-31e5-4d48-a6a4-28d5fd81c898', 8),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', '3eddeece-5867-4416-824d-c627f2aa5979', 100),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', '307f5492-e16a-431a-9cdb-75c9c6815a3f', 150),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', '4b434f28-cef0-4d8b-82a3-01fbf534cf22', 100),

    -- Vegetable Stir Fry
    ('203138be-1e83-4dcb-8130-07a60f2860b4', '09b582cb-a0ea-4c18-bd4f-c814e66c50dd', 2),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', 'f6e83d4b-4d67-4044-9ee8-eedd8b347d4a', 200),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', '683a34ed-13d9-4c92-bf04-27e852716210', 2),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', '69639b8d-d1e8-4cb7-879f-5a9bf362d87c', 3),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', '39d1ac57-8bf9-478e-ac53-3036a240610a', 1),

    -- French Onion Soup
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 'cb2e829b-fa70-4a66-a667-5473e3e8b723', 4),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 'cffb78b8-2cd4-4658-91ca-1da17e4f7a3a', 1),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', '246de5df-8917-4530-8b91-06f7d7d32412', 150),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', '6cd4fe2d-c2c0-4e1e-aa5a-ed773e116747', 4),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', '1b194de6-4e86-4b46-b3a0-bebcbad0e15e', 50),

    -- Banana Pancakes
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 'cc95f369-dda7-42d4-8035-bbaaacea963c', 2),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 'b83a4fc3-e819-4edc-9f40-6841beaa3b80', 150),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', '48b21301-e07c-4118-92b9-f173336093ba', 200),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', '1f821767-f0a9-4e03-ad70-c288f73efd9a', 2),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', '1b194de6-4e86-4b46-b3a0-bebcbad0e15e', 30),

    -- Greek Salad
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', '8ea17cd5-fea4-45f4-bfb6-35f0f04e583c', 3),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', '1834b787-3ceb-49e2-9207-e3de61020049', 1),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', '4725715a-2570-4737-a62c-ac43cbd7ea90', 150),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', '7cf382d1-b6c9-4424-93b8-59ad6e3c879e', 100),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', 'a4378573-1d8d-44f5-80f6-22175b0c4f9c', 3),

    -- Beef Bourguignon
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 'f63a1464-ce64-44e7-9088-046f044645e7', 800),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', '084ed9bf-48c4-42c7-afd1-ca63e6a95b0d', 500),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', '683a34ed-13d9-4c92-bf04-27e852716210', 3),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', '5245895d-bffe-4af2-89eb-7ed5e729d394', 200),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', '655c0e6e-a675-4a10-a0e7-55cde4ec19c6', 150),

    -- Margherita Pizza
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', '332343ec-3fde-4ef0-ae83-97d42ab8f861', 300),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 'ff1ac84b-ee97-40d2-9f38-ae919d48f9e8', 150),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', '373507a5-b659-4ca9-868c-8f39319946b2', 200),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', '6c81d1a3-66f7-42de-9ad1-05a5d748bcc8', 10),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 'a4378573-1d8d-44f5-80f6-22175b0c4f9c', 2),

    -- Chocolate Lava Cake
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', '00885e1e-20a0-4c3d-aa23-ce1bd5ddc385', 150),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', '1b194de6-4e86-4b46-b3a0-bebcbad0e15e', 100),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', '1f821767-f0a9-4e03-ad70-c288f73efd9a', 3),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', '78223711-7e76-4f37-99c1-6c80daa63f2e', 80),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 'b83a4fc3-e819-4edc-9f40-6841beaa3b80', 50);

-- ---------------------------------------------------------
-- RECIPE STEPS
-- ---------------------------------------------------------
INSERT INTO recipe_steps (recipe_id, step_order, steps)
VALUES
    -- Spaghetti Carbonara
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', 0, 'Bring a large pot of well-salted water to a rolling boil, add the pasta, and cook until al dente according to the package instructions, stirring occasionally to prevent sticking and reserving about 1 cup of the pasta water before draining'),
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', 1, 'Fry pancetta'),
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', 2, 'Mix eggs and cheese'),
    ('1ecba55b-a63c-43f6-a849-d736e6773f2d', 3, 'Combine all'),

    -- Chicken Tikka Masala
    ('8d96d734-11ea-408d-adc5-763311de5573', 0, 'Marinate chicken'),
    ('8d96d734-11ea-408d-adc5-763311de5573', 1, 'Grill chicken'),
    ('8d96d734-11ea-408d-adc5-763311de5573', 2, 'Prepare tomato cream sauce'),
    ('8d96d734-11ea-408d-adc5-763311de5573', 3, 'Simmer chicken in sauce'),
    ('8d96d734-11ea-408d-adc5-763311de5573', 4, 'Serve with rice'),

    -- Beef Tacos
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', 0, 'Brown ground beef'),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', 1, 'Season with spices'),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', 2, 'Warm tortillas'),
    ('3265de7a-3053-4e5c-97e6-5050e68aa8d0', 3, 'Assemble tacos with toppings'),

    -- Vegetable Stir Fry
    ('203138be-1e83-4dcb-8130-07a60f2860b4', 0, 'Chop all vegetables'),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', 1, 'Heat wok with oil'),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', 2, 'Stir fry vegetables on high heat'),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', 3, 'Add soy sauce and sesame oil'),
    ('203138be-1e83-4dcb-8130-07a60f2860b4', 4, 'Serve with rice'),

    -- French Onion Soup
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 0, 'Caramelize onions for 30 minutes'),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 1, 'Add beef broth and simmer'),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 2, 'Ladle into bowls'),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 3, 'Top with bread and gruyere'),
    ('07b9c24b-efe4-4ea9-b3a0-f53a7eff9c1d', 4, 'Broil until cheese is golden'),

    -- Banana Pancakes
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 0, 'Mash bananas'),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 1, 'Mix batter ingredients'),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 2, 'Heat pan with butter'),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 3, 'Pour batter and cook until bubbles form'),
    ('ba581df1-3a58-474d-a824-f62f63ce0858', 4, 'Flip and cook other side'),

    -- Greek Salad
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', 0, 'Chop tomatoes and cucumber'),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', 1, 'Slice red onion'),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', 2, 'Add olives and feta'),
    ('3a529f82-0fcc-48d4-99ce-c8b5cf945622', 3, 'Dress with olive oil and oregano'),

    -- Beef Bourguignon
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 0, 'Brown beef in batches'),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 1, 'Sauté onions and carrots'),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 2, 'Add wine and broth'),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 3, 'Braise in oven for 2 hours'),
    ('65fc2e5d-4b24-4db1-bab7-d8172257c32e', 4, 'Add mushrooms and finish'),

    -- Margherita Pizza
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 0, 'Prepare dough and let rise'),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 1, 'Spread tomato sauce'),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 2, 'Add mozzarella'),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 3, 'Bake at high heat for 10 minutes'),
    ('89b1dcef-112f-4226-9cb8-727a57cee5ca', 4, 'Top with fresh basil'),

    -- Chocolate Lava Cake
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 0, 'Melt chocolate and butter'),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 1, 'Whisk eggs and sugar'),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 2, 'Fold in flour'),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 3, 'Pour into ramekins'),
    ('6bd1c2d0-11d1-4efd-a0ea-d94474f5f4c3', 4, 'Bake for 12 minutes and serve immediately');

-- ---------------------------------------------------------
-- USER
-- ---------------------------------------------------------
INSERT INTO users (user_id, email, display_name, provider, provider_id)
VALUES (
           'b0311fb0-533c-40b0-be48-2bd0ba1f33a8',
           'seeduser@cookbook.com',
           'Seed User',
        'google',
        'google'
       );
