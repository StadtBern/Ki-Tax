UPDATE erwerbspensum
SET taetigkeit = 'GESUNDHEITLICHE_EINSCHRAENKUNGEN'
WHERE gesundheitliche_einschraenkungen = 1;

UPDATE erwerbspensum_aud
SET taetigkeit = 'GESUNDHEITLICHE_EINSCHRAENKUNGEN'
WHERE gesundheitliche_einschraenkungen = 1;

ALTER TABLE erwerbspensum DROP gesundheitliche_einschraenkungen;
ALTER TABLE erwerbspensum_aud DROP gesundheitliche_einschraenkungen;