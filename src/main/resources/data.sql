INSERT INTO Owner (name, email) VALUES ('John', 'john@beatles.com');
INSERT INTO Owner (name, email) VALUES ('Paul', 'paul@beatles.com');
INSERT INTO Owner (name, email) VALUES ('Ringo', 'ringo@beatles.com');

INSERT INTO Ewallet (owner, currency, amount, name) VALUES(1, 'USD', 100.0, 'Johns USD wallet');
INSERT INTO Ewallet (owner, currency, amount, name) VALUES(1, 'EUR', 100.0, 'Johns EUR wallet');
INSERT INTO Ewallet (owner, currency, amount, name) VALUES(1, 'EUR', 200.0, 'Johns second EUR wallet');
INSERT INTO Ewallet (owner, currency, amount, name) VALUES(2, 'EUR', 0.0, 'Pauls EUR wallet');
INSERT INTO Ewallet (owner, currency, amount, name) VALUES(3, 'USD', 56.70, 'Ringos USD wallet');
INSERT INTO Ewallet (owner, currency, amount, name) VALUES(3, 'EUR', 20.0, 'Ringos EUR wallet');