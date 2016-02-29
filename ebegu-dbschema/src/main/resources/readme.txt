Achtung, für die Datenbankskripts gelten folgende Regeln:
=========================================================

1.  Constraints: Alle Constraints (PK, FK, Unique) müssen einen Namen haben. Andernfalls können sie nicht mehr über Flyway entfernt werden, falls notwendig
    Namenskonvention:   Primary Key Constraint: pk_<table>
                        Foreign Key Constraint: fk_<target-table>_<source-table>
                        Unique Constraints: uc_<table>_<attribute>
    Beispiele:
        constraint PK_Amt primary key (id),
        constraint UK_Amt_visum unique (visum)
        alter table Organisationseinheit
                add constraint FK_Organisationseinheit_Amt
                foreign key (amt_id)
                references Amt;

2. Namenskonvention: Wir lassen immer den defaultmässig generierten Namen, also nicht mit @JoinColumn(name=XXX) etc. überschreiben. Ausnahmen müssen
    begründet sein.

3.  Schema generierung: Alle Skripts sollten wenn möglich über die Schema-Generierung (hbm2ddl, aktivierbar über Profile "generate-ddl") erstellt werden.
    Ansosnten ist die Gefahr gross, dass Code und Tabellendefinitionen nicht mehr übereinstimmen


4. @NotNull vs. @Column(nullable=false): Ersteres fliesst leider nicht in die Schema-Generierung mit ein. Daher müssen immer beide zusammen verwendet werden.
   Konvention: Wir verwenden die @Column-Annotation grundsätzlich immer (egal ob nullable oder nicht), obwohl nullable der default wäre.
   Damit stellen wir sicher, dass wir unterscheiden können, ob ein Wert absichtlich nicht gesetzt wurde, oder vergessen gegangen ist

5. Reihenfolge der Skripts: Da theoretisch mehrere Personen gleichzeitig in ihren Feature-Branches neue Skripts erstellen können, ist es Sache des
    Reviewers, die richtige Reihenfolge sicherzustellen und eventuell das Skript umzubenennen.