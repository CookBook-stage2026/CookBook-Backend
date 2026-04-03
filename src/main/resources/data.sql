-- ======================
-- RECIPES
-- ======================
INSERT INTO recipes (recipe_id, name, description, duration_in_minutes)
VALUES ('11111111-1111-1111-1111-111111111111',
        'Spaghetti Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara',
        'A classic Italian pasta dish with eggs, cheese and pancetta A classic Italian pasta dish with eggs, cheese and pancetta A classic Italian pasta dish with eggs, cheese and pancetta A classic Italian pasta dish with eggs, cheese and pancetta',
        30),

       ('22222222-2222-2222-2222-222222222222', 'Chicken Tikka Masala',
        'Creamy and spiced Indian curry with tender chicken',
        45),

       ('33333333-3333-3333-3333-333333333333', 'Beef Tacos',
        'Mexican street-style tacos with seasoned ground beef',
        25),

       ('44444444-4444-4444-4444-444444444444', 'Vegetable Stir Fry',
        'Quick and healthy Asian-inspired stir fry',
        20),

       ('55555555-5555-5555-5555-555555555555', 'French Onion Soup',
        'Rich and hearty French classic topped with melted cheese',
        60),

       ('66666666-6666-6666-6666-666666666666', 'Banana Pancakes',
        'Fluffy pancakes with a hint of banana',
        20),

       ('77777777-7777-7777-7777-777777777777', 'Greek Salad',
        'Fresh Mediterranean salad with feta and olives',
        15),

       ('88888888-8888-8888-8888-888888888888', 'Beef Bourguignon',
        'Classic French braised beef stew in red wine',
        180),

       ('99999999-9999-9999-9999-999999999999', 'Margherita Pizza',
        'Simple Neapolitan pizza with fresh basil and mozzarella',
        40),

       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Chocolate Lava Cake',
        'Decadent warm chocolate cake with a molten center',
        25);


-- ======================
-- STEPS
-- ======================
INSERT INTO recipe_steps (recipe_id, step_order, steps)
VALUES
-- Carbonara
('11111111-1111-1111-1111-111111111111', 0, 'Boil pasta'),
('11111111-1111-1111-1111-111111111111', 1, 'Fry pancetta'),
('11111111-1111-1111-1111-111111111111', 2, 'Mix eggs and cheese'),
('11111111-1111-1111-1111-111111111111', 3, 'Combine all'),

-- Chicken Tikka Masala
('22222222-2222-2222-2222-222222222222', 0, 'Marinate chicken'),
('22222222-2222-2222-2222-222222222222', 1, 'Grill chicken'),
('22222222-2222-2222-2222-222222222222', 2, 'Prepare tomato cream sauce'),
('22222222-2222-2222-2222-222222222222', 3, 'Simmer chicken in sauce'),
('22222222-2222-2222-2222-222222222222', 4, 'Serve with rice'),

-- Beef Tacos
('33333333-3333-3333-3333-333333333333', 0, 'Brown ground beef'),
('33333333-3333-3333-3333-333333333333', 1, 'Season with spices'),
('33333333-3333-3333-3333-333333333333', 2, 'Warm tortillas'),
('33333333-3333-3333-3333-333333333333', 3, 'Assemble tacos with toppings'),

-- Vegetable Stir Fry
('44444444-4444-4444-4444-444444444444', 0, 'Chop all vegetables'),
('44444444-4444-4444-4444-444444444444', 1, 'Heat wok with oil'),
('44444444-4444-4444-4444-444444444444', 2, 'Stir fry vegetables on high heat'),
('44444444-4444-4444-4444-444444444444', 3, 'Add soy sauce and sesame oil'),
('44444444-4444-4444-4444-444444444444', 4, 'Serve with rice'),

-- French Onion Soup
('55555555-5555-5555-5555-555555555555', 0, 'Caramelize onions for 30 minutes'),
('55555555-5555-5555-5555-555555555555', 1, 'Add beef broth and simmer'),
('55555555-5555-5555-5555-555555555555', 2, 'Ladle into bowls'),
('55555555-5555-5555-5555-555555555555', 3, 'Top with bread and gruyere'),
('55555555-5555-5555-5555-555555555555', 4, 'Broil until cheese is golden'),

-- Banana Pancakes
('66666666-6666-6666-6666-666666666666', 0, 'Mash bananas'),
('66666666-6666-6666-6666-666666666666', 1, 'Mix batter ingredients'),
('66666666-6666-6666-6666-666666666666', 2, 'Heat pan with butter'),
('66666666-6666-6666-6666-666666666666', 3, 'Pour batter and cook until bubbles form'),
('66666666-6666-6666-6666-666666666666', 4, 'Flip and cook other side'),

-- Greek Salad
('77777777-7777-7777-7777-777777777777', 0, 'Chop tomatoes and cucumber'),
('77777777-7777-7777-7777-777777777777', 1, 'Slice red onion'),
('77777777-7777-7777-7777-777777777777', 2, 'Add olives and feta'),
('77777777-7777-7777-7777-777777777777', 3, 'Dress with olive oil and oregano'),

-- Beef Bourguignon
('88888888-8888-8888-8888-888888888888', 0, 'Brown beef in batches'),
('88888888-8888-8888-8888-888888888888', 1, 'Sauté onions and carrots'),
('88888888-8888-8888-8888-888888888888', 2, 'Add wine and broth'),
('88888888-8888-8888-8888-888888888888', 3, 'Braise in oven for 2 hours'),
('88888888-8888-8888-8888-888888888888', 4, 'Add mushrooms and finish'),

