UPDATE kind SET kinderabzug='GANZER_ABZUG' WHERE kinderabzug='KEINE_STEUERERKLAERUNG';
UPDATE kind_aud SET kinderabzug='GANZER_ABZUG' WHERE kinderabzug='KEINE_STEUERERKLAERUNG';

/* Delete all files for Sorgerechtsvereinbarung */
DELETE FROM dokument WHERE dokument_grund_id =  (SELECT id FROM dokument_grund WHERE dokument_typ='SORGERECHTSVEREINBARUNG');
DELETE FROM dokument_grund WHERE dokument_typ='SORGERECHTSVEREINBARUNG';
DELETE FROM dokument_aud WHERE dokument_grund_id =  (SELECT id FROM dokument_grund_aud WHERE dokument_typ='SORGERECHTSVEREINBARUNG');
DELETE FROM dokument_grund_aud WHERE dokument_typ='SORGERECHTSVEREINBARUNG';