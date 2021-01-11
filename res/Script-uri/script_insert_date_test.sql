-- Categorii_nr_meniu_nume_cat_UN
-- Ingrediente_nume_ingr_prod_UN

-- Inserare in tabela administratori

INSERT INTO Administratori(nume_prenume_cont, parola_cont) VALUES('Cojocaru Constantin-Cosmin', 'JLMOPR');
INSERT INTO Administratori(nume_prenume_cont, parola_cont) VALUES('Budeanu Radu-Andrei', 'JLMOPR');
INSERT INTO Administratori(nume_prenume_cont, parola_cont) VALUES('Alexandru Gabriel', 'JLMOPR');
INSERT INTO Administratori(nume_prenume_cont, parola_cont) VALUES('Ionut Popescu', 'JLMOPR');
INSERT INTO Administratori(nume_prenume_cont, parola_cont) VALUES('Alin Popescu', 'JLMOPR');

-- Inserare in tabela meniuri

INSERT INTO Meniuri(nume_meniu) VALUES('Meniu an 2019');
INSERT INTO Meniuri(nume_meniu) VALUES('Meniu an 2018');
INSERT INTO Meniuri(nume_meniu) VALUES('Meniu an 2017');
INSERT INTO Meniuri(nume_meniu) VALUES('Meniu an 2016');
INSERT INTO Meniuri(nume_meniu) VALUES('Meniu an 2015');

------------------------------------------ MENIUL 2019 ------------------------------------------

-- Inserare in tabela categorii

INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Preparate lichide', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Preparate din peste', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Preparate din carne', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Pizza', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Salate', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));

INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Vinuri', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Bere', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Bauturi non-alcoolice', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Bauturi spirtoase', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2019'));

------------------------------------------ MENIUL 2018 ------------------------------------------

INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Preparate lichide', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Preparate din peste', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Preparate din carne', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Pizza', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Salate', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));

INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Vinuri', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Bere', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Bauturi non-alcoolice', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));
INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu) VALUES('Bauturi spirtoase', (SELECT nr_meniu FROM meniuri where nume_meniu = 'Meniu an 2018'));

-- Inserare in tabela tipuri_aliment

INSERT INTO tipuri_aliment(nume_tip) VALUES('post');
INSERT INTO tipuri_aliment(nume_tip) VALUES('carne');
INSERT INTO tipuri_aliment(nume_tip) VALUES('peste');
INSERT INTO tipuri_aliment(nume_tip) VALUES('lactat');
INSERT INTO tipuri_aliment(nume_tip) VALUES('salata');
INSERT INTO tipuri_aliment(nume_tip) VALUES('lichid');
INSERT INTO tipuri_aliment(nume_tip) VALUES('fructe');
INSERT INTO tipuri_aliment(nume_tip) VALUES('legume');
INSERT INTO tipuri_aliment(nume_tip) VALUES('dulce');
INSERT INTO tipuri_aliment(nume_tip) VALUES('cereale');
INSERT INTO tipuri_aliment(nume_tip) VALUES('fainoase');
INSERT INTO tipuri_aliment(nume_tip) VALUES('alcoolic');
INSERT INTO tipuri_aliment(nume_tip) VALUES('non-alcoolic');

-- Inserare in tabela Produse

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Supa cu legume', 'preparat', 20, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'lichid'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Supa cu taitei', 'preparat', 25, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'lichid'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Ciorba de vita cu legume', 'preparat', 20, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'lichid'));

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Pastrav la gratar cu legume', 'preparat', 35, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'lichid'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Somon afumat', 'preparat', 23, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'peste'));

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Cotlet de porc la gratar', 'preparat', 22, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Friptura de vita cu legume', 'preparat', 22, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Piept de pui la gratar cu cartofi', 'preparat', 18, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Specialtatea casei', 'preparat', 18, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Pizza cu piept de pui', 'preparat', 18, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Pizza de post', 'preparat', 15, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'post'));

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Salata de rosii si castraveti', 'preparat', 20, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'salata'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Salata de spanac cu pui', 'preparat', 25, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'salata'));

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Timisoreana 330 ml', 'bautura', 5, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'alcoolic'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Ursus 330 ml', 'bautura', 6, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'alcoolic'));

INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Apa plata 0.5 l', 'bautura', 4, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'non-alcoolic'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Suc portocale 330 ml', 'bautura', 5, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'non-alcoolic'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Limonada 330 ml', 'bautura', 5, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'non-alcoolic'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Fanta 330 ml', 'bautura', 4, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'non-alcoolic'));
INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip) VALUES('Pepsi 330 ml', 'bautura', 5, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'non-alcoolic'));

-- Inserare in tabela categorii_produse MENIU 2019

INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate lichide' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Supa cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate lichide' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Supa cu taitei'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate lichide' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Ciorba de vita cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din peste' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pastrav la gratar cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din peste' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Somon afumat'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din carne' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Cotlet de porc la gratar'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din carne' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Friptura de vita cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din carne' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Piept de pui la gratar cu cartofi'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Pizza' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Specialtatea casei'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Pizza' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pizza cu piept de pui'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Pizza' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pizza de post'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Salate' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de rosii si castraveti'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Salate' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de spanac cu pui'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bere' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Timisoreana 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bere' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Ursus 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Apa plata 0.5 l'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Suc portocale 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Limonada 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Fanta 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2019')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pepsi 330 ml'));

-- Inserare in tabela categorii_produse MENIU 2018

INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate lichide' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Supa cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate lichide' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Supa cu taitei'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate lichide' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Ciorba de vita cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din peste' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pastrav la gratar cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din peste' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Somon afumat'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din carne' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Cotlet de porc la gratar'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din carne' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Friptura de vita cu legume'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Preparate din carne' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Piept de pui la gratar cu cartofi'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Pizza' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Specialtatea casei'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Pizza' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pizza cu piept de pui'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Pizza' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pizza de post'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Salate' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de rosii si castraveti'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Salate' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de spanac cu pui'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bere' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Timisoreana 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bere' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Ursus 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Apa plata 0.5 l'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Suc portocale 330 ml'));
INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = 'Bauturi non-alcoolice' and meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = 'Meniu an 2018')), (SELECT nr_produs FROM Produse WHERE nume_produs = 'Limonada 330 ml'));

-- Inserare in tabela Ingrdiente

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('somn', 50, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'peste'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('pastrav', 30, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'peste'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('file de somon', 20, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'peste'));

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('cotlet de porc', 50, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('bucati carne de vita', 60, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('piept de pui', 35, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('salam de porc', 20, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('salam de vita', 30, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'carne'));

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('cascaval', 30, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'lactat'));

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('rosii', 70, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'legume'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('castraveti', 120, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'legume'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('amestec de legume', 75, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'legume'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('cartofi', 120, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'legume'));

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('orez', 30, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'cereale'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('pachet taitei', 30, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'fainoase'));

INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('lamaie', 40, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'fructe'));
INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, tipuri_aliment_id_tip) VALUES('portocala', 55, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = 'fructe'));

-- Inserare in tabela Retete

INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Supa cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'amestec de legume' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Supa cu taitei'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'pachet taitei' and producator IS NULL), 0.5);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Ciorba de vita cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'bucati carne de vita' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Ciorba de vita cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'amestec de legume' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Pastrav la gratar cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'pastrav' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Pastrav la gratar cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'lamaie'), 0.5);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Somon afumat'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'file de somon' and producator IS NULL), 2);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Cotlet de porc la gratar'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'cotlet de porc' and producator IS NULL), 2);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Friptura de vita cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'bucati carne de vita' and producator IS NULL), 2);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Friptura de vita cu legume'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'amestec de legume' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Piept de pui la gratar cu cartofi'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'piept de pui' and producator IS NULL), 0.5);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Piept de pui la gratar cu cartofi'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'cartofi' and producator IS NULL), 4);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Specialtatea casei'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'salam de porc' and producator IS NULL), 0.5);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Specialtatea casei'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'cascaval' and producator IS NULL), 1.5);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Pizza cu piept de pui'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'piept de pui' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de rosii si castraveti'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'rosii' and producator IS NULL), 3);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de rosii si castraveti'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'castraveti' and producator IS NULL), 3);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Pizza de post'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'amestec de legume' and producator IS NULL), 1);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Salata de spanac cu pui'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'piept de pui' and producator IS NULL), 0.5);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Suc portocale 330 ml'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'portocala' and producator IS NULL), 3);
INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = 'Limonada 330 ml'), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = 'lamaie' and producator IS NULL), 2);

-- Inserare in tabela stocuri_produs

INSERT INTO stocuri_produs(stoc_produs, Produse_nr_produs) VALUES(30, (SELECT nr_produs FROM Produse WHERE nume_produs = 'Timisoreana 330 ml'));
INSERT INTO stocuri_produs(stoc_produs, Produse_nr_produs) VALUES(25, (SELECT nr_produs FROM Produse WHERE nume_produs = 'Ursus 330 ml'));
INSERT INTO stocuri_produs(stoc_produs, Produse_nr_produs) VALUES(50, (SELECT nr_produs FROM Produse WHERE nume_produs = 'Apa plata 0.5 l'));
INSERT INTO stocuri_produs(stoc_produs, Produse_nr_produs) VALUES(25, (SELECT nr_produs FROM Produse WHERE nume_produs = 'Fanta 330 ml'));
INSERT INTO stocuri_produs(stoc_produs, Produse_nr_produs) VALUES(20, (SELECT nr_produs FROM Produse WHERE nume_produs = 'Pepsi 330 ml'));