-- Stergere tabele

-- nume_prenume     REGEXP_LIKE(nume_prenume_cont, '^([A-Z][a-z]+)\ ([A-Z][a-z]+)(\-[A-Z][a-z]+)?$')
-- parola           REGEXP_LIKE(parola_cont, '^\S+$')
-- nume_meniu v1    REGEXP_LIKE(nume_meniu, '^[A-Z][a-z]+([\ \-]([A-Z]?[a-z]+|[1-9][0-9]*))*$')
-- nume_meniu v2    REGEXP_LIKE(nume_meniu, '^[A-Z][a-z]+([\ \-]?[A-Z]?[a-z0-9]+)*$')
-- nume_tip         REGEXP_LIKE(nume_tip, '^[a-z]+\-?[a-z]+$')
-- nume_categorie   REGEXP_LIKE(nume_categorie, '^[A-Z][a-z]+([\ \-][a-z]+)*$')
-- nume_produs      REGEXP_LIKE(nume_produs, '^[A-Z][a-z]+([\ \-]([A-Z]?[a-z]+|[0-9]+(\.[0-9]*[1-9])?))*$')
-- nume_ingredient  REGEXP_LIKE(nume_ingredient, '^[a-z]+(\ [a-z]+)*$')

BEGIN
   FOR cur_rec IN (SELECT object_name, object_type
                     FROM user_objects
                    WHERE object_type IN
                             ('TABLE',
                              'VIEW',
                              'PACKAGE',
                              'PROCEDURE',
                              'FUNCTION',
                              'SEQUENCE',
                              'SYNONYM',
                              'PACKAGE BODY'
                             ))
   LOOP
      BEGIN
         IF cur_rec.object_type = 'TABLE'
         THEN
            EXECUTE IMMEDIATE    'DROP '
                              || cur_rec.object_type
                              || ' "'
                              || cur_rec.object_name
                              || '" CASCADE CONSTRAINTS';
         ELSE
            EXECUTE IMMEDIATE    'DROP '
                              || cur_rec.object_type
                              || ' "'
                              || cur_rec.object_name
                              || '"';
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.put_line (   'FAILED: DROP '
                                  || cur_rec.object_type
                                  || ' "'
                                  || cur_rec.object_name
                                  || '"'
                                 );
      END;
   END LOOP;
END;
