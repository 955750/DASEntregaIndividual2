package com.example.dasentregaindividual2.base_de_datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.dasentregaindividual2.R;

import java.util.ArrayList;

public class BaseDeDatos extends SQLiteOpenHelper {

    public BaseDeDatos(
        @Nullable Context context,
        @Nullable String name,
        @Nullable SQLiteDatabase.CursorFactory factory,
        int version
    ) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        crearTablas(sqLiteDatabase);
        añadirUsuarios(sqLiteDatabase);
        añadirEquipos(sqLiteDatabase);
        añadirJugadores(sqLiteDatabase);
        añadirPartidos(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Usuario");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Equipo");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Jugador");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Partido");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Favorito");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Juega");
        onCreate(sqLiteDatabase);
    }

    private void crearTablas(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS Usuario (" +
                " 'nombre_usuario' TEXT PRIMARY KEY NOT NULL, " +
                " 'contraseña' TEXT NOT NULL" +
            ")"
        );

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS Equipo (" +
                " 'nombre' TEXT PRIMARY KEY NOT NULL," +
                " 'escudo_id' INTEGER NOT NULL, " +
                " 'part_ganados_tot' INTEGER NOT NULL, " +
                " 'part_perdidos_tot' INTEGER NOT NULL, " +
                " 'puntos_favor_tot' INTEGER NOT NULL, " +
                " 'puntos_contra_tot' INTEGER NOT NULL, " +
                " 'part_ganados_ult_10' INTEGER NOT NULL, " +
                " 'part_perdidos_ult_10' INTEGER NOT NULL" +
            ")"
        );

        sqLiteDatabase.execSQL(
            "CREATE TABLE Jugador (" +
                " 'numero' INTEGER NOT NULL, " +
                " 'nombre_equipo' TEXT NOT NULL, " +
                " 'nombre' TEXT NOT NULL, " +
                " 'nacionalidad' TEXT, " +
                " 'altura' FLOAT, " +
                " 'fecha_nacimiento' TEXT, " +
                " PRIMARY KEY('numero', 'nombre_equipo'), " +
                " FOREIGN KEY('nombre_equipo') REFERENCES Equipo('nombre')" +
            ")"
        );

        sqLiteDatabase.execSQL(
            "CREATE TABLE Partido (" +
                " 'id' INTEGER NOT NULL, " +
                " 'numero_jornada' INTEGER NOT NULL, " +
                " 'fecha' TEXT NOT NULL, " +
                " 'hora' TEXT NOT NULL, " +
                " PRIMARY KEY('id', 'numero_jornada')" +
            ")"
        );

        sqLiteDatabase.execSQL(
            "CREATE TABLE Juega (" +
                " 'nombre_equipo' TEXT NOT NULL, " +
                " 'partido_id' INTEGER NOT NULL, " +
                " 'partido_num_jornada' INTEGER NOT NULL," +
                " 'puntos' INTEGER NOT NULL, " +
                " 'local' INTEGER NOT NULL, " +
                " PRIMARY KEY('nombre_equipo', 'partido_id', 'partido_num_jornada')," +
                " FOREIGN KEY('nombre_equipo') REFERENCES Equipo('nombre')," +
                " FOREIGN KEY('partido_id', 'partido_num_jornada')" +
                " REFERENCES Partido('id', 'numero_jornada')" +
            ")"
        );

        sqLiteDatabase.execSQL(
            "CREATE TABLE Favorito (" +
                " 'nombre_usuario' TEXT NOT NULL, " +
                " 'nombre_equipo' TEXT NOT NULL, " +
                " PRIMARY KEY('nombre_usuario', 'nombre_equipo'), " +
                " FOREIGN KEY('nombre_usuario') REFERENCES Usuario('nombre_usuario'), " +
                " FOREIGN KEY('nombre_equipo') REFERENCES Equipo('nombre')" +
            ")"
        );
    }

    private void añadirUsuarios(SQLiteDatabase sqLiteDatabase) {
        Log.d("BaseDeDatos", "añadirUsuarios");
        sqLiteDatabase.execSQL(
            "INSERT INTO Usuario (" +
                "'nombre_usuario', 'contraseña'" +
            ")" +
            "VALUES ('julen_fuentes', 'patata123')"
        );
        sqLiteDatabase.execSQL(
            "INSERT INTO Usuario (" +
                "'nombre_usuario', 'contraseña'" +
            ")" +
            "VALUES ('iker_sobron', 'patata456')"
        );
    }

    private void añadirEquipos(SQLiteDatabase sqLiteDatabase) {
        Log.d("BaseDeDatos", "añadirEquipos");
        String[] nombres = {"Alba Berlin", "Anadolu Efes", "AS Monaco", "Baskonia", "Bayern Munich",
                            "Crvena Zvezda", "Emporio Armani Milan", "FC Barcelona", "Fenerbahce",
                            "LDLC Asvel Villeurbane", "Maccabi Tel Aviv", "Olympiacos",
                            "Panathinaikos", "Partizan Belgrade", "Real Madrid", "Valencia Basket",
                            "Virtus Bologna", "Zalgiris Kaunas"};
        Integer[] idsEscudos = {R.drawable.escudo_alba_berlin, R.drawable.escudo_anadolu_efes,
            R.drawable.escudo_as_monaco, R.drawable.escudo_baskonia, R.drawable.escudo_bayern_munich,
            R.drawable.escudo_crvena_zvezda, R.drawable.escudo_emporio_armani_milan,
            R.drawable.escudo_fc_barcelona, R.drawable.escudo_fenerbahce,
            R.drawable.escudo_ldlc_asvel_villeurbanne, R.drawable.escudo_maccabi_tel_aviv,
            R.drawable.escudo_olympiacos, R.drawable.escudo_panathinaikos,
            R.drawable.escudo_partizan_belgrade, R.drawable.escudo_real_madrid,
            R.drawable.escudo_valencia_basket, R.drawable.escudo_virtus_bologna,
            R.drawable.escudo_zalgiris_kaunas
        };
        Integer[] partGanadosTot = {7, 13, 16, 14, 10, 11, 10, 18, 16, 8, 13, 18, 8, 14, 18, 13,
                                    12, 13};
        Integer[] partPerdidosTot = {19, 12, 10, 12, 16, 15, 15, 8, 9, 18, 13, 8, 18, 12, 7, 13,
                                     14, 13};
        Integer[] puntosFavorTot = {2077, 2046, 2135, 2216, 2002, 1944, 1799, 2085, 2100, 1953,
                                    2136, 2202, 2020, 2185, 2080, 2123, 2018, 1964};
        Integer[] puntosContraTot = {2201, 1964, 2104, 2144, 2058, 2024, 1886, 1978, 2004, 2090,
                                     2159, 1979, 2125, 2123, 1902, 2198, 2102, 2044};
        Integer[] partGanadosUlt10 = {3, 5, 5, 3, 5, 3, 5, 7, 6, 2, 4, 8, 2, 7, 8, 6, 5, 5};
        Integer[] partPerdidosUlt10 = {7, 5, 5, 7, 5, 7, 5, 3, 4, 8, 6, 2, 8, 3, 2, 4, 5, 5};

        for (int i = 0; i < 18; i++) {
            ContentValues nuevoEquipo = new ContentValues();
            nuevoEquipo.put("nombre", nombres[i]);
            nuevoEquipo.put("escudo_id", idsEscudos[i]);
            nuevoEquipo.put("part_ganados_tot", partGanadosTot[i]);
            nuevoEquipo.put("part_perdidos_tot", partPerdidosTot[i]);
            nuevoEquipo.put("puntos_favor_tot", puntosFavorTot[i]);
            nuevoEquipo.put("puntos_contra_tot", puntosContraTot[i]);
            nuevoEquipo.put("part_ganados_ult_10", partGanadosUlt10[i]);
            nuevoEquipo.put("part_perdidos_ult_10", partPerdidosUlt10[i]);
            sqLiteDatabase.insert("Equipo", null, nuevoEquipo);
        }
    }

    private void añadirJugadores(SQLiteDatabase sqLiteDatabase) {
        String[] nombresEquipos = {"Alba Berlin", "Anadolu Efes", "AS Monaco", "Baskonia",
                "Bayern Munich", "Crvena Zvezda", "Emporio Armani Milan", "FC Barcelona",
                "Fenerbahce", "LDLC Asvel Villeurbane", "Maccabi Tel Aviv", "Olympiacos",
                "Panathinaikos", "Partizan Belgrade", "Real Madrid", "Valencia Basket",
                "Virtus Bologna", "Zalgiris Kaunas"
        };

        /*
        Información de los jugadores de todos los equipos
        */
        ArrayList<Integer[]> numerosEquipos = new ArrayList<>();
        ArrayList<String[]> nombresJugadoresEquipos = new ArrayList<>();

        /* ALBA BERLIN */
        Integer[] numerosAlba = {0, 1, 2, 3, 6, 7, 8, 9, 10, 11, 14, 19, 21, 25, 32, 43, 44, 45, 50};
        String[] nombresJugadoresAlba = {"Maodo Lo", "Gabriele Procida", "Nils Machowski",
                "Jaleen Smith", "Malte Delow", "Yanni Wetzell", "Marcus Eriksson",
                "Jonas Mattisseck", "Tim Schneider", "Rikus Schulte", "Linus Ruf", "Louis Olinde",
                "Christ Koumadje", "Elias Rapieque", "Johaness Thiemann", "Luke Sikma",
                "Ben Lammers", "Tamir Blatt", "Yovel Zoosman"
        };
        numerosEquipos.add(numerosAlba);
        nombresJugadoresEquipos.add(nombresJugadoresAlba);

        /* ANADOLU EFES */
        Integer[] numerosEfes = {0, 1, 2, 4, 5, 6, 11, 12, 14, 18, 19, 21, 22, 24, 41, 42};
        String[] nombresJugadoresEfes = {"Shane Larkin", "Rodrigue Beaubois", "Chris Singleton",
                "Dogus Balbay", "Karahan Efeoglu", "Elijah Bryant", "Erten Gazi", "Will Clyburn",
                "Furkan Haltali", "Egehan Arna", "Bugrahan Tuncer", "Tibor Pleiss",
                "Vasilije Micic", "Amath M'Baye", "Ante Zizic", "Bryant Dunston"
        };
        numerosEquipos.add(numerosEfes);
        nombresJugadoresEquipos.add(nombresJugadoresEfes);

        /* AS MONACO */
        Integer[] numerosMonaco = {0, 1, 2, 3, 4, 5, 6, 11, 20, 23, 24, 32, 45, 55, 99};
        String[] nombresJugadoresMonaco = {"Elie Okobo", "Chima Moneke", "Matys Pommier",
                "Jordan Loyd", "Jaron Blossomgame", "Joan Makoundou", "Ambroise Couture",
                "Alpha Diallo", "Donatas Montiejunas", "Mohammad Amini", "Yakuba Ouattara",
                "Matthew Strazel", "Donta Hall", "Mike James", "John Brown"
        };
        numerosEquipos.add(numerosMonaco);
        nombresJugadoresEquipos.add(nombresJugadoresMonaco);

        /* BASKONIA */
        Integer[] numerosBaskonia = {0, 1, 2, 4, 6, 7, 8, 9, 11, 13, 14, 19, 20, 21, 23, 24, 31, 34, 47};
        String[] nombresJugadoresBaskonia = {"Markus Howard", "Max Heidegger", "Sander Raieste",
                "Joseba Querejeta", "Pavel Savkov", "Pierria Henry", "Tadas Sederkerskis",
                "Vanja Marinkovic", "Daniel Diez", "Darius Thompson", "Sergej Macura", "Pape Sow",
                "Ousmane Ndiaye", "Maik Kotsar", "Steven Enoch", "Matthew Costello",
                "Rokas Giedraitis", "Daulton Hommes", "Arturs Kurucs"
        };
        numerosEquipos.add(numerosBaskonia);
        nombresJugadoresEquipos.add(nombresJugadoresBaskonia);

        /* BAYERN MUNICH */
        Integer[] numerosBayern = {0, 2, 4, 5, 7, 8, 9, 10, 11, 13, 16, 17, 20, 21, 33, 45};
        String [] nombresJugadoresBayern = {"Nick Weiler-Babb", "Corey Walden", "Dennis Seeley",
                "Cassius Winston", "Niels Giffey", "Othello Hunter", "Isaac Bonga",
                "Ognjen Jaramaz", "Vladimir Lucic", "Andreas Obst", "Paul Zipser", "Niklas Wimberg",
                "Elias Harris", "Augustine Rubit", "Freddie Gillespie", "Zylan Anthony Cheatham"
        };
        numerosEquipos.add(numerosBayern);
        nombresJugadoresEquipos.add(nombresJugadoresBayern);

        /* CRVENA ZVEZDA */
        Integer[] numerosCrvena = {0, 1, 2, 7, 9, 10, 12, 13, 15, 20, 22, 26, 27, 32, 33, 50};
        String[] nombresJugadoresCrvena = {"John Holland", "Luca Vildoza", "Stefan Lazarevic",
                "Facundo Campazzo", "Luka Mitrovic", "Branko Lazic", "Hassan Martin",
                "Ognjen Dobric", "Miroslav Raduljica", "Nikola Ivanovic", "Dalibor Ilic",
                "Nemanja Nedovic", "Stefan Markovic", "Ognjen Kuzmic", "Filip Petrusev",
                "Benjamin Bentil"
        };
        numerosEquipos.add(numerosCrvena);
        nombresJugadoresEquipos.add(nombresJugadoresCrvena);

        /* EMPORIO ARMANI MILAN */
        Integer[] numerosMilan = {0, 1, 2, 3, 5, 7, 9, 12, 13, 17, 19, 22, 25, 31, 40, 42, 70, 77};
        String[] nombresJugadoresMilan = {"Brandon Davies", "Deshaun Thomas",
                "Timothe Luwawu-Cabarrot", "Naz Mitrou-Long", "Kevin Pangos", "Stefano Tonut",
                "Nicolo Melli", "Billy Baron", "Shabazz Napier", "Giampaolo Ricci", "Paul Biligha",
                "Devon Hall", "Tommaso Baldasso", "Shavon Shields", "Davide Alviti", "Kyle Hines",
                "Luigi Datome", "Johannes Voigtmann"
        };
        numerosEquipos.add(numerosMilan);
        nombresJugadoresEquipos.add(nombresJugadoresMilan);

        /* FC BARCELONA */
        Integer[] numerosBarcelona = {1, 3, 5, 6, 8, 10, 13, 20, 21, 22, 23, 24, 31, 33, 46, 50,
                                      54, 55};
        String[] nombresJugadoresBarcelona = {"Oscar Da Silva", "Oriol Pauli", "Sertac Sanli",
                "Jan Vesely", "Sergi Martinez", "Nikola Kalinic", "Tomas Satoransky",
                "Nicolas Laprovittola", "Alex Abrines", "Cory Higgins", "Mike Tobey", "Kyle Kuric",
                "Rokas Jokubaitis", "Nikola Mirotic", "James Nnaji", "Martin Iglesias",
                "Kasparas Jakucionis", "Dame Sarr"
        };
        numerosEquipos.add(numerosBarcelona);
        nombresJugadoresEquipos.add(nombresJugadoresBarcelona);

        /* FENERBAHCE */
        Integer[] numerosFenerbahce = {0, 1, 2, 3, 4, 5, 8, 9, 10, 11, 13, 21, 22, 23, 27, 31, 33,
                                       37};
        String[] nombresJugadoresFenerbahce = {"Johnathan Motley", "Metecan Birsen",
                "Sehmus Hazer", "Scottie Wilbekin", "Carsen Edwards", "Ismet Akpinar",
                "Nemanja Bjelica", "Samet Geyik", "Melih Mahmutoglu", "Nigel Hayes-Davis", "Tarik Biberovic",
                "Dyshawn Pierre", "Tonye Jekiri", "Marko Guduric", "Tyler Dorsey", "Devin Booker",
                "Nick Calathes", "Kostas Antetokoumpo"
        };
        numerosEquipos.add(numerosFenerbahce);
        nombresJugadoresEquipos.add(nombresJugadoresFenerbahce);

        /* LDLC ASVEL VILLEURBANE */
        Integer[] numerosAsvel = {0, 2, 3, 5, 7, 8, 9, 10, 12, 19, 23, 31, 32, 33, 35};
        String[] nombresJugadoresAsvel = {"Jonah Mathews", "Amine Noua", "Dee Bost",
                "Charles Kahudi", "Joffrey Lauvergne", "Antoine Diot", "Alex Tyus",
                "Zaccharie Risacher", "Nando de Colo", "Youssoupha Fall", "David Lighty",
                "Yaacov Noam", "Retin Obasohan", "Parket Jackson-Cartwright", "Yves Pons"
        };
        numerosEquipos.add(numerosAsvel);
        nombresJugadoresEquipos.add(nombresJugadoresAsvel);

        /* MACCABI TEL AVIV */
        Integer[] numerosMaccabi = {3, 4, 5, 6, 8, 9, 10, 12, 13, 15, 17, 18, 20, 22, 32, 33, 50};
        String[] nombresJugadoresMaccabi = {"Jalen Adams", "Lorenzo Brown", "Wade Baldwin IV",
                "Jarell Martin", "Rafi Menco", "Roman Sorkin", "Guy Pnini", "John Dibartolomeo",
                "Darrun Hilliard", "Jake Cohen", "Suleiman Braimoh", "Tomer Agmon",
                "Austin Hollins", "Alex Poythress", "Josh Nebo", "Iftah Ziv", "Bonzie Colson"
        };
        numerosEquipos.add(numerosMaccabi);
        nombresJugadoresEquipos.add(nombresJugadoresMaccabi);

        /* OLYMPIACOS */
        Integer[] numerosOlympiacos = {0, 3, 4, 5, 9, 10, 11, 14, 16, 19, 21, 25, 28, 77, 81};
        String[] nombresJugadoresOlympiacos = {"Thomas Walkup", "Isaiah Cannan",
                "Michalis Lountzis", "Giannoulis Larentzakis", "George Papas", "Moustapha Fall",
                "Kostas Sloukas", "Sasha Vezenkov", "Kostas Papanikolau", "Panagiotis Tsamis",
                "Joel Bolomboy", "Alec Peters", "Tarik Black", "Shaquielle McKissic",
                "Veniamin Abosi"
        };
        numerosEquipos.add(numerosOlympiacos);
        nombresJugadoresEquipos.add(nombresJugadoresOlympiacos);

        /* PANATHINAIKOS */
        Integer[] numerosPanathinaikos = {0, 3, 5, 6, 7, 8, 9, 11, 12, 16, 17, 20, 21, 24, 37, 40,
                                          72, 77};
        String[] nombresJugadoresPanathinaikos = {"Panagiotis Kalaitzakis", "Nate Wolters",
                "Paris Lee", "Georgios Papagiannis", "Eleftherios Bochoridis", "Derrick Williams",
                "Dimitris Agravanis", "Nikos Pappas", "Andrew Andrews", "Georgios Kalaitzakis",
                "Matt Thomas", "Alexandros Samontourov", "Neoklis Avdalas", "Dwayne Bacon",
                "Mateusz Ponitka", "Marius Grigonis", "Eleftherios Mantzoukas", "Arturas Gudaitis"
        };
        numerosEquipos.add(numerosPanathinaikos);
        nombresJugadoresEquipos.add(nombresJugadoresPanathinaikos);

        /* PARTIZAN BELGRADE */
        Integer[] numerosPartizan = {1, 2, 4, 5, 7, 9, 10, 11, 12, 21, 26, 32, 33, 41};
        String[] nombresJugadoresPartizan = {"Tristan Vukcevic", "Zach Leday", "Aleksa Avramovic",
                "Balsa Koprivica", "Kevin Punter", "Alen Smailagic", "Ioannis Papapetrou",
                "Dante Exum", "Vladimir Brodziansky", "James Nunnally", "Mathias Lessort",
                "Uros Trifunovic", "Danilo Andjusic", "Yam Madar"
        };
        numerosEquipos.add(numerosPartizan);
        nombresJugadoresEquipos.add(nombresJugadoresPartizan);

        /* REAL MADRID */
        Integer[] numerosMadrid = {0, 1, 3, 5, 6, 7, 8, 11, 12, 13, 14, 17, 21, 22, 23, 28, 30, 31};
        String[] nombresJugadoresMadrid = {"Nigel Williams-Goss", "Fabien Causeur",
                "Anthony Randolph", "Rudy Fernandez", "Alberto Abalde", "Hugo Gonzalez",
                "Adam Hanga", "Mario Hezonja", "Carlos Alocen", "Sergio Rodriguez",
                "Gabriel Deck", "Vincent Poirier", "Petr Cornelie", "Walter Tavares",
                "Sergio Llull", "Guerschon Yabusele", "Eli Ndiaye", "Dznan Musa"
        };
        numerosEquipos.add(numerosMadrid);
        nombresJugadoresEquipos.add(nombresJugadoresMadrid);

        /* VALENCIA BASKET */
        Integer[] numerosValencia = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 16, 21, 24, 41, 50, 54, 55, 57};
        String[] nombresJugadoresValencia = {"Jared Harper", "Victor Claver", "Josep Puerto",
                "Klemen Prepelic", "Jaime Pradilla", "James Webb III", "Xabi Lopez-Arostegui",
                "Chris Jones", "Guillem Ferrando", "San Van Rossom", "Jonah Radebaugh",
                "Shannon Evans", "Bojan Dubljevic", "Millan Jimenez", "Kyle Alexander",
                "Martin Hermannsson", "Jasiel Rivero", "Sergio de Larrea", "Pablo Navarro",
                "Lucas Marí", "David Barbera"
        };
        numerosEquipos.add(numerosValencia);
        nombresJugadoresEquipos.add(nombresJugadoresValencia);

        /* VIRTUS BOLOGNA */
        Integer[] numerosVirtus = {0, 1, 3, 6, 7, 14, 19, 21, 23, 24, 25, 29, 34, 37, 44, 55};
        String[] nombresJugadoresVirtus = {"Isaia Cordinier", "Niccolo Mannion", "Marco Belinelli",
                "Alessandro Pajola", "Ismael Bako", "Mam Jaiteh", "Iffe Lundberg",
                "Tornike Shengelia", "Daniel Hackett", "Leo Menalo", "Jordan Mickey", "Gora Camara",
                "Kyle Weems", "Semi Ojeleye", "Milos Teodosic", "Awudu Abass"
        };
        numerosEquipos.add(numerosVirtus);
        nombresJugadoresEquipos.add(nombresJugadoresVirtus);

        /* ZALGIRIS KAUNAS */
        Integer[] numerosZalgiris = {1, 2, 3, 4, 7, 8, 10, 14, 15, 16, 17, 25, 33, 34, 45, 51, 92};
        String[] nombresJugadoresZalgiris = {"Isaiah Taylor", "Keenan Evans", "Dovydas Giedraitis",
                "Lukas Lekavicius", "Kajus Kublickas", "Kevarrius Hayes", "Rolands Smits",
                "Motiejus Krivas", "Laurynas Birutis", "Karolis Lukosiunas", "Ignas Brazdeikis",
                "Achille Polonara", "Tomas Dimsa", "Tyler Cavanaugh", "Liutauras Lelevicius",
                "Arnas Butkevicius", "Edgaras Ulanovas"
        };
        numerosEquipos.add(numerosZalgiris);
        nombresJugadoresEquipos.add(nombresJugadoresZalgiris);

        for (int i = 0; i < nombresEquipos.length; i++) {
            String nombreEq = nombresEquipos[i];
            Integer[] numerosEq = numerosEquipos.get(i);
            String[] nombresJug = nombresJugadoresEquipos.get(i);
            añadirJugadoresDeUnEquipo(sqLiteDatabase, nombreEq, numerosEq, nombresJug);
        }
    }

    private void añadirJugadoresDeUnEquipo(
        SQLiteDatabase sqLiteDatabase,
        String nombreEq,
        Integer[] numerosEq,
        String[] nombresJug
    ) {
        for (int i = 0; i < numerosEq.length; i++) {
            ContentValues nuevoJugador = new ContentValues();
            nuevoJugador.put("numero", numerosEq[i]);
            nuevoJugador.put("nombre_equipo", nombreEq);
            nuevoJugador.put("nombre", nombresJug[i]);
            sqLiteDatabase.insert("Jugador", null, nuevoJugador);
        }
    }

    private void añadirPartidos(SQLiteDatabase sqLiteDatabase) {
        /* AÑADIR MÁS JORNADAS EN UN FUTURO */
        int[] jornadas = {27};

        /* JORNADA 27 */
        String[] fechasJ27 = {"2023-03-07", "2023-03-07", "2023-03-07", "2023-03-07",
                              "2023-03-07", "2023-03-07", "2023-03-08", "2023-03-08",
                              "2023-03-08"};
        String[] horasJ27 = {"20:00", "20:00", "20:30", "20:30", "20:45", "21:00", "18:30",
                             "20:30", "21:00"};
        int[] puntosJ27 = {63, 75, 84, 85,
                               76, 94, 88, 70,
                               74, 72, 78, 77,
                               70, 88, 81, 84,
                               79, 66};
        int[] partidosIdJ27 = {2, 8, 9, 5,
                               6, 8, 4, 1,
                               7, 6, 7, 1,
                               9, 3, 5, 4,
                               3, 2};
        int[] localJ27 = {1, 0, 1, 0,
                          1, 1, 0, 0,
                          0, 0, 1, 1,
                          0, 0, 1, 1,
                          1, 0};
        for (Integer jornada : jornadas) {
            añadirJornada(jornada, fechasJ27, horasJ27, sqLiteDatabase);
            añadirEquiposAJornada(jornada, partidosIdJ27, puntosJ27, localJ27, sqLiteDatabase);
        }
    }

    private void añadirJornada(
        int numJornada,
        String[] fechas,
        String[] horas,
        SQLiteDatabase sqLiteDatabase
    ) {
        // Al ser 18 equipos cada jornada son 9 partidos ( id [0 - 8] )
        for (int i = 0; i <= 8; i++) {
            ContentValues nuevaJornada = new ContentValues();
            nuevaJornada.put("id", i + 1);
            nuevaJornada.put("numero_jornada", numJornada);
            nuevaJornada.put("fecha", fechas[i]);
            nuevaJornada.put("hora", horas[i]);
            sqLiteDatabase.insert("Partido", null, nuevaJornada);
        }
    }

    private void añadirEquiposAJornada(
        int numJornada,
        int[] jornadaIds,
        int[] puntosJornada,
        int[] localJornada,
        SQLiteDatabase sqLiteDatabase
    ) {
        String[] nombresEquipos = {"Alba Berlin", "Anadolu Efes", "AS Monaco", "Baskonia",
                "Bayern Munich", "Crvena Zvezda", "Emporio Armani Milan", "FC Barcelona",
                "Fenerbahce", "LDLC Asvel Villeurbane", "Maccabi Tel Aviv", "Olympiacos",
                "Panathinaikos", "Partizan Belgrade", "Real Madrid", "Valencia Basket",
                "Virtus Bologna", "Zalgiris Kaunas"
        };

        for (int i = 0; i < nombresEquipos.length; i++) {
            ContentValues nuevoEquipoJornada = new ContentValues();
            nuevoEquipoJornada.put("nombre_equipo", nombresEquipos[i]);
            nuevoEquipoJornada.put("partido_id", jornadaIds[i]);
            nuevoEquipoJornada.put("partido_num_jornada", numJornada);
            nuevoEquipoJornada.put("puntos", puntosJornada[i]);
            nuevoEquipoJornada.put("local", localJornada[i]);
            sqLiteDatabase.insert("Juega", null, nuevoEquipoJornada);
        }
    }
}