-- Margherita Pizza
('99999999-9999-9999-9999-999999999999', 0, 'Prepare dough and let rise'),
('99999999-9999-9999-9999-999999999999', 1, 'Spread tomato sauce'),
('99999999-9999-9999-9999-999999999999', 2, 'Add mozzarella'),
('99999999-9999-9999-9999-999999999999', 3, 'Bake at high heat for 10 minutes'),
('99999999-9999-9999-9999-999999999999', 4, 'Top with fresh basil'),

-- Chocolate Lava Cake
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 0, 'Melt chocolate and butter'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, 'Whisk eggs and sugar'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 2, 'Fold in flour'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 3, 'Pour into ramekins'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 4, 'Bake for 12 minutes and serve immediately');


-- ======================
-- INGREDIENTS
-- ======================
INSERT INTO ingredients (id, name, quantity, unit, recipe_id)
VALUES
-- Carbonara
('i1', 'Spaghetti', 200, 'gram', '11111111-1111-1111-1111-111111111111'),
('i2', 'Pancetta', 100, 'gram', '11111111-1111-1111-1111-111111111111'),
('i3', 'Eggs', 2, NULL, '11111111-1111-1111-1111-111111111111'),

-- Chicken Tikka
('i4', 'Chicken breast', 500, 'gram', '22222222-2222-2222-2222-222222222222'),
('i5', 'Yogurt', 200, 'gram', '22222222-2222-2222-2222-222222222222'),
('i6', 'Tomato sauce', 400, 'gram', '22222222-2222-2222-2222-222222222222'),
('i7', 'Heavy cream', 100, 'ml', '22222222-2222-2222-2222-222222222222'),
('i8', 'Garam masala', 2, 'tsp', '22222222-2222-2222-2222-222222222222'),

-- Beef Tacos
('i9', 'Ground beef', 400, 'gram', '33333333-3333-3333-3333-333333333333'),
('i10', 'Tortillas', 8, NULL, '33333333-3333-3333-3333-333333333333'),
('i11', 'Cheddar cheese', 100, 'gram', '33333333-3333-3333-3333-333333333333'),
('i12', 'Salsa', 150, 'gram', '33333333-3333-3333-3333-333333333333'),
('i13', 'Sour cream', 100, 'gram', '33333333-3333-3333-3333-333333333333'),

-- Vegetable Stir Fry
('i14', 'Bell peppers', 2, NULL, '44444444-4444-4444-4444-444444444444'),
('i15', 'Broccoli', 200, 'gram', '44444444-4444-4444-4444-444444444444'),
('i16', 'Carrots', 2, NULL, '44444444-4444-4444-4444-444444444444'),
('i17', 'Soy sauce', 3, 'tbsp', '44444444-4444-4444-4444-444444444444'),
('i18', 'Sesame oil', 1, 'tbsp', '44444444-4444-4444-4444-444444444444'),

-- French Onion Soup
('i19', 'Onions', 4, NULL, '55555555-5555-5555-5555-555555555555'),
('i20', 'Beef broth', 1, 'liter', '55555555-5555-5555-5555-555555555555'),
('i21', 'Gruyere cheese', 150, 'gram', '55555555-5555-5555-5555-555555555555'),
('i22', 'Baguette', 4, NULL, '55555555-5555-5555-5555-555555555555'),
('i23', 'Butter', 50, 'gram', '55555555-5555-5555-5555-555555555555'),

-- Banana Pancakes
('i24', 'Bananas', 2, NULL, '66666666-6666-6666-6666-666666666666'),
('i25', 'Flour', 150, 'gram', '66666666-6666-6666-6666-666666666666'),
('i26', 'Milk', 200, 'ml', '66666666-6666-6666-6666-666666666666'),
('i27', 'Eggs', 2, NULL, '66666666-6666-6666-6666-666666666666'),
('i28', 'Butter', 30, 'gram', '66666666-6666-6666-6666-666666666666'),

-- Greek Salad
('i29', 'Tomatoes', 3, NULL, '77777777-7777-7777-7777-777777777777'),
('i30', 'Cucumber', 1, NULL, '77777777-7777-7777-7777-777777777777'),
('i31', 'Feta cheese', 150, 'gram', '77777777-7777-7777-7777-777777777777'),
('i32', 'Kalamata olives', 100, 'gram', '77777777-7777-7777-7777-777777777777'),
('i33', 'Olive oil', 3, 'tbsp', '77777777-7777-7777-7777-777777777777'),

-- Beef Bourguignon
('i34', 'Beef chuck', 800, 'gram', '88888888-8888-8888-8888-888888888888'),
('i35', 'Red wine', 500, 'ml', '88888888-8888-8888-8888-888888888888'),
('i36', 'Carrots', 3, NULL, '88888888-8888-8888-8888-888888888888'),
('i37', 'Mushrooms', 200, 'gram', '88888888-8888-8888-8888-888888888888'),
('i38', 'Bacon', 150, 'gram', '88888888-8888-8888-8888-888888888888'),

-- Margherita Pizza
('i39', 'Pizza dough', 300, 'gram', '99999999-9999-9999-9999-999999999999'),
('i40', 'Tomato sauce', 150, 'gram', '99999999-9999-9999-9999-999999999999'),
('i41', 'Fresh mozzarella', 200, 'gram', '99999999-9999-9999-9999-999999999999'),
('i42', 'Fresh basil', 10, 'gram', '99999999-9999-9999-9999-999999999999'),
('i43', 'Olive oil', 2, 'tbsp', '99999999-9999-9999-9999-999999999999'),

-- Chocolate Lava Cake
('i44', 'Dark chocolate', 150, 'gram', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('i45', 'Butter', 100, 'gram', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('i46', 'Eggs', 3, NULL, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('i47', 'Sugar', 80, 'gram', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('i48', 'Flour', 50, 'gram', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');